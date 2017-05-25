package com.mercadolibre.restclient.httpc.util;

import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.pool.ConnPoolControl;

public class HTTPCAsyncClientMonitor extends HTTPCClientMonitor {

    public HTTPCAsyncClientMonitor(String poolName, ConnPoolControl<HttpRoute> pool) {
        super(poolName, pool);
    }

    @Override
    protected String getType() {
        return "async";
    }

}
