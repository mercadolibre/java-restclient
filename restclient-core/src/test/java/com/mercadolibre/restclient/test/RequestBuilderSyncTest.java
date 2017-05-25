package com.mercadolibre.restclient.test;

import com.google.common.collect.ImmutableMap;
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

import static com.mercadolibre.restclient.http.HttpMethod.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class RequestBuilderSyncTest extends RestClientTestBase {

    private static RestClient restClient;

    @BeforeClass
    public static void beforeClass() throws Exception {
        RESTPool pool = RESTPool.builder().withName("test").build();
        restClient = RestClient.builder().withPool(pool).build();
    }

    @Test
    public void shouldGetWithBuilder() throws RestException {
        String url = "http://dummy.com/test";
        String body = "ok";

        MockResponse.builder()
                .withURL(url)
                .withMethod(GET)
                .withStatusCode(200)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                .withResponseBody(body)
                .build();

        Response response = restClient.withPool("test").get(url);

        assertEquals(200, response.getStatus());
        assertEquals(body, response.getString());
    }

    @Test
    public void shouldPostWithBuilder() throws RestException {
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

        Response response = restClient.withPool("test").post(url, body.getBytes(StandardCharsets.UTF_8));

        assertEquals(201, response.getStatus());
        assertEquals(body, response.getString());
        assertEquals(response.getHeaders().getHeader(ContentType.HEADER_NAME).getValue(), ContentType.TEXT_PLAIN.toString());
    }

    @Test
    public void shouldPutWithBuilder() throws RestException {
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

        Response response = restClient.withPool("test").put(url, body.getBytes(StandardCharsets.UTF_8));

        assertEquals(200, response.getStatus());
        assertEquals(body, response.getString());
        assertEquals(response.getHeaders().getHeader(ContentType.HEADER_NAME).getValue(), ContentType.TEXT_PLAIN.toString());
    }

    @Test
    public void shouldPostWithBuilderAndNoBody() throws RestException {
        String url = "http://dummy.com/test";
        String body = "ok";

        MockResponse.builder()
                .withURL(url)
                .withMethod(POST)
                .withStatusCode(201)
                .withRequestBody(body)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                .build();

        Response response = restClient.withPool("test").post(url);

        assertEquals(201, response.getStatus());
        assertEquals(response.getHeaders().getHeader(ContentType.HEADER_NAME).getValue(), ContentType.TEXT_PLAIN.toString());
    }

    @Test
    public void shouldPutWithBuilderAndNoBody() throws RestException {
        String url = "http://dummy.com/test";
        String body = "ok";

        MockResponse.builder()
                .withURL(url)
                .withMethod(PUT)
                .withStatusCode(200)
                .withRequestBody(body)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                .build();

        Response response = restClient.withPool("test").put(url);

        assertEquals(200, response.getStatus());
        assertEquals(response.getHeaders().getHeader(ContentType.HEADER_NAME).getValue(), ContentType.TEXT_PLAIN.toString());
    }

    @Test
    public void shouldDeleteWithBuilder() throws RestException {
        String url = "http://dummy.com/test";

        MockResponse.builder()
                .withURL(url)
                .withMethod(DELETE)
                .withStatusCode(204)
                .build();

        Response response = restClient.withPool("test").delete(url);

        assertEquals(204, response.getStatus());
    }

    @Test
    public void shouldHeadWithBuilder() throws RestException {
        String url = "http://dummy.com/test";

        MockResponse.builder()
                .withURL(url)
                .withMethod(HEAD)
                .withStatusCode(200)
                .build();

        Response response = restClient.withPool("test").head(url);

        assertEquals(200, response.getStatus());
    }

    @Test
    public void shouldOptionsWithBuilder() throws RestException {
        String url = "http://dummy.com/test";

        MockResponse.builder()
                .withURL(url)
                .withMethod(OPTIONS)
                .withStatusCode(200)
                .build();

        Response response = restClient.withPool("test").options(url);

        assertEquals(200, response.getStatus());
    }

    @Test
    public void shouldPurgeWithBuilder() throws RestException {
        String url = "http://dummy.com/test";

        MockResponse.builder()
                .withURL(url)
                .withMethod(PURGE)
                .withStatusCode(204)
                .build();

        Response response = restClient.withPool("test").purge(url);

        assertEquals(204, response.getStatus());
    }

    @Test
    public void shouldGetWithBuilderAndHeaders() throws RestException {
        String url = "http://dummy.com/test";
        String body = "ok";

        MockResponse.builder()
                .withURL(url)
                .withMethod(GET)
                .withStatusCode(200)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                .withResponseBody(body)
                .build();

        Response response = restClient.withPool("test").get(url, new Headers(ImmutableMap.of("test", "1")));

        assertEquals(200, response.getStatus());
        assertEquals(body, response.getString());
        assertEquals("1", response.getHeaders().getHeader("REQUEST-test").getValue());
    }

    @Test
    public void shouldPostWithBuilderAndHeaders() throws RestException {
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

        Response response = restClient.withPool("test").post(url, new Headers(ImmutableMap.of("test","1")), body.getBytes(StandardCharsets.UTF_8));

        assertEquals(201, response.getStatus());
        assertEquals(body, response.getString());
        assertEquals(response.getHeaders().getHeader(ContentType.HEADER_NAME).getValue(), ContentType.TEXT_PLAIN.toString());
        assertEquals("1", response.getHeaders().getHeader("REQUEST-test").getValue());
    }

    @Test
    public void shouldPutWithBuilderAndHeaders() throws RestException {
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

        Response response = restClient.withPool("test").put(url, new Headers(ImmutableMap.of("test","1")), body.getBytes(StandardCharsets.UTF_8));

        assertEquals(200, response.getStatus());
        assertEquals(body, response.getString());
        assertEquals(response.getHeaders().getHeader(ContentType.HEADER_NAME).getValue(), ContentType.TEXT_PLAIN.toString());
        assertEquals("1", response.getHeaders().getHeader("REQUEST-test").getValue());
    }

    @Test
    public void shouldPostWithBuilderAndHeadersAndNoBody() throws RestException {
        String url = "http://dummy.com/test";
        String body = "ok";

        MockResponse.builder()
                .withURL(url)
                .withMethod(POST)
                .withStatusCode(201)
                .withRequestBody(body)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                .build();

        Response response = restClient.withPool("test").post(url, new Headers(ImmutableMap.of("test","1")));

        assertEquals(201, response.getStatus());
        assertEquals(response.getHeaders().getHeader(ContentType.HEADER_NAME).getValue(), ContentType.TEXT_PLAIN.toString());
        assertEquals("1", response.getHeaders().getHeader("REQUEST-test").getValue());
    }

    @Test
    public void shouldPutWithBuilderAndHeadersAndNoBody() throws RestException {
        String url = "http://dummy.com/test";
        String body = "ok";

        MockResponse.builder()
                .withURL(url)
                .withMethod(PUT)
                .withStatusCode(200)
                .withRequestBody(body)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                .build();

        Response response = restClient.withPool("test").put(url, new Headers(ImmutableMap.of("test","1")));

        assertEquals(200, response.getStatus());
        assertEquals(response.getHeaders().getHeader(ContentType.HEADER_NAME).getValue(), ContentType.TEXT_PLAIN.toString());
        assertEquals("1", response.getHeaders().getHeader("REQUEST-test").getValue());
    }

    @Test
    public void shouldDeleteWithBuilderAndHeaders() throws RestException {
        String url = "http://dummy.com/test";

        MockResponse.builder()
                .withURL(url)
                .withMethod(DELETE)
                .withStatusCode(204)
                .build();

        Response response = restClient.withPool("test").delete(url, new Headers(ImmutableMap.of("test","1")));

        assertEquals(204, response.getStatus());
        assertEquals("1", response.getHeaders().getHeader("REQUEST-test").getValue());
    }

    @Test
    public void shouldHeadWithBuilderAndHeaders() throws RestException {
        String url = "http://dummy.com/test";

        MockResponse.builder()
                .withURL(url)
                .withMethod(HEAD)
                .withStatusCode(200)
                .build();

        Response response = restClient.withPool("test").head(url, new Headers(ImmutableMap.of("test","1")));

        assertEquals(200, response.getStatus());
        assertEquals("1", response.getHeaders().getHeader("REQUEST-test").getValue());
    }

    @Test
    public void shouldOptionsWithBuilderAndHeaders() throws RestException {
        String url = "http://dummy.com/test";

        MockResponse.builder()
                .withURL(url)
                .withMethod(OPTIONS)
                .withStatusCode(200)
                .build();

        Response response = restClient.withPool("test").options(url, new Headers(ImmutableMap.of("test","1")));

        assertEquals(200, response.getStatus());
        assertEquals("1", response.getHeaders().getHeader("REQUEST-test").getValue());
    }

    @Test
    public void shouldPurgeWithBuilderAndHeaders() throws RestException {
        String url = "http://dummy.com/test";

        MockResponse.builder()
                .withURL(url)
                .withMethod(PURGE)
                .withStatusCode(204)
                .build();

        Response response = restClient.withPool("test").purge(url, new Headers(ImmutableMap.of("test","1")));

        assertEquals(204, response.getStatus());
        assertEquals("1", response.getHeaders().getHeader("REQUEST-test").getValue());
    }

    @Test
    public void shouldGetWithBuilderAndOutputStream() throws RestException, IOException {
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

            response = restClient.withPool("test").get(url, os);
            output = new String(os.toByteArray(), StandardCharsets.UTF_8);
        }

        assertEquals(200, response.getStatus());
        assertNull(response.getString());
        assertEquals(body, output);
    }

    @Test
    public void shouldGetWithBuilderAndOutputStreamAndHeaders() throws RestException, IOException {
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

            response = restClient.withPool("test").get(url, new Headers(Collections.singletonMap("test", "1")), os);
            output = new String(os.toByteArray(), StandardCharsets.UTF_8);
        }

        assertEquals(200, response.getStatus());
        assertNull(response.getString());
        assertEquals(body, output);
        assertEquals("1", response.getHeaders().getHeader("REQUEST-test").getValue());
    }

    @Test
    public void shouldPostWithBuilderAndOutputStream() throws RestException, IOException {
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

            response = restClient.withPool("test").post(url, body.getBytes(StandardCharsets.UTF_8), os);
            output = new String(os.toByteArray(), StandardCharsets.UTF_8);
        }

        assertEquals(201, response.getStatus());
        assertNull(response.getString());
        assertEquals(body, output);
    }

    @Test
    public void shouldPostWithBuilderAndOutputStreamAndHeaders() throws RestException, IOException {
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

            response = restClient.withPool("test").post(url, new Headers(Collections.singletonMap("test","1")), body.getBytes(StandardCharsets.UTF_8), os);
            output = new String(os.toByteArray(), StandardCharsets.UTF_8);
        }

        assertEquals(201, response.getStatus());
        assertNull(response.getString());
        assertEquals(body, output);
        assertEquals("1", response.getHeaders().getHeader("REQUEST-test").getValue());
    }

    @Test
    public void shouldPostWithBuilderAndOutputStreamAndHeadersAndNoBody() throws RestException, IOException {
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

            response = restClient.withPool("test").post(url, new Headers(Collections.singletonMap("test","1")), os);
            output = new String(os.toByteArray(), StandardCharsets.UTF_8);
        }

        assertEquals(201, response.getStatus());
        assertNull(response.getString());
        assertEquals(output, "");
        assertEquals("1", response.getHeaders().getHeader("REQUEST-test").getValue());
    }

    @Test
    public void shouldPostWithBuilderAndOutputStreamAndNoBody() throws RestException, IOException {
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

            response = restClient.withPool("test").post(url, os);
            output = new String(os.toByteArray(), StandardCharsets.UTF_8);
        }

        assertEquals(201, response.getStatus());
        assertNull(response.getString());
        assertEquals(output, "");
    }

    @Test
    public void shouldPutWithBuilderAndOutputStream() throws RestException, IOException {
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

            response = restClient.withPool("test").put(url, body.getBytes(StandardCharsets.UTF_8), os);
            output = new String(os.toByteArray(), StandardCharsets.UTF_8);
        }

        assertEquals(200, response.getStatus());
        assertNull(response.getString());
        assertEquals(body, output);
    }

    @Test
    public void shouldPutWithBuilderAndOutputStreamAndHeaders() throws RestException, IOException {
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

            response = restClient.withPool("test").put(url, new Headers(Collections.singletonMap("test","1")), body.getBytes(StandardCharsets.UTF_8), os);
            output = new String(os.toByteArray(), StandardCharsets.UTF_8);
        }

        assertEquals(200, response.getStatus());
        assertNull(response.getString());
        assertEquals(body, output);
        assertEquals("1", response.getHeaders().getHeader("REQUEST-test").getValue());
    }

    @Test
    public void shouldPutWithBuilderAndOutputStreamAndNoBody() throws RestException, IOException {
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

            response = restClient.withPool("test").put(url, os);
            output = new String(os.toByteArray(), StandardCharsets.UTF_8);
        }

        assertEquals(200, response.getStatus());
        assertNull(response.getString());
        assertEquals(output,"");
    }

    @Test
    public void shouldPutWithBuilderAndOutputStreamAndHeadersAndNoBody() throws RestException, IOException {
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

            response = restClient.withPool("test").put(url, new Headers(Collections.singletonMap("test","1")), os);
            output = new String(os.toByteArray(), StandardCharsets.UTF_8);
        }

        assertEquals(200, response.getStatus());
        assertNull(response.getString());
        assertEquals(output,"");
        assertEquals("1", response.getHeaders().getHeader("REQUEST-test").getValue());
    }

    @Test
    public void shouldGetWithBaseURL() throws RestException, IOException {
        RESTPool pool = RESTPool
                .builder()
                .withName("test")
                .withBaseURL("http://localhost")
                .build();

        RestClient restClient = RestClient.builder().withPool(pool).build();

        MockResponse.builder()
                .withURL("http://localhost/test")
                .withMethod(GET)
                .withStatusCode(200)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
                .withResponseBody("ok")
                .build();

        Response response = restClient.withPool("test").get("/test");

        assertEquals(200, response.getStatus());
        assertEquals("ok", response.getString());
    }

}
