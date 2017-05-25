package com.mercadolibre.restclient;

import com.mercadolibre.restclient.async.HTTPCallback;
import com.mercadolibre.restclient.exception.RestException;
import com.mercadolibre.restclient.mock.HTTPCMockHandler;
import com.mercadolibre.restclient.mock.TestClients;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.HttpResponse;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

import static com.mercadolibre.restclient.http.HttpMethod.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class HTTPCAsyncSpec extends HTTPCTestBase {

    private HTTPCallback<HttpResponse> getCallback(Request r) {
        return new HTTPCallback<>(r);
    }

    @Test
    public void shouldGet() throws RestException, ExecutionException, InterruptedException {
        HTTPCMockHandler.INSTANCE.addMock("GET", 200, Collections.singletonMap("Content-Type", "text/plain;charset=utf8"), "ok".getBytes());

        Request request = makeRequest(GET, "/test");
        Response response = TestClients.getAsyncClient().asyncGet(request, getCallback(request)).get();

        assertEquals(200, response.getStatus());
        assertEquals("ok", response.getString());
    }

    @Test
    public void shouldPost() throws RestException, ExecutionException, InterruptedException {
        HTTPCMockHandler.INSTANCE.addMock("POST", 201, Collections.singletonMap("Content-Type", "text/plain;charset=utf8"));

        Request request = makeRequest(POST, "/test", "asd".getBytes());
        Response response = TestClients.getAsyncClient().asyncPost(request, getCallback(request)).get();

        assertEquals(201, response.getStatus());
        assertEquals("asd", response.getString());
    }

    @Test
    public void shouldPut() throws RestException, ExecutionException, InterruptedException {
        HTTPCMockHandler.INSTANCE.addMock("PUT", 200, Collections.singletonMap("Content-Type", "text/plain;charset=utf8"));

        Request request = makeRequest(PUT, "/test", "asd".getBytes());
        Response response = TestClients.getAsyncClient().asyncPut(request, getCallback(request)).get();

        assertEquals(200, response.getStatus());
        assertEquals("asd", response.getString());
    }

    @Test
    public void shouldDelete() throws RestException, ExecutionException, InterruptedException {
        HTTPCMockHandler.INSTANCE.addMock("DELETE", 204, new byte[0]);

        Request request = makeRequest(DELETE, "/test");
        Response response = TestClients.getAsyncClient().asyncDelete(request, getCallback(request)).get();

        assertEquals(204, response.getStatus());
        assertTrue(ArrayUtils.isEmpty(response.getBytes()));
    }

    @Test
    public void shouldHead() throws RestException, ExecutionException, InterruptedException {
        HTTPCMockHandler.INSTANCE.addMock("HEAD", 200, Collections.singletonMap("X-Test","1"));

        Request request = makeRequest(HEAD, "/test");
        Response response = TestClients.getAsyncClient().asyncHead(request, getCallback(request)).get();

        assertEquals(200, response.getStatus());
        assertEquals("1", response.getHeader("X-Test").getValue());
        assertTrue(ArrayUtils.isEmpty(response.getBytes()));
    }

    @Test
    public void shouldOptions() throws RestException, ExecutionException, InterruptedException {
        HTTPCMockHandler.INSTANCE.addMock("OPTIONS", 200, Collections.singletonMap("X-Test","1"));

        Request request = makeRequest(OPTIONS, "/test");
        Response response = TestClients.getAsyncClient().asyncOptions(request, getCallback(request)).get();

        assertEquals(200, response.getStatus());
        assertEquals("1", response.getHeader("X-Test").getValue());
        assertTrue(ArrayUtils.isEmpty(response.getBytes()));
    }

    @Test
    public void shouldDownload() throws RestException, IOException, ExecutionException, InterruptedException {
        Response response;

        HTTPCMockHandler.INSTANCE.addMock("GET", 200, Collections.singletonMap("Content-Type", "text/plain;charset=utf8"), "ok".getBytes());

        Request request = makeRequest(GET, "/test");
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try {
            request.setOutputStream(output);

            response = TestClients.getAsyncClient().asyncGet(request, getCallback(request)).get();
        } finally {
            output.close();
        }

        assertEquals(200, response.getStatus());
        assertTrue(ArrayUtils.isEmpty(response.getBytes()));
        assertEquals("ok", new String(output.toByteArray()));
    }

}
