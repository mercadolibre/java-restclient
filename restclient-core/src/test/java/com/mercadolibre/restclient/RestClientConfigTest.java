package com.mercadolibre.restclient;

import com.mercadolibre.restclient.cache.RESTCache;
import com.mercadolibre.restclient.exception.RestException;
import com.mercadolibre.restclient.http.Authentication;
import com.mercadolibre.restclient.http.ContentType;
import com.mercadolibre.restclient.http.HttpMethod;
import com.mercadolibre.restclient.http.Proxy;
import com.mercadolibre.restclient.interceptor.*;
import com.mercadolibre.restclient.mock.MockUtil;
import com.mercadolibre.restclient.multipart.ByteArrayPart;
import com.mercadolibre.restclient.multipart.Part;
import com.mercadolibre.restclient.multipart.StringPart;
import com.mercadolibre.restclient.retry.RetryStrategy;
import com.mercadolibre.restclient.retry.SimpleRetryStrategy;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;


public class RestClientConfigTest extends RestClientTestBase {

    @Test
    public void shouldSetProxy() throws RestException {
        Proxy proxy = new Proxy("localhost",80);
        Request request = RestClient.getDefault().withProxy(proxy).withURL("http://test").build();

        assertEquals(proxy, request.getProxy());
    }

    @Test
    public void shouldSetProxyByHost() throws RestException {
        Proxy proxy = new Proxy("localhost",80);
        Request request = RestClient.getDefault().withProxy("localhost",80).withURL("http://test").build();

        assertEquals(proxy, request.getProxy());
    }

    @Test
    public void shouldSetPoolByName() throws RestException, IOException {
        RESTPool pool = RESTPool.builder().withName("test").build();
        RestClient restClient = RestClient.builder().withPool(pool).build();
        Request request = restClient.withPool(pool.getName()).withURL("http://test").build();

        assertEquals(restClient.getHolder().getClient(pool.getName()), request.getClients().getSyncClient());
    }

    @Test
    public void shouldSetPool() throws RestException, IOException {
        RESTPool pool = RESTPool.builder().withName("test").build();
        RestClient restClient = RestClient.builder().withPool(pool).build();
        Request request = restClient.withPool(pool).withURL("http://test").build();

        assertEquals(restClient.getHolder().getClient(pool.getName()), request.getClients().getSyncClient());
    }

    @Test
    public void shouldSetParameters() throws RestException {
        Map<String,String> parameters = new HashMap<>();
        parameters.put("a","1");
        parameters.put("b","2");

        Request request = RestClient.getDefault().withParameters(parameters).withURL("http://test").build();

        assertEquals(parameters, request.getParameters());
    }

    @Test
    public void shouldSetParameter() throws RestException {
        Map<String,String> parameters = new HashMap<>();
        parameters.put("a","1");

        Request request = RestClient.getDefault().withParameter("a","1").withURL("http://test").build();

        assertEquals(parameters, request.getParameters());
    }

    @Test
    public void shouldSetAttributes() throws RestException {
        Map<String,Object> attributes = new HashMap<>();
        attributes.put("a",1);
        attributes.put("b",2);

        Request request = RestClient.getDefault().withAttributes(attributes).withURL("http://test").build();

        assertEquals(attributes, request.getAttributes());
    }

    @Test
    public void shouldSetAttribute() throws RestException {
        Map<String,Object> attributes = new HashMap<>();
        attributes.put("a",1);

        Request request = RestClient.getDefault().withAttribute("a",1).withURL("http://test").build();

        assertEquals(attributes, request.getAttributes());
    }

    @Test
    public void shouldSetAuthentication() throws RestException {
        Authentication auth = new Authentication("localhost",80,"user","pass");
        Request request = RestClient.getDefault().withAuthentication(auth).withURL("http://test").build();

        assertEquals(auth, request.getAuthentication());
    }

    @Test
    public void shouldSetRetryStrategy() throws RestException {
        RetryStrategy retryStrategy = new SimpleRetryStrategy(1,100);
        Request request = RestClient.getDefault().withRetryStrategy(retryStrategy).withURL("http://test").build();

        assertEquals(retryStrategy, request.getRetryStrategy());
    }

    @Test
    public void shouldAddRequestInterceptors() throws RestException {
        RequestInterceptor first = new AcceptInterceptor("1");
        RequestInterceptor second = new AcceptInterceptor("2");
        RequestInterceptor third = new AcceptInterceptor("3");

        Request request = RestClient.getDefault()
                .withInterceptorFirst(second)
                .withInterceptorFirst(first)
                .withInterceptorLast(third)
                .withURL("http://test")
                .build();

        assertEquals(6, request.getRequestInterceptors().size());
        assertEquals(AddTimeInterceptor.INSTANCE, request.getRequestInterceptors().pop());
        assertEquals(new ContentTypeInterceptor(ContentType.APPLICATION_JSON), request.getRequestInterceptors().pop());
        assertEquals(new AcceptInterceptor(ContentType.APPLICATION_JSON.getMimeType()), request.getRequestInterceptors().pop());
        assertEquals(first, request.getRequestInterceptors().pop());
        assertEquals(second, request.getRequestInterceptors().pop());
        assertEquals(third, request.getRequestInterceptors().pop());
    }

    @Test
    public void shouldAddResponseInterceptors() throws RestException {
        ResponseInterceptor first = new ResponseInterceptor() {
            @Override
            public void intercept(Response r) {

            }
        };

        ResponseInterceptor second = new ResponseInterceptor() {
            @Override
            public void intercept(Response r) {

            }
        };

        ResponseInterceptor third = new ResponseInterceptor() {
            @Override
            public void intercept(Response r) {

            }
        };

        Request request = RestClient.getDefault()
                .withInterceptorFirst(second)
                .withInterceptorFirst(first)
                .withInterceptorLast(third)
                .withURL("http://test")
                .build();

        assertEquals(3, request.getResponseInterceptors().size());
        assertEquals(first, request.getResponseInterceptors().pop());
        assertEquals(second, request.getResponseInterceptors().pop());
        assertEquals(third, request.getResponseInterceptors().pop());
    }

    @Test
    public void shouldAddRequestInterceptorsLast() throws RestException {
        RequestInterceptor first = new AcceptInterceptor("1");
        RequestInterceptor second = new AcceptInterceptor("2");
        RequestInterceptor third = new AcceptInterceptor("3");

        Request request = RestClient.getDefault()
                .withInterceptorLast(second)
                .withInterceptorFirst(first)
                .withInterceptorLast(third)
                .withURL("http://test")
                .build();

        assertEquals(6, request.getRequestInterceptors().size());
        assertEquals(AddTimeInterceptor.INSTANCE, request.getRequestInterceptors().pop());
        assertEquals(new ContentTypeInterceptor(ContentType.APPLICATION_JSON), request.getRequestInterceptors().pop());
        assertEquals(new AcceptInterceptor(ContentType.APPLICATION_JSON.getMimeType()), request.getRequestInterceptors().pop());
        assertEquals(first, request.getRequestInterceptors().pop());
        assertEquals(second, request.getRequestInterceptors().pop());
        assertEquals(third, request.getRequestInterceptors().pop());
    }

    @Test
    public void shouldAddResponseInterceptorsLast() throws RestException {
        ResponseInterceptor first = new ResponseInterceptor() {
            @Override
            public void intercept(Response r) {

            }
        };

        ResponseInterceptor second = new ResponseInterceptor() {
            @Override
            public void intercept(Response r) {

            }
        };

        ResponseInterceptor third = new ResponseInterceptor() {
            @Override
            public void intercept(Response r) {

            }
        };

        Request request = RestClient.getDefault()
                .withInterceptorLast(second)
                .withInterceptorFirst(first)
                .withInterceptorLast(third)
                .withURL("http://test")
                .build();

        assertEquals(3, request.getResponseInterceptors().size());
        assertEquals(first, request.getResponseInterceptors().pop());
        assertEquals(second, request.getResponseInterceptors().pop());
        assertEquals(third, request.getResponseInterceptors().pop());
    }

    @Test
    public void shouldSetOutputStream() throws RestException, IOException {
        try (OutputStream os = new ByteArrayOutputStream()) {
            Request request = RestClient.getDefault().withOutputStream(os).withURL("http://test").build();
            assertEquals(os, request.getOutputStream());
        }
    }

    @Test
    public void shouldSetParts() throws RestException {
        Set<Part<?>> parts = new HashSet<>();
        parts.add(new ByteArrayPart("1", "b1", new byte[0]));
        parts.add(new StringPart("2", ""));

        Request request = RestClient.getDefault().withParts(parts).withURL("http://test").withMethod(HttpMethod.POST).build();

        assertEquals(parts, request.getParts());
    }

    @Test
    public void shouldSetPart() throws RestException {
        ByteArrayPart part = new ByteArrayPart("1", "b1", new byte[0]);
        Set<Part<?>> parts = new HashSet<>();
        parts.add(part);

        Request request = RestClient.getDefault().withPart(part).withURL("http://test").withMethod(HttpMethod.POST).build();

        assertEquals(parts, request.getParts());
    }

    @Test
    public void shouldSetCache() throws RestException {
        RESTCache cache = new RESTCache("cache") {
            @Override
            public Response get(String url) {
                return null;
            }

            @Override
            public void put(String url, Response response) {
            }

            @Override
            public void evict(String url) {
            }

            @Override
            public void evictAll() {
            }

            @Override
            public void close() throws IOException {
            }
        };

        Request request = RestClient.getDefault().withCache(cache).withURL("http://test").build();

        assertEquals(cache, request.getCache());
    }

    @Test
    public void shouldSetCacheByName() throws RestException, IOException {
        RESTCache cache = new RESTCache("cache") {
            @Override
            public Response get(String url) {
                return null;
            }

            @Override
            public void put(String url, Response response) {
            }

            @Override
            public void evict(String url) {
            }

            @Override
            public void evictAll() {
            }

            @Override
            public void close() throws IOException {
            }
        };

        RESTPool pool = RESTPool.builder().withName("cached").withCache(cache).build();

        RestClient restClient = RestClient.builder().withPool(pool).build();

        assertEquals(cache, restClient.getHolder().getCache("cache"));
    }

    @Test
    public void shouldParseParameters() throws RestException {
        Request request = new Request();
        request.setURL("http://test/r?a=1&b=2");

        assertEquals("http://test/r", request.getPlainURL());
        assertEquals("http://test/r?a=1&b=2", request.getURL());
        assertEquals("/r", request.getURI());

        assertEquals("1", request.getParameter("a"));
        assertEquals("2", request.getParameter("b"));
        assertEquals(2, request.getParameters().size());
    }

    @Test
    public void shouldParseParametersAndMergeAfter() throws RestException {
        Request request = new Request();
        request.setURL("http://test/r?a=1&b=2");
        request.setParameter("c", "3");

        assertEquals("http://test/r", request.getPlainURL());
        assertEquals("http://test/r?a=1&b=2&c=3", request.getURL());
        assertEquals("/r", request.getURI());

        assertEquals("1", request.getParameter("a"));
        assertEquals("2", request.getParameter("b"));
        assertEquals("3", request.getParameter("c"));
        assertEquals(3, request.getParameters().size());
    }

    @Test
    public void shouldParseParametersAndMergeBefore() throws RestException {
        Request request = new Request();
        request.setParameter("c", "3");
        request.setURL("http://test/r?a=1&b=2");

        assertEquals("http://test/r", request.getPlainURL());
        assertEquals("http://test/r?a=1&b=2&c=3", request.getURL());
        assertEquals("/r", request.getURI());

        assertEquals("1", request.getParameter("a"));
        assertEquals("2", request.getParameter("b"));
        assertEquals("3", request.getParameter("c"));
        assertEquals(3, request.getParameters().size());
    }

    @Test(expected = RuntimeException.class)
    public void shouldDisableDefaultPool() throws RestException, IOException, NoSuchFieldException, IllegalAccessException {
        RESTPool pool = RESTPool.builder().withName("test").build();
        RestClient restClient = RestClient.builder().withPool(pool).disableDefault().build();
        Request request = restClient.withPool(pool.getName()).withURL("http://test").build();

        assertEquals(restClient.getHolder().getClient(pool.getName()), request.getClients().getSyncClient());

        ClientHolder holder = (ClientHolder) MockUtil.getAttribute("holder", restClient);

        holder.getDefaultClients();
    }

}
