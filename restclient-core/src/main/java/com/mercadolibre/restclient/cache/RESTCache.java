package com.mercadolibre.restclient.cache;

import com.google.common.util.concurrent.*;
import com.mercadolibre.restclient.Response;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.*;

import static com.mercadolibre.restclient.log.LogUtil.log;


/**
 * @author mlabarinas
 */
public abstract class RESTCache implements Closeable {
	
	private static final int MAX_SIZE = 500;
	
	private static final ListeningExecutorService pool = MoreExecutors.listeningDecorator(new ThreadPoolExecutor(0, Runtime.getRuntime().availableProcessors(), 10000L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(MAX_SIZE), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy()));
	
	private static final ExecutorService rejectedPool = Executors.newCachedThreadPool();
	
	protected String name;
	protected RESTCache nextLevel;
	protected boolean allowStaleResponse;
	
	public RESTCache(String name) {
		this.allowStaleResponse = true;
		
		this.name = name;
	}

	public RESTCache(String name, RESTCache nextLevel) {
		this(name);

		if (nextLevel == null) throw new IllegalArgumentException("Next level cache should not be null");
		this.nextLevel = nextLevel;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public RESTCache getNextLevel() {
		return nextLevel;
	}

	public void setNextLevel(RESTCache nextLevel) {
		this.nextLevel = nextLevel;
	}
	
	public boolean hasNextLevel() {
		return this.nextLevel != null;
	}
	
	public boolean allowStaleResponse() {
		return allowStaleResponse;
	}

	public void setAllowStaleResponse(boolean allowStaleResponse) {
		this.allowStaleResponse = allowStaleResponse;
	}

	public Response internalGet(String url) {
		Response response = get(url);

		if(response == null) {
			if(hasNextLevel()) {
				try {
					response = nextLevel.internalGet(url);
				
				} catch(Throwable t) {
					log.error("Fail getting from nextlevel cache", t);
				}

				if(response == null) return null;

				if(!response.getCacheControl().isExpired() || allowStaleResponse && response.getCacheControl().isFreshForRevalidate())
					put(url, response);

			} else
				return null;
		}

		return response;
	}
	
	public void internalPut(String url, Response response) {
		put(url, response);

		if(hasNextLevel()) {
			try {
				nextLevel.internalPut(url, response);
			
			} catch(Throwable t) {
				log.error("Fail putting on nextlevel cache", t);
			}
		}
	}
	
	public Future<Response> internalAsyncGet(final String url, final CacheCallback<Response> callback) {
		try {
			ListenableFuture<Response> future = pool.submit(new Callable<Response>() {
				public Response call() throws Exception {
					return internalGet(url);
				}
			});
			
			Futures.addCallback(future, new FutureCallback<Response>() {
				public void onSuccess(Response response) {
					callback.success(response);
				}
			  
				public void onFailure(Throwable t) {
					callback.failure(t);
				}
			});
		
		} catch(RejectedExecutionException e) {
			final Exception exception = e;
 			
			rejectedPool.execute(new Runnable() {
				public void run() {
					callback.failure(exception);
				}
			});
		}
		
		return callback.getFuture();
	}
	
	public void internalAsyncPut(final String url, final Response response) {
		pool.execute(new Runnable() {
			public void run() {
				internalPut(url, response);
			}
		});
	}
	
	public abstract Response get(String url);
	public abstract void put(String url, Response response);
	public abstract void evict(String url);
	public abstract void evictAll();

	@Override
	public abstract void close() throws IOException;
}
