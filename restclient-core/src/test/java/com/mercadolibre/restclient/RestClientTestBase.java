package com.mercadolibre.restclient;

import com.mercadolibre.restclient.cache.DummyCache;
import com.mercadolibre.restclient.mock.RequestMockHolder;
import org.apache.commons.io.IOUtils;
import org.junit.After;

import java.io.*;
import java.util.zip.Deflater;
import java.util.zip.GZIPOutputStream;


public class RestClientTestBase {

    protected byte[] getDeflated(String s) {
        byte[] content = s.getBytes();

        ByteArrayOutputStream output = new ByteArrayOutputStream(content.length);
        Deflater deflater = new Deflater();
        deflater.setInput(content);
        deflater.finish();

        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            output.write(buffer, 0, count);
        }

        return output.toByteArray();
    }

    protected byte[] getGzipped(String s) throws IOException {
        byte[] content = s.getBytes();

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (InputStream input = new ByteArrayInputStream(content);
             OutputStream gzip = new GZIPOutputStream(output)) {
            IOUtils.copy(input, gzip);
        }

        return output.toByteArray();
    }

    @After
    public void after() {
        RequestMockHolder.clear();
        DummyCache.getDefault().reload();
    }

}
