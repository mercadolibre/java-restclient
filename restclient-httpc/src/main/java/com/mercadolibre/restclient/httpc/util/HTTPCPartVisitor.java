package com.mercadolibre.restclient.httpc.util;

import com.mercadolibre.restclient.http.ContentType;
import com.mercadolibre.restclient.multipart.*;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;

public class HTTPCPartVisitor implements PartVisitor {

    private MultipartEntityBuilder builder;

    public HTTPCPartVisitor(MultipartEntityBuilder builder) {
        this.builder = builder;
    }

    private org.apache.http.entity.ContentType adaptContentType(ContentType contentType) {
        return org.apache.http.entity.ContentType.create(contentType.getMimeType(), contentType.getCharset());
    }

    @Override
    public void visitStringPart(StringPart part) {
        builder.addPart(part.getName(), new StringBody(part.getContent(), adaptContentType(part.getContentType())));
    }

    @Override
    public void visitByteArrayPart(ByteArrayPart part) {
        builder.addPart(part.getName(), new ByteArrayBody(part.getContent(), adaptContentType(part.getContentType()), part.getArrayName()));
    }

    @Override
    public void visitFilePart(FilePart part) {
        builder.addPart(part.getName(), new FileBody(part.getContent(), adaptContentType(part.getContentType())));
    }

    @Override
    public void visitInputStreamPart(InputStreamPart part) {
        builder.addPart(part.getName(), new InputStreamBody(part.getContent(), adaptContentType(part.getContentType())));
    }

}
