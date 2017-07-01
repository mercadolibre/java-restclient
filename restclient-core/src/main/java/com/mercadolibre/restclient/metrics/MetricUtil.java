package com.mercadolibre.restclient.metrics;

import com.mercadolibre.metrics.MetricCollector;
import com.mercadolibre.restclient.Request;
import com.mercadolibre.restclient.Response;

public class MetricUtil {

    public static MetricCollector.Tags getExecutionTags(Request request, Response response) {
        return new MetricCollector.Tags()
                .add("method", request.getMethod())
                .add("rest_pool", request.getPool())
                .add("status", response.getStatus());
    }

    public static MetricCollector.Tags getRequestTags(Request request) {
        return new MetricCollector.Tags()
                .add("method", request.getMethod())
                .add("rest_pool", request.getPool());
    }

}
