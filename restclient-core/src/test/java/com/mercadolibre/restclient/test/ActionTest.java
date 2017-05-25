package com.mercadolibre.restclient.test;

import com.mercadolibre.restclient.*;
import com.mercadolibre.restclient.async.Action;
import com.mercadolibre.restclient.async.Callback;
import com.mercadolibre.restclient.async.HTTPCallback;
import com.mercadolibre.restclient.exception.RestException;
import com.mercadolibre.restclient.http.Headers;
import com.mercadolibre.restclient.http.HttpMethod;
import com.mercadolibre.restclient.mock.MockUtil;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.Future;


public class ActionTest {

    private ExecAsyncREST<String> dummyAsyncClient() {
        return new ExecAsyncREST<String>() {
            @Override
            public Future<Response> asyncGet(Request r) throws RestException {
                return null;
            }

            @Override
            public Future<Response> asyncPost(Request r) throws RestException {
                return null;
            }

            @Override
            public Future<Response> asyncPut(Request r) throws RestException {
                return null;
            }

            @Override
            public Future<Response> asyncDelete(Request r) throws RestException {
                return null;
            }

            @Override
            public Future<Response> asyncHead(Request r) throws RestException {
                return null;
            }

            @Override
            public Future<Response> asyncOptions(Request r) throws RestException {
                return null;
            }

            @Override
            public Future<Response> asyncPurge(Request r) throws RestException {
                return null;
            }

            @Override
            public void asyncGet(Request r, Callback<Response> callback) throws RestException {

            }

            @Override
            public void asyncPost(Request r, Callback<Response> callback) throws RestException {

            }

            @Override
            public void asyncPut(Request r, Callback<Response> callback) throws RestException {

            }

            @Override
            public void asyncDelete(Request r, Callback<Response> callback) throws RestException {

            }

            @Override
            public void asyncHead(Request r, Callback<Response> callback) throws RestException {

            }

            @Override
            public void asyncOptions(Request r, Callback<Response> callback) throws RestException {

            }

            @Override
            public void asyncPurge(Request r, Callback<Response> callback) throws RestException {

            }

            @SuppressWarnings("unchecked")
            private void echo(HttpMethod method, HTTPCallback<String> callback) {
                Response response = new Response(200, new Headers(), method.toString().getBytes());
                try {
                    MockUtil.call("successAction", callback, new Object[]{response, null}, new Class<?>[]{Response.class, RestException.class});
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override @SuppressWarnings("unchecked")
            public Future<Response> asyncGet(Request r, HTTPCallback<String> callback) {
                echo(HttpMethod.GET, callback);

                return callback.getFuture();
            }

            @Override
            public Future<Response> asyncPost(Request r, HTTPCallback<String> callback) {
                echo(HttpMethod.POST, callback);

                return callback.getFuture();
            }

            @Override
            public Future<Response> asyncPut(Request r, HTTPCallback<String> callback) {
                echo(HttpMethod.PUT, callback);

                return callback.getFuture();
            }

            @Override
            public Future<Response> asyncDelete(Request r, HTTPCallback<String> callback) {
                echo(HttpMethod.DELETE, callback);

                return callback.getFuture();
            }

            @Override
            public Future<Response> asyncHead(Request r, HTTPCallback<String> callback) {
                echo(HttpMethod.HEAD, callback);

                return callback.getFuture();
            }

            @Override
            public Future<Response> asyncOptions(Request r, HTTPCallback<String> callback) {
                echo(HttpMethod.OPTIONS, callback);

                return callback.getFuture();
            }

            @Override
            public Future<Response> asyncPurge(Request r, HTTPCallback<String> callback) {
                echo(HttpMethod.PURGE, callback);

                return callback.getFuture();
            }

            @Override
            public void close() throws IOException {
            }
        };
    }

    private Request getRequest(HttpMethod method) {
        Request request = MockUtil.mockRequest("http://localhost/test", method);

        try {
            MockUtil.setAttribute("clients", request, new ClientHolder.Clients<>(null, dummyAsyncClient(), RESTPool.DEFAULT));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return request;
    }

    private boolean checkMethod(HttpMethod method) {
        HTTPCallback<String> callback = new HTTPCallback<>(getRequest(method));

        Action.resend(getRequest(method), callback);

        try {
            return method.toString().equals(new String(callback.getFuture().get().getBytes()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void shouldResendMethods() {
        for (HttpMethod method : HttpMethod.values())
            checkMethod(method);
    }

}
