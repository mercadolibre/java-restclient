package com.mercadolibre.restclient.test;

import com.mercadolibre.restclient.*;
import com.mercadolibre.restclient.async.DummyCallbackProcessor;
import com.mercadolibre.restclient.cache.CacheControl;
import com.mercadolibre.restclient.cache.DummyCache;
import com.mercadolibre.restclient.cache.RESTCache;
import com.mercadolibre.restclient.exception.RestException;
import com.mercadolibre.restclient.http.ContentType;
import com.mercadolibre.restclient.mock.MockUtil;
import com.mercadolibre.restclient.mock.RequestMockHolder;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import static com.mercadolibre.restclient.http.HttpMethod.GET;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


public class RestClientAsyncCacheTest extends RestClientTestBase {

    @Test
    public void shouldGetWithCacheInRequest() throws RestException, ExecutionException, InterruptedException {
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

        Response response = RestClient.getDefault().withCache(DummyCache.getDefault()).asyncGet(url).get();

        assertEquals(200, response.getStatus());
        assertEquals(body, response.getString());
        assertEquals(DummyCache.getDefault().get(url).getString(), response.getString());

        RequestMockHolder.clear();
        MockResponse.builder()
                .withURL(url)
                .withMethod(GET)
                .withStatusCode(500)
                .build();

        response = RestClient.getDefault().withCache(DummyCache.getDefault()).asyncGet(url).get();

        assertEquals(200, response.getStatus());
        assertEquals(body, response.getString());
    }

    @Test
    public void shouldGetWithCacheInPool() throws RestException, IOException, ExecutionException, InterruptedException {
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

        Response response = restClient.withPool("cached").asyncGet(url).get();

        assertEquals(200, response.getStatus());
        assertEquals(body, response.getString());
        assertEquals(DummyCache.getDefault().get(url).getString(), response.getString());

        RequestMockHolder.clear();
        MockResponse.builder()
                .withURL(url)
                .withMethod(GET)
                .withStatusCode(500)
                .build();

        response = restClient.withPool("cached").asyncGet(url).get();

        assertEquals(200, response.getStatus());
        assertEquals(body, response.getString());
    }

    @Test
    public void shouldCacheInMultipleLevels() throws RestException, ExecutionException, InterruptedException {
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

        Response response = RestClient.getDefault().withCache(l1).asyncGet(url).get();

        LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(1));

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

        response = RestClient.getDefault().withCache(l1).asyncGet(url).get();
        assertEquals(200, response.getStatus());
        assertEquals(body, response.getString());
        assertEquals(l1.get(url).getString(), response.getString());
        assertEquals(l2.get(url).getString(), response.getString());
    }

    @Test
    public void shouldGetWithCacheFailure() throws RestException, ExecutionException, InterruptedException {
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

        Response response = RestClient.getDefault().withCache(DummyCache.getDefault()).asyncGet(url).get();

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

        response = RestClient.getDefault().withCache(DummyCache.getDefault()).asyncGet(url).get();

        assertEquals(404, response.getStatus());
        assertNull(DummyCache.getDefault().get(url));
    }

    @Test
    public void shouldGetWithCacheAndStaleRevalidate() throws RestException, InterruptedException, ExecutionException {
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

        Response response = RestClient.getDefault().withCache(DummyCache.getDefault()).asyncGet(url).get();

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

        response = RestClient.getDefault().withCache(DummyCache.getDefault()).asyncGet(url).get();

        assertEquals(200, response.getStatus());
        assertEquals(body, response.getString());

        Thread.sleep(100);
        assertEquals("second", DummyCache.getDefault().get(url).getString());
    }

    @Test
    public void shouldGetWithCacheAndStaleIfError() throws RestException, InterruptedException, ExecutionException {
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

        Response response = RestClient.getDefault().withCache(DummyCache.getDefault()).asyncGet(url).get();

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

        response = RestClient.getDefault().withCache(DummyCache.getDefault()).asyncGet(url).get();

        assertEquals(200, response.getStatus());
        assertEquals(body, response.getString());

        Thread.sleep(100);
        assertEquals("ok", DummyCache.getDefault().get(url).getString());
    }

    @Test
    public void shouldGetWithCacheAndStaleRevalidateWithNoStaleAllowed() throws RestException, InterruptedException, ExecutionException {
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

        RESTCache cache = new DummyCache("no_stale");
        cache.setAllowStaleResponse(false);

        Response response = RestClient.getDefault().withCache(cache).asyncGet(url).get();

        assertEquals(200, response.getStatus());
        assertEquals(body, response.getString());
        assertEquals(cache.get(url).getString(), response.getString());

        cache.get(url).getCacheControl().setExpiration(new Date().getTime() - 100);

        RequestMockHolder.clear();
        MockResponse.builder()
                .withURL(url)
                .withMethod(GET)
                .withStatusCode(200)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                .withResponseHeader("Cache-Control", "max-age=1000")
                .withResponseBody("second")
                .build();

        response = RestClient.getDefault().withCache(cache).asyncGet(url).get();

        assertEquals(200, response.getStatus());
        assertEquals("second", response.getString());

        Thread.sleep(100);
        assertEquals("second", cache.get(url).getString());
    }

    @Test
    public void shouldGetWithCacheException() throws RestException, ExecutionException, InterruptedException {
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

        Response response = RestClient.getDefault().withCache(DummyCache.getDefault()).asyncGet(url).get();

        assertEquals(200, response.getStatus());
        assertEquals(body, response.getString());
        assertEquals(DummyCache.getDefault().get(url).getString(), response.getString());

        DummyCache.getDefault().setThrow(true);
        RequestMockHolder.clear();

        MockResponse.builder()
                .withURL(url)
                .withMethod(GET)
                .withStatusCode(404)
                .build();

        response = RestClient.getDefault().withCache(DummyCache.getDefault()).asyncGet(url).get();

        assertEquals(404, response.getStatus());
    }

    @Test(expected = ExecutionException.class)
    public void shouldGetWithCacheFailAndRestException() throws RestException, ExecutionException, InterruptedException {
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

        Response response = RestClient.getDefault().withCache(DummyCache.getDefault()).asyncGet(url).get();

        assertEquals(200, response.getStatus());
        assertEquals(body, response.getString());
        assertEquals(DummyCache.getDefault().get(url).getString(), response.getString());

        DummyCache.getDefault().setFail(true);
        RequestMockHolder.clear();

        MockResponse.builder()
                .withURL(url)
                .withMethod(GET)
                .withStatusCode(404)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                .withResponseBody(DummyCallbackProcessor.THROW)
                .build();

        RestClient.getDefault().withCache(DummyCache.getDefault()).asyncGet(url).get();
    }

    @Test(expected = ExecutionException.class)
    public void shouldGetWithCacheExceptionAndRestException() throws RestException, ExecutionException, InterruptedException {
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

        Response response = RestClient.getDefault().withCache(DummyCache.getDefault()).asyncGet(url).get();

        assertEquals(200, response.getStatus());
        assertEquals(body, response.getString());
        assertEquals(DummyCache.getDefault().get(url).getString(), response.getString());

        DummyCache.getDefault().setThrow(true);
        RequestMockHolder.clear();

        MockResponse.builder()
                .withURL(url)
                .withMethod(GET)
                .withStatusCode(404)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                .withResponseBody(DummyCallbackProcessor.THROW)
                .build();

        RestClient.getDefault().withCache(DummyCache.getDefault()).asyncGet(url).get();
    }

    @Test
    public void shouldGetWithCacheAndStaleRevalidateWithNoStaleAllowedAndExpiration() throws RestException, InterruptedException, ExecutionException {
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

        RESTCache cache = new DummyCache("no_stale");
        cache.setAllowStaleResponse(false);

        Response response = RestClient.getDefault().withCache(cache).asyncGet(url).get();

        assertEquals(200, response.getStatus());
        assertEquals(body, response.getString());
        assertEquals(cache.get(url).getString(), response.getString());

        cache.get(url).getCacheControl().setExpiration(new Date().getTime() - 100);

        RequestMockHolder.clear();
        MockResponse.builder()
                .withURL(url)
                .withMethod(GET)
                .withStatusCode(200)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                .withResponseBody("second")
                .build();

        response = RestClient.getDefault().withCache(cache).asyncGet(url).get();

        assertEquals(200, response.getStatus());
        assertEquals("second", response.getString());
    }

    @Test(expected = ExecutionException.class)
    public void shouldGetWithCacheAndStaleRevalidateWithNoStaleAllowedAndExpirationIfException() throws RestException, InterruptedException, ExecutionException {
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

        RESTCache cache = new DummyCache("no_stale");
        cache.setAllowStaleResponse(false);

        Response response = RestClient.getDefault().withCache(cache).asyncGet(url).get();

        assertEquals(200, response.getStatus());
        assertEquals(body, response.getString());
        assertEquals(cache.get(url).getString(), response.getString());

        cache.get(url).getCacheControl().setExpiration(new Date().getTime() - 100);

        RequestMockHolder.clear();
        MockResponse.builder()
                .withURL(url)
                .withMethod(GET)
                .withStatusCode(200)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                .withResponseBody("second")
                .shouldFail()
                .build();

        RestClient.getDefault().withCache(cache).asyncGet(url).get();
    }

    @Test
    public void shouldGoToBackendIfFullyExpired() throws RestException, InterruptedException, ExecutionException, NoSuchFieldException, IllegalAccessException {
        String url = "http://dummy.com/test";
        String body = "ok";

        MockResponse.builder()
                .withURL(url)
                .withMethod(GET)
                .withStatusCode(200)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                .withResponseHeader("Cache-Control", "max-age=1")
                .withResponseBody(body)
                .build();

        RESTCache cache = DummyCache.getDefault();

        Response response = RestClient.getDefault().withCache(cache).asyncGet(url).get();

        assertEquals(200, response.getStatus());
        assertEquals(body, response.getString());
        assertEquals(cache.get(url).getString(), response.getString());

        CacheControl cacheControl = cache.get(url).getCacheControl();

        cacheControl.setExpiration(System.currentTimeMillis() - 1000);

        MockUtil.setAttribute("created", cacheControl, System.currentTimeMillis() - 2000);

        RequestMockHolder.clear();
        MockResponse.builder()
                .withURL(url)
                .withMethod(GET)
                .withStatusCode(200)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                .withResponseHeader("Cache-Control", "max-age=0")
                .withResponseBody("second")
                .build();

        response = RestClient.getDefault().withCache(cache).asyncGet(url).get();

        assertEquals(200, response.getStatus());
        assertEquals("second", response.getString());
        assertEquals("ok", cache.get(url).getString());
    }

    @Test
    public void shouldGoToBackendAndCacheIfStale() throws RestException, InterruptedException, ExecutionException, NoSuchFieldException, IllegalAccessException {
        String url = "http://dummy.com/test";
        String body = "ok";

        MockResponse.builder()
                .withURL(url)
                .withMethod(GET)
                .withStatusCode(200)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                .withResponseHeader("Cache-Control", "max-age=1")
                .withResponseBody(body)
                .build();

        RESTCache cache = DummyCache.getDefault();

        Response response = RestClient.getDefault().withCache(cache).asyncGet(url).get();

        assertEquals(200, response.getStatus());
        assertEquals(body, response.getString());
        assertEquals(cache.get(url).getString(), response.getString());

        CacheControl cacheControl = cache.get(url).getCacheControl();

        cacheControl.setExpiration(System.currentTimeMillis() - 1000);

        MockUtil.setAttribute("created", cacheControl, System.currentTimeMillis() - 2000);

        RequestMockHolder.clear();
        MockResponse.builder()
                .withURL(url)
                .withMethod(GET)
                .withStatusCode(200)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                .withResponseHeader("Cache-Control", "max-age=0, stale-while-revalidate=10")
                .withResponseBody("second")
                .build();

        response = RestClient.getDefault().withCache(cache).asyncGet(url).get();

        assertEquals(200, response.getStatus());
        assertEquals("second", response.getString());
        assertEquals("second", cache.get(url).getString());
    }

    @Test
    public void shouldSendCachedResponseOnFailureIfAllowed() throws RestException, InterruptedException, ExecutionException, NoSuchFieldException, IllegalAccessException {
        String url = "http://dummy.com/test";
        String body = "ok";

        MockResponse.builder()
                .withURL(url)
                .withMethod(GET)
                .withStatusCode(200)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                .withResponseHeader("Cache-Control", "max-age=1, stale-if-error=1000")
                .withResponseBody(body)
                .build();

        RESTCache cache = DummyCache.getDefault();

        Response response = RestClient.getDefault().withCache(cache).asyncGet(url).get();

        assertEquals(200, response.getStatus());
        assertEquals(body, response.getString());
        assertEquals(cache.get(url).getString(), response.getString());

        CacheControl cacheControl = cache.get(url).getCacheControl();

        cacheControl.setExpiration(System.currentTimeMillis() - 1000);

        MockUtil.setAttribute("created", cacheControl, System.currentTimeMillis() - 2000);

        RequestMockHolder.clear();
        MockResponse.builder()
                .withURL(url)
                .withMethod(GET)
                .withStatusCode(200)
                .shouldFail()
                .build();

        response = RestClient.getDefault().withCache(cache).asyncGet(url).get();

        assertEquals(200, response.getStatus());
        assertEquals("ok", response.getString());
        assertEquals("ok", cache.get(url).getString());
    }

}
