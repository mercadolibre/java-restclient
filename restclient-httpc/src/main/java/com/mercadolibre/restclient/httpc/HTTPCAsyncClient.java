package com.mercadolibre.restclient.httpc;

import java.io.IOException;
import java.util.concurrent.Future;

import com.mercadolibre.restclient.httpc.util.HTTPCClientMonitor;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.nio.client.methods.HttpAsyncMethods;
import org.apache.http.nio.protocol.BasicAsyncResponseConsumer;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.apache.http.nio.protocol.HttpAsyncResponseConsumer;
import org.apache.http.protocol.HttpContext;

import com.mercadolibre.restclient.ExecCallbackAsyncREST;
import com.mercadolibre.restclient.Request;
import com.mercadolibre.restclient.Response;
import com.mercadolibre.restclient.async.HTTPCallback;
import com.mercadolibre.restclient.httpc.async.HTTPCCallback;
import com.mercadolibre.restclient.httpc.util.HTTPCUtil;


public class HTTPCAsyncClient implements ExecCallbackAsyncREST<HttpResponse> {

    private CloseableHttpAsyncClient client;
    private IdleAsyncConnectionEvictor evictor;
    private HTTPCClientMonitor monitor;

    public HTTPCAsyncClient(CloseableHttpAsyncClient client, IdleAsyncConnectionEvictor evictor, HTTPCClientMonitor monitor) {
        this.client = client;
        this.evictor = evictor;
        evictor.start();

        this.monitor = monitor;
    }

    @Override
    public void close() throws IOException {
        if (evictor != null) evictor.shutdown();
        if (client != null) client.close();
        if (monitor != null) monitor.close();
    }

    private Future<Response> executeRequest(Request request, HttpRequestBase method, HttpContext context, HTTPCallback<HttpResponse> callback) {
        if (request.isDownload()) {
            HttpAsyncRequestProducer producer = HttpAsyncMethods.create(method);
            HttpAsyncResponseConsumer<HttpResponse> consumer = new BasicAsyncResponseConsumer();
            return executeRequest(producer, consumer, context, callback);
        } else
            return executeRequest(method, context, callback);
    }

    private Future<Response> executeRequest(HttpRequestBase httpMethod, HttpContext httpContext, HTTPCallback<HttpResponse> callback) {
        client.execute(httpMethod, httpContext, new HTTPCCallback(callback));

        return callback.getFuture();
    }

    private Future<Response> executeRequest(HttpAsyncRequestProducer producer, HttpAsyncResponseConsumer<HttpResponse> consumer, HttpContext httpContext, HTTPCallback<HttpResponse> callback) {
        client.execute(producer, consumer, httpContext, new HTTPCCallback(callback));

        return callback.getFuture();
    }

    @Override
    public Future<Response> asyncGet(Request request, HTTPCallback<HttpResponse> callback) {
        HttpGet method = new HttpGet(request.getURL());
        
        HTTPCUtil.setMethodAttributes(method, request);
        
        HttpContext context = HTTPCUtil.createContext(request);

        return executeRequest(request, method, context, callback);
    }

    @Override
    public Future<Response> asyncPost(Request request, HTTPCallback<HttpResponse> callback) {
        HttpPost method = new HttpPost(request.getURL());
        
        HTTPCUtil.setMethodAttributes(method, request);
        
        HttpContext context = HTTPCUtil.createContext(request);
        
        return executeRequest(request, method, context, callback);
    }

    @Override
    public Future<Response> asyncPut(Request request, HTTPCallback<HttpResponse> callback) {
        HttpPut method = new HttpPut(request.getURL());
        
        HTTPCUtil.setMethodAttributes(method, request);
        
        HttpContext context = HTTPCUtil.createContext(request);

        return executeRequest(request, method, context, callback);
    }

    @Override
    public Future<Response> asyncDelete(Request request, HTTPCallback<HttpResponse> callback) {
        HttpDelete method = new HttpDelete(request.getURL());
        
        HTTPCUtil.setMethodAttributes(method, request);
        
        HttpContext context = HTTPCUtil.createContext(request);

        return executeRequest(method, context, callback);
    }

    @Override
    public Future<Response> asyncHead(Request request, HTTPCallback<HttpResponse> callback) {
        HttpHead method = new HttpHead(request.getURL());
        
        HTTPCUtil.setMethodAttributes(method, request);
        
        HttpContext context = HTTPCUtil.createContext(request);

        return executeRequest(method, context, callback);
    }

    @Override
    public Future<Response> asyncOptions(Request request, HTTPCallback<HttpResponse> callback) {
        HttpOptions method = new HttpOptions(request.getURL());
        
        HTTPCUtil.setMethodAttributes(method, request);
        
        HttpContext context = HTTPCUtil.createContext(request);

        return executeRequest(method, context, callback);
    }

    @Override
    public Future<Response> asyncPurge(Request request, HTTPCallback<HttpResponse> callback) {
        HttpPurge method = new HttpPurge(request.getURL());
        
        HTTPCUtil.setMethodAttributes(method, request);
        
        HttpContext context = HTTPCUtil.createContext(request);

        return executeRequest(method, context, callback);
    }

}