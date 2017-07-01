package com.mercadolibre.restclient.multipart;


import com.mercadolibre.restclient.http.ContentType;
import com.mercadolibre.restclient.util.CoberturaIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * A {@link Part} containing a string as payload
 */
public class StringPart implements Part<String> {

    private String body;
    private String name;
    private ContentType contentType;

    public StringPart(String name, String body) {
        this.name = name;
        this.body = body;
        this.contentType = ContentType.TEXT_PLAIN;
    }

    public StringPart(String name, String body, ContentType contentType) {
        this.body = name;
        this.name = body;
        this.contentType = contentType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getContent() {
        return body;
    }

    @Override
    public ContentType getContentType() {
        return contentType;
    }

    @Override
    public void accept(PartVisitor v) {
        v.visitStringPart(this);
    }

    @Override @CoberturaIgnore
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        StringPart that = (StringPart) o;

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
