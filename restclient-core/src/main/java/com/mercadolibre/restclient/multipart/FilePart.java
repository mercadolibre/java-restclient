package com.mercadolibre.restclient.multipart;

import com.mercadolibre.restclient.http.ContentType;
import com.mercadolibre.restclient.util.CoberturaIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.File;

/**
 * A {@link Part} containing a file reference as payload
 */
public class FilePart implements Part<File> {

    private File file;
    private String name;
    private ContentType contentType;

    public FilePart(String name, File file) {
        this.name = name;
        this.file = file;
        this.contentType = ContentType.TEXT_PLAIN;
    }

    /**
     * @deprecated use {@link #FilePart(String, File, ContentType)} instead
     */
    @Deprecated
    public FilePart(File file, String name, ContentType contentType) {
        this.file = file;
        this.name = name;
        this.contentType = contentType;
    }

    public FilePart(String name, File file, ContentType contentType) {
        this.name = name;
        this.file = file;
        this.contentType = contentType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public File getContent() {
        return file;
    }

    @Override
    public ContentType getContentType() {
        return contentType;
    }

    @Override
    public void accept(PartVisitor v) {
        v.visitFilePart(this);
    }

    @Override @CoberturaIgnore
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        FilePart filePart = (FilePart) o;

        return new EqualsBuilder()
                .append(name, filePart.name)
                .isEquals();
    }

    @Override @CoberturaIgnore
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .toHashCode();
    }

}
