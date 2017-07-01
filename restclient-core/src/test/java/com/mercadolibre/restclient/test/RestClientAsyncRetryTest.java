package com.mercadolibre.restclient.test;

import com.mercadolibre.restclient.*;
import com.mercadolibre.restclient.async.DummyCallbackProcessor;
import com.mercadolibre.restclient.exception.RestException;
import com.mercadolibre.restclient.http.ContentType;
import com.mercadolibre.restclient.retry.RetryResponse;
import com.mercadolibre.restclient.retry.RetryStrategy;
import com.mercadolibre.restclient.retry.SimpleRetryStrategy;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

import static com.mercadolibre.restclient.http.HttpMethod.GET;
import static com.mercadolibre.restclient.http.HttpMethod.POST;
import static org.junit.Assert.assertEquals;

public class RestClientAsyncRetryTest extends RestClientTestBase {

    @Test
    public void shouldRetryGet() throws RestException, ExecutionException, InterruptedException {
        String url = "http://dummy.com/test";
        String body = "ok";

        MockResponse.builder()
                .withURL(url)
                .withMethod(GET)
                .withStatusCode(200)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                .withResponseBody(body)
                .shouldFailAt(1)
                .build();

        Response response = RestClient.getDefault().withRetryStrategy(new SimpleRetryStrategy(3,1)).asyncGet(url).get();

        assertEquals(200, response.getStatus());
        assertEquals(body, response.getString());
    }

    @Test(expected = ExecutionException.class)
    public void shouldFailGetWithoutRetries() throws RestException, ExecutionException, InterruptedException {
        String url = "http://dummy.com/test";
        String body = "ok";

        MockResponse.builder()
                .withURL(url)
                .withMethod(GET)
                .withStatusCode(200)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                .withResponseBody(body)
                .shouldFailAt(1)
                .build();

        RestClient.getDefault().asyncGet(url).get();
    }

    @Test(expected = ExecutionException.class)
    public void shouldRetryOnMakeResponseException() throws RestException, ExecutionException, InterruptedException {
        String url = "http://dummy.com/test";

        MockResponse.builder()
                .withURL(url)
                .withMethod(POST)
                .withStatusCode(201)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                .withResponseBody(DummyCallbackProcessor.THROW)
                .build();

        final AtomicInteger retries = new AtomicInteger();

        Future<Response> response = RestClient.getDefault().withRetryStrategy(new RetryStrategy() {
            @Override
            public RetryResponse shouldRetry(Request req, Response r, Exception e, int rs) {
                retries.getAndIncrement();
                return new RetryResponse(false, 0);
            }
        }).asyncPost(url);

        LockSupport.parkNanos(2000000);

        assertEquals(1, retries.get());

        response.get();
    }

    @Test
    public void shouldRetryByResponseCode() throws RestException, ExecutionException, InterruptedException {
        String url = "http://dummy.com/test";
        String body = "ok";

        MockResponse.builder()
                .withURL(url)
                .withMethod(GET)
                .withStatusCodes(500,200)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                .withResponseBody(body)
                .build();

        Response response = RestClient.getDefault().withRetryStrategy(new SimpleRetryStrategy(3,0)).asyncGet(url).get();

        assertEquals(200, response.getStatus());
        assertEquals(body, response.getString());
    }

}
