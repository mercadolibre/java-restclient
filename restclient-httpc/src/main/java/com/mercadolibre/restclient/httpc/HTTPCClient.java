package com.mercadolibre.restclient.httpc;

import java.io.IOException;

import com.mercadolibre.restclient.httpc.util.HTTPCClientMonitor;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.IdleConnectionEvictor;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.mercadolibre.restclient.EmptyResponse;
import com.mercadolibre.restclient.ExecREST;
import com.mercadolibre.restclient.Request;
import com.mercadolibre.restclient.Response;
import com.mercadolibre.restclient.exception.RestException;
import com.mercadolibre.restclient.httpc.util.HTTPCUtil;


public class HTTPCClient implements ExecREST {

    private CloseableHttpClient client;
    private IdleConnectionEvictor evictor;
    private HTTPCClientMonitor monitor;

    public HTTPCClient(CloseableHttpClient client, IdleConnectionEvictor evictor, HTTPCClientMonitor monitor) {
        this.client = client;
        this.evictor = evictor;
        evictor.start();

        this.monitor = monitor;
    }

    private Response executeRequest(Request r, HttpRequestBase httpMethod, HttpContext httpContext) throws RestException {
        return r.isDownload() ? executeDownload(httpMethod, httpContext, r) : executeRequest(httpMethod, httpContext);
    }

    private Response executeRequest(HttpRequestBase httpMethod, HttpContext httpContext) throws RestException {
        try (CloseableHttpResponse httpResponse = client.execute(httpMethod, httpContext)) {
            return new Response(httpResponse.getStatusLine().getStatusCode(), HTTPCUtil.getHeaders(httpResponse), HTTPCUtil.handleResponse(httpResponse));
        
        } catch (Exception e) {
            httpMethod.abort();
            throw new RestException(e, e.getMessage());
        }
    }

    private Response executeDownload(HttpRequestBase method, HttpContext context, Request request) throws RestException {
        try (CloseableHttpResponse response = client.execute(method, context)) {
        	EmptyResponse emptyResponse = new EmptyResponse(response.getStatusLine().getStatusCode(), HTTPCUtil.getHeaders(response));
        	
            request.populateOutputStream(response.getEntity().getContent(), emptyResponse.getHeaders().getHeader("Content-Encoding"));
            
            EntityUtils.consume(response.getEntity());

            return emptyResponse;
            
        } catch (IOException e) {
            method.abort();
            throw new RestException(e, e.getMessage());
        }
    }

    @Override
    public void close() throws IOException {
        if (evictor != null) evictor.shutdown();
        if (client != null) client.close();
        if (monitor != null) monitor.close();
    }

    public Response get(Request request) throws RestException {
        HttpGet method = new HttpGet(request.getURL());
        
        HTTPCUtil.setMethodAttributes(method, request);
        
        HttpContext context = HTTPCUtil.createContext(request);

        return executeRequest(request, method, context);
    }

    @Override
    public Response post(Request request) throws RestException {
        HttpPost method = new HttpPost(request.getURL());
        
        HTTPCUtil.setMethodAttributes(method, request);
        
        HttpContext context = HTTPCUtil.createContext(request);
        
        return executeRequest(request, method, context);
    }

    @Override
    public Response put(Request request) throws RestException {
        HttpPut method = new HttpPut(request.getURL());
        
        HTTPCUtil.setMethodAttributes(method, request);
        
        HttpContext context = HTTPCUtil.createContext(request);
        
        return executeRequest(request, method, context);
    }

    @Override
    public Response delete(Request request) throws RestException {
        HttpDelete method = new HttpDelete(request.getURL());
        
        HTTPCUtil.setMethodAttributes(method, request);

        HttpContext context = HTTPCUtil.createContext(request);
        
        return executeRequest(method, context);
    }

    @Override
    public Response head(Request request) throws RestException {
        HttpHead method = new HttpHead(request.getURL());
        
        HTTPCUtil.setMethodAttributes(method, request);

        HttpContext context = HTTPCUtil.createContext(request);
        
        return executeRequest(method, context);
    }

    @Override
    public Response options(Request request) throws RestException {
        HttpOptions method = new HttpOptions(request.getURL());
        
        HTTPCUtil.setMethodAttributes(method, request);

        HttpContext context = HTTPCUtil.createContext(request);
        
        return executeRequest(method, context);
    }

    @Override
    public Response purge(Request request) throws RestException {
        HttpPurge method = new HttpPurge(request.getURL());
        
        HTTPCUtil.setMethodAttributes(method, request);

        HttpContext context = HTTPCUtil.createContext(request);
        
        return executeRequest(method, context);
    }

}