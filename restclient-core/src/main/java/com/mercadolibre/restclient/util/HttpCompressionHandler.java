package com.mercadolibre.restclient.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

import com.google.common.base.Joiner;
import com.google.common.io.ByteStreams;
import com.mercadolibre.restclient.Request;
import com.mercadolibre.restclient.Response;
import com.mercadolibre.restclient.exception.RestException;
import com.mercadolibre.restclient.http.Header;
import com.mercadolibre.restclient.http.HeaderElement;

/**
 * @author mlabarinas
 */
public class HttpCompressionHandler {

	private static final String ACCEPT_ENCODING_HEADER = "Accept-Encoding";
	private static final String CONTENT_ENCODING_HEADER = "Content-Encoding";
	
    private static final String CONTENT_ENCODING_GZIP = "gzip";
    private static final String CONTENT_ENCODING_DEFLATE = "deflate";
	private static final List<String> DEFAULT_CONTENT_ENCODINGS = Arrays.asList(CONTENT_ENCODING_GZIP, CONTENT_ENCODING_DEFLATE);

	private static final int GZIP_BUFFER_SIZE = 4096;
	private static final int DEFLATE_BUFFER_SIZE = 4096;
	
	private static final String FORCE_CONTENT_ENCODING_ATTRIBUTE = "forceContentEncoding";
	private static final String FORCE_RETRY_ATTRIBUTE = "force_retry";

	public static void handleRequest(Request request) {
		if (request.getClients() != null && request.getClients().getPool().compression() || Boolean.TRUE.equals(request.getAttribute(FORCE_RETRY_ATTRIBUTE))) return;

    	boolean forceContentEncoding = true;

        if (!request.getHeaders().contains(ACCEPT_ENCODING_HEADER)) {
        	request.getHeaders().add(new Header(ACCEPT_ENCODING_HEADER, Joiner.on(",").join(DEFAULT_CONTENT_ENCODINGS)));

        	request.setAttribute(FORCE_RETRY_ATTRIBUTE, true);
        
        } else {
        	List<String> acceptEncodingCurrentElements = new ArrayList<>();
        	
        	for (HeaderElement element : request.getHeaders().getHeader(ACCEPT_ENCODING_HEADER).getElements()) {
        		if (DEFAULT_CONTENT_ENCODINGS.contains(element.getName())) {
        			forceContentEncoding = false;
        		}
        		
        		acceptEncodingCurrentElements.add(element.getName());
        	}
        	
        	if (forceContentEncoding) {
        		acceptEncodingCurrentElements.addAll(DEFAULT_CONTENT_ENCODINGS);
        		
        		request.getHeaders().add(new Header(ACCEPT_ENCODING_HEADER, Joiner.on(",").join(acceptEncodingCurrentElements)));
        	}
        }
        
        request.setAttribute(FORCE_CONTENT_ENCODING_ATTRIBUTE, forceContentEncoding);
	}
	
	public static void handleResponse(Request request, Response response) throws RestException {
		if (response == null || request.getClients() != null && request.getClients().getPool().compression()) return;

		if (!request.isDownload() && request.getAttributes().containsKey(FORCE_CONTENT_ENCODING_ATTRIBUTE) && (Boolean) request.getAttribute(FORCE_CONTENT_ENCODING_ATTRIBUTE) && response.getHeaders().contains(CONTENT_ENCODING_HEADER)) {
    		for (HeaderElement element : response.getHeaders().getHeader(CONTENT_ENCODING_HEADER).getElements()) {
		        try {
	    			if (CONTENT_ENCODING_GZIP.equalsIgnoreCase(element.getName())) {
	                    handleGzip(response);
	                }
	
	                if (CONTENT_ENCODING_DEFLATE.equalsIgnoreCase(element.getName())) {
	                    handleDeflate(response);
	                }
		        
		        } catch (Exception e) {
		        	throw new RestException(e);
		        }
    		}
    	}
	}
	
	public static void handleContent(InputStream in, OutputStream out, Header contentEncoding, boolean compression) throws RestException {
		boolean handled = false;
		
		try {
			if (!compression && contentEncoding != null) {
				for (HeaderElement element : contentEncoding.getElements()) {
			        try {
		    			if (CONTENT_ENCODING_GZIP.equalsIgnoreCase(element.getName())) {
		                    handleGzip(in, out);
		                
		                    handled = true;
		    			}
		
		                if (CONTENT_ENCODING_DEFLATE.equalsIgnoreCase(element.getName())) {
		                    handleDeflate(in, out);
		                
		                    handled = true;
		                }
			        
			        } catch (Exception e) {
			        	throw new RestException(e);
			        }
	    		}
			}
			
			if (!handled) {
				ByteStreams.copy(in, out);
			}
			
		} catch (Exception e) {
			throw new RestException(e);
		}
	}
	
	private static void handleGzip(InputStream in, OutputStream out) throws IOException {
		if (in == null) return;
		
		try (InputStream inputStream = new GZIPInputStream(in)) {
			byte[] buffer = new byte[GZIP_BUFFER_SIZE];
			int length;
			
			while ((length = inputStream.read(buffer)) > 0) {
			    out.write(buffer, 0, length);
			}
		}
	}
	
	private static void handleDeflate(InputStream in, OutputStream out) throws IOException {
		if (in == null) return;
		
		try (InputStream inputStream = new InflaterInputStream(in)) {
			byte[] buffer = new byte[DEFLATE_BUFFER_SIZE];
			int length;
			
			while ((length = inputStream.read(buffer)) > 0) {
			    out.write(buffer, 0, length);
			}
		}
	}

	private static void handleGzip(Response response) throws IOException {
		if (response.getBytes() == null) return;
		
		try (InputStream in = new ByteArrayInputStream(response.getBytes());
			ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			handleGzip(in, out);
		
			response.setBytes(out.toByteArray());
		}
	}
	
	private static void handleDeflate(Response response) throws IOException {
		if (response.getBytes() == null) return;
		
		try (InputStream in = new ByteArrayInputStream(response.getBytes());
			ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			handleDeflate(in, out);
		
			response.setBytes(out.toByteArray());
		}
	}
	
}