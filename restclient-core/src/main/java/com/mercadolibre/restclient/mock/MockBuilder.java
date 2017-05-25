package com.mercadolibre.restclient.mock;

import com.mercadolibre.restclient.Builder;
import com.mercadolibre.restclient.RestClient;

public abstract class MockBuilder<T extends MockBuilder<T,R>, R> extends Builder<T,R> {

    public MockBuilder() {
    }

    public MockBuilder(RestClient restClient) {
        super(restClient);
    }

}
