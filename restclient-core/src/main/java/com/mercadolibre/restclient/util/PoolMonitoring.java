package com.mercadolibre.restclient.util;

import com.mercadolibre.metrics.MetricCollector;
import com.mercadolibre.metrics.Metrics;

import java.util.Map;
import java.util.concurrent.*;

import static com.mercadolibre.restclient.log.LogUtil.log;

/**
 * @author mlabarinas
 */
public enum PoolMonitoring {

	INSTANCE;
	
	private ConcurrentMap<String, ExecutorService> pools = new ConcurrentHashMap<>();

	PoolMonitoring() {
    	Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new Runnable() {
			public void run() {
				sendMetrics();
			}
		}, 1, 5, TimeUnit.SECONDS);
    }

    private void sendMetrics() {
    	for(Map.Entry<String, ExecutorService> pool : pools.entrySet()) {
    		try {
    			if(pool.getValue() instanceof ThreadPoolExecutor) {
	    			Metrics.INSTANCE.count("restclient.internal.pool.size", ((ThreadPoolExecutor) pool.getValue()).getPoolSize(), new MetricCollector.Tags().add("rest_pool", pool.getKey()).toArray());
					Metrics.INSTANCE.count("restclient.internal.pool.queue.size", ((ThreadPoolExecutor) pool.getValue()).getQueue().size(), new MetricCollector.Tags().add("rest_pool", pool.getKey()).toArray());
    			}
	    			
    		} catch(Exception e) {
    			log.error("Could not record metrics for pool: " + pool.getKey(), e);
    		}
    	}
    }

    public void register(String name, ExecutorService pool) {
    	if(!pools.containsKey(name)) {
    		pools.put(name, pool);
    	}
    }

    public void remove(String name) {
        pools.remove(name);
    }

}