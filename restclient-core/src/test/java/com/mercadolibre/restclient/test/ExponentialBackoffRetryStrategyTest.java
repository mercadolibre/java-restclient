package com.mercadolibre.restclient.test;

import com.mercadolibre.restclient.Request;
import com.mercadolibre.restclient.Response;
import com.mercadolibre.restclient.http.Headers;
import com.mercadolibre.restclient.http.HttpMethod;
import com.mercadolibre.restclient.mock.MockUtil;
import com.mercadolibre.restclient.retry.ExponentialBackoffRetryStrategy;
import com.mercadolibre.restclient.retry.RetryResponse;
import org.junit.Test;
import static org.junit.Assert.*;


public class ExponentialBackoffRetryStrategyTest {

    @Test
    public void shouldIncrementExponentially() {
        Request request = MockUtil.mockRequest("http://localhost/test", HttpMethod.GET);
        Response response = new Response(500, new Headers(), new byte[0]);

        ExponentialBackoffRetryStrategy strategy = new ExponentialBackoffRetryStrategy(10, 1000, 0.2, 2);

        RetryResponse rr;
        int retry = 1;
        do {
            assertTrue(retry < 20);

            rr = strategy.shouldRetry(request, response, null, retry);

            double m = strategy.getMin() * Math.pow(strategy.getMultiplier(), retry);

            assertTrue(rr.getDelay() >= (1-strategy.getFactor())*m);
            assertTrue(rr.getDelay() <= (1+strategy.getFactor())*m);

            retry++;
        } while (rr.retry());


    }

}
