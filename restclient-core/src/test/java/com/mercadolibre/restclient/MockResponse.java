package com.mercadolibre.restclient;

import static com.google.common.base.Preconditions.checkArgument;
import com.mercadolibre.restclient.http.Header;
import com.mercadolibre.restclient.http.Headers;
import com.mercadolibre.restclient.http.HttpMethod;
import com.mercadolibre.restclient.mock.RequestMockHolder;

import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * A representation of a match between a {@link Request} and {@link Response} for mock purposes.
 * It also has a {@link com.mercadolibre.restclient.MockResponse.Counter} which keeps track of current request runs, so different responses could be applied. <p><p>
 *
 * Mocks are matched according to a specific criteria:
 * <ol>
 *  <li>
 *      <span>Match URL:</span>
 *      <ul>
 *          <li>Full URL match (with parameters)</li>
 *          <li>URL match without parameters</li>
 *      </ul>
 *  </li>
 *  <li>
 *      <span>Match headers</span>
 *  </li>
 *  <li>
 *      <span>Match body</span>
 *  </li>
 * </ol>
 * <p>
 *
 * Given a request, the matcher will traverse this list in order for each registered mock.
 * When a certain criterion does not match, it'll stop that particular mock iteration and continue with next one.
 * Notice that at least URL must match, in order for a mock to be considered as a response.<p>
 * Finally, it'll return the item that most matches had fulfilled, i.e. a mock that matched URL and headers will prevail over another which only matched URL.
 */
public class MockResponse {

    private Request request;
    private Response response;
    private Counter counter;
    private MockInterceptor interceptor;
    private Pattern pattern;
    private RequestProcessor processor;

    private MockResponse() {
        this.request = new Request();
        this.counter = new Counter();
    }

    /**
     * Returns a builder for a MockResponse
     * @return a {@link com.mercadolibre.restclient.MockResponse.Builder} instance
     */
    public static Builder builder() {
        return new Builder(new MockResponse());
    }

    /**
     * A counter to track a request sequence, so different responses could be applied to each one
     */
    public static class Counter {
        private int count = 0;
        private boolean alwaysFail = false;
        private Set<Integer> runsToFail = new HashSet<>();
        private int[] statuses;

        /**
         * Tells whether current request should fail
         * @return a boolean flag
         */
        public boolean shouldFail() {
            return shouldFail(count);
        }

        private boolean shouldFail(int count) {
            return alwaysFail || runsToFail.contains(count);
        }

        /**
         * Increments current run and tells if it should fail
         * @return a boolean flag
         */
        public boolean runAndShouldFail() {
            return shouldFail(++count);
        }

        /**
         * Returns current run status
         * @return an HTTP status code
         */
        public Integer getStatus() {
            return getStatus(count);
        }

        private Integer getStatus(int count) {
            if (statuses == null) return null;

            return count >= statuses.length ? statuses[statuses.length - 1] : statuses[count];
        }

        /**
         * Increments current run and return its status
         * @return an HTTP status code
         */
        public Integer runAndGetStatus() {
            return getStatus(count++);
        }

        /**
         * Increments run number
         * @return Run number before increment
         */
        public int run() {
            return count++;
        }

    }

    /**
     * Build and instance of {@link MockResponse}
     */
    public static class Builder {

        private MockResponse mock;
        private int statusCode;
        private Headers responseHeaders = new Headers();
        private byte[] body;

        private Builder(MockResponse mock) {
            this.mock = mock;
        }

        /**
         * Specifies an URL this mock should match
         * @param url a string representing a valid URL
         * @return this builder
         */
        public Builder withURL(String url) {
            checkArgument(mock.pattern == null, "An URL regex pattern has already been set for this mock");

            mock.request.setURL(url);
            return this;
        }

        /**
         * Specifies an URL pattern this mock should match
         * @param pattern a regex pattern
         * @return this builder
         */
        public Builder withURL(Pattern pattern) {
            checkArgument(mock.request.getPlainURL() == null, "An URL has already been set for this mock");

            mock.pattern = pattern;
            return this;
        }

        /**
         * Specifies an HTTP method this mock should match
         * @param method an HTTPMethod instance
         * @return this builder
         */
        public Builder withMethod(HttpMethod method) {
            mock.request.setMethod(method);
            return this;
        }

        /**
         * Specifies request headers this mock should match
         * @param headers a Headers instance
         * @return this builder
         */
        public Builder withRequestHeaders(Headers headers) {
            mock.request.setHeaders(headers);
            return this;
        }

        /**
         * Specifies a header this mock should match
         * @param h a Header instance
         * @return this builder
         */
        public Builder withRequestHeader(Header h) {
            mock.request.getHeaders().add(h);
            return this;
        }

        /**
         * Specifies a header this mock should match
         * @param name header name
         * @param value header value
         * @return this builder
         */
        public Builder withRequestHeader(String name, String value) {
            mock.request.getHeaders().add(new Header(name,value));
            return this;
        }

        /**
         * Specifies which headers should be returned for this mock response
         * @param responseHeaders a Headers instance
         * @return this builder
         */
        public Builder withResponseHeaders(Headers responseHeaders) {
            checkArgument(mock.processor == null, "Request processor has already been set. Response should be specified either dynamically or statically");

            this.responseHeaders = responseHeaders;
            return this;
        }

        /**
         * Specifies a body for this mock to add to response
         * @param body response body
         * @return this builder
         */
        public Builder withRequestBody(byte[] body) {
            mock.request.setBody(body);
            return this;
        }

        /**
         * Specifies a request body this mock should match
         * @param body request body
         * @param charset request body charset
         * @return this builder
         */
        public Builder withRequestBody(String body, Charset charset) {
            mock.request.setBody(body.getBytes(charset));
            return this;
        }

        /**
         * Specifies a request body this mock should match
         * @param body request body
         * @return this builder
         */
        public Builder withRequestBody(String body) {
            return withRequestBody(body, Charset.defaultCharset());
        }

        /**
         * Adds a header to this mock response
         * @param h a Header instance
         * @return this builder
         */
        public Builder withResponseHeader(Header h) {
            checkArgument(mock.processor == null, "Request processor has already been set. Response should be specified either dynamically or statically");

            responseHeaders.add(h);
            return this;
        }

        /**
         * Adds a header to this mock response
         * @param name header name
         * @param value header value
         * @return this builder
         */
        public Builder withResponseHeader(String name, String value) {
            checkArgument(mock.processor == null, "Request processor has already been set. Response should be specified either dynamically or statically");

            responseHeaders.add(new Header(name,value));
            return this;
        }

        /**
         * Specifies a status code to be returned by this mock response
         * @param statusCode an HTTP status code
         * @return this builder
         */
        public Builder withStatusCode(int statusCode) {
            checkArgument(mock.processor == null, "Request processor has already been set. Response should be specified either dynamically or statically");

            this.statusCode = statusCode;
            return this;
        }

        /**
         * Specifies multiple status code to be returned in successive calls to this mock. Last one is repeated when exhausted.
         * @param statusCodes an array of HTTP status codes
         * @return this builder
         */
        public Builder withStatusCodes(int... statusCodes) {
            checkArgument(mock.processor == null, "Request processor has already been set. Response should be specified either dynamically or statically");

            mock.counter.statuses = statusCodes;
            return this;
        }

        /**
         * Specifies a body to be returned for this mock
         * @param body a response body
         * @return this builder
         */
        public Builder withResponseBody(byte[] body) {
            checkArgument(mock.processor == null, "Request processor has already been set. Response should be specified either dynamically or statically");

            this.body = body;
            return this;
        }

        /**
         * Specifies a body to be returned for this mock
         * @param body a response body
         * @param charset response body charset
         * @return this builder
         */
        public Builder withResponseBody(String body, Charset charset) {
            checkArgument(mock.processor == null, "Request processor has already been set. Response should be specified either dynamically or statically");

            this.body = body.getBytes(charset);
            return this;
        }

        /**
         * Specifies a body to be returned for this mock, using system default charset
         * @param body a response body
         * @return this builder
         */
        public Builder withResponseBody(String body) {
            return withResponseBody(body, Charset.defaultCharset());
        }

        /**
         * Adds a {@link RequestProcessor} that will evaluate on each request and define a Response dynamically
         * @param processor a request processor
         * @return this builder
         */
        public Builder withRequestProcessor(RequestProcessor processor) {
            checkArgument(body == null && statusCode == 0 && mock.counter.statuses == null &&
                            responseHeaders.isEmpty() && !mock.counter.alwaysFail && mock.counter.runsToFail.isEmpty(),
                    "Response attribute has already been set. Response should be specified either dynamically or statically");

            mock.processor = processor;
            return this;
        }

        /**
         * Instructs this mock to fail with RestException upon request
         * @return this builder
         */
        public Builder shouldFail() {
            checkArgument(mock.processor == null, "Request processor has already been set. Response should be specified either dynamically or statically");

            mock.counter.alwaysFail = true;
            return this;
        }

        /**
         * Instructs this mock to fail at specific run
         * @param run a run number, beginning at 1
         * @return this builder
         */
        public Builder shouldFailAt(int run) {
            checkArgument(mock.processor == null, "Request processor has already been set. Response should be specified either dynamically or statically");

            mock.counter.runsToFail.add(run);
            return this;
        }

        /**
         * Instructs this mock to fail at specific runs
         * @param runs an array of runs, beginning at 1
         * @return this builder
         */
        public Builder shouldFailAt(int... runs) {
            for (int run : runs) shouldFailAt(run);
            return this;
        }

        /**
         * Instructs this mock to copy request body to response when invoked
         * @return this builder
         */
        public Builder echoBody() {
            if (mock.request.getBody() == null) throw new IllegalArgumentException("Request body is null");
            return withResponseBody(mock.request.getBody());
        }

        /**
         * Adds an interceptor to be called upon mock invocation
         * @param interceptor a {@link MockInterceptor} instance
         * @return this builder
         */
        public Builder withMockInterceptor(MockInterceptor interceptor) {
            mock.interceptor = interceptor;
            return this;
        }

        /**
         * Builds a {@link MockResponse}
         * @return a MockResponse instance
         */
        public MockResponse build() {
            checkArgument(mock.request.getPlainURL() != null || mock.pattern != null, "Either a full URL or a regex pattern must be defined");

            mock.response = !mock.counter.shouldFail() ? new Response(statusCode, responseHeaders, body) : null;

            RequestMockHolder.addMockResponse(mock);

            return mock;
        }
    }

    public Request getRequest() {
        return request;
    }

    public Response getResponse() {
        return response;
    }

    public Counter getCounter() {
        return counter;
    }

    public MockInterceptor getInterceptor() {
        return interceptor;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public RequestProcessor getProcessor() {
        return processor;
    }

    /**
     * Returns this mock counters' status, if any, and this mock {@link Response} status otherwise.
     * @return an HTTP status
     */
    public int getStatus() {
        Integer counterStatus = counter.getStatus();
        return counterStatus != null ? counterStatus : response.getStatus();
    }

    /**
     * Increments counter run and returns this mock counters' status, if any, and this mock {@link Response} status otherwise.
     * @return an HTTP status
     */
    public int runAndGetStatus() {
        Integer counterStatus = counter.runAndGetStatus();
        return counterStatus != null ? counterStatus : response.getStatus();
    }
}
