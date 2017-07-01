package com.mercadolibre.restclient.httpc.stream;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.client.methods.AsyncByteConsumer;
import org.apache.http.protocol.HttpContext;

public class OutputStreamConsumer extends AsyncByteConsumer<HttpResponse> {

    private OutputStream outputStream;
    private HttpResponse response;
 
    public OutputStreamConsumer(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    protected void onByteReceived(ByteBuffer buf, IOControl ioctrl) throws IOException {
        while (buf.hasRemaining())
            outputStream.write(buf.get());
    }

    @Override
    protected void onResponseReceived(HttpResponse response) throws HttpException, IOException {
        this.response = response;
    }

    @Override
    protected HttpResponse buildResult(HttpContext context) throws Exception {
        return response;
    }

}
