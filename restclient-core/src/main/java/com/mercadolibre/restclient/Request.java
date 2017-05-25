package com.mercadolibre.restclient;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mercadolibre.restclient.cache.RESTCache;
import com.mercadolibre.restclient.exception.RestException;
import com.mercadolibre.restclient.http.Authentication;
import com.mercadolibre.restclient.http.Header;
import com.mercadolibre.restclient.http.Headers;
import com.mercadolibre.restclient.http.HttpMethod;
import com.mercadolibre.restclient.http.Proxy;
import com.mercadolibre.restclient.interceptor.RequestInterceptor;
import com.mercadolibre.restclient.interceptor.ResponseInterceptor;
import com.mercadolibre.restclient.multipart.MultipartMode;
import com.mercadolibre.restclient.multipart.Part;
import com.mercadolibre.restclient.retry.RetryStrategy;
import com.mercadolibre.restclient.util.HttpCompressionHandler;
import org.apache.commons.lang3.StringUtils;

public class Request {

    private static final Pattern URI_PATTERN = Pattern.compile("^https?://[^/]+([^?]*).*$");

    private String url;
    private Map<String, String> parameters;
    private Headers headers;
    private byte[] body;
    private HttpMethod method;
    private Proxy proxy;
    private Authentication authentication;
	private ClientHolder.Clients<?> clients;
    private Cache cache;
    private RetryStrategy retryStrategy;
    private Deque<RequestInterceptor> requestInterceptors;
    private Deque<ResponseInterceptor> responseInterceptors;
    private Map<String,Object> attributes;
    private Download download;
    private Set<Part<?>> parts;
    private MultipartMode multipartMode;

    protected Request() {
    	this.headers = new Headers();
    	this.parameters = new HashMap<>();
        this.attributes = new HashMap<>();
        this.cache = new Cache();
        this.download = new Download();
        this.requestInterceptors = new LinkedList<>();
        this.responseInterceptors = new LinkedList<>();
    }

    public String getURL() {
        StringBuilder url = new StringBuilder(this.url);
    	
        if (!parameters.isEmpty()) {
            url.append("?");

        	for (Map.Entry<String, String> parameter : parameters.entrySet())
                url.append(parameter.getKey()).append("=").append(parameter.getValue()).append("&");

            url.setLength(url.length() - 1);
        }

        return url.toString();
    }

    public String getPlainURL() {
        return url;
    }

    public String getURI() {
        Matcher m = URI_PATTERN.matcher(url);
        return m.matches() ? m.group(1) : url;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public String getParameter(String key) {
        return parameters.get(key);
    }
    
    public Headers getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }

    public Proxy getProxy() {
        return proxy;
    }
    
    public Authentication getAuthentication() {
        return authentication;
    }

    public RESTCache getCache() {
    	return cache.getRaw();
    }

    public boolean isCacheable() {
    	return !isDownload() && cache.getRaw() != null && !cache.isByPass();
    }
    
    public boolean hasCache() {
    	return cache.getRaw() != null;
    }
    
    public HttpMethod getMethod() {
        return method;
    }

	@SuppressWarnings("unchecked")
	public <R> ClientHolder.Clients<R> getClients() {
        return (ClientHolder.Clients<R>) clients;
    }

    public RESTPool getPool() {
        return clients != null ? clients.getPool() : null;
    }

    public RetryStrategy getRetryStrategy() {
        return retryStrategy;
    }

    public Deque<RequestInterceptor> getRequestInterceptors() {
        return requestInterceptors;
    }

    public Deque<ResponseInterceptor> getResponseInterceptors() {
        return responseInterceptors;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    public OutputStream getOutputStream() {
        return this.download.getOutputStream();
    }
    
    public boolean isDownload() {
    	return this.download.getOutputStream() != null;
    }

	public Set<Part<?>> getParts() {
        return parts;
    }

    public MultipartMode getMultipartMode() {
        return multipartMode;
    }

    protected void setURL(String url) {
        if (url.contains("?")) {
            String[] split = url.split("\\?");
            if (split.length == 2) fillParameters(split[1]);

            this.url = split[0];
        } else
            this.url = url;
    }

    private void fillParameters(String raw) {
        for (String pair : raw.split("&")) {
            String[] p = pair.split("=");
            if (StringUtils.isNotBlank(p[0]))
                parameters.put(p[0], p.length == 2 ? p[1] : "");
        }
    }
    
    public void setParameter(String key, String value) {
        parameters.put(key, value);
    }

    protected void setHeaders(Headers headers) {
        this.headers = headers;
    }

    protected void setBody(byte[] body) {
        if (this.parts != null)
            throw new IllegalArgumentException("Parts have already been defined for this request");

        this.body = body;
    }

    protected void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }
    
    protected void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

    protected void setCache(RESTCache cache) {
    	this.cache.setRaw(cache);
    }
    
    public void byPassCache(boolean byPass) {
    	this.cache.setByPass(byPass);
    }
    
    protected void setMethod(HttpMethod method) {
        this.method = method;
    }

	protected void setClients(ClientHolder.Clients<?> clients) {
        this.clients = clients;
    }

    protected void setRetryStrategy(RetryStrategy retryStrategy) {
        this.retryStrategy = retryStrategy;
    }

    protected void setRequestInterceptors(Deque<RequestInterceptor> requestInterceptors) {
        this.requestInterceptors = requestInterceptors;
    }

    protected void setResponseInterceptors(Deque<ResponseInterceptor> responseInterceptors) {
        this.responseInterceptors = responseInterceptors;
    }

    protected void setAttributes(Map<String,Object> attributes) {
        this.attributes = attributes;
    }

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public void setOutputStream(OutputStream outputStream) {
        if (download.getOutputStream() != null)
            throw new IllegalArgumentException("Output stream already defined");

        download.setOutputStream(outputStream);
    }
    
    public void populateOutputStream(InputStream inputStream, Header contentEncoding) throws RestException {
    	download.populate(inputStream, contentEncoding, clients.getPool().compression());
    }
    
	protected void setParts(Set<Part<?>> parts) {
        if (body != null)
            throw new IllegalArgumentException("A body has already been defined for this request");

        this.parts = parts;
    }

	protected void setPart(Part<?> part) {
        if (parts == null)
            setParts(new HashSet<Part<?>>());

        parts.add(part);
    }

    protected void setMultipartMode(MultipartMode multipartMode) {
        this.multipartMode = multipartMode;
    }

    protected void addInterceptorFirst(RequestInterceptor i) {
        requestInterceptors.addFirst(i);
    }

    protected void addInterceptorLast(RequestInterceptor i) {
        requestInterceptors.addLast(i);
    }

    protected void addInterceptorFirst(ResponseInterceptor i) {
        responseInterceptors.addFirst(i);
    }

    protected void addInterceptorLast(ResponseInterceptor i) {
        responseInterceptors.addLast(i);
    }

    public void applyRequestInterceptors() {
        for (RequestInterceptor i : requestInterceptors)
            i.intercept(this);
    }

    public void applyResponseInterceptors(Response r) {
        for (ResponseInterceptor i : responseInterceptors)
            i.intercept(r);
    }

    public Request clone() {
        Request r = new Request();

        r.url = this.url;
        r.parameters = new HashMap<>(parameters);
        r.headers = headers.clone();
        r.body = body != null ? Arrays.copyOf(body, body.length) : null;
        r.method = method;
        r.proxy = proxy != null ? new Proxy(proxy.getHostname(), proxy.getPort(), proxy.getUsername(), proxy.getPassword()) : null;
        r.authentication = authentication != null ? new Authentication(authentication.getHostname(), authentication.getPort(), authentication.getUsername(), authentication.getPassword()) : null;
        r.clients = clients;
        r.cache = cache;
        r.retryStrategy = retryStrategy;
        r.requestInterceptors = new LinkedList<>(requestInterceptors);
        r.responseInterceptors = new LinkedList<>(responseInterceptors);
        r.attributes = new HashMap<>(attributes);
        r.download = download;
        r.parts = parts != null ? new HashSet<>(parts) : null;
        r.multipartMode = multipartMode;

        return r;
    }

    private static class Cache {

    	private RESTCache raw;
    	private boolean byPass;
    	
    	public Cache() {
    		super();
    	}

		public RESTCache getRaw() {
			return raw;
		}

		public void setRaw(RESTCache raw) {
			this.raw = raw;
		}

		public boolean isByPass() {
			return byPass;
		}

		public void setByPass(boolean byPass) {
			this.byPass = byPass;
		}
    	
    }
    
    private static class Download {
    	
    	private OutputStream outputStream;
    	
    	public Download() {
    		super();
    	}
		
		public OutputStream getOutputStream() {
			return outputStream;
		}

		public void setOutputStream(OutputStream outputStream) {
			this.outputStream = outputStream;
		}

		public void populate(InputStream inputStream, Header contentEncoding, boolean compression) throws RestException {
			HttpCompressionHandler.handleContent(inputStream, outputStream, contentEncoding, compression);
		}
		
    }

}