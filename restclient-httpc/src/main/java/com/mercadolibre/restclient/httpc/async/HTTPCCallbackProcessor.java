package com.mercadolibre.restclient.httpc.async;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import com.mercadolibre.restclient.EmptyResponse;
import com.mercadolibre.restclient.Request;
import com.mercadolibre.restclient.Response;
import com.mercadolibre.restclient.async.CallbackProcessor;
import com.mercadolibre.restclient.exception.RestException;
import com.mercadolibre.restclient.http.Headers;
import com.mercadolibre.restclient.httpc.util.HTTPCUtil;

public class HTTPCCallbackProcessor implements CallbackProcessor<HttpResponse> {

    @Override
    public Response makeResponse(Request request, HttpResponse response) throws RestException {
        int status = response.getStatusLine().getStatusCode();
        Headers headers = HTTPCUtil.getHeaders(response);
        
        try {
	        if(request.isDownload()) {
	        	EmptyResponse emptyResponse = new EmptyResponse(response.getStatusLine().getStatusCode(), headers);
	        	
	        	request.populateOutputStream(response.getEntity().getContent(), emptyResponse.getHeaders().getHeader("Content-Encoding"));
	        	
	        	EntityUtils.consume(response.getEntity());
	        	
	        	return emptyResponse;
	        
	        } else {
	        	return new Response(status, headers, HTTPCUtil.handleResponse(response));
	        }
        
        } catch(Exception e) {
        	throw new RestException(e);
        }
    }

}