package com.mercadolibre.restclient.httpc.util;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class HTTPCClientMonitorExecutor {
    protected static final ScheduledExecutorService pool = new ScheduledThreadPoolExecutor(1);
}
