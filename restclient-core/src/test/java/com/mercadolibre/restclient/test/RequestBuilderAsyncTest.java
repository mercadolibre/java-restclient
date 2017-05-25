package com.mercadolibre.restclient.test;

import com.mercadolibre.restclient.*;
import com.mercadolibre.restclient.exception.RestException;
import com.mercadolibre.restclient.http.ContentType;
import com.mercadolibre.restclient.http.Headers;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

import static com.mercadolibre.restclient.http.HttpMethod.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


public class RequestBuilderAsyncTest extends RestClientTestBase {

    private static RestClient restClient;

    @BeforeClass
    public static void beforeClass() throws Exception {
        RESTPool pool = RESTPool.builder().withName("test").build();
        restClient = RestClient.builder().withPool(pool).build();
    }

    @Test
    public void shouldGetWithBuilder() throws RestException, ExecutionException, InterruptedException {
        String url = "http://dummy.com/test";
        String body = "ok";

        MockResponse.builder()
                .withURL(url)
                .withMethod(GET)
                .withStatusCode(200)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                .withResponseBody(body)
                .build();

        Response response = restClient.withPool("test").asyncGet(url).get();

        assertEquals(200, response.getStatus());
        assertEquals(body, response.getString());
    }

    @Test
    public void shouldPostWithBuilder() throws RestException, ExecutionException, InterruptedException {
        String url = "http://dummy.com/test";
        String body = "ok";

        MockResponse.builder()
                .withURL(url)
                .withMethod(POST)
                .withStatusCode(201)
                .withRequestBody(body)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                .echoBody()
                .build();

        Response response = restClient.withPool("test").asyncPost(url, body.getBytes(StandardCharsets.UTF_8)).get();

        assertEquals(201, response.getStatus());
        assertEquals(body, response.getString());
        assertEquals(response.getHeaders().getHeader(ContentType.HEADER_NAME).getValue(), ContentType.TEXT_PLAIN.toString());
    }

    @Test
    public void shouldPutWithBuilder() throws RestException, ExecutionException, InterruptedException {
        String url = "http://dummy.com/test";
        String body = "ok";

        MockResponse.builder()
                .withURL(url)
                .withMethod(PUT)
                .withStatusCode(200)
                .withRequestBody(body)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                .echoBody()
                .build();

        Response response = restClient.withPool("test").asyncPut(url, body.getBytes(StandardCharsets.UTF_8)).get();

        assertEquals(200, response.getStatus());
        assertEquals(body, response.getString());
        assertEquals(response.getHeaders().getHeader(ContentType.HEADER_NAME).getValue(), ContentType.TEXT_PLAIN.toString());
    }

    @Test
    public void shouldPostWithBuilderAndNoBody() throws RestException, ExecutionException, InterruptedException {
        String url = "http://dummy.com/test";

        MockResponse.builder()
                .withURL(url)
                .withMethod(POST)
                .withStatusCode(201)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                .build();

        Response response = restClient.withPool("test").asyncPost(url).get();

        assertEquals(201, response.getStatus());
        assertEquals(response.getHeaders().getHeader(ContentType.HEADER_NAME).getValue(), ContentType.TEXT_PLAIN.toString());
    }

    @Test
    public void shouldPutWithBuilderAndNoBody() throws RestException, ExecutionException, InterruptedException {
        String url = "http://dummy.com/test";

        MockResponse.builder()
                .withURL(url)
                .withMethod(PUT)
                .withStatusCode(200)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                .build();

        Response response = restClient.withPool("test").asyncPut(url).get();

        assertEquals(200, response.getStatus());
        assertEquals(response.getHeaders().getHeader(ContentType.HEADER_NAME).getValue(), ContentType.TEXT_PLAIN.toString());
    }

    @Test
    public void shouldDeleteWithBuilder() throws RestException, ExecutionException, InterruptedException {
        String url = "http://dummy.com/test";

        MockResponse.builder()
                .withURL(url)
                .withMethod(DELETE)
                .withStatusCode(204)
                .build();

        Response response = restClient.withPool("test").asyncDelete(url).get();

        assertEquals(204, response.getStatus());
    }

    @Test
    public void shouldHeadWithBuilder() throws RestException, ExecutionException, InterruptedException {
        String url = "http://dummy.com/test";

        MockResponse.builder()
                .withURL(url)
                .withMethod(HEAD)
                .withStatusCode(200)
                .build();

        Response response = restClient.withPool("test").asyncHead(url).get();

        assertEquals(200, response.getStatus());
    }

    @Test
    public void shouldOptionsWithBuilder() throws RestException, ExecutionException, InterruptedException {
        String url = "http://dummy.com/test";

        MockResponse.builder()
                .withURL(url)
                .withMethod(OPTIONS)
                .withStatusCode(200)
                .build();

        Response response = restClient.withPool("test").asyncOptions(url).get();

        assertEquals(200, response.getStatus());
    }

    @Test
    public void shouldPurgeWithBuilder() throws RestException, ExecutionException, InterruptedException {
        String url = "http://dummy.com/test";

        MockResponse.builder()
                .withURL(url)
                .withMethod(PURGE)
                .withStatusCode(204)
                .build();

        Response response = restClient.withPool("test").asyncPurge(url).get();

        assertEquals(204, response.getStatus());
    }

    @Test
    public void shouldGetWithBuilderAndHeaders() throws RestException, ExecutionException, InterruptedException {
        String url = "http://dummy.com/test";
        String body = "ok";

        MockResponse.builder()
                .withURL(url)
                .withMethod(GET)
                .withStatusCode(200)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                .withResponseBody(body)
                .build();

        Response response = restClient.withPool("test").asyncGet(url, new Headers(Collections.singletonMap("test", "1"))).get();

        assertEquals(200, response.getStatus());
        assertEquals(body, response.getString());
        assertEquals("1", response.getHeader("REQUEST-test").getValue());
    }

    @Test
    public void shouldPostWithBuilderAndHeaders() throws RestException, ExecutionException, InterruptedException {
        String url = "http://dummy.com/test";
        String body = "ok";

        MockResponse.builder()
                .withURL(url)
                .withMethod(POST)
                .withStatusCode(201)
                .withRequestBody(body)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                .echoBody()
                .build();

        Response response = restClient.withPool("test").asyncPost(url, new Headers(Collections.singletonMap("test","1")), body.getBytes(StandardCharsets.UTF_8)).get();

        assertEquals(201, response.getStatus());
        assertEquals(body, response.getString());
        assertEquals(response.getHeaders().getHeader(ContentType.HEADER_NAME).getValue(), ContentType.TEXT_PLAIN.toString());
        assertEquals("1", response.getHeader("REQUEST-test").getValue());
    }

    @Test
    public void shouldPostWithBuilderAndHeadersAndNoBody() throws RestException, ExecutionException, InterruptedException {
        String url = "http://dummy.com/test";

        MockResponse.builder()
                .withURL(url)
                .withMethod(POST)
                .withStatusCode(201)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                .build();

        Response response = restClient.withPool("test").asyncPost(url, new Headers(Collections.singletonMap("test","1"))).get();

        assertEquals(201, response.getStatus());
        assertEquals(response.getHeaders().getHeader(ContentType.HEADER_NAME).getValue(), ContentType.TEXT_PLAIN.toString());
        assertEquals("1", response.getHeader("REQUEST-test").getValue());
    }

    @Test
    public void shouldPutWithBuilderAndHeaders() throws RestException, ExecutionException, InterruptedException {
        String url = "http://dummy.com/test";
        String body = "ok";

        MockResponse.builder()
                .withURL(url)
                .withMethod(PUT)
                .withStatusCode(200)
                .withRequestBody(body)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                .echoBody()
                .build();

        Response response = restClient.withPool("test").asyncPut(url, new Headers(Collections.singletonMap("test","1")), body.getBytes(StandardCharsets.UTF_8)).get();

        assertEquals(200, response.getStatus());
        assertEquals(body, response.getString());
        assertEquals(response.getHeaders().getHeader(ContentType.HEADER_NAME).getValue(), ContentType.TEXT_PLAIN.toString());
        assertEquals("1", response.getHeader("REQUEST-test").getValue());
    }

    @Test
    public void shouldPutWithBuilderAndHeadersAndNoBody() throws RestException, ExecutionException, InterruptedException {
        String url = "http://dummy.com/test";
        String body = "ok";

        MockResponse.builder()
                .withURL(url)
                .withMethod(PUT)
                .withStatusCode(200)
                .withRequestBody(body)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                .echoBody()
                .build();

        Response response = restClient.withPool("test").asyncPut(url, new Headers(Collections.singletonMap("test","1"))).get();

        assertEquals(200, response.getStatus());
        assertEquals(body, response.getString());
        assertEquals(response.getHeaders().getHeader(ContentType.HEADER_NAME).getValue(), ContentType.TEXT_PLAIN.toString());
        assertEquals("1", response.getHeader("REQUEST-test").getValue());
    }

    @Test
    public void shouldDeleteWithBuilderAndHeaders() throws RestException, ExecutionException, InterruptedException {
        String url = "http://dummy.com/test";

        MockResponse.builder()
                .withURL(url)
                .withMethod(DELETE)
                .withStatusCode(204)
                .build();

        Response response = restClient.withPool("test").asyncDelete(url, new Headers(Collections.singletonMap("test","1"))).get();

        assertEquals(204, response.getStatus());
        assertEquals("1", response.getHeader("REQUEST-test").getValue());
    }

    @Test
    public void shouldHeadWithBuilderAndHeaders() throws RestException, ExecutionException, InterruptedException {
        String url = "http://dummy.com/test";

        MockResponse.builder()
                .withURL(url)
                .withMethod(HEAD)
                .withStatusCode(200)
                .build();

        Response response = restClient.withPool("test").asyncHead(url, new Headers(Collections.singletonMap("test","1"))).get();

        assertEquals(200, response.getStatus());
        assertEquals("1", response.getHeader("REQUEST-test").getValue());
    }

    @Test
    public void shouldOptionsWithBuilderAndHeaders() throws RestException, ExecutionException, InterruptedException {
        String url = "http://dummy.com/test";

        MockResponse.builder()
                .withURL(url)
                .withMethod(OPTIONS)
                .withStatusCode(200)
                .build();

        Response response = restClient.withPool("test").asyncOptions(url, new Headers(Collections.singletonMap("test","1"))).get();

        assertEquals(200, response.getStatus());
        assertEquals("1", response.getHeader("REQUEST-test").getValue());
    }

    @Test
    public void shouldPurgeWithBuilderAndHeaders() throws RestException, ExecutionException, InterruptedException {
        String url = "http://dummy.com/test";

        MockResponse.builder()
                .withURL(url)
                .withMethod(PURGE)
                .withStatusCode(204)
                .build();

        Response response = restClient.withPool("test").asyncPurge(url, new Headers(Collections.singletonMap("test","1"))).get();

        assertEquals(204, response.getStatus());
        assertEquals("1", response.getHeader("REQUEST-test").getValue());
    }

    @Test
    public void shouldPostWithBuilderAndOutputStream() throws RestException, IOException, ExecutionException, InterruptedException {
        String url = "http://dummy.com/test";
        String body = "ok";
        String output;
        Response response;

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            MockResponse.builder()
                    .withURL(url)
                    .withMethod(POST)
                    .withStatusCode(201)
                    .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                    .withRequestBody(body)
                    .echoBody()
                    .build();

            response = restClient.withPool("test").asyncPost(url, body.getBytes(StandardCharsets.UTF_8), os).get();
            output = new String(os.toByteArray(), StandardCharsets.UTF_8);
        }

        assertEquals(201, response.getStatus());
        assertNull(response.getString());
        assertEquals(body, output);
    }

    @Test
    public void shouldGetWithBuilderAndOutputStream() throws RestException, IOException, ExecutionException, InterruptedException {
        String url = "http://dummy.com/test";
        String body = "ok";
        String output;
        Response response;

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            MockResponse.builder()
                    .withURL(url)
                    .withMethod(GET)
                    .withStatusCode(200)
                    .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                    .withResponseBody(body)
                    .build();

            response = restClient.withPool("test").asyncGet(url, os).get();
            output = new String(os.toByteArray(), StandardCharsets.UTF_8);
        }

        assertEquals(200, response.getStatus());
        assertNull(response.getString());
        assertEquals(body, output);
    }

    @Test
    public void shouldGetWithBuilderAndOutputStreamAndHeaders() throws RestException, IOException, ExecutionException, InterruptedException {
        String url = "http://dummy.com/test";
        String body = "ok";
        String output;
        Response response;

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            MockResponse.builder()
                    .withURL(url)
                    .withMethod(GET)
                    .withStatusCode(200)
                    .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                    .withResponseBody(body)
                    .build();

            response = restClient.withPool("test").asyncGet(url, new Headers(Collections.singletonMap("test","1")), os).get();
            output = new String(os.toByteArray(), StandardCharsets.UTF_8);
        }

        assertEquals(200, response.getStatus());
        assertNull(response.getString());
        assertEquals(body, output);
        assertEquals("1", response.getHeader("REQUEST-test").getValue());
    }

    @Test
    public void shouldPutWithBuilderAndOutputStream() throws RestException, IOException, ExecutionException, InterruptedException {
        String url = "http://dummy.com/test";
        String body = "ok";
        String output;
        Response response;

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            MockResponse.builder()
                    .withURL(url)
                    .withMethod(PUT)
                    .withStatusCode(200)
                    .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                    .withRequestBody(body)
                    .echoBody()
                    .build();

            response = restClient.withPool("test").asyncPut(url, body.getBytes(StandardCharsets.UTF_8), os).get();
            output = new String(os.toByteArray(), StandardCharsets.UTF_8);
        }

        assertEquals(200, response.getStatus());
        assertNull(response.getString());
        assertEquals(body, output);
    }

    @Test
    public void shouldPostWithBuilderAndOutputStreamAndNoBody() throws RestException, IOException, ExecutionException, InterruptedException {
        String url = "http://dummy.com/test";
        String output;
        Response response;

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            MockResponse.builder()
                    .withURL(url)
                    .withMethod(POST)
                    .withStatusCode(201)
                    .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                    .build();

            response = restClient.withPool("test").asyncPost(url, os).get();
            output = new String(os.toByteArray(), StandardCharsets.UTF_8);
        }

        assertEquals(201, response.getStatus());
        assertNull(response.getString());
        assertEquals("", output);
    }

    @Test
    public void shouldPutWithBuilderAndOutputStreamAndNoBody() throws RestException, IOException, ExecutionException, InterruptedException {
        String url = "http://dummy.com/test";
        String output;
        Response response;

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            MockResponse.builder()
                    .withURL(url)
                    .withMethod(PUT)
                    .withStatusCode(200)
                    .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                    .build();

            response = restClient.withPool("test").asyncPut(url, os).get();
            output = new String(os.toByteArray(), StandardCharsets.UTF_8);
        }

        assertEquals(200, response.getStatus());
        assertNull(response.getString());
        assertEquals("", output);
    }

    @Test
    public void shouldPostWithBuilderAndOutputStreamAndHeaders() throws RestException, IOException, ExecutionException, InterruptedException {
        String url = "http://dummy.com/test";
        String body = "ok";
        String output;
        Response response;

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            MockResponse.builder()
                    .withURL(url)
                    .withMethod(POST)
                    .withStatusCode(201)
                    .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                    .withRequestBody(body)
                    .echoBody()
                    .build();

            response = restClient.withPool("test").asyncPost(url, new Headers(Collections.singletonMap("test","1")), body.getBytes(StandardCharsets.UTF_8), os).get();
            output = new String(os.toByteArray(), StandardCharsets.UTF_8);
        }

        assertEquals(201, response.getStatus());
        assertNull(response.getString());
        assertEquals(body, output);
        assertEquals("1", response.getHeader("REQUEST-test").getValue());
    }

    @Test
    public void shouldPutWithBuilderAndOutputStreamAndHeaders() throws RestException, IOException, ExecutionException, InterruptedException {
        String url = "http://dummy.com/test";
        String body = "ok";
        String output;
        Response response;

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            MockResponse.builder()
                    .withURL(url)
                    .withMethod(PUT)
                    .withStatusCode(200)
                    .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                    .withRequestBody(body)
                    .echoBody()
                    .build();

            response = restClient.withPool("test").asyncPut(url, new Headers(Collections.singletonMap("test", "1")), body.getBytes(StandardCharsets.UTF_8), os).get();
            output = new String(os.toByteArray(), StandardCharsets.UTF_8);
        }

        assertEquals(200, response.getStatus());
        assertNull(response.getString());
        assertEquals(body, output);
        assertEquals("1", response.getHeader("REQUEST-test").getValue());
    }

    @Test
    public void shouldPostWithBuilderAndOutputStreamAndHeadersAndNoBody() throws RestException, IOException, ExecutionException, InterruptedException {
        String url = "http://dummy.com/test";
        String output;
        Response response;

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            MockResponse.builder()
                    .withURL(url)
                    .withMethod(POST)
                    .withStatusCode(201)
                    .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                    .build();

            response = restClient.withPool("test").asyncPost(url, new Headers(Collections.singletonMap("test","1")), os).get();
            output = new String(os.toByteArray(), StandardCharsets.UTF_8);
        }

        assertEquals(201, response.getStatus());
        assertNull(response.getString());
        assertEquals("", output);
        assertEquals("1", response.getHeader("REQUEST-test").getValue());
    }

    @Test
    public void shouldPutWithBuilderAndOutputStreamAndHeadersAndNoBody() throws RestException, IOException, ExecutionException, InterruptedException {
        String url = "http://dummy.com/test";
        String output;
        Response response;

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            MockResponse.builder()
                    .withURL(url)
                    .withMethod(PUT)
                    .withStatusCode(200)
                    .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                    .build();

            response = restClient.withPool("test").asyncPut(url, new Headers(Collections.singletonMap("test","1")), os).get();
            output = new String(os.toByteArray(), StandardCharsets.UTF_8);
        }

        assertEquals(200, response.getStatus());
        assertNull(response.getString());
        assertEquals("", output);
        assertEquals("1", response.getHeader("REQUEST-test").getValue());
    }
    
}
