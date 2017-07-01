package com.mercadolibre.restclient.multipart;


import com.mercadolibre.restclient.http.ContentType;
import com.mercadolibre.restclient.util.CoberturaIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * A {@link Part} containing a byte[] as payload
 */
public class ByteArrayPart implements Part<byte[]> {

    private byte[] body;
    private String name;
    private ContentType contentType;
    private String arrayName;

    public ByteArrayPart(String name, String arrayName, byte[] body) {
        this.name = name;
        this.arrayName = arrayName;
        this.body = body;
        this.contentType = ContentType.TEXT_PLAIN;
    }

    /**
     * @deprecated use {@link #ByteArrayPart(String, String, byte[], ContentType)} instead
     */
    @Deprecated
    public ByteArrayPart(byte[] body, String name, ContentType contentType) {
        this.body = body;
        this.name = name;
        this.contentType = contentType;
    }

    public ByteArrayPart(String name, String arrayName, byte[] body, ContentType contentType) {
        this.name = name;
        this.arrayName = arrayName;
        this.body = body;
        this.contentType = contentType;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getArrayName() {
        return arrayName;
    }

    @Override
    public byte[] getContent() {
        return body;
    }

    @Override
    public ContentType getContentType() {
        return contentType;
    }

    @Override
    public void accept(PartVisitor v) {
        v.visitByteArrayPart(this);
    }

    @Override @CoberturaIgnore
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ByteArrayPart that = (ByteArrayPart) o;

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
