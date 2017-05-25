package com.mercadolibre.restclient;

import com.mercadolibre.restclient.mock.HTTPCMockServer;
import com.mercadolibre.restclient.mock.TestClients;
import org.junit.ClassRule;
import org.junit.rules.ExternalResource;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({HTTPCSyncSpec.class, HTTPCAsyncSpec.class})
public class HTTPCSuiteTest {

    @ClassRule
    public static ExternalResource testRule = new ExternalResource() {
        @Override
        protected void before() throws Throwable {
            HTTPCMockServer.INSTANCE.start();
            TestClients.reload();
        }

        @Override
        protected void after() {
            TestClients.close();
            HTTPCMockServer.INSTANCE.stop();
        }
    };

}
