package com.mercadolibre.restclient.test;

import static com.mercadolibre.restclient.http.HttpMethod.DELETE;
import static com.mercadolibre.restclient.http.HttpMethod.GET;
import static com.mercadolibre.restclient.http.HttpMethod.HEAD;
import static com.mercadolibre.restclient.http.HttpMethod.OPTIONS;
import static com.mercadolibre.restclient.http.HttpMethod.POST;
import static com.mercadolibre.restclient.http.HttpMethod.PURGE;
import static com.mercadolibre.restclient.http.HttpMethod.PUT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import com.mercadolibre.restclient.*;
import org.junit.Test;

import com.mercadolibre.restclient.exception.RestException;
import com.mercadolibre.restclient.http.ContentType;
import com.mercadolibre.restclient.http.Headers;


public class RestClientAsyncTest extends RestClientTestBase {

    @Test
    public void shouldGetWithDefaultPool() throws RestException, ExecutionException, InterruptedException {
        String url = "http://dummy.com/test";
        String body = "ok";

        MockResponse.builder()
                .withURL(url)
                .withMethod(GET)
                .withStatusCode(200)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                .withResponseBody(body)
                .build();

        Response response = RestClient.getDefault().asyncGet(url).get();

        assertEquals(200, response.getStatus());
        assertEquals(body, response.getString());
    }

    @Test
    public void shouldPostWithDefaultPool() throws RestException, ExecutionException, InterruptedException {
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

        Response response = RestClient.getDefault().asyncPost(url, body.getBytes(StandardCharsets.UTF_8)).get();

        assertEquals(201, response.getStatus());
        assertEquals(body, response.getString());
        assertEquals(response.getHeaders().getHeader(ContentType.HEADER_NAME).getValue(), ContentType.TEXT_PLAIN.toString());
    }

    @Test
    public void shouldPutWithDefaultPool() throws RestException, ExecutionException, InterruptedException {
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

        Response response = RestClient.getDefault().asyncPut(url, body.getBytes(StandardCharsets.UTF_8)).get();

        assertEquals(200, response.getStatus());
        assertEquals(body, response.getString());
        assertEquals(response.getHeaders().getHeader(ContentType.HEADER_NAME).getValue(), ContentType.TEXT_PLAIN.toString());
    }

    @Test
    public void shouldPostWithDefaultPoolAndNoBody() throws RestException, ExecutionException, InterruptedException {
        String url = "http://dummy.com/test";

        MockResponse.builder()
                .withURL(url)
                .withMethod(POST)
                .withStatusCode(201)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                .build();

        Response response = RestClient.getDefault().asyncPost(url).get();

        assertEquals(201, response.getStatus());
        assertEquals(response.getHeaders().getHeader(ContentType.HEADER_NAME).getValue(), ContentType.TEXT_PLAIN.toString());
    }

    @Test
    public void shouldPutWithDefaultPoolAndNoBody() throws RestException, ExecutionException, InterruptedException {
        String url = "http://dummy.com/test";

        MockResponse.builder()
                .withURL(url)
                .withMethod(PUT)
                .withStatusCode(200)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                .build();

        Response response = RestClient.getDefault().asyncPut(url).get();

        assertEquals(200, response.getStatus());
        assertEquals(response.getHeaders().getHeader(ContentType.HEADER_NAME).getValue(), ContentType.TEXT_PLAIN.toString());
    }

    @Test
    public void shouldDeleteWithDefaultPool() throws RestException, ExecutionException, InterruptedException {
        String url = "http://dummy.com/test";

        MockResponse.builder()
                .withURL(url)
                .withMethod(DELETE)
                .withStatusCode(204)
                .build();

        Response response = RestClient.getDefault().asyncDelete(url).get();

        assertEquals(204, response.getStatus());
    }

    @Test
    public void shouldHeadWithDefaultPool() throws RestException, ExecutionException, InterruptedException {
        String url = "http://dummy.com/test";

        MockResponse.builder()
                .withURL(url)
                .withMethod(HEAD)
                .withStatusCode(200)
                .build();

        Response response = RestClient.getDefault().asyncHead(url).get();

        assertEquals(200, response.getStatus());
    }

    @Test
    public void shouldOptionsWithDefaultPool() throws RestException, ExecutionException, InterruptedException {
        String url = "http://dummy.com/test";

        MockResponse.builder()
                .withURL(url)
                .withMethod(OPTIONS)
                .withStatusCode(200)
                .build();

        Response response = RestClient.getDefault().asyncOptions(url).get();

        assertEquals(200, response.getStatus());
    }

    @Test
    public void shouldPurgeWithDefaultPool() throws RestException, ExecutionException, InterruptedException {
        String url = "http://dummy.com/test";

        MockResponse.builder()
                .withURL(url)
                .withMethod(PURGE)
                .withStatusCode(204)
                .build();

        Response response = RestClient.getDefault().asyncPurge(url).get();

        assertEquals(204, response.getStatus());
    }

    @Test
    public void shouldGetWithDefaultPoolAndHeaders() throws RestException, ExecutionException, InterruptedException {
        String url = "http://dummy.com/test";
        String body = "ok";

        MockResponse.builder()
                .withURL(url)
                .withMethod(GET)
                .withStatusCode(200)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                .withResponseBody(body)
                .build();

        Response response = RestClient.getDefault().asyncGet(url, new Headers(Collections.singletonMap("test","1"))).get();

        assertEquals(200, response.getStatus());
        assertEquals(body, response.getString());
        assertEquals("1", response.getHeader("REQUEST-test").getValue());
    }

    @Test
    public void shouldPostWithDefaultPoolAndHeaders() throws RestException, ExecutionException, InterruptedException {
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

        Response response = RestClient.getDefault().asyncPost(url, new Headers(Collections.singletonMap("test","1")), body.getBytes(StandardCharsets.UTF_8)).get();

        assertEquals(201, response.getStatus());
        assertEquals(body, response.getString());
        assertEquals(response.getHeaders().getHeader(ContentType.HEADER_NAME).getValue(), ContentType.TEXT_PLAIN.toString());
        assertEquals("1", response.getHeader("REQUEST-test").getValue());
    }

    @Test
    public void shouldPostWithDefaultPoolAndHeadersAndNoBody() throws RestException, ExecutionException, InterruptedException {
        String url = "http://dummy.com/test";

        MockResponse.builder()
                .withURL(url)
                .withMethod(POST)
                .withStatusCode(201)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                .build();

        Response response = RestClient.getDefault().asyncPost(url, new Headers(Collections.singletonMap("test","1"))).get();

        assertEquals(201, response.getStatus());
        assertEquals(response.getHeaders().getHeader(ContentType.HEADER_NAME).getValue(), ContentType.TEXT_PLAIN.toString());
        assertEquals("1", response.getHeader("REQUEST-test").getValue());
    }

    @Test
    public void shouldPutWithDefaultPoolAndHeaders() throws RestException, ExecutionException, InterruptedException {
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

        Response response = RestClient.getDefault().asyncPut(url, new Headers(Collections.singletonMap("test","1")), body.getBytes(StandardCharsets.UTF_8)).get();

        assertEquals(200, response.getStatus());
        assertEquals(body, response.getString());
        assertEquals(response.getHeaders().getHeader(ContentType.HEADER_NAME).getValue(), ContentType.TEXT_PLAIN.toString());
        assertEquals("1", response.getHeader("REQUEST-test").getValue());
    }

    @Test
    public void shouldPutWithDefaultPoolAndHeadersAndNoBody() throws RestException, ExecutionException, InterruptedException {
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

        Response response = RestClient.getDefault().asyncPut(url, new Headers(Collections.singletonMap("test","1"))).get();

        assertEquals(200, response.getStatus());
        assertEquals(body, response.getString());
        assertEquals(response.getHeaders().getHeader(ContentType.HEADER_NAME).getValue(), ContentType.TEXT_PLAIN.toString());
        assertEquals("1", response.getHeader("REQUEST-test").getValue());
    }

    @Test
    public void shouldDeleteWithDefaultPoolAndHeaders() throws RestException, ExecutionException, InterruptedException {
        String url = "http://dummy.com/test";

        MockResponse.builder()
                .withURL(url)
                .withMethod(DELETE)
                .withStatusCode(204)
                .build();

        Response response = RestClient.getDefault().asyncDelete(url, new Headers(Collections.singletonMap("test","1"))).get();

        assertEquals(204, response.getStatus());
        assertEquals("1", response.getHeader("REQUEST-test").getValue());
    }

    @Test
    public void shouldHeadWithDefaultPoolAndHeaders() throws RestException, ExecutionException, InterruptedException {
        String url = "http://dummy.com/test";

        MockResponse.builder()
                .withURL(url)
                .withMethod(HEAD)
                .withStatusCode(200)
                .build();

        Response response = RestClient.getDefault().asyncHead(url, new Headers(Collections.singletonMap("test","1"))).get();

        assertEquals(200, response.getStatus());
        assertEquals("1", response.getHeader("REQUEST-test").getValue());
    }

    @Test
    public void shouldOptionsWithDefaultPoolAndHeaders() throws RestException, ExecutionException, InterruptedException {
        String url = "http://dummy.com/test";

        MockResponse.builder()
                .withURL(url)
                .withMethod(OPTIONS)
                .withStatusCode(200)
                .build();

        Response response = RestClient.getDefault().asyncOptions(url, new Headers(Collections.singletonMap("test","1"))).get();

        assertEquals(200, response.getStatus());
        assertEquals("1", response.getHeader("REQUEST-test").getValue());
    }

    @Test
    public void shouldPurgeWithDefaultPoolAndHeaders() throws RestException, ExecutionException, InterruptedException {
        String url = "http://dummy.com/test";

        MockResponse.builder()
                .withURL(url)
                .withMethod(PURGE)
                .withStatusCode(204)
                .build();

        Response response = RestClient.getDefault().asyncPurge(url, new Headers(Collections.singletonMap("test","1"))).get();

        assertEquals(204, response.getStatus());
        assertEquals("1", response.getHeader("REQUEST-test").getValue());
    }

    @Test
    public void shouldPostWithDefaultPoolAndOutputStream() throws RestException, IOException, ExecutionException, InterruptedException {
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

            response = RestClient.getDefault().asyncPost(url, body.getBytes(StandardCharsets.UTF_8), os).get();
            output = new String(os.toByteArray(), StandardCharsets.UTF_8);
        }

        assertEquals(201, response.getStatus());
        assertNull(response.getString());
        assertEquals(body, output);
    }

    @Test
    public void shouldGetWithDefaultPoolAndOutputStream() throws RestException, IOException, ExecutionException, InterruptedException {
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

            response = RestClient.getDefault().asyncGet(url, os).get();
            output = new String(os.toByteArray(), StandardCharsets.UTF_8);
        }

        assertEquals(200, response.getStatus());
        assertNull(response.getString());
        assertEquals(body, output);
    }

    @Test
    public void shouldGetWithDefaultPoolAndOutputStreamAndHeaders() throws RestException, IOException, ExecutionException, InterruptedException {
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

            response = RestClient.getDefault().asyncGet(url, new Headers(Collections.singletonMap("test","1")), os).get();
            output = new String(os.toByteArray(), StandardCharsets.UTF_8);
        }

        assertEquals(200, response.getStatus());
        assertNull(response.getString());
        assertEquals(body, output);
        assertEquals("1", response.getHeader("REQUEST-test").getValue());
    }

    @Test
    public void shouldPutWithDefaultPoolAndOutputStream() throws RestException, IOException, ExecutionException, InterruptedException {
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

            response = RestClient.getDefault().asyncPut(url, body.getBytes(StandardCharsets.UTF_8), os).get();
            output = new String(os.toByteArray(), StandardCharsets.UTF_8);
        }

        assertEquals(200, response.getStatus());
        assertNull(response.getString());
        assertEquals(body, output);
    }

    @Test
    public void shouldPostWithDefaultPoolAndOutputStreamAndNoBody() throws RestException, IOException, ExecutionException, InterruptedException {
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

            response = RestClient.getDefault().asyncPost(url, os).get();
            output = new String(os.toByteArray(), StandardCharsets.UTF_8);
        }

        assertEquals(201, response.getStatus());
        assertNull(response.getString());
        assertEquals("", output);
    }

    @Test
    public void shouldPutWithDefaultPoolAndOutputStreamAndNoBody() throws RestException, IOException, ExecutionException, InterruptedException {
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

            response = RestClient.getDefault().asyncPut(url, os).get();
            output = new String(os.toByteArray(), StandardCharsets.UTF_8);
        }

        assertEquals(200, response.getStatus());
        assertNull(response.getString());
        assertEquals("", output);
    }

    @Test
    public void shouldPostWithDefaultPoolAndOutputStreamAndHeaders() throws RestException, IOException, ExecutionException, InterruptedException {
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

            response = RestClient.getDefault().asyncPost(url, new Headers(Collections.singletonMap("test","1")), body.getBytes(StandardCharsets.UTF_8), os).get();
            output = new String(os.toByteArray(), StandardCharsets.UTF_8);
        }

        assertEquals(201, response.getStatus());
        assertNull(response.getString());
        assertEquals(body, output);
        assertEquals("1", response.getHeader("REQUEST-test").getValue());
    }

    @Test
    public void shouldPutWithDefaultPoolAndOutputStreamAndHeaders() throws RestException, IOException, ExecutionException, InterruptedException {
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

            response = RestClient.getDefault().asyncPut(url, new Headers(Collections.singletonMap("test", "1")), body.getBytes(StandardCharsets.UTF_8), os).get();
            output = new String(os.toByteArray(), StandardCharsets.UTF_8);
        }

        assertEquals(200, response.getStatus());
        assertNull(response.getString());
        assertEquals(body, output);
        assertEquals("1", response.getHeader("REQUEST-test").getValue());
    }

    @Test
    public void shouldPostWithDefaultPoolAndOutputStreamAndHeadersAndNoBody() throws RestException, IOException, ExecutionException, InterruptedException {
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

            response = RestClient.getDefault().asyncPost(url, new Headers(Collections.singletonMap("test","1")), os).get();
            output = new String(os.toByteArray(), StandardCharsets.UTF_8);
        }

        assertEquals(201, response.getStatus());
        assertNull(response.getString());
        assertEquals("", output);
        assertEquals("1", response.getHeader("REQUEST-test").getValue());
    }

    @Test
    public void shouldPutWithDefaultPoolAndOutputStreamAndHeadersAndNoBody() throws RestException, IOException, ExecutionException, InterruptedException {
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

            response = RestClient.getDefault().asyncPut(url, new Headers(Collections.singletonMap("test","1")), os).get();
            output = new String(os.toByteArray(), StandardCharsets.UTF_8);
        }

        assertEquals(200, response.getStatus());
        assertNull(response.getString());
        assertEquals("", output);
        assertEquals("1", response.getHeader("REQUEST-test").getValue());
    }

    @Test
    public void shouldHandleGzipContent() throws RestException, IOException, ExecutionException, InterruptedException {
        String url = "http://dummy.com/test";
        byte[] body = getGzipped("ok");

        MockResponse.builder()
                .withURL(url)
                .withMethod(GET)
                .withStatusCode(200)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                .withResponseHeader("Content-Encoding", "gzip")
                .withResponseBody(body)
                .build();

        Response response = RestClient.getDefault().asyncGet(url).get();

        assertEquals(200, response.getStatus());
        assertEquals("ok", response.getString());
    }

    @Test
    public void shouldHandleDeflateContent() throws RestException, IOException, ExecutionException, InterruptedException {
        String url = "http://dummy.com/test";
        byte[] body = getDeflated("ok");

        MockResponse.builder()
                .withURL(url)
                .withMethod(GET)
                .withStatusCode(200)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                .withResponseHeader("Content-Encoding", "deflate")
                .withResponseBody(body)
                .build();

        Response response = RestClient.getDefault().asyncGet(url).get();

        assertEquals(200, response.getStatus());
        assertEquals("ok", response.getString());
    }

    @Test
    public void shouldHandleGzipContentInOutputStream() throws RestException, IOException, ExecutionException, InterruptedException {
        String url = "http://dummy.com/test";
        byte[] body = getGzipped("ok");
        String output;
        Response response;

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            MockResponse.builder()
                    .withURL(url)
                    .withMethod(GET)
                    .withStatusCode(200)
                    .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                    .withResponseHeader("Content-Encoding", "gzip")
                    .withResponseBody(body)
                    .build();

            response = RestClient.getDefault().asyncGet(url, os).get();
            output = new String(os.toByteArray(), StandardCharsets.UTF_8);
        }

        assertEquals(200, response.getStatus());
        assertNull(response.getString());
        assertEquals("ok", output);
    }

    @Test
    public void shouldHandleDeflateContentInOutputStream() throws RestException, IOException, ExecutionException, InterruptedException {
        String url = "http://dummy.com/test";
        final byte[] body = getDeflated("ok");
        String output;
        Response response;

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            MockResponse.builder()
                    .withURL(Pattern.compile("http://dummy\\.com.*"))
                    .withMethod(GET)
                    .withRequestProcessor(new RequestProcessor() {
                        public Response makeResponse(Request request, int run) throws RestException {
                            Headers headers = new Headers()
                                    .add(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                                    .add("Content-Encoding", "deflate");
                            return new Response(200, headers, body);
                        }
                    })
                    .build();

            response = RestClient.getDefault().asyncGet(url, os).get();
            output = new String(os.toByteArray(), StandardCharsets.UTF_8);
        }

        assertEquals(200, response.getStatus());
        assertNull(response.getString());
        assertEquals("ok", output);
    }

}
