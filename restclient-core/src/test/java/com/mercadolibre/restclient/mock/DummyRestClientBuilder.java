package com.mercadolibre.restclient.mock;

import com.mercadolibre.restclient.*;

import java.io.IOException;


public class DummyRestClientBuilder extends MockBuilder<DummyRestClientBuilder,Response> {

    public DummyRestClientBuilder(RestClient restClient) {
        super(restClient);
    }

    public DummyRestClientBuilder() {
    }

    @Override
    public DummyRestClientBuilder newInstance(RestClient client) {
        return new DummyRestClientBuilder(client);
    }

    @Override
    protected ExecREST buildClient(RESTPool pool) throws IOException {
        return new DummyClient(pool);
    }

    @Override
    protected ExecCallbackAsyncREST<Response> buildAsyncClient(RESTPool pool) throws IOException {
        return new DummyClient(pool);
    }

}
