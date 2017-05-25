package com.mercadolibre.restclient.multipart;

public interface PartVisitor {

    void visitStringPart(StringPart part);
    void visitByteArrayPart(ByteArrayPart part);
    void visitFilePart(FilePart part);
    void visitInputStreamPart(InputStreamPart part);

}
