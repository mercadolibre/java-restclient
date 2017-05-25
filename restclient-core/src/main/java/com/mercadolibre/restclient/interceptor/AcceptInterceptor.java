package com.mercadolibre.restclient.interceptor;

import com.mercadolibre.restclient.Request;
import com.mercadolibre.restclient.http.Header;
import com.mercadolibre.restclient.http.HeaderParser;
import com.mercadolibre.restclient.util.CoberturaIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Adds an HTTP Accept header to a {@link Request}
 */
public class AcceptInterceptor implements RequestInterceptor {

    private String type;
    private boolean overwrite = false;

    public AcceptInterceptor(boolean overwrite, String... types) {
        this(types);
        this.overwrite = overwrite;
    }

    public AcceptInterceptor(String... types) {
        StringBuilder sb = new StringBuilder();

        for (String s : types) {
            sb.append(s);
            sb.append(HeaderParser.ELEM_DELIM);
            sb.append(" ");
        }

        sb.setLength(sb.length() - 2);

        this.type = sb.toString();
    }

    @Override
    public void intercept(Request r) {
        if (overwrite || !r.getHeaders().contains("Accept"))
            r.getHeaders().add(new Header("Accept",type));
    }

    @Override @CoberturaIgnore
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AcceptInterceptor that = (AcceptInterceptor) o;

        return new EqualsBuilder()
                .append(type, that.type)
                .isEquals();
    }

    @Override @CoberturaIgnore
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(type)
                .toHashCode();
    }
}
