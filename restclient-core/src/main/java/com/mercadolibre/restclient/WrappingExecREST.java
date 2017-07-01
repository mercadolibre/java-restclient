package com.mercadolibre.restclient;

import com.mercadolibre.metrics.Metrics;
import com.mercadolibre.restclient.cache.StaleRequestQueue;
import com.mercadolibre.restclient.exception.RestException;
import com.mercadolibre.restclient.interceptor.AddTimeInterceptor;
import com.mercadolibre.restclient.log.LogUtil;
import com.mercadolibre.restclient.metrics.MetricUtil;
import com.mercadolibre.restclient.retry.RetryResponse;
import com.mercadolibre.restclient.retry.RetryStrategy;
import com.mercadolibre.restclient.util.HttpCompressionHandler;

import java.io.IOException;

import static com.mercadolibre.restclient.log.LogUtil.log;


public class WrappingExecREST implements ExecREST {
	
	private final ExecREST client;

    public WrappingExecREST(ExecREST client) {
        this.client = client;
    }

    protected interface Operation {
        Response execute(Request r) throws RestException;
    }
    
    private class ResponseHandlerOperation implements Operation {
    	private Operation task;

        public ResponseHandlerOperation(Operation task) {
            this.task = task;
        }

        public Response execute(Request r) throws RestException {
        	HttpCompressionHandler.handleRequest(r);
        	
        	Response output = task.execute(r);
	    	
        	HttpCompressionHandler.handleResponse(r, output);
        	
            return output;
        }
        
    }

    private class LoggingOperation implements Operation {
        
    	private Operation task;

        public LoggingOperation(Operation task) {
            this.task = task;
        }

        public Response execute(Request r) throws RestException {
            Response output = task.execute(r);
            
            if(log.isTraceEnabled()) {
            	log.trace(LogUtil.makeTimeLogLine(r, output, System.currentTimeMillis() - (long) r.getAttribute(AddTimeInterceptor.NAME)));
            }
            	
            return output;
        }

    }

    private class MetricOperation implements Operation {
        
    	private Operation task;

        public MetricOperation(Operation task) {
            this.task = task;
        }
    	
        @Override
        public Response execute(Request r) throws RestException {
            Response output = task.execute(r);
            Metrics.INSTANCE.recordExecutionTime("restclient.http.time", System.currentTimeMillis() - (long) r.getAttribute(AddTimeInterceptor.NAME), MetricUtil.getExecutionTags(r, output).toArray());

            return output;
        }

    }
    
    private class CacheableOperation implements Operation {
        
    	private Operation task;

        public CacheableOperation(Operation task) {
            this.task = task;
        }
    	
        @Override
        public Response execute(Request r) throws RestException {
        	if(!r.isCacheable()) {
        		return task.execute(r);
        	}

        	Response response;
        	Response cachedResponse = r.getCache().internalGet(r.getURL());

            r.byPassCache(true);
        	
        	if (cachedResponse == null) {
        		response = task.execute(r);
        		
        		if(!response.getCacheControl().isExpired() || r.getCache().allowStaleResponse() && response.getCacheControl().isFreshForRevalidate()) {
        			r.getCache().internalPut(r.getURL(), response);
        		}
        		
        		return response;
        	}
        	
        	if (!cachedResponse.getCacheControl().isExpired()) {
        		return cachedResponse;
        	}

        	if (r.getCache().allowStaleResponse() && cachedResponse.getCacheControl().isFreshForRevalidate()) {
        		StaleRequestQueue.enqueue(r);
        		return cachedResponse;
        	}
        	
        	try {
        		response = task.execute(r);
        		
        		if (!response.getCacheControl().isExpired() || r.getCache().allowStaleResponse() && response.getCacheControl().isFreshForRevalidate()) {
        			r.getCache().internalPut(r.getURL(), response);
        		}
        		
        		if (response.getStatus() / 100 == 5 && r.getCache().allowStaleResponse() && cachedResponse.getCacheControl().isFreshForError() && cachedResponse.getStatus() / 100 != 5) {
        				response = cachedResponse;
        		}
        	
        	} catch (RestException e) {
        		if (r.getCache().allowStaleResponse() && cachedResponse.getCacheControl().isFreshForError()) {
        			response = cachedResponse;
        		} else {
        			throw e;
        		}
        	}
        	
        	return response;
        }
        
    }

    protected Response execute(Request r, Operation task) throws RestException {
        r.applyRequestInterceptors();

        RetryStrategy retryStrategy = r.getRetryStrategy();
        if (retryStrategy == null) return task.execute(r);

        int retry = 0;
        Response response;
        RestException exception;
        RetryResponse rr;

        do {
            response = null;
            exception = null;

            try {
                response = task.execute(r);
                r.applyResponseInterceptors(response);
            } catch (RestException e) {
                exception = e;
                exceptionAction(r,e);
            }

            rr = retryStrategy.shouldRetry(r, response, exception, retry++);
            if (rr.retry()) retryAction(r, rr);

        } while (rr.retry());

        if (response != null) return response;

        throw exception;
    }

    @Override
    public void close() throws IOException {
        client.close();
    }

    protected void retryAction(Request r, RetryResponse rr) {
        if (log.isTraceEnabled()) log.trace(LogUtil.makeRetryLogLine(r));
        Metrics.INSTANCE.incrementCounter("restclient.http.retry", MetricUtil.getRequestTags(r).toArray());

        try {
            Thread.sleep(rr.getDelay());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    protected void exceptionAction(Request r, RestException e) {
        if (log.isErrorEnabled()) log.error(LogUtil.makeExceptionLogLine(r), e);
        Metrics.INSTANCE.incrementCounter("restclient.http.error", MetricUtil.getRequestTags(r).toArray());
    }

    @Override
    public Response get(Request r) throws RestException {
        return execute(r, new LoggingOperation(new MetricOperation(new CacheableOperation(new ResponseHandlerOperation(new Operation() {
        	public Response execute(Request r) throws RestException {
        		return client.get(r);
        	}
        })))));
    }

    @Override
    public Response post(Request r) throws RestException {
    	return execute(r, new LoggingOperation(new MetricOperation(new ResponseHandlerOperation(new Operation() {
        	public Response execute(Request r) throws RestException {
        		return client.post(r);
        	}
        }))));
    }

    @Override
    public Response put(Request r) throws RestException {
    	return execute(r, new LoggingOperation(new MetricOperation(new ResponseHandlerOperation(new Operation() {
        	public Response execute(Request r) throws RestException {
        		return client.put(r);
        	}
        }))));
    }

    @Override
    public Response delete(Request r) throws RestException {
    	return execute(r, new LoggingOperation(new MetricOperation(new ResponseHandlerOperation(new Operation() {
        	public Response execute(Request r) throws RestException {
        		return client.delete(r);
        	}
        }))));
    }

    @Override
    public Response head(Request r) throws RestException {
    	return execute(r, new LoggingOperation(new MetricOperation(new ResponseHandlerOperation(new Operation() {
        	public Response execute(Request r) throws RestException {
        		return client.head(r);
        	}
        }))));
    }

    @Override
    public Response options(Request r) throws RestException {
    	return execute(r, new LoggingOperation(new MetricOperation(new ResponseHandlerOperation(new Operation() {
        	public Response execute(Request r) throws RestException {
        		return client.options(r);
        	}
        }))));
    }

    @Override
    public Response purge(Request r) throws RestException {
    	return execute(r, new LoggingOperation(new MetricOperation(new ResponseHandlerOperation(new Operation() {
        	public Response execute(Request r) throws RestException {
        		return client.purge(r);
        	}
        }))));
    }

}