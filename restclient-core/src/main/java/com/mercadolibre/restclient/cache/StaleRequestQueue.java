package com.mercadolibre.restclient.cache;

import com.mercadolibre.restclient.Request;
import com.mercadolibre.restclient.Response;
import com.mercadolibre.restclient.exception.RestException;
import com.mercadolibre.restclient.util.PoolMonitoring;
import java.util.concurrent.*;

import static com.mercadolibre.restclient.log.LogUtil.log;

/**
 * @author mlabarinas
 */
public class StaleRequestQueue {

    private static final int MAX_SIZE = 2000;

    private static final ExecutorService pool = new ThreadPoolExecutor(0, Runtime.getRuntime().availableProcessors(), 10000L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(MAX_SIZE), Executors.defaultThreadFactory(), new ThreadPoolExecutor.DiscardOldestPolicy());

    static {
        PoolMonitoring.INSTANCE.register("stale-request-queue", pool);
    }

    public static void enqueue(final Request r) {
        if (!r.hasCache()) return;

        pool.submit(new Runnable() {
            public void run() {
                try {
                    if (!r.getCache().get(r.getURL()).getCacheControl().isExpired()) return;

                    Response response = r.getClients().getSyncClient().get(r);

                    if (response.getStatus() / 100 == 2) {
                        r.getCache().internalPut(r.getURL(), response);
                    }

                } catch (RestException e) {
                    log.error("Got exception for stale uri: " + r.getURL(), e);
                }
            }
        });
    }



}
