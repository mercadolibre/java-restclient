package com.mercadolibre.restclient.async;

import com.mercadolibre.restclient.Request;

public class Action {

    public static <R> void resend(Request request, HTTPCallback<R> callback) {
        switch (request.getMethod()) {
            case GET: request.<R>getClients().getAsyncClient().asyncGet(request, callback); break;
            case POST: request.<R>getClients().getAsyncClient().asyncPost(request, callback); break;
            case PUT: request.<R>getClients().getAsyncClient().asyncPut(request, callback); break;
            case DELETE: request.<R>getClients().getAsyncClient().asyncDelete(request, callback); break;
            case HEAD: request.<R>getClients().getAsyncClient().asyncHead(request, callback); break;
            case OPTIONS: request.<R>getClients().getAsyncClient().asyncOptions(request, callback); break;
            case PURGE: request.<R>getClients().getAsyncClient().asyncPurge(request, callback); break;
            default: throw new IllegalStateException("Unknown method for retry");
        }
    }

}
