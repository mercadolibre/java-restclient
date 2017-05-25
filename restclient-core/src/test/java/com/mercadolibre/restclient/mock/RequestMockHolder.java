package com.mercadolibre.restclient.mock;

import com.google.common.collect.Maps;
import com.mercadolibre.restclient.*;
import com.mercadolibre.restclient.exception.RestException;
import com.mercadolibre.restclient.http.Header;
import com.mercadolibre.restclient.http.Headers;
import com.mercadolibre.restclient.http.HttpMethod;
import com.mercadolibre.restclient.interceptor.RequestInterceptor;
import com.mercadolibre.restclient.util.HttpCompressionHandler;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public enum RequestMockHolder {
    INSTANCE;

    private static final Response NULL_RESPONSE = new Response(-1, new Headers(), new byte[0]);

    private static final Map<MockResponse,Response> requests = new ConcurrentHashMap<>();

    private static final class Matcher {
        private Request request;
        private int score = 0;

        public Matcher(Request request) {
            this.request = request;
        }

        private boolean matches(String url) {
            String[] split = url.split("\\?");

            return request.getPlainURL().equals(split[0]) && (split.length == 1 || matchParams(fillParameters(split[1])));
        }

        private boolean matches(Pattern p) {
            return p.matcher(request.getURL()).matches();
        }

        private boolean matches(Headers headers) {
            return headers == null || headers.equals(request.getHeaders());
        }

        private boolean matches(byte[] body) {
            if (body == null) return true;
            if (request.getBody() == null) return false;
            if (body.length != request.getBody().length) return false;

            for (int i=0; i < body.length; i++)
                if (body[i] != request.getBody()[i])
                    return false;

            return true;
        }

        private boolean matches(HttpMethod method) {
            return method == request.getMethod();
        }

        public boolean matches(MockResponse r, boolean cropURL) {
            Request template = r.getRequest().clone();
            populateTemplate(template);

            int newScore = 0;

            boolean matchesURL;
            if (template.getPlainURL() != null) {
                String url = cropURL ? template.getURL().split("\\?")[0] : template.getURL();
                matchesURL = matches(url);
            } else {
                forceTemplateURL(template, request.getPlainURL());
                matchesURL = matches(r.getPattern());
            }

            if (matchesURL && matches(template.getMethod())) {
                newScore++;
                if (matches(template.getHeaders())) {
                    newScore++;
                    if (matches(template.getBody()))
                        newScore++;
                }
            } else
                return false;

            if (newScore > score) {
                score = newScore;
                return true;
            }

            return false;
        }

        private boolean matchParams(Map<String,String> params) {
            return Maps.difference(params, request.getParameters()).areEqual();
        }

        Map<String,String> fillParameters(String params) {
            Map<String,String> output = new HashMap<>();

            for (String pair : params.split("&")) {
                String[] split = pair.split("=");
                if (StringUtils.isNotBlank(split[0]))
                    output.put(split[0], split[1]);
            }

            return output;
        }

        private void forceTemplateURL(Request template, String url) {
            try {
                Field field = template.getClass().getDeclaredField("url");
                field.setAccessible(true);

                field.set(template, url);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        private void populateTemplate(Request template) {
            Iterator<RequestInterceptor> it = RESTPool.Builder.DEFAULT_INTERCEPTORS.descendingIterator();
            while (it.hasNext())
                template.getRequestInterceptors().addFirst(it.next());

            template.applyRequestInterceptors();

            HttpCompressionHandler.handleRequest(template);
        }

    }

    public Response bestMatch(Request r) throws RestException {
        Response output = null;
        MockResponse mock = null;

        Matcher m = new Matcher(r);

        for (Map.Entry<MockResponse,Response> e : requests.entrySet())
            if (m.matches(e.getKey(), false)) {
                output = e.getValue() != NULL_RESPONSE ? e.getValue() : null;
                mock = e.getKey();
            }

        if (mock == null)
            for (Map.Entry<MockResponse,Response> e : requests.entrySet())
                if (m.matches(e.getKey(), true)) {
                    output = e.getValue() != NULL_RESPONSE ? e.getValue() : null;
                    mock = e.getKey();
                }

        if (mock == null) throw new IllegalStateException("No mock matches request");

        if (mock.getProcessor() != null) {
            try {
                Response response = mock.getProcessor().makeResponse(r, mock.getCounter().run());
                if (mock.getInterceptor() != null) mock.getInterceptor().intercept(r, response, null);

                return handleOutputStream(r, response, response.getStatus());
            } catch (RestException e) {
                if (mock.getInterceptor() != null) mock.getInterceptor().intercept(r, null, e);
                throw e;
            }
        }

        if (output != null) {
            int status = mock.getStatus();

            if (r.getHeaders() != null) {
                Headers headers = new Headers();

                for (Header h : output.getHeaders())
                    headers.add(h);

                for (Header h : r.getHeaders())
                    headers.add(new Header("REQUEST-" + h.getName(), h.getValue()));

                output = new Response(status, headers, output.getBytes());
            }

            output = handleOutputStream(r, output, status);

        }

        if (mock.getCounter().runAndShouldFail()) {
            RestException exception =  new RestException("Mock request fail");
            if (mock.getInterceptor() != null) mock.getInterceptor().intercept(r, null, exception);

            throw exception;
        }

        if (mock.getInterceptor() != null) mock.getInterceptor().intercept(r, output, null);

        return output;
    }

    private Response handleOutputStream(Request r, Response output, int status) throws RestException {
        if (r.getOutputStream() != null) {
            if (output.getBytes() != null)
                try (InputStream input = new ByteArrayInputStream(output.getBytes())) {
                    r.populateOutputStream(input, output.getHeaders().getHeader("Content-Encoding"));
                } catch (IOException e) {
                    throw new RestException(e, e.getMessage());
                }

            return new EmptyResponse(status, output.getHeaders());
        }

        return output;
    }

    public static void addMockResponse(MockResponse mock) {
        Response response = mock.getResponse() != null ? mock.getResponse() : NULL_RESPONSE;

        requests.put(mock, response);
    }

    public static void clear() {
        requests.clear();
    }

}
