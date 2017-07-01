package com.mercadolibre.restclient.retry;

import com.mercadolibre.restclient.Request;
import com.mercadolibre.restclient.Response;
import com.mercadolibre.restclient.http.HttpMethod;

import java.net.HttpURLConnection;
import java.util.Random;
import java.util.Set;

/**
 * A retry strategy implementing exponential backoff. It'll increase wait time exponentially between a specified min wait time, until it exceeds an also specified max wait time.
 * Every new retry is computed as a random number between [(1-factor)*r, (1+factor)*r], where r = min * multiplier^retries
 */
public class ExponentialBackoffRetryStrategy implements RetryStrategy {

    public static final double DEFAULT_MULTIPLIER = 2;
    public static final double DEFAULT_FACTOR = 0.2;

    private long min;
    private long max;
    private double factor = DEFAULT_FACTOR;
    private double multiplier = DEFAULT_MULTIPLIER;
    private Set<HttpMethod> allowedMethods;
    private Random r;

    public ExponentialBackoffRetryStrategy(long min, long max) {
        if (min <= 0 || max <= 0 || max <= min)
            throw new IllegalArgumentException("min and max must comply with 0 < min < max");

        this.allowedMethods = DEFAULT_RETRIABLE_METHODS;
        this.r = new Random();
    	
        this.min = min;
        this.max = max;
    }

    public ExponentialBackoffRetryStrategy(long min, long max, Set<HttpMethod> allowedMethods) {
        this(min, max);
    	
    	this.allowedMethods = allowedMethods;
    }

    public ExponentialBackoffRetryStrategy(long min, long max, double factor) {
        this(min, max);
    	
    	this.factor = factor;
    }

    public ExponentialBackoffRetryStrategy(long min, long max, double factor, Set<HttpMethod> allowedMethods) {
        this(min, max, allowedMethods);
    	
    	this.factor = factor;
    }

    public ExponentialBackoffRetryStrategy(long min, long max, double factor, double multiplier) {
        this(min, max, factor);
    	
    	this.multiplier = multiplier;
    }

    public ExponentialBackoffRetryStrategy(long min, long max, double factor, double multiplier, Set<HttpMethod> allowedMethods) {
        this(min, max, factor, multiplier);
    	
    	this.allowedMethods = allowedMethods;
    }

    @Override
    public RetryResponse shouldRetry(Request req, Response r, Exception e, int retries) {
        long delay = 0L;
        boolean retry = (e != null || shouldRetryCode(r.getStatus())) && allowedMethods.contains(req.getMethod());
        if (retry) {
            delay = getDelay(retries);
            retry = delay <= max;
        }

        return new RetryResponse(retry,delay);
    }

    private boolean shouldRetryCode(int code) {
        return code >= HttpURLConnection.HTTP_INTERNAL_ERROR;
    }

    private long getFromInterval(double a, double b) {
        return new Double(r.nextDouble() * (b-a) + a).longValue();
    }

    private long getDelay(int retries) {
        double interval = min * Math.pow(multiplier,retries);
        return getFromInterval((1-factor)*interval, (1+factor)*interval);
    }

    public long getMin() {
        return min;
    }

    public long getMax() {
        return max;
    }

    public double getFactor() {
        return factor;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public Set<HttpMethod> getAllowedMethods() {
        return allowedMethods;
    }
}
