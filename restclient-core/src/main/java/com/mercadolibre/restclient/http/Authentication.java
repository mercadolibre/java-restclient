package com.mercadolibre.restclient.http;

import com.mercadolibre.restclient.util.CoberturaIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * @author mlabarinas
 */
public class Authentication {

    private String hostname;
    private int port;
    private String username;
    private String password;

    public Authentication(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public Authentication(String hostname, int port, String username, String password) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override @CoberturaIgnore
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Authentication that = (Authentication) o;

        return new EqualsBuilder()
                .append(port, that.port)
                .append(hostname, that.hostname)
                .append(username, that.username)
                .append(password, that.password)
                .isEquals();
    }

    @Override @CoberturaIgnore
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(hostname)
                .append(port)
                .append(username)
                .append(password)
                .toHashCode();
    }

}