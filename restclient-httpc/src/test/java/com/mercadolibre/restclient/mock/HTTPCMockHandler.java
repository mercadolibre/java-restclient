package com.mercadolibre.restclient.mock;

import org.apache.http.*;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public enum HTTPCMockHandler implements HttpRequestHandler {
    INSTANCE;

    private final Map<String,MockResponse> mocks = new HashMap<>();

    private static class MockResponse {
        private int status;
        private Map<String,String> headers;
        private byte[] body;
        private boolean shouldFail;

        public MockResponse(int status, Map<String, String> headers, byte[] body) {
            this.status = status;
            this.headers = headers;
            this.body = body;
            this.shouldFail = false;
        }

        public MockResponse(int status, Map<String, String> headers) {
            this.status = status;
            this.headers = headers;
            this.shouldFail = false;
        }

        public MockResponse(int status, byte[] body) {
            this.status = status;
            this.body = body;
            this.shouldFail = false;
        }

        public MockResponse(int status, Map<String, String> headers, byte[] body, boolean shouldFail) {
            this.status = status;
            this.headers = headers;
            this.body = body;
            this.shouldFail = shouldFail;
        }

        public MockResponse(int status, byte[] body, boolean shouldFail) {
            this.status = status;
            this.body = body;
            this.shouldFail = shouldFail;
        }
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        MockResponse mock = mocks.get(request.getRequestLine().getMethod());
        if (mock == null) throw new RuntimeException("Mock not present");

        if (mock.shouldFail) throw new HttpException("Mock fail");

        response.setStatusCode(mock.status);

        if (mock.headers != null)
            for (Map.Entry<String,String> e : mock.headers.entrySet())
                response.setHeader(e.getKey(), e.getValue());

        if (mock.body != null)
            response.setEntity(new ByteArrayEntity(mock.body));
        else {
            byte[] requestBody = parseRequestBody(request);
            if (requestBody != null)
                response.setEntity(new ByteArrayEntity(requestBody));
        }
    }

    private byte[] parseRequestBody(HttpRequest request) throws IOException {
        HttpEntity entity = null;
        if (request instanceof HttpEntityEnclosingRequest)
            entity = ((HttpEntityEnclosingRequest) request).getEntity();

        return entity != null ? EntityUtils.toByteArray(entity) : null;
    }

    public void clear() {
        mocks.clear();
    }

    public void addMock(String method, int status, Map<String,String> headers, byte[] body) {
        mocks.put(method, new MockResponse(status, headers, body));
    }

    public void addMock(String method, int status, byte[] body) {
        mocks.put(method, new MockResponse(status, body));
    }

    public void addMock(String method, int status, Map<String,String> headers, byte[] body, boolean shouldFail) {
        mocks.put(method, new MockResponse(status, headers, body, shouldFail));
    }

    public void addMock(String method, int status, byte[] body, boolean shouldFail) {
        mocks.put(method, new MockResponse(status, body, shouldFail));
    }

    public void addMock(String method, int status, Map<String,String> headers) {
        mocks.put(method, new MockResponse(status, headers));
    }


}
