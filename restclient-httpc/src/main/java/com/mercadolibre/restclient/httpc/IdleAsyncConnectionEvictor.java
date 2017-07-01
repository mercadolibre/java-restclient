package com.mercadolibre.restclient.httpc;

import org.apache.http.nio.conn.NHttpClientConnectionManager;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import static com.mercadolibre.restclient.log.LogUtil.log;


public final class IdleAsyncConnectionEvictor {

    private final Thread thread;
    private final long sleepTimeMs;
    private final long maxIdleTimeMs;

    public IdleAsyncConnectionEvictor(
            final NHttpClientConnectionManager connectionManager,
            final ThreadFactory threadFactory,
            final long sleepTime, final TimeUnit sleepTimeUnit,
            final long maxIdleTime, final TimeUnit maxIdleTimeUnit) {
        ThreadFactory threadFactory1 = threadFactory != null ? threadFactory : new DefaultThreadFactory();
        this.sleepTimeMs = sleepTimeUnit != null ? sleepTimeUnit.toMillis(sleepTime) : sleepTime;
        this.maxIdleTimeMs = maxIdleTimeUnit != null ? maxIdleTimeUnit.toMillis(maxIdleTime) : maxIdleTime;
        this.thread = threadFactory1.newThread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        Thread.sleep(sleepTimeMs);
                        connectionManager.closeExpiredConnections();
                        if (maxIdleTimeMs > 0) {
                            connectionManager.closeIdleConnections(maxIdleTimeMs, TimeUnit.MILLISECONDS);
                        }
                    }
                } catch (final Exception ex) {
                    log.warn("Evictor exception", ex);
                }
            }
        });
    }

    public IdleAsyncConnectionEvictor(
            final NHttpClientConnectionManager connectionManager,
            final long sleepTime, final TimeUnit sleepTimeUnit,
            final long maxIdleTime, final TimeUnit maxIdleTimeUnit) {
        this(connectionManager, null, sleepTime, sleepTimeUnit, maxIdleTime, maxIdleTimeUnit);
    }

    public IdleAsyncConnectionEvictor(
            final NHttpClientConnectionManager connectionManager,
            final long maxIdleTime, final TimeUnit maxIdleTimeUnit) {
        this(connectionManager, null,
                maxIdleTime > 0 ? maxIdleTime : 5, maxIdleTimeUnit != null ? maxIdleTimeUnit : TimeUnit.SECONDS,
                maxIdleTime, maxIdleTimeUnit);
    }

    public void start() {
        thread.start();
    }

    public void shutdown() {
        thread.interrupt();
    }

    public boolean isRunning() {
        return thread.isAlive();
    }

    public void awaitTermination(final long time, final TimeUnit tunit) throws InterruptedException {
        thread.join((tunit != null ? tunit : TimeUnit.MILLISECONDS).toMillis(time));
    }

    static class DefaultThreadFactory implements ThreadFactory {

        @Override
        public Thread newThread(final Runnable r) {
            final Thread t = new Thread(r, "Connection evictor");
            t.setDaemon(true);
            return t;
        }

    }

}
