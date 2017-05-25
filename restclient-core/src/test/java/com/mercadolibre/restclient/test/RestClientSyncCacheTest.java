package com.mercadolibre.restclient.test;

import com.mercadolibre.restclient.*;
import com.mercadolibre.restclient.cache.DummyCache;
import com.mercadolibre.restclient.cache.RESTCache;
import com.mercadolibre.restclient.exception.RestException;
import com.mercadolibre.restclient.http.ContentType;
import com.mercadolibre.restclient.mock.RequestMockHolder;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;

import static com.mercadolibre.restclient.http.HttpMethod.GET;
import static org.junit.Assert.*;


public class RestClientSyncCacheTest extends RestClientTestBase {

    @Test
    public void shouldGetWithCacheInRequest() throws RestException {
        String url = "http://dummy.com/test";
        String body = "ok";

        MockResponse.builder()
                .withURL(url)
                .withMethod(GET)
                .withStatusCode(200)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                .withResponseHeader("Cache-Control", "max-age=3600")
                .withResponseBody(body)
                .build();

        Response response = RestClient.getDefault().withCache(DummyCache.getDefault()).get(url);

        assertEquals(200, response.getStatus());
        assertEquals(body, response.getString());
        assertEquals(DummyCache.getDefault().get(url).getString(), response.getString());

        RequestMockHolder.clear();
        MockResponse.builder()
                .withURL(url)
                .withMethod(GET)
                .withStatusCode(500)
                .build();

        response = RestClient.getDefault().withCache(DummyCache.getDefault()).get(url);

        assertEquals(200, response.getStatus());
        assertEquals(body, response.getString());
    }

    @Test
    public void shouldGetWithCacheInPool() throws RestException, IOException {
        String url = "http://dummy.com/test";
        String body = "ok";

        RESTPool pool = RESTPool
                .builder()
                .withCache(DummyCache.getDefault())
                .withName("cached")
                .build();

        RestClient restClient = RestClient
                .builder()
                .withPool(pool)
                .build();

        MockResponse.builder()
                .withURL(url)
                .withMethod(GET)
                .withStatusCode(200)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                .withResponseHeader("Cache-Control", "max-age=3600")
                .withResponseBody(body)
                .build();

        Response response = restClient.withPool("cached").get(url);

        assertEquals(200, response.getStatus());
        assertEquals(body, response.getString());
        assertEquals(DummyCache.getDefault().get(url).getString(), response.getString());

        RequestMockHolder.clear();
        MockResponse.builder()
                .withURL(url)
                .withMethod(GET)
                .withStatusCode(500)
                .build();

        response = restClient.withPool("cached").get(url);

        assertEquals(200, response.getStatus());
        assertEquals(body, response.getString());
    }

    @Test
    public void shouldCacheInMultipleLevels() throws RestException {
        String url = "http://dummy.com/test";
        String body = "ok";

        RESTCache l2 = new DummyCache("l2");
        RESTCache l1 = new DummyCache("l1", l2);

        MockResponse.builder()
                .withURL(url)
                .withMethod(GET)
                .withStatusCode(200)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                .withResponseHeader("Cache-Control", "max-age=3600")
                .withResponseBody(body)
                .build();

        Response response = RestClient.getDefault().withCache(l1).get(url);

        assertEquals(200, response.getStatus());
        assertEquals(body, response.getString());
        assertEquals(l1.get(url).getString(), response.getString());
        assertEquals(l2.get(url).getString(), response.getString());

        RequestMockHolder.clear();
        MockResponse.builder()
                .withURL(url)
                .withMethod(GET)
                .withStatusCode(500)
                .build();

        l1.evictAll();

        response = RestClient.getDefault().withCache(l1).get(url);
        assertEquals(200, response.getStatus());
        assertEquals(body, response.getString());
        assertEquals(l1.get(url).getString(), response.getString());
        assertEquals(l2.get(url).getString(), response.getString());
    }

    @Test
    public void shouldGetWithCacheFailure() throws RestException {
        String url = "http://dummy.com/test";
        String body = "ok";

        MockResponse.builder()
                .withURL(url)
                .withMethod(GET)
                .withStatusCode(200)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                .withResponseHeader("Cache-Control", "max-age=3600")
                .withResponseBody(body)
                .build();

        Response response = RestClient.getDefault().withCache(DummyCache.getDefault()).get(url);

        assertEquals(200, response.getStatus());
        assertEquals(body, response.getString());
        assertEquals(DummyCache.getDefault().get(url).getString(), response.getString());

        DummyCache.getDefault().setFail(true);

        RequestMockHolder.clear();
        MockResponse.builder()
                .withURL(url)
                .withMethod(GET)
                .withStatusCode(404)
                .build();

        response = RestClient.getDefault().withCache(DummyCache.getDefault()).get(url);

        assertEquals(404, response.getStatus());
        assertNull(DummyCache.getDefault().get(url));
    }

    @Test
    public void shouldGetWithCacheAndStaleRevalidate() throws RestException, InterruptedException {
        String url = "http://dummy.com/test";
        String body = "ok";

        MockResponse.builder()
                .withURL(url)
                .withMethod(GET)
                .withStatusCode(200)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                .withResponseHeader("Cache-Control", "max-age=1, stale-while-revalidate=3600")
                .withResponseBody(body)
                .build();

        Response response = RestClient.getDefault().withCache(DummyCache.getDefault()).get(url);

        assertEquals(200, response.getStatus());
        assertEquals(body, response.getString());
        assertEquals(DummyCache.getDefault().get(url).getString(), response.getString());

        DummyCache.getDefault().get(url).getCacheControl().setExpiration(new Date().getTime() - 100);

        RequestMockHolder.clear();
        MockResponse.builder()
                .withURL(url)
                .withMethod(GET)
                .withStatusCode(200)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                .withResponseHeader("Cache-Control", "max-age=1000")
                .withResponseBody("second")
                .build();

        response = RestClient.getDefault().withCache(DummyCache.getDefault()).get(url);

        assertEquals(200, response.getStatus());
        assertEquals(body, response.getString());

        Thread.sleep(100);
        assertEquals("second", DummyCache.getDefault().get(url).getString());
    }

    @Test
    public void shouldGetWithCacheAndStaleIfError() throws RestException, InterruptedException {
        String url = "http://dummy.com/test";
        String body = "ok";

        MockResponse.builder()
                .withURL(url)
                .withMethod(GET)
                .withStatusCode(200)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                .withResponseHeader("Cache-Control", "max-age=1, stale-if-error=3600")
                .withResponseBody(body)
                .build();

        Response response = RestClient.getDefault().withCache(DummyCache.getDefault()).get(url);

        assertEquals(200, response.getStatus());
        assertEquals(body, response.getString());
        assertEquals(DummyCache.getDefault().get(url).getString(), response.getString());

        DummyCache.getDefault().get(url).getCacheControl().setAge(100);
        DummyCache.getDefault().get(url).getCacheControl().setExpiration();

        RequestMockHolder.clear();
        MockResponse.builder()
                .withURL(url)
                .withMethod(GET)
                .withStatusCode(500)
                .build();

        response = RestClient.getDefault().withCache(DummyCache.getDefault()).get(url);

        assertEquals(200, response.getStatus());
        assertEquals(body, response.getString());

        Thread.sleep(100);
        assertEquals("ok", DummyCache.getDefault().get(url).getString());
    }

}
