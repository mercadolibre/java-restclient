package com.mercadolibre.restclient;

import com.mercadolibre.restclient.async.Callback;
import com.mercadolibre.restclient.cache.RESTCache;
import com.mercadolibre.restclient.exception.RestException;
import com.mercadolibre.restclient.http.Authentication;
import com.mercadolibre.restclient.http.Headers;
import com.mercadolibre.restclient.http.Proxy;
import com.mercadolibre.restclient.interceptor.RequestInterceptor;
import com.mercadolibre.restclient.interceptor.ResponseInterceptor;
import com.mercadolibre.restclient.multipart.MultipartMode;
import com.mercadolibre.restclient.multipart.Part;
import com.mercadolibre.restclient.retry.RetryStrategy;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import static com.mercadolibre.restclient.http.HttpMethod.*;

/**
 * Core implementation of a REST client. It implements sync and async interfaces and contains a collection of {@link RESTPool}.
 * It decouples client implementations from core functionality.
 */
public final class RestClient implements REST, StreamREST, AsyncREST, StreamAsyncREST, Closeable {

    private ClientHolder holder;

    private RestClient() {
    	this.holder = new ClientHolder();
    }

    private static final class DefaultHolder {
        public static final RestClient INSTANCE = makeDefault();

        private static RestClient makeDefault() {
            try {
                return builder().build();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void close() throws IOException {
        holder.close();
    }

    /**
     * Returns default RestClient implementation
     * @return Singleton instance of default RestClient
     */
    public static RestClient getDefault() {
        return DefaultHolder.INSTANCE;
    }

    public ClientHolder getHolder() {
        return holder;
    }

    /**
     * Returns a builder for constructing a RestClient based on {@link RESTPool} definitions.
     * @return an instance of {@link Builder}
     */
	public static Builder<?,?> builder() {
        return Engine.newBuilder(new RestClient());
    }

    /**
     * Specifies the pool under which current request is to be executed
     * @param pool an instance of the RESTPool
     * @return an instance of RequestBuilder
     * @throws RestException if the pool doesn't exist in current Rest Client definition
     */
    public RequestBuilder withPool(RESTPool pool) throws RestException {
        return new RequestBuilder(this).withPool(pool);
    }

    /**
     * Specifies the pool under which current request is to be executed
     * @param pool the name of the RESTPool
     * @return an instance of RequestBuilder
     * @throws RestException if the pool doesn't exist in current Rest Client definition
     */
    public RequestBuilder withPool(String pool) throws RestException {
        return new RequestBuilder(this).withPool(pool);
    }

    /**
     * Specifies a {@link Proxy} for the current request
     * @param proxy the proxy definition
     * @return an instance of RequestBuilder
     */
    public RequestBuilder withProxy(Proxy proxy) {
        return new RequestBuilder(this).withProxy(proxy);
    }

    /**
     * Creates a {@link Proxy} instance for the given host and port, and applies it to current request
     * @param host the proxy server host
     * @param port the proxy server port
     * @return an instance of RequestBuilder
     */
    public RequestBuilder withProxy(String host, int port) {
        return new RequestBuilder(this).withProxy(host, port);
    }

    /**
     * Adds parameters to this request URL
     * @param parameters map of parameters
     * @return an instance of RequestBuilder
     */
    public RequestBuilder withParameters(Map<String, String> parameters) {
        return new RequestBuilder(this).withParameters(parameters);
    }

    /**
     * Adds a parameter to this request URL
     * @param name parameter name
     * @param value parameter value
     * @return an instance of RequestBuilder
     */
    public RequestBuilder withParameter(String name, String value) {
        return new RequestBuilder(this).withParameter(name, value);
    }

    /**
     * Adds attributes to this request
     * @param attributes map of attributes
     * @return an instance of RequestBuilder
     */
    public RequestBuilder withAttributes(Map<String,Object> attributes) {
        return new RequestBuilder(this).withAttributes(attributes);
    }

    /**
     * Adds attribute to this request
     * @param name attributes name
     * @param value attribute value
     * @return an instance of RequestBuilder
     */
    public RequestBuilder withAttribute(String name, Object value) {
        return new RequestBuilder(this).withAttribute(name, value);
    }

    /**
     * Adds basic authentication to this request
     * @param authentication {@link Authentication} instance
     * @return an instance of RequestBuilder
     */
    public RequestBuilder withAuthentication(Authentication authentication) {
        return new RequestBuilder(this).withAuthentication(authentication);
    }

    /**
     * Adds a retry strategy to this request
     * @param retryStrategy {@link RetryStrategy} instance
     * @return an instance of RequestBuilder
     */
    public RequestBuilder withRetryStrategy(RetryStrategy retryStrategy) {
        return new RequestBuilder(this).withRetryStrategy(retryStrategy);
    }

    /**
     * Adds a request interceptor first in list
     * @param i {@link RequestInterceptor} instance
     * @return an instance of RequestBuilder
     */
    public RequestBuilder withInterceptorFirst(RequestInterceptor i) {
        return new RequestBuilder(this).withInterceptorFirst(i);
    }

    /**
     * Adds a request interceptor last in list
     * @param i {@link RequestInterceptor} instance
     * @return an instance of RequestBuilder
     */
    public RequestBuilder withInterceptorLast(RequestInterceptor i) {
        return new RequestBuilder(this).withInterceptorLast(i);
    }

    /**
     * Adds a response interceptor first in list
     * @param i {@link ResponseInterceptor} instance
     * @return an instance of RequestBuilder
     */
    public RequestBuilder withInterceptorFirst(ResponseInterceptor i) {
        return new RequestBuilder(this).withInterceptorFirst(i);
    }

    /**
     * Adds a request interceptor last in list
     * @param i {@link ResponseInterceptor} instance
     * @return an instance of RequestBuilder
     */
    public RequestBuilder withInterceptorLast(ResponseInterceptor i) {
        return new RequestBuilder(this).withInterceptorLast(i);
    }

    /**
     * Adds an output stream for resource download. All response data will go through it. Response body will be null.
     * @param outputStream - {@link OutputStream} instance
     * @return an instance of RequestBuilder
     */
    public RequestBuilder withOutputStream(OutputStream outputStream) {
        return new RequestBuilder(this).withOutputStream(outputStream);
    }

    /**
     * Adds a set of {@link Part} for a multipart upload
     * @param parts the set of {@link Part}
     * @return an instance of RequestBuilder
     */
	public RequestBuilder withParts(Set<Part<?>> parts) {
        return new RequestBuilder(this).withParts(parts);
    }

    /**
     * Adds a {@link Part} for a multipart upload
     * @param part a {@link Part} instance
     * @return an instance of RequestBuilder
     */
	public RequestBuilder withPart(Part<?> part) {
        return new RequestBuilder(this).withPart(part);
    }


    /**
     * Specifies a multipart mode for this request
     * @param mode a {@link MultipartMode}
     * @return an instance of RequestBuilder
     */
    public RequestBuilder withMultipartMode(MultipartMode mode) {
	    return new RequestBuilder(this).withMultipartMode(mode);
    }

    /**
     * Adds a {@link RESTCache} definition for this request
     * @param cache a {@link RESTCache instance}
     * @return an instance of RequestBuilder
     */
    public RequestBuilder withCache(RESTCache cache) {
        return new RequestBuilder(this).withCache(cache);
    }

    /**
     * Adds a {@link RESTCache} definition for this request
     * @param cache a {@link RESTCache} instance name
     * @return an instance of RequestBuilder
     */
    public RequestBuilder withCache(String cache) {
        return new RequestBuilder(this).withCache(cache);
    }

    private RequestBuilder requestBuilder() {
        return new RequestBuilder(this);
    }

    /**
     * Implementation of HTTP GET
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @return server response
     * @throws RestException when request sending or receiving has failed
     */
    @Override
    public Response get(String url) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(GET).build();
        return holder.getDefaultClient().get(r);
    }

    /**
     * Implementation of HTTP GET
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param headers an instance of {@link Headers} to be sent with the request
     * @return server response
     * @throws RestException when request sending or receiving has failed
     */
    @Override
    public Response get(String url, Headers headers) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(GET).withHeaders(headers).build();
        return holder.getDefaultClient().get(r);
    }

    /**
     * Implementation of HTTP POST
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param body body to be sent with the request
     * @return server response
     * @throws RestException when request sending or receiving has failed
     */
    @Override
    public Response post(String url, byte[] body) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(POST).withBody(body).build();
        return holder.getDefaultClient().post(r);
    }

    /**
     * Implementation of HTTP POST
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param headers an instance of {@link Headers} to be sent with the request
     * @param body body to be sent with the request
     * @return server response
     * @throws RestException when request sending or receiving has failed
     */
    @Override
    public Response post(String url, Headers headers, byte[] body) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(POST).withHeaders(headers).withBody(body).build();
        return holder.getDefaultClient().post(r);
    }

    /**
     * Implementation of HTTP PUT
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param body body to be sent with the request
     * @return server response
     * @throws RestException when request sending or receiving has failed
     */
    @Override
    public Response put(String url, byte[] body) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(PUT).withBody(body).build();
        return holder.getDefaultClient().put(r);
    }

    /**
     * Implementation of HTTP PUT
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param headers an instance of {@link Headers} to be sent with the request
     * @param body body to be sent with the request
     * @return server response
     * @throws RestException when request sending or receiving has failed
     */
    @Override
    public Response put(String url, Headers headers, byte[] body) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(PUT).withHeaders(headers).withBody(body).build();
        return holder.getDefaultClient().put(r);
    }

    /**
     * Implementation of HTTP DELETE
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @return server response
     * @throws RestException when request sending or receiving has failed
     */
    @Override
    public Response delete(String url) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(DELETE).build();
        return holder.getDefaultClient().delete(r);
    }

    /**
     * Implementation of HTTP DELETE
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param headers an instance of {@link Headers} to be sent with the request
     * @return server response
     * @throws RestException when request sending or receiving has failed
     */
    @Override
    public Response delete(String url, Headers headers) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(DELETE).withHeaders(headers).build();
        return holder.getDefaultClient().delete(r);
    }

    /**
     * Async implementation of HTTP GET
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @return server response promise
     * @throws RestException when request could not be made
     */
    @Override
    public Future<Response> asyncGet(String url) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(GET).build();
        return holder.getDefaultAsyncClient().asyncGet(r);
    }

    /**
     * Async implementation of HTTP GET
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param headers an instance of {@link Headers} to be sent with the request
     * @return server response promise
     * @throws RestException when request could not be made
     */
    @Override
    public Future<Response> asyncGet(String url, Headers headers) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(GET).withHeaders(headers).build();
        return holder.getDefaultAsyncClient().asyncGet(r);
    }

    /**
     * Async implementation of HTTP POST
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param body body to be sent with the request
     * @return server response promise
     * @throws RestException when request could not be made
     */
    @Override
    public Future<Response> asyncPost(String url, byte[] body) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(POST).withBody(body).build();
        return holder.getDefaultAsyncClient().asyncPost(r);
    }

    /**
     * Async implementation of HTTP POST
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param headers an instance of {@link Headers} to be sent with the request
     * @param body body to be sent with the request
     * @return server response promise
     * @throws RestException when request could not be made
     */
    @Override
    public Future<Response> asyncPost(String url, Headers headers, byte[] body) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(POST).withHeaders(headers).withBody(body).build();
        return holder.getDefaultAsyncClient().asyncPost(r);
    }

    /**
     * Async implementation of HTTP PUT
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param body body to be sent with the request
     * @return server response promise
     * @throws RestException when request could not be made
     */
    @Override
    public Future<Response> asyncPut(String url, byte[] body) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(PUT).withBody(body).build();
        return holder.getDefaultAsyncClient().asyncPut(r);
    }

    /**
     * Async implementation of HTTP PUT
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param headers an instance of {@link Headers} to be sent with the request
     * @param body body to be sent with the request
     * @return server response promise
     * @throws RestException when request could not be made
     */
    @Override
    public Future<Response> asyncPut(String url, Headers headers, byte[] body) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(PUT).withHeaders(headers).withBody(body).build();
        return holder.getDefaultAsyncClient().asyncPut(r);
    }

    /**
     * Async implementation of HTTP DELETE
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @return server response promise
     * @throws RestException when request could not be made
     */
    @Override
    public Future<Response> asyncDelete(String url) throws RestException {
        Request r = requestBuilder().withMethod(DELETE).withURL(url).build();
        return holder.getDefaultAsyncClient().asyncDelete(r);
    }

    /**
     * Async implementation of HTTP DELETE
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param headers an instance of {@link Headers} to be sent with the request
     * @return server response promise
     * @throws RestException when request could not be made
     */
    @Override
    public Future<Response> asyncDelete(String url, Headers headers) throws RestException {
        Request r = requestBuilder().withMethod(DELETE).withURL(url).withHeaders(headers).build();
        return holder.getDefaultAsyncClient().asyncDelete(r);
    }

    /**
     * Implementation of HTTP HEAD
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @return server response
     * @throws RestException when request sending or receiving has failed
     */
    @Override
    public Response head(String url) throws RestException {
        Request r = requestBuilder().withMethod(HEAD).withURL(url).build();
        return holder.getDefaultClient().head(r);
    }

    /**
     * Implementation of HTTP HEAD
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param headers an instance of {@link Headers} to be sent with the request
     * @return server response
     * @throws RestException when request sending or receiving has failed
     */
    @Override
    public Response head(String url, Headers headers) throws RestException {
        Request r = requestBuilder().withMethod(HEAD).withURL(url).withHeaders(headers).build();
        return holder.getDefaultClient().head(r);
    }

    /**
     * Implementation of HTTP OPTIONS
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @return server response
     * @throws RestException when request sending or receiving has failed
     */
    @Override
    public Response options(String url) throws RestException {
        Request r = requestBuilder().withMethod(OPTIONS).withURL(url).build();
        return holder.getDefaultClient().options(r);
    }

    /**
     * Implementation of HTTP OPTIONS
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param headers an instance of {@link Headers} to be sent with the request
     * @return server response
     * @throws RestException when request sending or receiving has failed
     */
    @Override
    public Response options(String url, Headers headers) throws RestException {
        Request r = requestBuilder().withMethod(OPTIONS).withURL(url).withHeaders(headers).build();
        return holder.getDefaultClient().options(r);
    }

    /**
     * Implementation of HTTP PURGE
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @return server response
     * @throws RestException when request sending or receiving has failed
     */
    @Override
    public Response purge(String url) throws RestException {
        Request r = requestBuilder().withMethod(PURGE).withURL(url).build();
        return holder.getDefaultClient().purge(r);
    }

    /**
     * Implementation of HTTP PURGE
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param headers an instance of {@link Headers} to be sent with the request
     * @return server response
     * @throws RestException when request sending or receiving has failed
     */
    @Override
    public Response purge(String url, Headers headers) throws RestException {
        Request r = requestBuilder().withMethod(PURGE).withURL(url).withHeaders(headers).build();
        return holder.getDefaultClient().purge(r);
    }

    /**
     * Async implementation of HTTP HEAD
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @return server response promise
     * @throws RestException when request could not be made
     */
    @Override
    public Future<Response> asyncHead(String url) throws RestException {
        Request r = requestBuilder().withMethod(HEAD).withURL(url).build();
        return holder.getDefaultAsyncClient().asyncHead(r);
    }

    /**
     * Async implementation of HTTP HEAD
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param headers an instance of {@link Headers} to be sent with the request
     * @return server response promise
     * @throws RestException when request could not be made
     */
    @Override
    public Future<Response> asyncHead(String url, Headers headers) throws RestException {
        Request r = requestBuilder().withMethod(HEAD).withURL(url).withHeaders(headers).build();
        return holder.getDefaultAsyncClient().asyncHead(r);
    }

    /**
     * Async implementation of HTTP OPTIONS
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @return server response promise
     * @throws RestException when request could not be made
     */
    @Override
    public Future<Response> asyncOptions(String url) throws RestException {
        Request r = requestBuilder().withMethod(OPTIONS).withURL(url).build();
        return holder.getDefaultAsyncClient().asyncOptions(r);
    }

    /**
     * Async implementation of HTTP OPTIONS
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param headers an instance of {@link Headers} to be sent with the request
     * @return server response promise
     * @throws RestException when request could not be made
     */
    @Override
    public Future<Response> asyncOptions(String url, Headers headers) throws RestException {
        Request r = requestBuilder().withMethod(OPTIONS).withURL(url).withHeaders(headers).build();
        return holder.getDefaultAsyncClient().asyncOptions(r);
    }

    /**
     * Async implementation of HTTP PURGE
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @return server response promise
     * @throws RestException when request could not be made
     */
    @Override
    public Future<Response> asyncPurge(String url) throws RestException {
        Request r = requestBuilder().withMethod(PURGE).withURL(url).build();
        return holder.getDefaultAsyncClient().asyncPurge(r);
    }

    /**
     * Async implementation of HTTP PURGE
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param headers an instance of {@link Headers} to be sent with the request
     * @return server response promise
     * @throws RestException when request could not be made
     */
    @Override
    public Future<Response> asyncPurge(String url, Headers headers) throws RestException {
        Request r = requestBuilder().withMethod(PURGE).withURL(url).withHeaders(headers).build();
        return holder.getDefaultAsyncClient().asyncPurge(r);
    }

    /**
     * Implementation of HTTP GET for data download
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param stream an stream where data will be sent
     * @return server response
     * @throws RestException when request sending or receiving has failed
     */
    @Override
    public Response get(String url, OutputStream stream) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(GET).withOutputStream(stream).build();
        return holder.getDefaultClient().get(r);
    }

    /**
     * Implementation of HTTP GET for data download
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param headers an instance of {@link Headers} to be sent with the request
     * @param stream an stream where data will be sent
     * @return server response
     * @throws RestException when request sending or receiving has failed
     */
    @Override
    public Response get(String url, Headers headers, OutputStream stream) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(GET).withOutputStream(stream).withHeaders(headers).build();
        return holder.getDefaultClient().get(r);
    }

    /**
     * Implementation of HTTP POST for data download
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param body body to be sent with the request
     * @param stream an stream where data will be sent
     * @return server response
     * @throws RestException when request sending or receiving has failed
     */
    @Override
    public Response post(String url, byte[] body, OutputStream stream) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(POST).withOutputStream(stream).withBody(body).build();
        return holder.getDefaultClient().post(r);
    }

    /**
     * Implementation of HTTP POST for data download
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param headers an instance of {@link Headers} to be sent with the request
     * @param body body to be sent with the request
     * @param stream an stream where data will be sent
     * @return server response
     * @throws RestException when request sending or receiving has failed
     */
    @Override
    public Response post(String url, Headers headers, byte[] body, OutputStream stream) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(POST).withOutputStream(stream).withBody(body).withHeaders(headers).build();
        return holder.getDefaultClient().post(r);
    }

    /**
     * Implementation of HTTP PUT for data download
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param body body to be sent with the request
     * @param stream an stream where data will be sent
     * @return server response
     * @throws RestException when request sending or receiving has failed
     */
    @Override
    public Response put(String url, byte[] body, OutputStream stream) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(PUT).withOutputStream(stream).withBody(body).build();
        return holder.getDefaultClient().put(r);
    }

    /**
     * Implementation of HTTP PUT for data download
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param headers an instance of {@link Headers} to be sent with the request
     * @param body body to be sent with the request
     * @param stream an stream where data will be sent
     * @return server response
     * @throws RestException when request sending or receiving has failed
     */
    @Override
    public Response put(String url, Headers headers, byte[] body, OutputStream stream) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(PUT).withOutputStream(stream).withBody(body).withHeaders(headers).build();
        return holder.getDefaultClient().put(r);
    }

    /**
     * Async implementation of HTTP GET for data download
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param outputStream an stream where data will be sent
     * @return server response promise
     * @throws RestException when request could not be made
     */
    @Override
    public Future<Response> asyncGet(String url, OutputStream outputStream) throws RestException {
        Request r = requestBuilder().withMethod(GET).withURL(url).withOutputStream(outputStream).build();
        return holder.getDefaultAsyncClient().asyncGet(r);
    }

    /**
     * Async implementation of HTTP GET for data download
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param headers an instance of {@link Headers} to be sent with the request
     * @param outputStream an stream where data will be sent
     * @return server response promise
     * @throws RestException when request could not be made
     */
    @Override
    public Future<Response> asyncGet(String url, Headers headers, OutputStream outputStream) throws RestException {
        Request r = requestBuilder().withMethod(GET).withURL(url).withHeaders(headers).withOutputStream(outputStream).build();
        return holder.getDefaultAsyncClient().asyncGet(r);
    }

    /**
     * Async implementation of HTTP POST for data download
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param body body to be sent with the request
     * @param outputStream an stream where data will be sent
     * @return server response promise
     * @throws RestException when request could not be made
     */
    @Override
    public Future<Response> asyncPost(String url, byte[] body, OutputStream outputStream) throws RestException {
        Request r = requestBuilder().withMethod(POST).withURL(url).withBody(body).withOutputStream(outputStream).build();
        return holder.getDefaultAsyncClient().asyncPost(r);
    }

    /**
     * Async implementation of HTTP POST for data download
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param headers an instance of {@link Headers} to be sent with the request
     * @param body body to be sent with the request
     * @param outputStream an stream where data will be sent
     * @return server response promise
     * @throws RestException when request could not be made
     */
    @Override
    public Future<Response> asyncPost(String url, Headers headers, byte[] body, OutputStream outputStream) throws RestException {
        Request r = requestBuilder().withMethod(POST).withURL(url).withHeaders(headers).withBody(body).withOutputStream(outputStream).build();
        return holder.getDefaultAsyncClient().asyncPost(r);
    }

    /**
     * Async implementation of HTTP PUT for data download
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param body body to be sent with the request
     * @param outputStream an stream where data will be sent
     * @return server response promise
     * @throws RestException when request could not be made
     */
    @Override
    public Future<Response> asyncPut(String url, byte[] body, OutputStream outputStream) throws RestException {
        Request r = requestBuilder().withMethod(PUT).withURL(url).withBody(body).withOutputStream(outputStream).build();
        return holder.getDefaultAsyncClient().asyncPut(r);
    }

    /**
     * Async implementation of HTTP PUT for data download
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param headers an instance of {@link Headers} to be sent with the request
     * @param body body to be sent with the request
     * @param outputStream an stream where data will be sent
     * @return server response promise
     * @throws RestException when request could not be made
     */
    @Override
    public Future<Response> asyncPut(String url, Headers headers, byte[] body, OutputStream outputStream) throws RestException {
        Request r = requestBuilder().withMethod(PUT).withURL(url).withHeaders(headers).withBody(body).withOutputStream(outputStream).build();
        return holder.getDefaultAsyncClient().asyncPut(r);
    }

    /**
     * Implementation of HTTP POST
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @return server response
     * @throws RestException when request sending or receiving has failed
     */
    @Override
    public Response post(String url) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(POST).build();
        return holder.getDefaultClient().post(r);
    }

    /**
     * Implementation of HTTP POST
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param headers an instance of {@link Headers} to be sent with the request
     * @return server response
     * @throws RestException when request sending or receiving has failed
     */
    @Override
    public Response post(String url, Headers headers) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(POST).withHeaders(headers).build();
        return holder.getDefaultClient().post(r);
    }

    /**
     * Implementation of HTTP PUT
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @return server response
     * @throws RestException when request sending or receiving has failed
     */
    @Override
    public Response put(String url) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(PUT).build();
        return holder.getDefaultClient().put(r);
    }

    /**
     * Implementation of HTTP PUT
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param headers an instance of {@link Headers} to be sent with the request
     * @return server response
     * @throws RestException when request sending or receiving has failed
     */
    @Override
    public Response put(String url, Headers headers) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(PUT).withHeaders(headers).build();
        return holder.getDefaultClient().put(r);
    }

    /**
     * Async implementation of HTTP POST
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @return server response promise
     * @throws RestException when request could not be made
     */
    @Override
    public Future<Response> asyncPost(String url) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(POST).build();
        return holder.getDefaultAsyncClient().asyncPost(r);
    }

    /**
     * Async implementation of HTTP POST
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param headers an instance of {@link Headers} to be sent with the request
     * @return server response promise
     * @throws RestException when request could not be made
     */
    @Override
    public Future<Response> asyncPost(String url, Headers headers) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(POST).withHeaders(headers).build();
        return holder.getDefaultAsyncClient().asyncPost(r);
    }

    /**
     * Async implementation of HTTP PUT
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @return server response promise
     * @throws RestException when request could not be made
     */
    @Override
    public Future<Response> asyncPut(String url) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(PUT).build();
        return holder.getDefaultAsyncClient().asyncPut(r);
    }

    /**
     * Async implementation of HTTP POST
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param headers an instance of {@link Headers} to be sent with the request
     * @return server response promise
     * @throws RestException when request could not be made
     */
    @Override
    public Future<Response> asyncPut(String url, Headers headers) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(PUT).withHeaders(headers).build();
        return holder.getDefaultAsyncClient().asyncPut(r);
    }

    /**
     * Async implementation of HTTP POST for data download
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param outputStream an stream where data will be sent
     * @return server response promise
     * @throws RestException when request could not be made
     */
    @Override
    public Future<Response> asyncPost(String url, OutputStream outputStream) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(POST).withOutputStream(outputStream).build();
        return holder.getDefaultAsyncClient().asyncPost(r);
    }

    /**
     * Async implementation of HTTP POST for data download
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param headers an instance of {@link Headers} to be sent with the request
     * @param outputStream an stream where data will be sent
     * @return server response promise
     * @throws RestException when request could not be made
     */
    @Override
    public Future<Response> asyncPost(String url, Headers headers, OutputStream outputStream) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(POST).withHeaders(headers).withOutputStream(outputStream).build();
        return holder.getDefaultAsyncClient().asyncPost(r);
    }

    /**
     * Async implementation of HTTP POST for data download
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param outputStream an stream where data will be sent
     * @return server response promise
     * @throws RestException when request could not be made
     */
    @Override
    public Future<Response> asyncPut(String url, OutputStream outputStream) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(PUT).withOutputStream(outputStream).build();
        return holder.getDefaultAsyncClient().asyncPut(r);
    }

    /**
     * Async implementation of HTTP POST for data download
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param headers an instance of {@link Headers} to be sent with the request
     * @param outputStream an stream where data will be sent
     * @return server response promise
     * @throws RestException when request could not be made
     */
    @Override
    public Future<Response> asyncPut(String url, Headers headers, OutputStream outputStream) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(PUT).withHeaders(headers).withOutputStream(outputStream).build();
        return holder.getDefaultAsyncClient().asyncPut(r);
    }

    /**
     * Implementation of HTTP POST for data download
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param stream an stream where data will be sent
     * @return server response
     * @throws RestException when request sending or receiving has failed
     */
    @Override
    public Response post(String url, OutputStream stream) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(POST).withOutputStream(stream).build();
        return holder.getDefaultClient().post(r);
    }

    /**
     * Implementation of HTTP POST for data download
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param headers an instance of {@link Headers} to be sent with the request
     * @param stream an stream where data will be sent
     * @return server response
     * @throws RestException when request sending or receiving has failed
     */
    @Override
    public Response post(String url, Headers headers, OutputStream stream) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(POST).withHeaders(headers).withOutputStream(stream).build();
        return holder.getDefaultClient().post(r);
    }

    /**
     * Implementation of HTTP PUT for data download
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param stream an stream where data will be sent
     * @return server response
     * @throws RestException when request sending or receiving has failed
     */
    @Override
    public Response put(String url, OutputStream stream) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(PUT).withOutputStream(stream).build();
        return holder.getDefaultClient().put(r);
    }

    /**
     * Implementation of HTTP PUT for data download
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param headers an instance of {@link Headers} to be sent with the request
     * @param stream an stream where data will be sent
     * @return server response
     * @throws RestException when request sending or receiving has failed
     */
    @Override
    public Response put(String url, Headers headers, OutputStream stream) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(PUT).withHeaders(headers).withOutputStream(stream).build();
        return holder.getDefaultClient().put(r);
    }

    /**
     * Async implementation of HTTP GET with callback
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException when request could not be made
     */
    @Override
    public void asyncGet(String url, Callback<Response> callback) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(GET).build();
        holder.getDefaultAsyncClient().asyncGet(r, callback);
    }

    /**
     * Async implementation of HTTP GET with callback
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param headers an instance of {@link Headers} to be sent with the request
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException when request could not be made
     */
    @Override
    public void asyncGet(String url, Headers headers, Callback<Response> callback) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(GET).withHeaders(headers).build();
        holder.getDefaultAsyncClient().asyncGet(r, callback);
    }

    /**
     * Async implementation of HTTP POST with callback
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException when request could not be made
     */
    @Override
    public void asyncPost(String url, Callback<Response> callback) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(POST).build();
        holder.getDefaultAsyncClient().asyncPost(r, callback);
    }

    /**
     * Async implementation of HTTP POST with callback
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param headers an instance of {@link Headers} to be sent with the request
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException when request could not be made
     */
    @Override
    public void asyncPost(String url, Headers headers, Callback<Response> callback) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(POST).withHeaders(headers).build();
        holder.getDefaultAsyncClient().asyncPost(r, callback);
    }

    /**
     * Async implementation of HTTP POST with callback
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param body body to be sent with the request
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException when request could not be made
     */
    @Override
    public void asyncPost(String url, byte[] body, Callback<Response> callback) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(POST).withBody(body).build();
        holder.getDefaultAsyncClient().asyncPost(r, callback);
    }

    /**
     * Async implementation of HTTP POST with callback
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param headers an instance of {@link Headers} to be sent with the request
     * @param body body to be sent with the request
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException when request could not be made
     */
    @Override
    public void asyncPost(String url, Headers headers, byte[] body, Callback<Response> callback) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(POST).withHeaders(headers).withBody(body).build();
        holder.getDefaultAsyncClient().asyncPost(r, callback);
    }

    /**
     * Async implementation of HTTP PUT with callback
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException when request could not be made
     */
    @Override
    public void asyncPut(String url, Callback<Response> callback) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(PUT).build();
        holder.getDefaultAsyncClient().asyncPut(r, callback);
    }

    /**
     * Async implementation of HTTP PUT with callback
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param headers an instance of {@link Headers} to be sent with the request
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException when request could not be made
     */
    @Override
    public void asyncPut(String url, Headers headers, Callback<Response> callback) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(PUT).withHeaders(headers).build();
        holder.getDefaultAsyncClient().asyncPut(r, callback);
    }

    /**
     * Async implementation of HTTP PUT with callback
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param body body to be sent with the request
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException when request could not be made
     */
    @Override
    public void asyncPut(String url, byte[] body, Callback<Response> callback) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(PUT).withBody(body).build();
        holder.getDefaultAsyncClient().asyncPut(r, callback);
    }

    /**
     * Async implementation of HTTP PUT with callback
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param headers an instance of {@link Headers} to be sent with the request
     * @param body body to be sent with the request
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException when request could not be made
     */
    @Override
    public void asyncPut(String url, Headers headers, byte[] body, Callback<Response> callback) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(PUT).withHeaders(headers).withBody(body).build();
        holder.getDefaultAsyncClient().asyncPut(r, callback);
    }

    /**
     * Async implementation of HTTP DELETE with callback
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException when request could not be made
     */
    @Override
    public void asyncDelete(String url, Callback<Response> callback) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(DELETE).build();
        holder.getDefaultAsyncClient().asyncDelete(r, callback);
    }

    /**
     * Async implementation of HTTP DELETE with callback
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param headers an instance of {@link Headers} to be sent with the request
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException when request could not be made
     */
    @Override
    public void asyncDelete(String url, Headers headers, Callback<Response> callback) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(DELETE).withHeaders(headers).build();
        holder.getDefaultAsyncClient().asyncDelete(r, callback);
    }

    /**
     * Async implementation of HTTP HEAD with callback
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException when request could not be made
     */
    @Override
    public void asyncHead(String url, Callback<Response> callback) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(HEAD).build();
        holder.getDefaultAsyncClient().asyncHead(r, callback);
    }

    /**
     * Async implementation of HTTP HEAD with callback
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param headers an instance of {@link Headers} to be sent with the request
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException when request could not be made
     */
    @Override
    public void asyncHead(String url, Headers headers, Callback<Response> callback) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(HEAD).withHeaders(headers).build();
        holder.getDefaultAsyncClient().asyncHead(r, callback);
    }

    /**
     * Async implementation of HTTP OPTIONS with callback
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException when request could not be made
     */
    @Override
    public void asyncOptions(String url, Callback<Response> callback) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(OPTIONS).build();
        holder.getDefaultAsyncClient().asyncOptions(r, callback);
    }

    /**
     * Async implementation of HTTP OPTIONS with callback
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param headers an instance of {@link Headers} to be sent with the request
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException when request could not be made
     */
    @Override
    public void asyncOptions(String url, Headers headers, Callback<Response> callback) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(OPTIONS).withHeaders(headers).build();
        holder.getDefaultAsyncClient().asyncOptions(r, callback);
    }

    /**
     * Async implementation of HTTP PURGE with callback
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException when request could not be made
     */
    @Override
    public void asyncPurge(String url, Callback<Response> callback) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(PURGE).build();
        holder.getDefaultAsyncClient().asyncPurge(r, callback);
    }

    /**
     * Async implementation of HTTP PURGE with callback
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param headers an instance of {@link Headers} to be sent with the request
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException when request could not be made
     */
    @Override
    public void asyncPurge(String url, Headers headers, Callback<Response> callback) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(PURGE).withHeaders(headers).build();
        holder.getDefaultAsyncClient().asyncPurge(r, callback);
    }

    /**
     * Async implementation of HTTP GET for data download with callback
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param outputStream an stream where data will be sent
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException when request could not be made
     */
    @Override
    public void asyncGet(String url, OutputStream outputStream, Callback<Response> callback) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(GET).withOutputStream(outputStream).build();
        holder.getDefaultAsyncClient().asyncGet(r, callback);
    }

    /**
     * Async implementation of HTTP GET for data download with callback
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param headers an instance of {@link Headers} to be sent with the request
     * @param outputStream an stream where data will be sent
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException when request could not be made
     */
    @Override
    public void asyncGet(String url, Headers headers, OutputStream outputStream, Callback<Response> callback) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(GET).withHeaders(headers).withOutputStream(outputStream).build();
        holder.getDefaultAsyncClient().asyncGet(r, callback);
    }

    /**
     * Async implementation of HTTP POST for data download with callback
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param outputStream an stream where data will be sent
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException when request could not be made
     */
    @Override
    public void asyncPost(String url, OutputStream outputStream, Callback<Response> callback) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(POST).withOutputStream(outputStream).build();
        holder.getDefaultAsyncClient().asyncPost(r, callback);
    }

    /**
     * Async implementation of HTTP POST for data download with callback
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param headers an instance of {@link Headers} to be sent with the request
     * @param outputStream an stream where data will be sent
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException when request could not be made
     */
    @Override
    public void asyncPost(String url, Headers headers, OutputStream outputStream, Callback<Response> callback) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(POST).withHeaders(headers).withOutputStream(outputStream).build();
        holder.getDefaultAsyncClient().asyncPost(r, callback);
    }

    /**
     * Async implementation of HTTP POST for data download with callback
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param body body to be sent with the request
     * @param outputStream an stream where data will be sent
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException when request could not be made
     */
    @Override
    public void asyncPost(String url, byte[] body, OutputStream outputStream, Callback<Response> callback) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(POST).withBody(body).withOutputStream(outputStream).build();
        holder.getDefaultAsyncClient().asyncPost(r, callback);
    }

    /**
     * Async implementation of HTTP POST for data download with callback
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param headers an instance of {@link Headers} to be sent with the request
     * @param body body to be sent with the request
     * @param outputStream an stream where data will be sent
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException when request could not be made
     */
    @Override
    public void asyncPost(String url, Headers headers, byte[] body, OutputStream outputStream, Callback<Response> callback) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(POST).withHeaders(headers).withBody(body).withOutputStream(outputStream).build();
        holder.getDefaultAsyncClient().asyncPost(r, callback);
    }

    /**
     * Async implementation of HTTP PUT for data download with callback
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param outputStream an stream where data will be sent
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException when request could not be made
     */
    @Override
    public void asyncPut(String url, OutputStream outputStream, Callback<Response> callback) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(PUT).withOutputStream(outputStream).build();
        holder.getDefaultAsyncClient().asyncPut(r, callback);
    }

    /**
     * Async implementation of HTTP PUT for data download with callback
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param headers an instance of {@link Headers} to be sent with the request
     * @param outputStream an stream where data will be sent
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException when request could not be made
     */
    @Override
    public void asyncPut(String url, Headers headers, OutputStream outputStream, Callback<Response> callback) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(PUT).withHeaders(headers).withOutputStream(outputStream).build();
        holder.getDefaultAsyncClient().asyncPut(r, callback);
    }

    /**
     * Async implementation of HTTP PUT for data download with callback
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param body body to be sent with the request
     * @param outputStream an stream where data will be sent
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException when request could not be made
     */
    @Override
    public void asyncPut(String url, byte[] body, OutputStream outputStream, Callback<Response> callback) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(PUT).withBody(body).withOutputStream(outputStream).build();
        holder.getDefaultAsyncClient().asyncPut(r, callback);
    }

    /**
     * Async implementation of HTTP PUT for data download with callback
     * @param url a full URL or a URI in case current {@link RESTPool} has baseURL defined
     * @param headers an instance of {@link Headers} to be sent with the request
     * @param body body to be sent with the request
     * @param outputStream an stream where data will be sent
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException when request could not be made
     */
    @Override
    public void asyncPut(String url, Headers headers, byte[] body, OutputStream outputStream, Callback<Response> callback) throws RestException {
        Request r = requestBuilder().withURL(url).withMethod(PUT).withHeaders(headers).withBody(body).withOutputStream(outputStream).build();
        holder.getDefaultAsyncClient().asyncPut(r, callback);
    }
}
