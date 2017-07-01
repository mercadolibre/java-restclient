package com.mercadolibre.restclient.test;

import com.mercadolibre.restclient.MockResponse;
import com.mercadolibre.restclient.Response;
import com.mercadolibre.restclient.RestClient;
import com.mercadolibre.restclient.RestClientTestBase;
import com.mercadolibre.restclient.async.Callback;
import com.mercadolibre.restclient.cache.DummyCache;
import com.mercadolibre.restclient.exception.RestException;
import com.mercadolibre.restclient.http.ContentType;
import com.mercadolibre.restclient.http.Headers;
import com.mercadolibre.restclient.mock.RequestMockHolder;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

import static com.mercadolibre.restclient.http.HttpMethod.*;
import static org.junit.Assert.*;


public class RestClientAsyncCallbackTest extends RestClientTestBase {

    private static class Holder {
        private volatile Response response;

        public Holder() {

        }

        public Holder(Response response) {
            this.response = response;
        }

        public void clear() {
            this.response = null;
        }
    }

    protected void waitForCondition(long ms, int retries, Callable<Boolean> condition) {
        while (retries-- > 0) {
            try {
                if (condition.call()) return;
            } catch (Exception e) { }

            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(ms));
        }
    }

    @Test
    public void shouldGet() throws RestException, ExecutionException, InterruptedException {
        String url = "http://dummy.com/test";
        String body = "ok";

        MockResponse.builder()
                .withURL(url)
                .withMethod(GET)
                .withStatusCode(200)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                .withResponseBody(body)
                .build();

        final Holder holder = new Holder();

        Callback<Response> callback = new Callback<Response>() {
            @Override
            public void success(Response response) {
                holder.response = response;
            }

            @Override
            public void failure(Throwable t) {
                throw new RuntimeException("Request failed");
            }

            @Override
            public void cancel() {

            }
        };

        RestClient.getDefault().asyncGet(url, callback);

        waitForCondition(1, 1000, new Callable<Boolean>() {
            public Boolean call() throws Exception {
                return holder.response != null;
            }
        });

        assertNotNull(holder.response);
        assertEquals(200, holder.response.getStatus());
        assertEquals(body, holder.response.getString());
    }

    @Test
    public void shouldGetWithHeaders() throws RestException, ExecutionException, InterruptedException {
        String url = "http://dummy.com/test";
        String body = "ok";

        MockResponse.builder()
                .withURL(url)
                .withMethod(GET)
                .withStatusCode(200)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                .withResponseBody(body)
                .build();

        final Holder holder = new Holder();

        Callback<Response> callback = new Callback<Response>() {
            @Override
            public void success(Response response) {
                holder.response = response;
            }

            @Override
            public void failure(Throwable t) {
                throw new RuntimeException("Request failed");
            }

            @Override
            public void cancel() {

            }
        };

        RestClient.getDefault().asyncGet(url, new Headers().add("X-Test","asd"), callback);

        waitForCondition(1, 1000, new Callable<Boolean>() {
            public Boolean call() throws Exception {
                return holder.response != null;
            }
        });

        assertNotNull(holder.response);
        assertEquals(200, holder.response.getStatus());
        assertEquals(body, holder.response.getString());
        assertEquals("asd", holder.response.getHeader("REQUEST-X-Test").getValue());
    }

    @Test
    public void shouldGetWithFailure() throws RestException, ExecutionException, InterruptedException {
        String url = "http://dummy.com/test";

        MockResponse.builder()
                .withURL(url)
                .withMethod(GET)
                .withStatusCode(200)
                .shouldFail()
                .build();

        final AtomicBoolean error = new AtomicBoolean(false);

        Callback<Response> callback = new Callback<Response>() {
            @Override
            public void success(Response response) {
            }

            @Override
            public void failure(Throwable t) {
                error.set(true);
            }

            @Override
            public void cancel() {

            }
        };

        RestClient.getDefault().asyncGet(url, callback);

        waitForCondition(1, 1000, new Callable<Boolean>() {
            public Boolean call() throws Exception {
                return error.get();
            }
        });

        assertTrue(error.get());
    }

    @Test
    public void shouldPost() throws RestException, ExecutionException, InterruptedException {
        String url = "http://dummy.com/test";
        String body = "ok";

        MockResponse.builder()
                .withURL(url)
                .withMethod(POST)
                .withStatusCode(201)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                .withResponseBody(body)
                .build();

        final Holder holder = new Holder();

        Callback<Response> callback = new Callback<Response>() {
            @Override
            public void success(Response response) {
                holder.response = response;
            }

            @Override
            public void failure(Throwable t) {
                throw new RuntimeException("Request failed");
            }

            @Override
            public void cancel() {

            }
        };

        RestClient.getDefault().asyncPost(url, callback);

        waitForCondition(1, 1000, new Callable<Boolean>() {
            public Boolean call() throws Exception {
                return holder.response != null;
            }
        });

        assertNotNull(holder.response);
        assertEquals(201, holder.response.getStatus());
        assertEquals(body, holder.response.getString());
    }

    @Test
    public void shouldGetWithCache() throws RestException, ExecutionException, InterruptedException {
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

        final Holder holder = new Holder();

        Callback<Response> callback = new Callback<Response>() {
            @Override
            public void success(Response response) {
                holder.response = response;
            }

            @Override
            public void failure(Throwable t) {
                throw new RuntimeException("Request failed");
            }

            @Override
            public void cancel() {

            }
        };

        RestClient.getDefault().withCache(DummyCache.getDefault()).asyncGet(url, callback);

        waitForCondition(1, 1000, new Callable<Boolean>() {
            public Boolean call() throws Exception {
                return holder.response != null;
            }
        });

        assertNotNull(holder.response);
        assertEquals(200, holder.response.getStatus());
        assertEquals(body, holder.response.getString());

        RequestMockHolder.clear();
        holder.clear();

        MockResponse.builder()
                .withURL(url)
                .withMethod(GET)
                .withStatusCode(404)
                .build();

        RestClient.getDefault().withCache(DummyCache.getDefault()).asyncGet(url, callback);

        waitForCondition(1, 1000, new Callable<Boolean>() {
            public Boolean call() throws Exception {
                return holder.response != null;
            }
        });

        assertNotNull(holder.response);
        assertEquals(200, holder.response.getStatus());
        assertEquals(body, holder.response.getString());

    }

}
