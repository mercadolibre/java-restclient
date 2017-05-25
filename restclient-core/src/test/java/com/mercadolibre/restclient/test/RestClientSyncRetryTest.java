package com.mercadolibre.restclient.test;

import com.mercadolibre.restclient.MockResponse;
import com.mercadolibre.restclient.Response;
import com.mercadolibre.restclient.RestClient;
import com.mercadolibre.restclient.RestClientTestBase;
import com.mercadolibre.restclient.exception.RestException;
import com.mercadolibre.restclient.http.ContentType;
import com.mercadolibre.restclient.retry.SimpleRetryStrategy;
import org.junit.Test;

import java.io.IOException;

import static com.mercadolibre.restclient.http.HttpMethod.GET;
import static org.junit.Assert.assertEquals;

public class RestClientSyncRetryTest extends RestClientTestBase {

    @Test
    public void shouldRetryGet() throws RestException {
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

        Response response = RestClient.getDefault().withRetryStrategy(new SimpleRetryStrategy(3,1)).get(url);

        assertEquals(200, response.getStatus());
        assertEquals(body, response.getString());
    }

    @Test(expected = RestException.class)
    public void shouldFailGetWithoutRetries() throws RestException {
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

        RestClient.getDefault().get(url);
    }

    @Test
    public void shouldRetryWithCompression() throws IOException, RestException {
        String url = "http://dummy.com/test";
        String body = "ok";

        MockResponse.builder()
                .withURL(url)
                .withMethod(GET)
                .withStatusCode(200)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                .withResponseHeader("Content-Encoding","gzip")
                .withResponseBody(getGzipped(body))
                .shouldFailAt(1)
                .build();

        Response response = RestClient.getDefault().withRetryStrategy(new SimpleRetryStrategy(3,1)).get(url);

        assertEquals(200, response.getStatus());
        assertEquals(body, response.getString());
    }

}
