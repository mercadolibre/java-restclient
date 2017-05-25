package com.mercadolibre.restclient.httpc.util;

import java.io.IOException;
import java.util.Collection;

import com.mercadolibre.restclient.multipart.MultipartMode;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.DeflateDecompressingEntity;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.mercadolibre.restclient.RESTPool;
import com.mercadolibre.restclient.Request;
import com.mercadolibre.restclient.http.Header;
import com.mercadolibre.restclient.http.Headers;
import com.mercadolibre.restclient.http.Proxy;
import com.mercadolibre.restclient.multipart.Part;
import com.mercadolibre.restclient.multipart.PartVisitor;


public class HTTPCUtil {

    private static final String CONTENT_ENCODING_GZIP = "gzip";
    private static final String CONTENT_ENCODING_DEFLATE = "deflate";

    public static byte[] handleResponse(HttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        if (entity == null) return null;

        byte[] output = EntityUtils.toByteArray(entity);
        EntityUtils.consume(entity);

        return output;
    }

    public static HttpEntity handleCompressedEntity(HttpEntity entity) {
        org.apache.http.Header contentEncoding = entity.getContentEncoding();
        if (contentEncoding != null)
            for (HeaderElement e : contentEncoding.getElements()) {
                if (CONTENT_ENCODING_GZIP.equalsIgnoreCase(e.getName())) {
                    return new GzipDecompressingEntity(entity);
                }

                if (CONTENT_ENCODING_DEFLATE.equalsIgnoreCase(e.getName())) {
                    return new DeflateDecompressingEntity(entity);
                }
            }

        return entity;
    }
    
    public static Headers getHeaders(HttpResponse response) {
        Headers output = new Headers();

        org.apache.http.Header[] headers = response.getAllHeaders();
        if (headers != null) {
            for (org.apache.http.Header header : headers)
                output.add(new Header(header.getName(), header.getValue()));
        }

        return output;
    }

	public static void setHeaders(HttpRequestBase method, Headers headers) {
        if (headers == null) return;

        for (Header h : headers)
            method.setHeader(h.getName(), h.getValue());
    }

    public static void setProxy(HttpRequestBase method, Proxy proxy) {
        method.setConfig(RequestConfig.custom().setProxy(new HttpHost(proxy.getHostname(), proxy.getPort())).build());
    }
    
    public static void setMethodAttributes(HttpEntityEnclosingRequestBase method,  Request request) {
		if(request.getBody() != null) { 
			method.setEntity(new ByteArrayEntity(request.getBody()));
		}

        if (request.getParts() != null) {
            HttpEntity re = createMultipartEntities(request.getMultipartMode(), request.getParts());
            method.setEntity(re);
        }
    	
    	setMethodAttributes((HttpRequestBase) method, request);
    }

	private static HttpEntity createMultipartEntities(MultipartMode mode, Collection<Part<?>> parts) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        builder.setMode(adaptMultipartMode(mode));

        PartVisitor visitor = new HTTPCPartVisitor(builder);
        for (Part<?> part : parts) part.accept(visitor);

        return builder.build();
    }

    private static HttpMultipartMode adaptMultipartMode(MultipartMode mode) {
        switch (mode) {
            case STRICT: return HttpMultipartMode.STRICT;
            case BROWSER_COMPATIBLE: return HttpMultipartMode.BROWSER_COMPATIBLE;
            case RFC6532: return HttpMultipartMode.RFC6532;
            default: throw new IllegalArgumentException("Unknown multipart mode: " + mode);
        }
    }

    public static void setMethodAttributes(HttpRequestBase method,  Request request) {
    	HTTPCUtil.setHeaders(method, request.getHeaders());
        
        if (request.getProxy() != null) {
            if (request.getProxy().getUsername() != null)
                throw new UnsupportedOperationException("Authenticated proxy is only supported through pool definition");

            HTTPCUtil.setProxy(method, request.getProxy());
        }
    }
    
    public static HttpContext createContext(Request request) {
    	HttpClientContext context = null;
    	
    	RESTPool pool = request.getPool();
    	
    	if(pool.getAuthentication() != null || request.getAuthentication() != null) {
    		context = HttpClientContext.create();
    		CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    		
    		if(request.getAuthentication() != null) {
    			credentialsProvider.setCredentials(
					new AuthScope(request.getAuthentication().getHostname(), request.getAuthentication().getPort()), 
				    new UsernamePasswordCredentials(request.getAuthentication().getUsername(), request.getAuthentication().getPassword())
				);
    			
    			context.setCredentialsProvider(credentialsProvider);
    		
    		} else if(pool.getAuthentication() != null) {
    			credentialsProvider.setCredentials(
					new AuthScope(pool.getAuthentication().getHostname(), pool.getAuthentication().getPort()), 
				    new UsernamePasswordCredentials(pool.getAuthentication().getUsername(), request.getAuthentication().getPassword())
				);
    			
    			context.setCredentialsProvider(credentialsProvider);
    		}
    	}
    	
    	return context != null ? context : new BasicHttpContext();
    }

}
