package com.mercadolibre.restclient.httpc;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;

import com.mercadolibre.restclient.httpc.util.HTTPCAsyncClientMonitor;
import com.mercadolibre.restclient.httpc.util.HTTPCClientMonitor;
import com.mercadolibre.restclient.httpc.util.HTTPCSyncClientMonitor;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.HttpConnectionFactory;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.IdleConnectionEvictor;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.ManagedHttpClientConnectionFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.SystemDefaultDnsResolver;
import org.apache.http.impl.io.DefaultHttpRequestWriterFactory;
import org.apache.http.impl.io.DefaultHttpResponseParserFactory;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.ManagedNHttpClientConnectionFactory;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.conn.ManagedNHttpClientConnection;
import org.apache.http.nio.conn.NHttpConnectionFactory;
import org.apache.http.nio.conn.NoopIOSessionStrategy;
import org.apache.http.nio.conn.SchemeIOSessionStrategy;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.nio.util.HeapByteBufferAllocator;

import com.mercadolibre.restclient.Builder;
import com.mercadolibre.restclient.ExecCallbackAsyncREST;
import com.mercadolibre.restclient.ExecREST;
import com.mercadolibre.restclient.RESTPool;
import com.mercadolibre.restclient.RestClient;
import com.mercadolibre.restclient.http.Proxy;


public class HTTPCBuilder extends Builder<HTTPCBuilder,HttpResponse> {

    private static final long REACTOR_SELECT_INTERVAL = 100;

    private HTTPCBuilder(RestClient client) {
        super(client);
    }

    public HTTPCBuilder() {
    }

    @Override
    public HTTPCBuilder newInstance(RestClient restClient) {
        return new HTTPCBuilder(restClient);
    }

	protected ExecCallbackAsyncREST<HttpResponse> buildAsyncClient(RESTPool pool) throws IOException {
        SSLContext sslContext;
        try {
            sslContext = SSLContext.getDefault();
        } catch (NoSuchAlgorithmException e) {
            throw new IOException(e);
        }

        Registry<SchemeIOSessionStrategy> socketRegistry = RegistryBuilder.<SchemeIOSessionStrategy>create()
                .register("http", NoopIOSessionStrategy.INSTANCE)
                .register("https", new SSLIOSessionStrategy(sslContext, NoopHostnameVerifier.INSTANCE))
                .build();

        IOReactorConfig socketConfig = IOReactorConfig.custom()
                .setIoThreadCount(pool.getReactorThreadCount())
                .setSoTimeout(new Long(pool.getSocketTimeout()).intValue())
                .setTcpNoDelay(true)
                .setSoKeepAlive(true)
                .setSelectInterval(REACTOR_SELECT_INTERVAL)
                .build();

        ConnectionConfig connectionConfig = ConnectionConfig.custom()
                .setCharset(StandardCharsets.UTF_8)
                .setMalformedInputAction(CodingErrorAction.IGNORE)
                .setUnmappableInputAction(CodingErrorAction.IGNORE)
                .build();

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(new Long(pool.getMaxPoolWait()).intValue())
                .setConnectTimeout(new Long(pool.getConnectionTimeout()).intValue())
                .setExpectContinueEnabled(pool.expectContinue())
                .setRedirectsEnabled(false)
                .setStaleConnectionCheckEnabled(pool.getValidationOnInactivity() >= 0)
                .build();

        NHttpConnectionFactory<ManagedNHttpClientConnection> connFactory = new ManagedNHttpClientConnectionFactory(
                new org.apache.http.impl.nio.codecs.DefaultHttpRequestWriterFactory(),
                new org.apache.http.impl.nio.codecs.DefaultHttpResponseParserFactory(),
                HeapByteBufferAllocator.INSTANCE
        );

        //TODO set validateAfterInactivity when supported
        PoolingNHttpClientConnectionManager ccm = new PoolingNHttpClientConnectionManager(
                new DefaultConnectingIOReactor(socketConfig),
                connFactory,
                socketRegistry,
                new SystemDefaultDnsResolver()
        );

        ccm.setMaxTotal(pool.getMaxTotal());
        ccm.setDefaultMaxPerRoute(pool.getMaxPerRoute());
        ccm.setDefaultConnectionConfig(connectionConfig);

        HttpAsyncClientBuilder builder = HttpAsyncClients.custom()
                .setConnectionManager(ccm)
                .setDefaultRequestConfig(requestConfig)
                .setKeepAliveStrategy(DefaultConnectionKeepAliveStrategy.INSTANCE)
                .disableCookieManagement();

        IdleAsyncConnectionEvictor evictor = new IdleAsyncConnectionEvictor(ccm, pool.getEvictorSleep(), TimeUnit.MILLISECONDS, pool.getMaxIdleTime(), TimeUnit.MILLISECONDS);

        addProxy(pool, builder);

        handleRedirects(pool, builder);

        CloseableHttpAsyncClient servClient = builder.build();

        servClient.start();

        HTTPCClientMonitor monitor = pool.hasConnectionMetrics() ? new HTTPCAsyncClientMonitor(pool.getName(), ccm) : null;

        return new HTTPCAsyncClient(servClient, evictor, monitor);
    }

    @Override
    protected ExecREST buildClient(RESTPool pool) throws IOException {
        SSLContext sslContext;
        try {
            sslContext = SSLContext.getDefault();
        } catch (NoSuchAlgorithmException e) {
            throw new IOException(e);
        }

        Registry<ConnectionSocketFactory> socketRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE))
                .build();

        //TODO buffers size
        SocketConfig socketConfig = SocketConfig.custom()
                .setSoTimeout(new Long(pool.getSocketTimeout()).intValue())
                .setTcpNoDelay(true)
                .setSoKeepAlive(true)
                .setSoReuseAddress(true)
                .build();

        ConnectionConfig connectionConfig = ConnectionConfig.custom()
                .setCharset(StandardCharsets.UTF_8)
                .setMalformedInputAction(CodingErrorAction.IGNORE)
                .setUnmappableInputAction(CodingErrorAction.IGNORE)
                .build();

        HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory = new ManagedHttpClientConnectionFactory(
                new DefaultHttpRequestWriterFactory(),
                new DefaultHttpResponseParserFactory()
        );

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(new Long(pool.getMaxPoolWait()).intValue())
                .setConnectTimeout(new Long(pool.getConnectionTimeout()).intValue())
                .setExpectContinueEnabled(pool.expectContinue())
                .build();


        PoolingHttpClientConnectionManager ccm = new PoolingHttpClientConnectionManager(socketRegistry, connFactory);

        ccm.setMaxTotal(pool.getMaxTotal());
        ccm.setDefaultMaxPerRoute(pool.getMaxPerRoute());
        ccm.setDefaultSocketConfig(socketConfig);
        ccm.setDefaultConnectionConfig(connectionConfig);
        ccm.setValidateAfterInactivity(pool.getValidationOnInactivity());

        HttpClientBuilder builder = HttpClients.custom()
                .setConnectionManager(ccm)
                .setDefaultRequestConfig(requestConfig)
                .disableAutomaticRetries()
                .setKeepAliveStrategy(DefaultConnectionKeepAliveStrategy.INSTANCE)
                .disableCookieManagement()
                .disableContentCompression();

        addProxy(pool, builder);

        handleRedirects(pool, builder);

        CloseableHttpClient servClient = builder.build();

        IdleConnectionEvictor evictor = new IdleConnectionEvictor(ccm, pool.getEvictorSleep(), TimeUnit.MILLISECONDS, pool.getMaxIdleTime(), TimeUnit.MILLISECONDS);

        HTTPCClientMonitor monitor = pool.hasConnectionMetrics() ? new HTTPCSyncClientMonitor(pool.getName(), ccm) : null;

        return new HTTPCClient(servClient, evictor, monitor);
    }

    private void addProxy(RESTPool pool, HttpClientBuilder builder) {
        if (pool.getProxy() == null) return;

        Proxy proxy = pool.getProxy();

        if (proxy.getUsername() != null) {
            CredentialsProvider provider = makeProxyCredentialsProvider(proxy);
            builder.setDefaultCredentialsProvider(provider);
        }

        HttpHost proxyHost = new HttpHost(proxy.getHostname(), proxy.getPort());
        builder.setRoutePlanner(new DefaultProxyRoutePlanner(proxyHost));
    }

    private void addProxy(RESTPool pool, HttpAsyncClientBuilder builder) {
        if (pool.getProxy() == null) return;

        Proxy proxy = pool.getProxy();

        if (proxy.getUsername() != null) {
            CredentialsProvider provider = makeProxyCredentialsProvider(proxy);
            builder.setDefaultCredentialsProvider(provider);
        }

        HttpHost proxyHost = new HttpHost(proxy.getHostname(), proxy.getPort());
        builder.setRoutePlanner(new DefaultProxyRoutePlanner(proxyHost));
    }
    
    private CredentialsProvider makeProxyCredentialsProvider(Proxy proxy) {
        CredentialsProvider provider = new BasicCredentialsProvider();
        try {
            provider.setCredentials(
                    new AuthScope(proxy.getHostname(), proxy.getPort()),
                    new UsernamePasswordCredentials(proxy.getUsername(), URLEncoder.encode(proxy.getPassword(), StandardCharsets.UTF_8.toString()))
            );

            return provider;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleRedirects(RESTPool pool, HttpClientBuilder builder) {
        if (!pool.followRedirects())
            builder.disableRedirectHandling();
    }

    private void handleRedirects(RESTPool pool, HttpAsyncClientBuilder builder) {
        if (pool.followRedirects())
            builder.setRedirectStrategy(DefaultRedirectStrategy.INSTANCE);
    }

}