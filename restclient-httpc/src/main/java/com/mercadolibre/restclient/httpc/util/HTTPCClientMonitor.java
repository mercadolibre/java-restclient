package com.mercadolibre.restclient.httpc.util;

import com.mercadolibre.metrics.Metrics;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.pool.ConnPoolControl;
import org.apache.http.pool.PoolStats;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public abstract class HTTPCClientMonitor implements Runnable, Closeable {

    private final String poolName;
    private final ConnPoolControl<HttpRoute> pool;
    private final ScheduledFuture<?> future;

    public HTTPCClientMonitor(String poolName, ConnPoolControl<HttpRoute> pool) {
        this.poolName = poolName;
        this.pool = pool;

        this.future = HTTPCClientMonitorExecutor.pool.scheduleAtFixedRate(this, 5, 30, TimeUnit.SECONDS);
    }

    public void run() {
        PoolStats stats = pool.getTotalStats();

        Metrics.INSTANCE.count("restclient.httpc.pool.available", stats.getAvailable(), "rest_pool:" + poolName, "type:" + getType());
        Metrics.INSTANCE.count("restclient.httpc.pool.leased", stats.getLeased(), "rest_pool:" + poolName, "type:" + getType());
        Metrics.INSTANCE.count("restclient.httpc.pool.pending", stats.getPending(), "rest_pool:" + poolName, "type:" + getType());
    }

    @Override
    public void close() throws IOException {
        future.cancel(true);
    }

    protected abstract String getType();

}
