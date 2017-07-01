package com.mercadolibre.restclient.mock;

import com.mercadolibre.restclient.RESTPool;
import com.mercadolibre.restclient.httpc.HTTPCAsyncClient;
import com.mercadolibre.restclient.httpc.HTTPCBuilder;
import com.mercadolibre.restclient.httpc.HTTPCClient;

import java.io.IOException;
import java.lang.reflect.Method;


public class TestClients {

    private static HTTPCClient syncClient;
    private static HTTPCAsyncClient asyncClient;

    @SuppressWarnings("unchecked")
    private static HTTPCClient syncClient() {
        try {
            Method method = HTTPCBuilder.class.getDeclaredMethod("buildClient", RESTPool.class);
            method.setAccessible(true);
            return (HTTPCClient) method.invoke(new HTTPCBuilder(), RESTPool.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static HTTPCAsyncClient asyncClient() {
        try {
            Method method = HTTPCBuilder.class.getDeclaredMethod("buildAsyncClient", RESTPool.class);
            method.setAccessible(true);
            return (HTTPCAsyncClient) method.invoke(new HTTPCBuilder(), RESTPool.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void reload() {
        close();
        syncClient = syncClient();
        asyncClient = asyncClient();
    }

    public static void close() {
        try {
            if (syncClient != null) syncClient.close();
        } catch (IOException e) { }

        try {
            if (asyncClient != null) asyncClient.close();
        } catch (IOException e) { }
    }

    public static HTTPCClient getSyncClient() {
        return syncClient;
    }

    public static HTTPCAsyncClient getAsyncClient() {
        return asyncClient;
    }
}
