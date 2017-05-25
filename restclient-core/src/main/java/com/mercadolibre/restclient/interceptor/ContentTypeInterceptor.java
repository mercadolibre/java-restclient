package com.mercadolibre.restclient.interceptor;

import com.mercadolibre.restclient.Request;
import com.mercadolibre.restclient.http.ContentType;
import com.mercadolibre.restclient.http.Header;
import com.mercadolibre.restclient.util.CoberturaIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Adds an HTTP Content-Type header to a {@link Request}
 */
public class ContentTypeInterceptor implements RequestInterceptor {

    private ContentType type;
    private boolean overwrite = false;

    public ContentTypeInterceptor(ContentType type) {
        this.type = type;
    }

    public ContentTypeInterceptor(String type, String charset){
        this(ContentType.get(type,charset));
    }

    public ContentTypeInterceptor(String type) {
        this(type, null);
    }

    public ContentTypeInterceptor(ContentType type, boolean overwrite) {
        this.type = type;
        this.overwrite = overwrite;
    }

    public ContentTypeInterceptor(String type, String charset, boolean overwrite){
        this(ContentType.get(type,charset), overwrite);
    }

    public ContentTypeInterceptor(String type, boolean overwrite) {
        this(type, null, overwrite);
    }

    @Override
    public void intercept(Request r) {
        if (overwrite || !r.getHeaders().contains(ContentType.HEADER_NAME))
            r.getHeaders().add(new Header(ContentType.HEADER_NAME, type.toString()));
    }

    @Override @CoberturaIgnore
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ContentTypeInterceptor that = (ContentTypeInterceptor) o;

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
