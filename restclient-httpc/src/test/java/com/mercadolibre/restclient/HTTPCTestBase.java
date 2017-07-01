package com.mercadolibre.restclient;

import com.mercadolibre.restclient.http.Headers;
import com.mercadolibre.restclient.http.HttpMethod;
import com.mercadolibre.restclient.interceptor.AddTimeInterceptor;
import com.mercadolibre.restclient.mock.HTTPCMockHandler;
import com.mercadolibre.restclient.mock.HTTPCMockServer;
import com.mercadolibre.restclient.retry.NoopRetryStrategy;
import org.apache.http.HttpResponse;
import org.junit.After;

import java.util.Map;


public class HTTPCTestBase {

    @After
    public void after() {
        HTTPCMockHandler.INSTANCE.clear();
    }

    protected Request makeRequest(HttpMethod method, String url, Map<String,String> headers, byte[] body) {
        Request r =  new Request();

        r.setMethod(method);
        r.setURL("http://localhost:" + HTTPCMockServer.INSTANCE.getPort() + url);
        r.setHeaders(new Headers(headers));
        r.setBody(body);
        r.setClients(new ClientHolder.Clients<HttpResponse>(null,null,RESTPool.DEFAULT));
        r.setAttribute(AddTimeInterceptor.NAME, System.currentTimeMillis());
        r.setRetryStrategy(NoopRetryStrategy.INSTANCE);

        return r;
    }

    protected Request makeRequest(HttpMethod method, String url, byte[] body) {
        Request r =  new Request();

        r.setMethod(method);
        r.setURL("http://localhost:" + HTTPCMockServer.INSTANCE.getPort() + url);
        r.setBody(body);
        r.setClients(new ClientHolder.Clients<HttpResponse>(null,null,RESTPool.DEFAULT));
        r.setAttribute(AddTimeInterceptor.NAME, System.currentTimeMillis());
        r.setRetryStrategy(NoopRetryStrategy.INSTANCE);

        return r;
    }

    protected Request makeRequest(HttpMethod method, String url) {
        Request r =  new Request();

        r.setMethod(method);
        r.setURL("http://localhost:" + HTTPCMockServer.INSTANCE.getPort() + url);
        r.setClients(new ClientHolder.Clients<HttpResponse>(null,null,RESTPool.DEFAULT));
        r.setAttribute(AddTimeInterceptor.NAME, System.currentTimeMillis());
        r.setRetryStrategy(NoopRetryStrategy.INSTANCE);

        return r;
    }

    protected Request makeRequest(HttpMethod method, String url, Map<String,String> headers) {
        Request r =  new Request();

        r.setMethod(method);
        r.setURL("http://localhost:" + HTTPCMockServer.INSTANCE.getPort() + url);
        r.setHeaders(new Headers(headers));
        r.setClients(new ClientHolder.Clients<HttpResponse>(null,null,RESTPool.DEFAULT));
        r.setAttribute(AddTimeInterceptor.NAME, System.currentTimeMillis());
        r.setRetryStrategy(NoopRetryStrategy.INSTANCE);

        return r;
    }

}
