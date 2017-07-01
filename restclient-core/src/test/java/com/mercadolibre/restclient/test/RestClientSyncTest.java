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

import com.mercadolibre.restclient.MockResponse;
import com.mercadolibre.restclient.Response;
import com.mercadolibre.restclient.RestClient;
import com.mercadolibre.restclient.RestClientTestBase;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.mercadolibre.restclient.exception.RestException;
import com.mercadolibre.restclient.http.ContentType;
import com.mercadolibre.restclient.http.Headers;


public class RestClientSyncTest extends RestClientTestBase {

    @Test
    public void shouldGetWithDefaultPool() throws RestException {
        String url = "http://dummy.com/test";
        String body = "ok";

        MockResponse.builder()
                .withURL(url)
                .withMethod(GET)
                .withStatusCode(200)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                .withResponseBody(body)
                .build();

        Response response = RestClient.getDefault().get(url);

        assertEquals(200, response.getStatus());
        assertEquals(body, response.getString());
    }

    @Test
    public void shouldPostWithDefaultPool() throws RestException {
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

        Response response = RestClient.getDefault().post(url, body.getBytes(StandardCharsets.UTF_8));

        assertEquals(201, response.getStatus());
        assertEquals(body, response.getString());
        assertEquals(response.getHeaders().getHeader(ContentType.HEADER_NAME).getValue(), ContentType.TEXT_PLAIN.toString());
    }

    @Test
    public void shouldPutWithDefaultPool() throws RestException {
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

        Response response = RestClient.getDefault().put(url, body.getBytes(StandardCharsets.UTF_8));

        assertEquals(200, response.getStatus());
        assertEquals(body, response.getString());
        assertEquals(response.getHeaders().getHeader(ContentType.HEADER_NAME).getValue(), ContentType.TEXT_PLAIN.toString());
    }

    @Test
    public void shouldPostWithDefaultPoolAndNoBody() throws RestException {
        String url = "http://dummy.com/test";
        String body = "ok";

        MockResponse.builder()
                .withURL(url)
                .withMethod(POST)
                .withStatusCode(201)
                .withRequestBody(body)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                .build();

        Response response = RestClient.getDefault().post(url);

        assertEquals(201, response.getStatus());
        assertEquals(response.getHeaders().getHeader(ContentType.HEADER_NAME).getValue(), ContentType.TEXT_PLAIN.toString());
    }

    @Test
    public void shouldPutWithDefaultPoolAndNoBody() throws RestException {
        String url = "http://dummy.com/test";
        String body = "ok";

        MockResponse.builder()
                .withURL(url)
                .withMethod(PUT)
                .withStatusCode(200)
                .withRequestBody(body)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                .build();

        Response response = RestClient.getDefault().put(url);

        assertEquals(200, response.getStatus());
        assertEquals(response.getHeaders().getHeader(ContentType.HEADER_NAME).getValue(), ContentType.TEXT_PLAIN.toString());
    }

    @Test
    public void shouldDeleteWithDefaultPool() throws RestException {
        String url = "http://dummy.com/test";

        MockResponse.builder()
                .withURL(url)
                .withMethod(DELETE)
                .withStatusCode(204)
                .build();

        Response response = RestClient.getDefault().delete(url);

        assertEquals(204, response.getStatus());
    }

    @Test
    public void shouldHeadWithDefaultPool() throws RestException {
        String url = "http://dummy.com/test";

        MockResponse.builder()
                .withURL(url)
                .withMethod(HEAD)
                .withStatusCode(200)
                .build();

        Response response = RestClient.getDefault().head(url);

        assertEquals(200, response.getStatus());
    }

    @Test
    public void shouldOptionsWithDefaultPool() throws RestException {
        String url = "http://dummy.com/test";

        MockResponse.builder()
                .withURL(url)
                .withMethod(OPTIONS)
                .withStatusCode(200)
                .build();

        Response response = RestClient.getDefault().options(url);

        assertEquals(200, response.getStatus());
    }

    @Test
    public void shouldPurgeWithDefaultPool() throws RestException {
        String url = "http://dummy.com/test";

        MockResponse.builder()
                .withURL(url)
                .withMethod(PURGE)
                .withStatusCode(204)
                .build();

        Response response = RestClient.getDefault().purge(url);

        assertEquals(204, response.getStatus());
    }

    @Test
    public void shouldGetWithDefaultPoolAndHeaders() throws RestException {
        String url = "http://dummy.com/test";
        String body = "ok";

        MockResponse.builder()
                .withURL(url)
                .withMethod(GET)
                .withStatusCode(200)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                .withResponseBody(body)
                .build();

        Response response = RestClient.getDefault().get(url, new Headers(ImmutableMap.of("test","1")));

        assertEquals(200, response.getStatus());
        assertEquals(body, response.getString());
        assertEquals("1", response.getHeaders().getHeader("REQUEST-test").getValue());
    }

    @Test
    public void shouldPostWithDefaultPoolAndHeaders() throws RestException {
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

        Response response = RestClient.getDefault().post(url, new Headers(ImmutableMap.of("test","1")), body.getBytes(StandardCharsets.UTF_8));

        assertEquals(201, response.getStatus());
        assertEquals(body, response.getString());
        assertEquals(response.getHeaders().getHeader(ContentType.HEADER_NAME).getValue(), ContentType.TEXT_PLAIN.toString());
        assertEquals("1", response.getHeaders().getHeader("REQUEST-test").getValue());
    }

    @Test
    public void shouldPutWithDefaultPoolAndHeaders() throws RestException {
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

        Response response = RestClient.getDefault().put(url, new Headers(ImmutableMap.of("test","1")), body.getBytes(StandardCharsets.UTF_8));

        assertEquals(200, response.getStatus());
        assertEquals(body, response.getString());
        assertEquals(response.getHeaders().getHeader(ContentType.HEADER_NAME).getValue(), ContentType.TEXT_PLAIN.toString());
        assertEquals("1", response.getHeaders().getHeader("REQUEST-test").getValue());
    }

    @Test
    public void shouldPostWithDefaultPoolAndHeadersAndNoBody() throws RestException {
        String url = "http://dummy.com/test";
        String body = "ok";

        MockResponse.builder()
                .withURL(url)
                .withMethod(POST)
                .withStatusCode(201)
                .withRequestBody(body)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                .build();

        Response response = RestClient.getDefault().post(url, new Headers(ImmutableMap.of("test","1")));

        assertEquals(201, response.getStatus());
        assertEquals(response.getHeaders().getHeader(ContentType.HEADER_NAME).getValue(), ContentType.TEXT_PLAIN.toString());
        assertEquals("1", response.getHeaders().getHeader("REQUEST-test").getValue());
    }

    @Test
    public void shouldPutWithDefaultPoolAndHeadersAndNoBody() throws RestException {
        String url = "http://dummy.com/test";
        String body = "ok";

        MockResponse.builder()
                .withURL(url)
                .withMethod(PUT)
                .withStatusCode(200)
                .withRequestBody(body)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                .build();

        Response response = RestClient.getDefault().put(url, new Headers(ImmutableMap.of("test","1")));

        assertEquals(200, response.getStatus());
        assertEquals(response.getHeaders().getHeader(ContentType.HEADER_NAME).getValue(), ContentType.TEXT_PLAIN.toString());
        assertEquals("1", response.getHeaders().getHeader("REQUEST-test").getValue());
    }

    @Test
    public void shouldDeleteWithDefaultPoolAndHeaders() throws RestException {
        String url = "http://dummy.com/test";

        MockResponse.builder()
                .withURL(url)
                .withMethod(DELETE)
                .withStatusCode(204)
                .build();

        Response response = RestClient.getDefault().delete(url, new Headers(ImmutableMap.of("test","1")));

        assertEquals(204, response.getStatus());
        assertEquals("1", response.getHeaders().getHeader("REQUEST-test").getValue());
    }

    @Test
    public void shouldHeadWithDefaultPoolAndHeaders() throws RestException {
        String url = "http://dummy.com/test";

        MockResponse.builder()
                .withURL(url)
                .withMethod(HEAD)
                .withStatusCode(200)
                .build();

        Response response = RestClient.getDefault().head(url, new Headers(ImmutableMap.of("test","1")));

        assertEquals(200, response.getStatus());
        assertEquals("1", response.getHeaders().getHeader("REQUEST-test").getValue());
    }

    @Test
    public void shouldOptionsWithDefaultPoolAndHeaders() throws RestException {
        String url = "http://dummy.com/test";

        MockResponse.builder()
                .withURL(url)
                .withMethod(OPTIONS)
                .withStatusCode(200)
                .build();

        Response response = RestClient.getDefault().options(url, new Headers(ImmutableMap.of("test","1")));

        assertEquals(200, response.getStatus());
        assertEquals("1", response.getHeaders().getHeader("REQUEST-test").getValue());
    }

    @Test
    public void shouldPurgeWithDefaultPoolAndHeaders() throws RestException {
        String url = "http://dummy.com/test";

        MockResponse.builder()
                .withURL(url)
                .withMethod(PURGE)
                .withStatusCode(204)
                .build();

        Response response = RestClient.getDefault().purge(url, new Headers(ImmutableMap.of("test","1")));

        assertEquals(204, response.getStatus());
        assertEquals("1", response.getHeaders().getHeader("REQUEST-test").getValue());
    }

    @Test
    public void shouldGetWithDefaultPoolAndOutputStream() throws RestException, IOException {
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

            response = RestClient.getDefault().get(url, os);
            output = new String(os.toByteArray(), StandardCharsets.UTF_8);
        }

        assertEquals(200, response.getStatus());
        assertNull(response.getString());
        assertEquals(body, output);
    }

    @Test
    public void shouldGetWithDefaultPoolAndOutputStreamAndHeaders() throws RestException, IOException {
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

            response = RestClient.getDefault().get(url, new Headers(Collections.singletonMap("test","1")), os);
            output = new String(os.toByteArray(), StandardCharsets.UTF_8);
        }

        assertEquals(200, response.getStatus());
        assertNull(response.getString());
        assertEquals(body, output);
        assertEquals("1", response.getHeaders().getHeader("REQUEST-test").getValue());
    }

    @Test
    public void shouldPostWithDefaultPoolAndOutputStream() throws RestException, IOException {
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

            response = RestClient.getDefault().post(url, body.getBytes(StandardCharsets.UTF_8), os);
            output = new String(os.toByteArray(), StandardCharsets.UTF_8);
        }

        assertEquals(201, response.getStatus());
        assertNull(response.getString());
        assertEquals(body, output);
    }

    @Test
    public void shouldPostWithDefaultPoolAndOutputStreamAndHeaders() throws RestException, IOException {
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

            response = RestClient.getDefault().post(url, new Headers(Collections.singletonMap("test","1")), body.getBytes(StandardCharsets.UTF_8), os);
            output = new String(os.toByteArray(), StandardCharsets.UTF_8);
        }

        assertEquals(201, response.getStatus());
        assertNull(response.getString());
        assertEquals(body, output);
        assertEquals("1", response.getHeaders().getHeader("REQUEST-test").getValue());
    }

    @Test
    public void shouldPostWithDefaultPoolAndOutputStreamAndHeadersAndNoBody() throws RestException, IOException {
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

            response = RestClient.getDefault().post(url, new Headers(Collections.singletonMap("test","1")), os);
            output = new String(os.toByteArray(), StandardCharsets.UTF_8);
        }

        assertEquals(201, response.getStatus());
        assertNull(response.getString());
        assertEquals(output, "");
        assertEquals("1", response.getHeaders().getHeader("REQUEST-test").getValue());
    }

    @Test
    public void shouldPostWithDefaultPoolAndOutputStreamAndNoBody() throws RestException, IOException {
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

            response = RestClient.getDefault().post(url, os);
            output = new String(os.toByteArray(), StandardCharsets.UTF_8);
        }

        assertEquals(201, response.getStatus());
        assertNull(response.getString());
        assertEquals(output, "");
    }

    @Test
    public void shouldPutWithDefaultPoolAndOutputStream() throws RestException, IOException {
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

            response = RestClient.getDefault().put(url, body.getBytes(StandardCharsets.UTF_8), os);
            output = new String(os.toByteArray(), StandardCharsets.UTF_8);
        }

        assertEquals(200, response.getStatus());
        assertNull(response.getString());
        assertEquals(body, output);
    }

    @Test
    public void shouldPutWithDefaultPoolAndOutputStreamAndHeaders() throws RestException, IOException {
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

            response = RestClient.getDefault().put(url, new Headers(Collections.singletonMap("test","1")), body.getBytes(StandardCharsets.UTF_8), os);
            output = new String(os.toByteArray(), StandardCharsets.UTF_8);
        }

        assertEquals(200, response.getStatus());
        assertNull(response.getString());
        assertEquals(body, output);
        assertEquals("1", response.getHeaders().getHeader("REQUEST-test").getValue());
    }

    @Test
    public void shouldPutWithDefaultPoolAndOutputStreamAndNoBody() throws RestException, IOException {
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

            response = RestClient.getDefault().put(url, os);
            output = new String(os.toByteArray(), StandardCharsets.UTF_8);
        }

        assertEquals(200, response.getStatus());
        assertNull(response.getString());
        assertEquals(output,"");
    }

    @Test
    public void shouldPutWithDefaultPoolAndOutputStreamAndHeadersAndNoBody() throws RestException, IOException {
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

            response = RestClient.getDefault().put(url, new Headers(Collections.singletonMap("test","1")), os);
            output = new String(os.toByteArray(), StandardCharsets.UTF_8);
        }

        assertEquals(200, response.getStatus());
        assertNull(response.getString());
        assertEquals(output,"");
        assertEquals("1", response.getHeaders().getHeader("REQUEST-test").getValue());
    }

    @Test
    public void shouldHandleGzipContent() throws RestException, IOException {
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

        Response response = RestClient.getDefault().get(url);

        assertEquals(200, response.getStatus());
        assertEquals("ok", response.getString());
    }

    @Test
    public void shouldHandleDeflateContent() throws RestException, IOException {
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

        Response response = RestClient.getDefault().get(url);

        assertEquals(200, response.getStatus());
        assertEquals("ok", response.getString());
    }

    @Test
    public void shouldHandleGzipContentInOutputStream() throws RestException, IOException {
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

            response = RestClient.getDefault().get(url, os);
            output = new String(os.toByteArray(), StandardCharsets.UTF_8);
        }

        assertEquals(200, response.getStatus());
        assertNull(response.getString());
        assertEquals("ok", output);
    }

    @Test
    public void shouldHandleDeflateContentInOutputStream() throws RestException, IOException {
        String url = "http://dummy.com/test";
        byte[] body = getDeflated("ok");
        String output;
        Response response;

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            MockResponse.builder()
                    .withURL(url)
                    .withMethod(GET)
                    .withStatusCode(200)
                    .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                    .withResponseHeader("Content-Encoding", "deflate")
                    .withResponseBody(body)
                    .build();

            response = RestClient.getDefault().get(url, os);
            output = new String(os.toByteArray(), StandardCharsets.UTF_8);
        }

        assertEquals(200, response.getStatus());
        assertNull(response.getString());
        assertEquals("ok", output);
    }

}
