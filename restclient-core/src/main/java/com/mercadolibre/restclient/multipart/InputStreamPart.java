package com.mercadolibre.restclient.multipart;


import com.mercadolibre.restclient.http.ContentType;
import com.mercadolibre.restclient.util.CoberturaIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.InputStream;

/**
 * A {@link Part} containing an input stream as payload
 */
public class InputStreamPart implements Part<InputStream> {

    private InputStream stream;
    private String name;
    private ContentType contentType;

    public InputStreamPart(String name, InputStream stream) {
        this.name = name;
        this.stream = stream;
        this.contentType = ContentType.TEXT_PLAIN;
    }

    /**
     * @deprecated use {@link #InputStreamPart(String, InputStream, ContentType)} instead
     */
    @Deprecated
    public InputStreamPart(InputStream stream, String name, ContentType contentType) {
        this.stream = stream;
        this.name = name;
        this.contentType = contentType;
    }

    public InputStreamPart(String name, InputStream stream, ContentType contentType) {
        this.name = name;
        this.stream = stream;
        this.contentType = contentType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public InputStream getContent() {
        return stream;
    }

    @Override
    public ContentType getContentType() {
        return contentType;
    }

    @Override
    public void accept(PartVisitor v) {
        v.visitInputStreamPart(this);
    }

    @Override @CoberturaIgnore
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        InputStreamPart that = (InputStreamPart) o;

        return new EqualsBuilder()
                .append(name, that.name)
                .isEquals();
    }

    @Override @CoberturaIgnore
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .toHashCode();
    }

}
