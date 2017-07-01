package com.mercadolibre.restclient.mock;

import org.apache.http.config.SocketConfig;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.bootstrap.ServerBootstrap;
import org.apache.http.localserver.SSLTestContexts;

import java.util.concurrent.TimeUnit;

public enum HTTPCMockServer {
    INSTANCE;

    private ServerBootstrap serverBootstrap;
    private final ProtocolScheme scheme = ProtocolScheme.http;
    private HttpServer server;

    private enum ProtocolScheme {
        http, https
    }

    HTTPCMockServer() {
        SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(15000).build();
        serverBootstrap = ServerBootstrap.bootstrap()
                .setSocketConfig(socketConfig)
                .setServerInfo("TEST/1.1")
                .setListenerPort(0)
                .registerHandler("/*", HTTPCMockHandler.INSTANCE);

        if (scheme.equals(ProtocolScheme.https))
            try {
                serverBootstrap.setSslContext(SSLTestContexts.createServerSSLContext());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
    }

    public synchronized void start() {
        if (server == null) {
            server = serverBootstrap.create();

            try {
                server.start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public synchronized void stop() {
        if (server != null) {
            server.shutdown(3L, TimeUnit.SECONDS);
            server = null;
        }
    }

    public Integer getPort() {
        return server != null ? server.getLocalPort() : null;
    }


}
