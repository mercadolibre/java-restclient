package com.mercadolibre.restclient;

import com.mercadolibre.restclient.async.Callback;
import com.mercadolibre.restclient.cache.RESTCache;
import com.mercadolibre.restclient.exception.RestException;
import com.mercadolibre.restclient.http.*;
import com.mercadolibre.restclient.interceptor.ContentTypeInterceptor;
import com.mercadolibre.restclient.interceptor.RequestInterceptor;
import com.mercadolibre.restclient.interceptor.ResponseInterceptor;
import com.mercadolibre.restclient.multipart.MultipartMode;
import com.mercadolibre.restclient.multipart.Part;
import com.mercadolibre.restclient.retry.RetryStrategy;
import com.mercadolibre.restclient.util.URLUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.Future;

import static com.mercadolibre.restclient.http.HttpMethod.*;


public final class RequestBuilder implements REST, StreamREST, AsyncREST, StreamAsyncREST {

    private RestClient restClient;
    private Request request;

    protected RequestBuilder() {
    	this.request = new Request();
    }
    
    protected RequestBuilder(RestClient restClient) {
        this.request = new Request();
        this.restClient = restClient;
    }

    /**
     * Sets the URL (or URI in case there's a base URL configured) for current request
     * @param url a string with a valid URL (or URI)
     * @return this builder
     */
    public RequestBuilder withURL(String url) {
        request.setURL(url);
        return this;
    }

    /**
     * Adds a map of parameters as part of query string for current request URL
     * @param parameters a map with name,value entries
     * @return this builder
     */
    public RequestBuilder withParameters(Map<String, String> parameters) {
        for (Map.Entry<String,String> e : parameters.entrySet()) {
            if (StringUtils.isNotBlank(e.getKey()))
                request.setParameter(e.getKey(), e.getValue());
        }

    	return this;
    }

    /**
     * Adds a parameter as part of query string for current request URL
     * @param name parameter name
     * @param value parameter value
     * @return this builder
     */
    public RequestBuilder withParameter(String name, String value) {
        if (StringUtils.isNotBlank(name))
    	    request.setParameter(name, value);

        return this;
    }

    /**
     * Adds optional attributes for current request
     * @param attributes a map of name,value entries
     * @return this builder
     */
    public RequestBuilder withAttributes(Map<String,Object> attributes) {
        request.setAttributes(attributes);
        return this;
    }

    /**
     * Adds an optional attribute for current request
     * @param name param name
     * @param value param value
     * @return this builder
     */
    public RequestBuilder withAttribute(String name, Object value) {
        request.setAttribute(name, value);
        return this;
    }

    /**
     * Adds headers for current request
     * @param headers a {@link Headers} instance
     * @return this builder
     */
    public RequestBuilder withHeaders(Headers headers) {
        request.setHeaders(headers);
        return this;
    }

    /**
     * Adds a header for current request
     * @param header a {@link Header} instance
     * @return this builder
     */
    public RequestBuilder withHeader(Header header) {
        request.getHeaders().add(header);
        return this;
    }

    /**
     * Adds a header for current request
     * @param name header name
     * @param value header value
     * @return this builder
     */
    public RequestBuilder withHeader(String name, String value) {
        return withHeader(new Header(name,value));
    }

    /**
     * Adds a body to current request
     * @param body a byte[] representing the request body
     * @return this builder
     */
    public RequestBuilder withBody(byte[] body) {
        request.setBody(body);
        return this;
    }

    /**
     * Specifies a pool for current request
     * @param pool a {@link RESTPool} instance
     * @return this builder
     * @throws RestException if there's no such pool associated with the client
     */
    public RequestBuilder withPool(RESTPool pool) throws RestException {
        return withPool(pool.getName());
    }

    /**
     * Specifies a pool for current request
     * @param poolName the pool name
     * @return this builder
     * @throws RestException if there's no such pool associated with the client
     */
	public RequestBuilder withPool(String poolName) throws RestException {
        ClientHolder.Clients<?> clients = restClient.getHolder().getClients(poolName);
        request.setClients(clients);
        return this;
    }

    /**
     * Specifies a proxy for current request
     * @param proxy a {@link Proxy} instance
     * @return this builder
     */
    public RequestBuilder withProxy(Proxy proxy) {
        request.setProxy(proxy);
        return this;
    }

    /**
     * Specifies a proxy for current request
     * @param host proxy host URL
     * @param port proxy port
     * @return this builder
     */
    public RequestBuilder withProxy(String host, int port) {
        request.setProxy(new Proxy(host, port));
        return this;
    }

    /**
     * Adds BASIC authentication for current request
     * @param authentication a {@link Authentication} instance
     * @return this builder
     */
    public RequestBuilder withAuthentication(Authentication authentication) {
        request.setAuthentication(authentication);
        return this;
    }

    /**
     * Sets method for current request. It won't be necessary in regular usage
     * @param method a {@link HttpMethod} constant
     * @return this builder
     */
    public RequestBuilder withMethod(HttpMethod method) {
        request.setMethod(method);
        return this;
    }

    /**
     * Specifies a retry strategy for current request
     * @param retryStrategy a {@link RetryStrategy} instance
     * @return this builder
     */
    public RequestBuilder withRetryStrategy(RetryStrategy retryStrategy) {
        request.setRetryStrategy(retryStrategy);
        return this;
    }

    /**
     * Adds a request interceptor for this request, first in its deque
     * @param i a {@link RequestInterceptor}
     * @return this builder
     */
    public RequestBuilder withInterceptorFirst(RequestInterceptor i) {
        request.addInterceptorFirst(i);
        return this;
    }

    /**
     * Adds a request interceptor for this request, last in its deque
     * @param i a {@link RequestInterceptor}
     * @return this builder
     */
    public RequestBuilder withInterceptorLast(RequestInterceptor i) {
        request.addInterceptorLast(i);
        return this;
    }

    /**
     * Adds a response interceptor for this request, first in its deque
     * @param i a {@link ResponseInterceptor}
     * @return this builder
     */
    public RequestBuilder withInterceptorFirst(ResponseInterceptor i) {
        request.addInterceptorFirst(i);
        return this;
    }

    /**
     * Adds a response interceptor for this request, last in its deque
     * @param i a {@link ResponseInterceptor}
     * @return this builder
     */
    public RequestBuilder withInterceptorLast(ResponseInterceptor i) {
        request.addInterceptorLast(i);
        return this;
    }

    /**
     * Sets an output stream for data download.
     * @param outputStream An {@link OutputStream} instance
     * @return this builder
     */
    public RequestBuilder withOutputStream(OutputStream outputStream) {
        request.setOutputStream(outputStream);
        return this;
    }

    /**
     * Adds a set of parts, for multipart uploading
     * @param parts a set of {@link Part}
     * @return this builder
     */
	public RequestBuilder withParts(Set<Part<?>> parts) {
        request.setParts(parts);
        return this;
    }

    /**
     * Adds a single part, for multipart uploading
     * @param part a {@link Part}
     * @return this builder
     */
	public RequestBuilder withPart(Part<?> part) {
        request.setPart(part);
        return this;
    }

    /**
     * Specifies a multipart mode
     * @param mode a {@link MultipartMode}
     * @return this builder
     */
    public RequestBuilder withMultipartMode(MultipartMode mode) {
	    request.setMultipartMode(mode);
	    return this;
    }

    /**
     * Specifies a cache for current request
     * @param cache a {@link RESTCache} instance
     * @return this builder
     */
    public RequestBuilder withCache(RESTCache cache) {
        request.setCache(cache);
        return this;
    }

    /**
     * Specifies a cache for current request
     * @param cache cache name
     * @return this builder
     */
    public RequestBuilder withCache(String cache) {
        return withCache(restClient.getHolder().getCache(cache));
    }

    private <T> Deque<T> join(Deque<T> first, Deque<T> last) {
        if (last == null || last.isEmpty()) return new LinkedList<>(first);

        Iterator<T> it = first.descendingIterator();
        while (it.hasNext())
            last.addFirst(it.next());

        return last;
    }

    protected Request build() {
        validateURL();

        if (request.getClients() == null)
            request.setClients(restClient.getHolder().getDefaultClients());

        request.setURL(URLUtils.completeURL(request.getPool().getBaseURL(), request.getPlainURL()));

        request.setRequestInterceptors(join(request.getPool().getRequestInterceptors(), request.getRequestInterceptors()));

        request.setResponseInterceptors(join(request.getPool().getResponseInterceptors(), request.getResponseInterceptors()));

        if (request.getRetryStrategy() == null)
            request.setRetryStrategy(request.getPool().getRetryStrategy());

        if (request.getParts() != null) {
            if (request.getMethod() != POST)
                throw new UnsupportedOperationException("Multipart upload is only supported for POST");

            if (request.getMultipartMode() == null)
                request.setMultipartMode(MultipartMode.STRICT);

            List<RequestInterceptor> interceptors = new LinkedList<>();
            for (RequestInterceptor i : request.getRequestInterceptors()) {
                if (i instanceof ContentTypeInterceptor) {
                    interceptors.add(i);
                }
            }

            request.getRequestInterceptors().removeAll(interceptors);
        }

        if (!request.hasCache()) {
            RESTCache cache = request.getClients().getPool().getCache();
            request.setCache(cache);
        }

        return request;
    }

    /**
     * Async implementation of HTTP GET
     * @param url a full formed URL (o a URI if a base URL applies)
     * @return a Future wrapping a {@link Response}
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncGet(String)
     */
    @Override
    public Future<Response> asyncGet(String url) throws RestException {
        withURL(url);
        withMethod(GET);
        build();
        return request.getClients().getAsyncClient().asyncGet(request);
    }

    /**
     * Async implementation of HTTP GET
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param headers a {@link Headers} instance
     * @return a Future wrapping a {@link Response}
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncGet(String, Headers)
     */
    @Override
    public Future<Response> asyncGet(String url, Headers headers) throws RestException {
        withURL(url);
        withMethod(GET);
        withHeaders(headers);
        build();
        return request.getClients().getAsyncClient().asyncGet(request);
    }

    /**
     * Async implementation of HTTP POST
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param body a byte[] containing request body
     * @return a Future wrapping a {@link Response}
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncPost(String, byte[])
     */
    @Override
    public Future<Response> asyncPost(String url, byte[] body) throws RestException {
        withURL(url);
        withMethod(POST);
        withBody(body);
        build();
        return request.getClients().getAsyncClient().asyncPost(request);
    }

    /**
     * Async implementation of HTTP POST
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param headers a {@link Headers} instance
     * @param body a byte[] containing request body
     * @return a Future wrapping a {@link Response}
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncPost(String, Headers, byte[])
     */
    @Override
    public Future<Response> asyncPost(String url, Headers headers, byte[] body) throws RestException {
        withURL(url);
        withHeaders(headers);
        withMethod(POST);
        withBody(body);
        build();
        return request.getClients().getAsyncClient().asyncPost(request);
    }

    /**
     * Async implementation of HTTP PUT
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param body a byte[] containing request body
     * @return a Future wrapping a {@link Response}
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncPut(String, byte[])
     */
    @Override
    public Future<Response> asyncPut(String url, byte[] body) throws RestException {
        withURL(url);
        withMethod(PUT);
        withBody(body);
        build();
        return request.getClients().getAsyncClient().asyncPut(request);
    }

    /**
     * Async implementation of HTTP PUT
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param headers a {@link Headers} instance
     * @param body a byte[] containing request body
     * @return a Future wrapping a {@link Response}
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncPut(String, Headers, byte[])
     */
    @Override
    public Future<Response> asyncPut(String url, Headers headers, byte[] body) throws RestException {
        withURL(url);
        withHeaders(headers);
        withMethod(PUT);
        withBody(body);
        build();
        return request.getClients().getAsyncClient().asyncPut(request);
    }

    /**
     * Async implementation of HTTP DELETE
     * @param url a full formed URL (o a URI if a base URL applies)
     * @return a Future wrapping a {@link Response}
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncDelete(String)
     */
    @Override
    public Future<Response> asyncDelete(String url) throws RestException {
        withURL(url);
        withMethod(DELETE);
        build();
        return request.getClients().getAsyncClient().asyncDelete(request);
    }

    /**
     * Async implementation of HTTP DELETE
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param headers a {@link Headers} instance
     * @return a Future wrapping a {@link Response}
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncDelete(String, Headers)
     */
    @Override
    public Future<Response> asyncDelete(String url, Headers headers) throws RestException {
        withURL(url);
        withHeaders(headers);
        withMethod(DELETE);
        build();
        return request.getClients().getAsyncClient().asyncDelete(request);
    }

    /**
     * Async implementation of HTTP HEAD
     * @param url a full formed URL (o a URI if a base URL applies)
     * @return a Future wrapping a {@link Response}
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncHead(String)
     */
    @Override
    public Future<Response> asyncHead(String url) throws RestException {
        withURL(url);
        withMethod(HEAD);
        build();
        return request.getClients().getAsyncClient().asyncHead(request);
    }

    /**
     * Async implementation of HTTP HEAD
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param headers a {@link Headers} instance
     * @return a Future wrapping a {@link Response}
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncHead(String, Headers)
     */
    @Override
    public Future<Response> asyncHead(String url, Headers headers) throws RestException {
        withURL(url);
        withHeaders(headers);
        withMethod(HEAD);
        build();
        return request.getClients().getAsyncClient().asyncHead(request);
    }

    /**
     * Async implementation of HTTP OPTIONS
     * @param url a full formed URL (o a URI if a base URL applies)
     * @return a Future wrapping a {@link Response}
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncOptions(String)
     */
    @Override
    public Future<Response> asyncOptions(String url) throws RestException {
        withURL(url);
        withMethod(OPTIONS);
        build();
        return request.getClients().getAsyncClient().asyncOptions(request);
    }

    /**
     * Async implementation of HTTP OPTIONS
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param headers a {@link Headers} instance
     * @return a Future wrapping a {@link Response}
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncOptions(String, Headers)
     */
    @Override
    public Future<Response> asyncOptions(String url, Headers headers) throws RestException {
        withURL(url);
        withHeaders(headers);
        withMethod(OPTIONS);
        build();
        return request.getClients().getAsyncClient().asyncOptions(request);
    }

    /**
     * Async implementation of HTTP PURGE
     * @param url a full formed URL (o a URI if a base URL applies)
     * @return a Future wrapping a {@link Response}
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncPurge(String, Headers)
     */
    @Override
    public Future<Response> asyncPurge(String url) throws RestException {
        withURL(url);
        withMethod(PURGE);
        build();
        return request.getClients().getAsyncClient().asyncPurge(request);
    }

    /**
     * Async implementation of HTTP PURGE
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param headers a {@link Headers} instance
     * @return a Future wrapping a {@link Response}
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncPurge(String, Headers)
     */
    @Override
    public Future<Response> asyncPurge(String url, Headers headers) throws RestException {
        withURL(url);
        withHeaders(headers);
        withMethod(PURGE);
        build();
        return request.getClients().getAsyncClient().asyncPurge(request);
    }

    /**
     * Implementation of HTTP GET
     * @param url a full formed URL (o a URI if a base URL applies)
     * @return a {@link Response} instance
     * @throws RestException if request couldn't be made
     * @see RestClient#get(String)
     */
    @Override
    public Response get(String url) throws RestException {
        withURL(url);
        withMethod(GET);
        build();
        return request.getClients().getSyncClient().get(request);
    }

    /**
     * Implementation of HTTP GET
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param headers a {@link Headers} instance
     * @return a {@link Response} instance
     * @throws RestException if request couldn't be made
     * @see RestClient#get(String, Headers)
     */
    @Override
    public Response get(String url, Headers headers) throws RestException {
        withURL(url);
        withHeaders(headers);
        withMethod(GET);
        build();
        return request.getClients().getSyncClient().get(request);
    }

    /**
     * Implementation of HTTP POST
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param body a byte[] containing request body
     * @return a {@link Response} instance
     * @throws RestException if request couldn't be made
     * @see RestClient#post(String, byte[])
     */
    @Override
    public Response post(String url, byte[] body) throws RestException {
        withURL(url);
        withBody(body);
        withMethod(POST);
        build();
        return request.getClients().getSyncClient().post(request);
    }

    /**
     * Implementation of HTTP POST
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param headers a {@link Headers} instance
     * @param body a byte[] containing request body
     * @return a {@link Response} instance
     * @throws RestException if request couldn't be made
     * @see RestClient#post(String, Headers, byte[])
     */
    @Override
    public Response post(String url, Headers headers, byte[] body) throws RestException {
        withURL(url);
        withHeaders(headers);
        withMethod(POST);
        withBody(body);
        build();
        return request.getClients().getSyncClient().post(request);
    }

    /**
     * Implementation of HTTP PUT
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param body a byte[] containing request body
     * @return a {@link Response} instance
     * @throws RestException if request couldn't be made
     * @see RestClient#put(String, byte[])
     */
    @Override
    public Response put(String url, byte[] body) throws RestException {
        withURL(url);
        withMethod(PUT);
        withBody(body);
        build();
        return request.getClients().getSyncClient().put(request);
    }

    /**
     * Implementation of HTTP PUT
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param headers a {@link Headers} instance
     * @param body a byte[] containing request body
     * @return a {@link Response} instance
     * @throws RestException if request couldn't be made
     * @see RestClient#put(String, Headers, byte[])
     */
    @Override
    public Response put(String url, Headers headers, byte[] body) throws RestException {
        withURL(url);
        withHeaders(headers);
        withMethod(PUT);
        withBody(body);
        build();
        return request.getClients().getSyncClient().put(request);
    }

    /**
     * Implementation of HTTP DELETE
     * @param url a full formed URL (o a URI if a base URL applies)
     * @return a {@link Response} instance
     * @throws RestException if request couldn't be made
     * @see RestClient#delete(String)
     */
    @Override
    public Response delete(String url) throws RestException {
        withURL(url);
        withMethod(DELETE);
        build();
        return request.getClients().getSyncClient().delete(request);
    }

    /**
     * Implementation of HTTP DELETE
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param headers a {@link Headers} instance
     * @return a {@link Response} instance
     * @throws RestException if request couldn't be made
     * @see RestClient#delete(String, Headers)
     */
    @Override
    public Response delete(String url, Headers headers) throws RestException {
        withURL(url);
        withMethod(DELETE);
        withHeaders(headers);
        build();
        return request.getClients().getSyncClient().delete(request);
    }

    /**
     * Implementation of HTTP HEAD
     * @param url a full formed URL (o a URI if a base URL applies)
     * @return a {@link Response} instance
     * @throws RestException if request couldn't be made
     * @see RestClient#head(String)
     */
    @Override
    public Response head(String url) throws RestException {
        withURL(url);
        withMethod(HEAD);
        build();
        return request.getClients().getSyncClient().head(request);
    }

    /**
     * Implementation of HTTP HEAD
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param headers a {@link Headers} instance
     * @return a {@link Response} instance
     * @throws RestException if request couldn't be made
     * @see RestClient#head(String, Headers)
     */
    @Override
    public Response head(String url, Headers headers) throws RestException {
        withURL(url);
        withHeaders(headers);
        withMethod(HEAD);
        build();
        return request.getClients().getSyncClient().head(request);
    }

    /**
     * Implementation of HTTP OPTIONS
     * @param url a full formed URL (o a URI if a base URL applies)
     * @return a {@link Response} instance
     * @throws RestException if request couldn't be made
     * @see RestClient#options(String)
     */
    @Override
    public Response options(String url) throws RestException {
        withURL(url);
        withMethod(OPTIONS);
        build();
        return request.getClients().getSyncClient().options(request);
    }

    /**
     * Implementation of HTTP OPTIONS
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param headers a {@link Headers} instance
     * @return a {@link Response} instance
     * @throws RestException if request couldn't be made
     * @see RestClient#options(String, Headers)
     */
    @Override
    public Response options(String url, Headers headers) throws RestException {
        withURL(url);
        withHeaders(headers);
        withMethod(OPTIONS);
        build();
        return request.getClients().getSyncClient().options(request);
    }

    /**
     * Implementation of HTTP PURGE
     * @param url a full formed URL (o a URI if a base URL applies)
     * @return a {@link Response} instance
     * @throws RestException if request couldn't be made
     * @see RestClient#purge(String)
     */
    @Override
    public Response purge(String url) throws RestException {
        withURL(url);
        withMethod(PURGE);
        build();
        return request.getClients().getSyncClient().purge(request);
    }

    /**
     * Implementation of HTTP PURGE
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param headers a {@link Headers} instance
     * @return a {@link Response} instance
     * @throws RestException if request couldn't be made
     * @see RestClient#purge(String, Headers)
     */
    @Override
    public Response purge(String url, Headers headers) throws RestException {
        withURL(url);
        withHeaders(headers);
        withMethod(PURGE);
        build();
        return request.getClients().getSyncClient().purge(request);
    }

    private void validateURL() {
        if (request.getURL() == null) throw new IllegalArgumentException("URL should not be null");
    }

    /**
     * Implementation of HTTP GET. At least URL must be already defined.
     * @return a {@link Response} instance
     * @throws RestException if request couldn't be made
     * @see #withURL(String)
     */
    public Response get() throws RestException {
        withMethod(GET);
        build();
        return request.getClients().getSyncClient().get(request);
    }

    /**
     * Implementation of HTTP POST. At least URL must be already defined.
     * @return a {@link Response} instance
     * @throws RestException if request couldn't be made
     * @see #withURL(String)
     */
    public Response post() throws RestException {
        withMethod(POST);
        build();
        return request.getClients().getSyncClient().post(request);
    }

    /**
     * Implementation of HTTP PUT. At least URL must be already defined.
     * @return a {@link Response} instance
     * @throws RestException if request couldn't be made
     * @see #withURL(String)
     */
    public Response put() throws RestException {
        withMethod(PUT);
        build();
        return request.getClients().getSyncClient().put(request);
    }

    /**
     * Implementation of HTTP DELETE. At least URL must be already defined.
     * @return a {@link Response} instance
     * @throws RestException if request couldn't be made
     * @see #withURL(String)
     */
    public Response delete() throws RestException {
        withMethod(DELETE);
        build();
        return request.getClients().getSyncClient().delete(request);
    }

    /**
     * Implementation of HTTP HEAD. At least URL must be already defined.
     * @return a {@link Response} instance
     * @throws RestException if request couldn't be made
     * @see #withURL(String)
     */
    public Response head() throws RestException {
        withMethod(HEAD);
        build();
        return request.getClients().getSyncClient().head(request);
    }

    /**
     * Implementation of HTTP OPTIONS. At least URL must be already defined.
     * @return a {@link Response} instance
     * @throws RestException if request couldn't be made
     * @see #withURL(String)
     */
    public Response options() throws RestException {
        withMethod(OPTIONS);
        build();
        return request.getClients().getSyncClient().options(request);
    }

    /**
     * Implementation of HTTP PURGE. At least URL must be already defined.
     * @return a {@link Response} instance
     * @throws RestException if request couldn't be made
     * @see #withURL(String)
     */
    public Response purge() throws RestException {
        withMethod(PURGE);
        build();
        return request.getClients().getSyncClient().purge(request);
    }

    /**
     * Async implementation of HTTP GET. Assumes that URL has been set for this request.
     * @return a Future wrapping a {@link Response}
     * @throws RestException if request couldn't be made
     */
    public Future<Response> asyncGet() throws RestException {
        withMethod(GET);
        build();
        return request.getClients().getAsyncClient().asyncGet(request);
    }

    /**
     * Async implementation of HTTP POST. Assumes that URL has been set for this request.
     * @return a Future wrapping a {@link Response}
     * @throws RestException if request couldn't be made
     */
    public Future<Response> asyncPost() throws RestException {
        withMethod(POST);
        build();
        return request.getClients().getAsyncClient().asyncPost(request);
    }

    /**
     * Async implementation of HTTP PUT. Assumes that URL has been set for this request.
     * @return a Future wrapping a {@link Response}
     * @throws RestException if request couldn't be made
     */
    public Future<Response> asyncPut() throws RestException {
        withMethod(PUT);
        build();
        return request.getClients().getAsyncClient().asyncPut(request);
    }

    /**
     * Async implementation of HTTP DELETE. Assumes that URL has been set for this request.
     * @return a Future wrapping a {@link Response}
     * @throws RestException if request couldn't be made
     */
    public Future<Response> asyncDelete() throws RestException {
        withMethod(DELETE);
        build();
        return request.getClients().getAsyncClient().asyncDelete(request);
    }

    /**
     * Async implementation of HTTP HEAD. Assumes that URL has been set for this request.
     * @return a Future wrapping a {@link Response}
     * @throws RestException if request couldn't be made
     */
    public Future<Response> asyncHead() throws RestException {
        withMethod(HEAD);
        build();
        return request.getClients().getAsyncClient().asyncHead(request);
    }

    /**
     * Async implementation of HTTP OPTIONS. Assumes that URL has been set for this request.
     * @return a Future wrapping a {@link Response}
     * @throws RestException if request couldn't be made
     */
    public Future<Response> asyncOptions() throws RestException {
        withMethod(OPTIONS);
        build();
        return request.getClients().getAsyncClient().asyncOptions(request);
    }

    /**
     * Async implementation of HTTP PURGE. Assumes that URL has been set for this request.
     * @return a Future wrapping a {@link Response}
     * @throws RestException if request couldn't be made
     */
    public Future<Response> asyncPurge() throws RestException {
        withMethod(PURGE);
        build();
        return request.getClients().getAsyncClient().asyncPurge(request);
    }

    /**
     * Implementation of HTTP GET for data download
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param stream an output stream where data will be streamed
     * @return a {@link Response} instance. Its body will be null.
     * @throws RestException if request couldn't be made
     * @see RestClient#get(String, OutputStream)
     */
    @Override
    public Response get(String url, OutputStream stream) throws RestException {
        withURL(url);
        withMethod(GET);
        withOutputStream(stream);
        build();

        return request.getClients().getSyncClient().get(request);
    }

    /**
     * Implementation of HTTP GET for data download
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param headers a {@link Headers} instance
     * @param stream an output stream where data will be streamed
     * @return a {@link Response} instance. Its body will be null.
     * @throws RestException if request couldn't be made
     * @see RestClient#get(String, Headers, OutputStream)
     */
    @Override
    public Response get(String url, Headers headers, OutputStream stream) throws RestException {
        withURL(url);
        withMethod(GET);
        withHeaders(headers);
        withOutputStream(stream);
        build();

        return request.getClients().getSyncClient().get(request);
    }

    /**
     * Implementation of HTTP POST for data download
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param body a byte[] containing request body
     * @param stream an output stream where data will be streamed
     * @return a {@link Response} instance. Its body will be null.
     * @throws RestException if request couldn't be made
     * @see RestClient#post(String, byte[], OutputStream)
     */
    @Override
    public Response post(String url, byte[] body, OutputStream stream) throws RestException {
        withURL(url);
        withMethod(POST);
        withBody(body);
        withOutputStream(stream);
        build();

        return request.getClients().getSyncClient().post(request);
    }

    /**
     * Implementation of HTTP POST for data download
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param headers a {@link Headers} instance
     * @param body a byte[] containing request body
     * @param stream an output stream where data will be streamed
     * @return a {@link Response} instance. Its body will be null.
     * @throws RestException if request couldn't be made
     * @see RestClient#post(String, Headers, byte[], OutputStream)
     */
    @Override
    public Response post(String url, Headers headers, byte[] body, OutputStream stream) throws RestException {
        withURL(url);
        withMethod(POST);
        withHeaders(headers);
        withBody(body);
        withOutputStream(stream);
        build();

        return request.getClients().getSyncClient().post(request);
    }

    /**
     * Implementation of HTTP PUT for data download
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param body a byte[] containing request body
     * @param stream an output stream where data will be streamed
     * @return a {@link Response} instance. Its body will be null.
     * @throws RestException if request couldn't be made
     * @see RestClient#put(String, byte[], OutputStream)
     */
    @Override
    public Response put(String url, byte[] body, OutputStream stream) throws RestException {
        withURL(url);
        withMethod(PUT);
        withBody(body);
        withOutputStream(stream);
        build();

        return request.getClients().getSyncClient().put(request);
    }

    /**
     * Implementation of HTTP PUT for data download
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param headers a {@link Headers} instance
     * @param body a byte[] containing request body
     * @param stream an output stream where data will be streamed
     * @return a {@link Response} instance. Its body will be null.
     * @throws RestException if request couldn't be made
     * @see RestClient#put(String, Headers, byte[], OutputStream)
     */
    @Override
    public Response put(String url, Headers headers, byte[] body, OutputStream stream) throws RestException {
        withURL(url);
        withMethod(PUT);
        withHeaders(headers);
        withBody(body);
        withOutputStream(stream);
        build();

        return request.getClients().getSyncClient().put(request);
    }

    /**
     * Async implementation of HTTP GET for data download
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param outputStream an output stream where data will be streamed
     * @return a Future wrapping a {@link Response}. Its body will be null.
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncGet(String, OutputStream)
     */
    @Override
    public Future<Response> asyncGet(String url, OutputStream outputStream) throws RestException {
        withURL(url);
        withMethod(GET);
        withOutputStream(outputStream);
        build();

        return request.getClients().getAsyncClient().asyncGet(request);
    }

    /**
     * Async implementation of HTTP GET for data download
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param headers a {@link Headers} instance
     * @param outputStream an output stream where data will be streamed
     * @return a Future wrapping a {@link Response}. Its body will be null.
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncGet(String, Headers, OutputStream)
     */
    @Override
    public Future<Response> asyncGet(String url, Headers headers, OutputStream outputStream) throws RestException {
        withURL(url);
        withMethod(GET);
        withHeaders(headers);
        withOutputStream(outputStream);
        build();

        return request.getClients().getAsyncClient().asyncGet(request);
    }

    /**
     * Async implementation of HTTP POST for data download
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param body a byte[] containing request body
     * @param outputStream an output stream where data will be streamed
     * @return a Future wrapping a {@link Response}. Its body will be null.
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncPost(String, byte[], OutputStream)
     */
    @Override
    public Future<Response> asyncPost(String url, byte[] body, OutputStream outputStream) throws RestException {
        withURL(url);
        withMethod(POST);
        withBody(body);
        withOutputStream(outputStream);
        build();

        return request.getClients().getAsyncClient().asyncPost(request);
    }

    /**
     * Async implementation of HTTP POST for data download
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param headers a {@link Headers} instance
     * @param body a byte[] containing request body
     * @param outputStream an output stream where data will be streamed
     * @return a Future wrapping a {@link Response}. Its body will be null.
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncPost(String, Headers, byte[], OutputStream)
     */
    @Override
    public Future<Response> asyncPost(String url, Headers headers, byte[] body, OutputStream outputStream) throws RestException {
        withURL(url);
        withMethod(POST);
        withHeaders(headers);
        withBody(body);
        withOutputStream(outputStream);
        build();

        return request.getClients().getAsyncClient().asyncPost(request);
    }

    /**
     * Async implementation of HTTP PUT for data download
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param body a byte[] containing request body
     * @param outputStream an output stream where data will be streamed
     * @return a Future wrapping a {@link Response}. Its body will be null.
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncPut(String, byte[], OutputStream)
     */
    @Override
    public Future<Response> asyncPut(String url, byte[] body, OutputStream outputStream) throws RestException {
        withURL(url);
        withMethod(PUT);
        withBody(body);
        withOutputStream(outputStream);
        build();

        return request.getClients().getAsyncClient().asyncPut(request);
    }

    /**
     * Async implementation of HTTP PUT for data download
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param headers a {@link Headers} instance
     * @param body a byte[] containing request body
     * @param outputStream an output stream where data will be streamed
     * @return a Future wrapping a {@link Response}. Its body will be null.
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncPut(String, Headers, byte[], OutputStream)
     */
    @Override
    public Future<Response> asyncPut(String url, Headers headers, byte[] body, OutputStream outputStream) throws RestException {
        withURL(url);
        withMethod(PUT);
        withHeaders(headers);
        withBody(body);
        withOutputStream(outputStream);
        build();

        return request.getClients().getAsyncClient().asyncPut(request);
    }

    /**
     * Async implementation of HTTP POST
     * @param url a full formed URL (o a URI if a base URL applies)
     * @return a Future wrapping a {@link Response}
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncPost(String)
     */
    @Override
    public Future<Response> asyncPost(String url) throws RestException {
        withURL(url);
        withMethod(POST);
        build();

        return request.getClients().getAsyncClient().asyncPost(request);
    }

    /**
     * Async implementation of HTTP POST
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param headers a {@link Headers} instance
     * @return a Future wrapping a {@link Response}
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncPost(String, Headers)
     */
    @Override
    public Future<Response> asyncPost(String url, Headers headers) throws RestException {
        withURL(url);
        withMethod(POST);
        withHeaders(headers);
        build();

        return request.getClients().getAsyncClient().asyncPost(request);
    }

    /**
     * Async implementation of HTTP PUT
     * @param url a full formed URL (o a URI if a base URL applies)
     * @return a Future wrapping a {@link Response}
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncPut(String)
     */
    @Override
    public Future<Response> asyncPut(String url) throws RestException {
        withURL(url);
        withMethod(PUT);
        build();

        return request.getClients().getAsyncClient().asyncPut(request);
    }

    /**
     * Async implementation of HTTP PUT
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param headers a {@link Headers} instance
     * @return a Future wrapping a {@link Response}
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncPut(String, Headers)
     */
    @Override
    public Future<Response> asyncPut(String url, Headers headers) throws RestException {
        withURL(url);
        withMethod(PUT);
        withHeaders(headers);
        build();

        return request.getClients().getAsyncClient().asyncPut(request);
    }

    /**
     * Implementation of HTTP POST
     * @param url a full formed URL (o a URI if a base URL applies)
     * @return a {@link Response} instance
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncPost(String)
     */
    @Override
    public Response post(String url) throws RestException {
        withURL(url);
        withMethod(POST);
        build();

        return request.getClients().getSyncClient().post(request);
    }

    /**
     * Implementation of HTTP POST
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param headers a {@link Headers} instance
     * @return a {@link Response} instance
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncPost(String, Headers)
     */
    @Override
    public Response post(String url, Headers headers) throws RestException {
        withURL(url);
        withMethod(POST);
        withHeaders(headers);
        build();

        return request.getClients().getSyncClient().post(request);
    }

    /**
     * Implementation of HTTP PUT
     * @param url a full formed URL (o a URI if a base URL applies)
     * @return a {@link Response} instance
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncPut(String)
     */
    @Override
    public Response put(String url) throws RestException {
        withURL(url);
        withMethod(PUT);
        build();

        return request.getClients().getSyncClient().put(request);
    }

    /**
     * Implementation of HTTP PUT
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param headers a {@link Headers} instance
     * @return a {@link Response} instance
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncPut(String, Headers)
     */
    @Override
    public Response put(String url, Headers headers) throws RestException {
        withURL(url);
        withMethod(PUT);
        withHeaders(headers);
        build();

        return request.getClients().getSyncClient().put(request);
    }

    /**
     * Async implementation of HTTP POST for data download
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param outputStream an output stream where data will be streamed
     * @return a Future wrapping a {@link Response}. Its body will be null.
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncPost(String, OutputStream)
     */
    @Override
    public Future<Response> asyncPost(String url, OutputStream outputStream) throws RestException {
        withURL(url);
        withMethod(POST);
        withOutputStream(outputStream);
        build();

        return request.getClients().getAsyncClient().asyncPost(request);
    }

    /**
     * Async implementation of HTTP POST for data download
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param headers a {@link Headers} instance
     * @param outputStream an output stream where data will be streamed
     * @return a Future wrapping a {@link Response}. Its body will be null.
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncPost(String, Headers, OutputStream)
     */
    @Override
    public Future<Response> asyncPost(String url, Headers headers, OutputStream outputStream) throws RestException {
        withURL(url);
        withMethod(POST);
        withHeaders(headers);
        withOutputStream(outputStream);
        build();

        return request.getClients().getAsyncClient().asyncPost(request);
    }

    /**
     * Async implementation of HTTP PUT for data download
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param outputStream an output stream where data will be streamed
     * @return a Future wrapping a {@link Response}. Its body will be null.
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncPut(String, OutputStream)
     */
    @Override
    public Future<Response> asyncPut(String url, OutputStream outputStream) throws RestException {
        withURL(url);
        withMethod(PUT);
        withOutputStream(outputStream);
        build();

        return request.getClients().getAsyncClient().asyncPut(request);
    }

    /**
     * Async implementation of HTTP PUT for data download
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param headers a {@link Headers} instance
     * @param outputStream an output stream where data will be streamed
     * @return a Future wrapping a {@link Response}. Its body will be null.
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncPut(String, Headers, OutputStream)
     */
    @Override
    public Future<Response> asyncPut(String url, Headers headers, OutputStream outputStream) throws RestException {
        withURL(url);
        withMethod(PUT);
        withHeaders(headers);
        withOutputStream(outputStream);
        build();

        return request.getClients().getAsyncClient().asyncPut(request);
    }

    /**
     * Implementation of HTTP POST for data download
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param stream an output stream where data will be streamed
     * @return a {@link Response} instance. Its body will be null.
     * @throws RestException if request couldn't be made
     * @see RestClient#post(String, OutputStream)
     */
    @Override
    public Response post(String url, OutputStream stream) throws RestException {
        withURL(url);
        withMethod(POST);
        withOutputStream(stream);
        build();

        return request.getClients().getSyncClient().post(request);
    }

    /**
     * Implementation of HTTP POST for data download
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param headers a {@link Headers} instance
     * @param stream an output stream where data will be streamed
     * @return a {@link Response} instance. Its body will be null.
     * @throws RestException if request couldn't be made
     * @see RestClient#post(String, Headers, OutputStream)
     */
    @Override
    public Response post(String url, Headers headers, OutputStream stream) throws RestException {
        withURL(url);
        withMethod(POST);
        withHeaders(headers);
        withOutputStream(stream);
        build();

        return request.getClients().getSyncClient().post(request);
    }

    /**
     * Implementation of HTTP PUT for data download
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param stream an output stream where data will be streamed
     * @return a {@link Response} instance. Its body will be null.
     * @throws RestException if request couldn't be made
     * @see RestClient#put(String, OutputStream)
     */
    @Override
    public Response put(String url, OutputStream stream) throws RestException {
        withURL(url);
        withMethod(PUT);
        withOutputStream(stream);
        build();

        return request.getClients().getSyncClient().put(request);
    }

    /**
     * Implementation of HTTP PUT for data download
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param headers a {@link Headers} instance
     * @param stream an output stream where data will be streamed
     * @return a {@link Response} instance. Its body will be null.
     * @throws RestException if request couldn't be made
     * @see RestClient#put(String, Headers, OutputStream)
     */
    @Override
    public Response put(String url, Headers headers, OutputStream stream) throws RestException {
        withURL(url);
        withMethod(PUT);
        withHeaders(headers);
        withOutputStream(stream);
        build();

        return request.getClients().getSyncClient().put(request);
    }

    /**
     * Async implementation of HTTP GET with callback
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncGet(String, Callback)
     */
    @Override
    public void asyncGet(String url, Callback<Response> callback) throws RestException {
        withURL(url);
        withMethod(GET);
        build();

        request.getClients().getAsyncClient().asyncGet(request, callback);
    }

    /**
     * Async implementation of HTTP GET with callback
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param headers a {@link Headers} instance
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncGet(String, Headers, Callback)
     */
    @Override
    public void asyncGet(String url, Headers headers, Callback<Response> callback) throws RestException {
        withURL(url);
        withMethod(GET);
        withHeaders(headers);
        build();

        request.getClients().getAsyncClient().asyncGet(request, callback);
    }

    /**
     * Async implementation of HTTP POST with callback
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncPost(String, Callback)
     */
    @Override
    public void asyncPost(String url, Callback<Response> callback) throws RestException {
        withURL(url);
        withMethod(POST);
        build();

        request.getClients().getAsyncClient().asyncPost(request, callback);
    }

    /**
     * Async implementation of HTTP POST with callback
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param headers a {@link Headers} instance
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncPost(String, Headers, Callback)
     */
    @Override
    public void asyncPost(String url, Headers headers, Callback<Response> callback) throws RestException {
        withURL(url);
        withMethod(POST);
        withHeaders(headers);
        build();

        request.getClients().getAsyncClient().asyncPost(request, callback);
    }

    /**
     * Async implementation of HTTP POST with callback
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param body a byte[] containing request body
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncPost(String, byte[], Callback)
     */
    @Override
    public void asyncPost(String url, byte[] body, Callback<Response> callback) throws RestException {
        withURL(url);
        withMethod(POST);
        withBody(body);
        build();

        request.getClients().getAsyncClient().asyncPost(request, callback);
    }

    /**
     * Async implementation of HTTP POST with callback
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param headers a {@link Headers} instance
     * @param body a byte[] containing request body
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncPost(String, Headers, byte[], Callback)
     */
    @Override
    public void asyncPost(String url, Headers headers, byte[] body, Callback<Response> callback) throws RestException {
        withURL(url);
        withMethod(POST);
        withHeaders(headers);
        withBody(body);
        build();

        request.getClients().getAsyncClient().asyncPost(request, callback);
    }

    /**
     * Async implementation of HTTP PUT with callback
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncPut(String, Callback)
     */
    @Override
    public void asyncPut(String url, Callback<Response> callback) throws RestException {
        withURL(url);
        withMethod(PUT);
        build();

        request.getClients().getAsyncClient().asyncPut(request, callback);
    }

    /**
     * Async implementation of HTTP PUT with callback
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param headers a {@link Headers} instance
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncPut(String, Headers, Callback)
     */
    @Override
    public void asyncPut(String url, Headers headers, Callback<Response> callback) throws RestException {
        withURL(url);
        withMethod(PUT);
        withHeaders(headers);
        build();

        request.getClients().getAsyncClient().asyncPut(request, callback);
    }

    /**
     * Async implementation of HTTP PUT with callback
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param body a byte[] containing request body
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncPut(String, byte[], Callback)
     */
    @Override
    public void asyncPut(String url, byte[] body, Callback<Response> callback) throws RestException {
        withURL(url);
        withMethod(PUT);
        withBody(body);
        build();

        request.getClients().getAsyncClient().asyncPut(request, callback);
    }

    /**
     * Async implementation of HTTP PUT with callback
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param headers a {@link Headers} instance
     * @param body a byte[] containing request body
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncPut(String, Headers, byte[], Callback)
     */
    @Override
    public void asyncPut(String url, Headers headers, byte[] body, Callback<Response> callback) throws RestException {
        withURL(url);
        withMethod(PUT);
        withHeaders(headers);
        withBody(body);
        build();

        request.getClients().getAsyncClient().asyncPut(request, callback);
    }

    /**
     * Async implementation of HTTP DELETE with callback
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncDelete(String, Callback)
     */
    @Override
    public void asyncDelete(String url, Callback<Response> callback) throws RestException {
        withURL(url);
        withMethod(DELETE);
        build();

        request.getClients().getAsyncClient().asyncDelete(request, callback);
    }

    /**
     * Async implementation of HTTP DELETE with callback
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param headers a {@link Headers} instance
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncDelete(String, Headers, Callback)
     */
    @Override
    public void asyncDelete(String url, Headers headers, Callback<Response> callback) throws RestException {
        withURL(url);
        withMethod(DELETE);
        withHeaders(headers);
        build();

        request.getClients().getAsyncClient().asyncDelete(request, callback);
    }

    /**
     * Async implementation of HTTP HEAD with callback
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncHead(String, Callback)
     */
    @Override
    public void asyncHead(String url, Callback<Response> callback) throws RestException {
        withURL(url);
        withMethod(HEAD);
        build();

        request.getClients().getAsyncClient().asyncHead(request, callback);
    }

    /**
     * Async implementation of HTTP HEAD with callback
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param headers a {@link Headers} instance
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncHead(String, Headers, Callback)
     */
    @Override
    public void asyncHead(String url, Headers headers, Callback<Response> callback) throws RestException {
        withURL(url);
        withMethod(HEAD);
        withHeaders(headers);
        build();

        request.getClients().getAsyncClient().asyncHead(request, callback);
    }

    /**
     * Async implementation of HTTP OPTIONS with callback
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncOptions(String, Callback)
     */
    @Override
    public void asyncOptions(String url, Callback<Response> callback) throws RestException {
        withURL(url);
        withMethod(OPTIONS);
        build();

        request.getClients().getAsyncClient().asyncOptions(request, callback);
    }

    /**
     * Async implementation of HTTP OPTIONS with callback
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param headers a {@link Headers} instance
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncOptions(String, Headers, Callback)
     */
    @Override
    public void asyncOptions(String url, Headers headers, Callback<Response> callback) throws RestException {
        withURL(url);
        withMethod(OPTIONS);
        withHeaders(headers);
        build();

        request.getClients().getAsyncClient().asyncOptions(request, callback);
    }

    /**
     * Async implementation of HTTP PURGE with callback
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncPurge(String, Callback)
     */
    @Override
    public void asyncPurge(String url, Callback<Response> callback) throws RestException {
        withURL(url);
        withMethod(PURGE);
        build();

        request.getClients().getAsyncClient().asyncPurge(request, callback);
    }

    /**
     * Async implementation of HTTP PURGE with callback
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param headers a {@link Headers} instance
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncPurge(String, Headers, Callback)
     */
    @Override
    public void asyncPurge(String url, Headers headers, Callback<Response> callback) throws RestException {
        withURL(url);
        withMethod(PURGE);
        withHeaders(headers);
        build();

        request.getClients().getAsyncClient().asyncPurge(request, callback);
    }

    /**
     * Async implementation of HTTP GET for data download, with callback
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param outputStream an output stream where data will be streamed
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncGet(String, OutputStream, Callback)
     */
    @Override
    public void asyncGet(String url, OutputStream outputStream, Callback<Response> callback) throws RestException {
        withURL(url);
        withMethod(GET);
        withOutputStream(outputStream);
        build();

        request.getClients().getAsyncClient().asyncGet(request, callback);
    }

    /**
     * Async implementation of HTTP GET for data download, with callback
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param headers a {@link Headers} instance
     * @param outputStream an output stream where data will be streamed
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncGet(String, Headers, OutputStream, Callback)
     */
    @Override
    public void asyncGet(String url, Headers headers, OutputStream outputStream, Callback<Response> callback) throws RestException {
        withURL(url);
        withMethod(GET);
        withHeaders(headers);
        withOutputStream(outputStream);
        build();

        request.getClients().getAsyncClient().asyncGet(request, callback);
    }

    /**
     * Async implementation of HTTP POST for data download, with callback
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param outputStream an output stream where data will be streamed
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncPost(String, OutputStream, Callback)
     */
    @Override
    public void asyncPost(String url, OutputStream outputStream, Callback<Response> callback) throws RestException {
        withURL(url);
        withMethod(POST);
        withOutputStream(outputStream);
        build();

        request.getClients().getAsyncClient().asyncPost(request, callback);
    }

    /**
     * Async implementation of HTTP POST for data download, with callback
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param headers a {@link Headers} instance
     * @param outputStream an output stream where data will be streamed
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncPost(String, Headers, OutputStream, Callback)
     */
    @Override
    public void asyncPost(String url, Headers headers, OutputStream outputStream, Callback<Response> callback) throws RestException {
        withURL(url);
        withMethod(POST);
        withHeaders(headers);
        withOutputStream(outputStream);
        build();

        request.getClients().getAsyncClient().asyncPost(request, callback);
    }

    /**
     * Async implementation of HTTP POST for data download, with callback
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param body a byte[] containing request body
     * @param outputStream an output stream where data will be streamed
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncPost(String, byte[], OutputStream, Callback)
     */
    @Override
    public void asyncPost(String url, byte[] body, OutputStream outputStream, Callback<Response> callback) throws RestException {
        withURL(url);
        withMethod(POST);
        withBody(body);
        withOutputStream(outputStream);
        build();

        request.getClients().getAsyncClient().asyncPost(request, callback);
    }

    /**
     * Async implementation of HTTP POST for data download, with callback
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param headers a {@link Headers} instance
     * @param body a byte[] containing request body
     * @param outputStream an output stream where data will be streamed
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncPost(String, Headers, byte[], OutputStream, Callback)
     */
    @Override
    public void asyncPost(String url, Headers headers, byte[] body, OutputStream outputStream, Callback<Response> callback) throws RestException {
        withURL(url);
        withMethod(POST);
        withHeaders(headers);
        withBody(body);
        withOutputStream(outputStream);
        build();

        request.getClients().getAsyncClient().asyncPost(request, callback);
    }

    /**
     * Async implementation of HTTP PUT for data download, with callback
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param outputStream an output stream where data will be streamed
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncPut(String, OutputStream, Callback)
     */
    @Override
    public void asyncPut(String url, OutputStream outputStream, Callback<Response> callback) throws RestException {
        withURL(url);
        withMethod(PUT);
        withOutputStream(outputStream);
        build();

        request.getClients().getAsyncClient().asyncPut(request, callback);
    }

    /**
     * Async implementation of HTTP PUT for data download, with callback
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param headers a {@link Headers} instance
     * @param outputStream an output stream where data will be streamed
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncPut(String, Headers, OutputStream, Callback)
     */
    @Override
    public void asyncPut(String url, Headers headers, OutputStream outputStream, Callback<Response> callback) throws RestException {
        withURL(url);
        withMethod(PUT);
        withHeaders(headers);
        withOutputStream(outputStream);
        build();

        request.getClients().getAsyncClient().asyncPut(request, callback);
    }

    /**
     * Async implementation of HTTP PUT for data download, with callback
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param body a byte[] containing request body
     * @param outputStream an output stream where data will be streamed
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncPut(String, byte[], OutputStream, Callback)
     */
    @Override
    public void asyncPut(String url, byte[] body, OutputStream outputStream, Callback<Response> callback) throws RestException {
        withURL(url);
        withMethod(PUT);
        withBody(body);
        withOutputStream(outputStream);
        build();

        request.getClients().getAsyncClient().asyncPut(request, callback);
    }

    /**
     * Async implementation of HTTP PUT for data download, with callback
     * @param url a full formed URL (o a URI if a base URL applies)
     * @param headers a {@link Headers} instance
     * @param body a byte[] containing request body
     * @param outputStream an output stream where data will be streamed
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException if request couldn't be made
     * @see RestClient#asyncPut(String, Headers, byte[], OutputStream, Callback)
     */
    @Override
    public void asyncPut(String url, Headers headers, byte[] body, OutputStream outputStream, Callback<Response> callback) throws RestException {
        withURL(url);
        withMethod(PUT);
        withHeaders(headers);
        withBody(body);
        withOutputStream(outputStream);
        build();

        request.getClients().getAsyncClient().asyncPut(request, callback);
    }

    /**
     * Async implementation of HTTP GET with callback. Assumes that URL has been set for this request.
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException if request couldn't be made
     */
    public void asyncGet(Callback<Response> callback) throws RestException {
        withMethod(GET);
        build();
        request.getClients().getAsyncClient().asyncGet(request, callback);
    }

    /**
     * Async implementation of HTTP POST with callback. Assumes that URL has been set for this request.
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException if request couldn't be made
     */
    public void asyncPost(Callback<Response> callback) throws RestException {
        withMethod(POST);
        build();
        request.getClients().getAsyncClient().asyncPost(request, callback);
    }

    /**
     * Async implementation of HTTP PUT with callback. Assumes that URL has been set for this request.
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException if request couldn't be made
     */
    public void asyncPut(Callback<Response> callback) throws RestException {
        withMethod(PUT);
        build();
        request.getClients().getAsyncClient().asyncPut(request, callback);
    }

    /**
     * Async implementation of HTTP DELETE with callback. Assumes that URL has been set for this request.
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException if request couldn't be made
     */
    public void asyncDelete(Callback<Response> callback) throws RestException {
        withMethod(DELETE);
        build();
        request.getClients().getAsyncClient().asyncDelete(request, callback);
    }

    /**
     * Async implementation of HTTP HEAD with callback. Assumes that URL has been set for this request.
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException if request couldn't be made
     */
    public void asyncHead(Callback<Response> callback) throws RestException {
        withMethod(HEAD);
        build();
        request.getClients().getAsyncClient().asyncHead(request, callback);
    }

    /**
     * Async implementation of HTTP OPTIONS with callback. Assumes that URL has been set for this request.
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException if request couldn't be made
     */
    public void asyncOptions(Callback<Response> callback) throws RestException {
        withMethod(OPTIONS);
        build();
        request.getClients().getAsyncClient().asyncOptions(request, callback);
    }

    /**
     * Async implementation of HTTP PURGE with callback. Assumes that URL has been set for this request.
     * @param callback a {@link Callback} to be called upon completion
     * @throws RestException if request couldn't be made
     */
    public void asyncPurge(Callback<Response> callback) throws RestException {
        withMethod(PURGE);
        build();
        request.getClients().getAsyncClient().asyncPurge(request, callback);
    }

}