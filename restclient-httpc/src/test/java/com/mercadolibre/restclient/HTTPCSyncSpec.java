package com.mercadolibre.restclient;

import com.mercadolibre.restclient.exception.RestException;
import static com.mercadolibre.restclient.http.HttpMethod.*;
import com.mercadolibre.restclient.mock.HTTPCMockHandler;
import com.mercadolibre.restclient.mock.TestClients;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;

import static org.junit.Assert.*;


public class HTTPCSyncSpec extends HTTPCTestBase {

    @Test
    public void shouldGet() throws RestException {
        HTTPCMockHandler.INSTANCE.addMock("GET", 200, Collections.singletonMap("Content-Type", "text/plain;charset=utf8"), "ok".getBytes());

        Response response = TestClients.getSyncClient().get(makeRequest(GET, "/test"));

        assertEquals(200, response.getStatus());
        assertEquals("ok", response.getString());
    }

    @Test
    public void shouldPost() throws RestException {
        HTTPCMockHandler.INSTANCE.addMock("POST", 201, Collections.singletonMap("Content-Type", "text/plain;charset=utf8"));

        Response response = TestClients.getSyncClient().post(makeRequest(POST, "/test", "asd".getBytes()));

        assertEquals(201, response.getStatus());
        assertEquals("asd", response.getString());
    }

    @Test
    public void shouldPut() throws RestException {
        HTTPCMockHandler.INSTANCE.addMock("PUT", 200, Collections.singletonMap("Content-Type", "text/plain;charset=utf8"));

        Response response = TestClients.getSyncClient().put(makeRequest(PUT, "/test", "asd".getBytes()));

        assertEquals(200, response.getStatus());
        assertEquals("asd", response.getString());
    }

    @Test
    public void shouldDelete() throws RestException {
        HTTPCMockHandler.INSTANCE.addMock("DELETE", 204, new byte[0]);

        Response response = TestClients.getSyncClient().delete(makeRequest(DELETE, "/test"));

        assertEquals(204, response.getStatus());
        assertTrue(ArrayUtils.isEmpty(response.getBytes()));
    }

    @Test
    public void shouldHead() throws RestException {
        HTTPCMockHandler.INSTANCE.addMock("HEAD", 200, Collections.singletonMap("X-Test","1"));

        Response response = TestClients.getSyncClient().head(makeRequest(HEAD, "/test"));

        assertEquals(200, response.getStatus());
        assertEquals("1", response.getHeader("X-Test").getValue());
        assertTrue(ArrayUtils.isEmpty(response.getBytes()));
    }

    @Test
    public void shouldOptions() throws RestException {
        HTTPCMockHandler.INSTANCE.addMock("OPTIONS", 200, Collections.singletonMap("X-Test","1"));

        Response response = TestClients.getSyncClient().options(makeRequest(OPTIONS, "/test"));

        assertEquals(200, response.getStatus());
        assertEquals("1", response.getHeader("X-Test").getValue());
        assertTrue(ArrayUtils.isEmpty(response.getBytes()));
    }

    @Test
    public void shouldDownload() throws RestException, IOException {
        Response response;

        HTTPCMockHandler.INSTANCE.addMock("GET", 200, Collections.singletonMap("Content-Type", "text/plain;charset=utf8"), "ok".getBytes());

        Request request = makeRequest(GET, "/test");
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try {
            request.setOutputStream(output);

            response = TestClients.getSyncClient().get(request);
        } finally {
            output.close();
        }

        assertEquals(200, response.getStatus());
        assertTrue(ArrayUtils.isEmpty(response.getBytes()));
        assertEquals("ok", new String(output.toByteArray()));
    }

}
