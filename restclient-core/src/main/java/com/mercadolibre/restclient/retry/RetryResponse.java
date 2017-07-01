package com.mercadolibre.restclient.retry;

/**
 * An utility class used by {@link RetryStrategy} to tell the client if it should retry a request and what action to take before that.
 */
public class RetryResponse {

    private boolean retry;
    private long delay;

    public RetryResponse(boolean retry, long delay) {
        this.retry = retry;
        this.delay = delay;
    }

    public RetryResponse(boolean retry) {
        this.retry = retry;
        this.delay = 0L;
    }

    /**
     * Tells the client if a request should be retried
     * @return true if retry should be attemped
     */
    public boolean retry() {
        return retry;
    }

    /**
     * Returns a delay time the client should wait, before attempting next retry
     * @return delay time in ms
     */
    public long getDelay() {
        return delay;
    }

}
