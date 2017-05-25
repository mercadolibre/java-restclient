package com.mercadolibre.restclient;

import com.mercadolibre.restclient.async.Callback;
import com.mercadolibre.restclient.async.DirectCallback;
import com.mercadolibre.restclient.async.HTTPCallback;
import com.mercadolibre.restclient.cache.CacheCallback;
import com.mercadolibre.restclient.cache.DirectCacheCallback;
import com.mercadolibre.restclient.exception.RestException;

import java.io.IOException;
import java.util.concurrent.Future;


public class WrappingExecAsyncREST<R> implements ExecAsyncREST<R> {

    private ExecCallbackAsyncREST<R> client;

    public WrappingExecAsyncREST(ExecCallbackAsyncREST<R> client) {
        this.client = client;
    }

    protected interface Operation {
        Future<Response> execute(Request r) throws RestException;
    }

    protected interface CallbackOperation<T> {
        Future<Response> execute(Request r, HTTPCallback<T> callback);
    }

    private class CacheableOperation implements Operation {
        
    	private final Operation task;

        public CacheableOperation(Operation task) {
            this.task = task;
        }

        protected CacheCallback<Response> getCallback(Request r) {
            return new CacheCallback<>(r);
        }

		@Override
		public Future<Response> execute(final Request r) throws RestException {
			return r.isCacheable() ? r.getCache().internalAsyncGet(r.getURL(), getCallback(r)) : task.execute(r);
		}
    }

    private class DirectCacheableOperation extends CacheableOperation {

        private Callback<Response> target;

        public DirectCacheableOperation(Operation task, Callback<Response> target) {
            super(task);
            this.target = target;
        }

        @Override
        protected CacheCallback<Response> getCallback(Request r) {
            return new DirectCacheCallback<>(r, target);
        }
    }

	private class CacheableCallbackOperation<S> implements CallbackOperation<S> {

        private final CallbackOperation<S> task;

        public CacheableCallbackOperation(CallbackOperation<S> task) {
            this.task = task;
        }

        @Override
        public Future<Response> execute(final Request r, final HTTPCallback<S> callback) {
            return r.isCacheable() ? r.getCache().internalAsyncGet(r.getURL(), new CacheCallback<>(r)) : task.execute(r, callback);
        }

    }

    @Override
    public void close() throws IOException {
        client.close();
    }

    protected Future<Response> execute(Request r, Operation task) throws RestException {
        r.applyRequestInterceptors();
        return task.execute(r);
    }

	protected Future<Response> execute(Request r, HTTPCallback<R> callback, CallbackOperation<R> task) {
        r.applyRequestInterceptors();
        return task.execute(r, callback);
    }

    @Override
    public Future<Response> asyncGet(Request r) throws RestException {
        return execute(r, new CacheableOperation(new Operation() {
            public Future<Response> execute(Request r) throws RestException {
                return client.asyncGet(r, new HTTPCallback<R>(r));
            }
        }));
    }

    @Override
    public Future<Response> asyncPost(Request r) throws RestException {
        return execute(r, new Operation() {
            public Future<Response> execute(Request r) throws RestException {
                return client.asyncPost(r, new HTTPCallback<R>(r));
            }
        });
    }

    @Override
    public Future<Response> asyncPut(Request r) throws RestException {
        return execute(r, new Operation() {
            public Future<Response> execute(Request r) throws RestException {
                return client.asyncPut(r, new HTTPCallback<R>(r));
            }
        });
    }

    @Override
    public Future<Response> asyncDelete(Request r) throws RestException {
        return execute(r, new Operation() {
            public Future<Response> execute(Request r) throws RestException {
                return client.asyncDelete(r, new HTTPCallback<R>(r));
            }
        });
    }

    @Override
    public Future<Response> asyncHead(Request r) throws RestException {
        return execute(r, new Operation() {
            public Future<Response> execute(Request r) throws RestException {
                return client.asyncHead(r, new HTTPCallback<R>(r));
            }
        });
    }

    @Override
    public Future<Response> asyncOptions(Request r) throws RestException {
        return execute(r, new Operation() {
            public Future<Response> execute(Request r) throws RestException {
                return client.asyncOptions(r, new HTTPCallback<R>(r));
            }
        });
    }

    @Override
    public Future<Response> asyncPurge(Request r) throws RestException {
        return execute(r, new Operation() {
            public Future<Response> execute(Request r) throws RestException {
                return client.asyncPurge(r, new HTTPCallback<R>(r));
            }
        });
    }

    @Override
    public Future<Response> asyncGet(Request r, HTTPCallback<R> callback) {
        return execute(r, callback, new CacheableCallbackOperation<>(new CallbackOperation<R>() {
            public Future<Response> execute(Request r, HTTPCallback<R> callback) {
                return client.asyncGet(r, callback);
            }
        }));
    }

    @Override
    public Future<Response> asyncPost(Request r, HTTPCallback<R> callback) {
        return execute(r, callback, new CallbackOperation<R>() {
            public Future<Response> execute(Request r, HTTPCallback<R> callback) {
                return client.asyncPost(r, callback);
            }
        });
    }

    @Override
    public Future<Response> asyncPut(Request r, HTTPCallback<R> callback) {
        return execute(r, callback, new CallbackOperation<R>() {
            public Future<Response> execute(Request r, HTTPCallback<R> callback) {
                return client.asyncPut(r, callback);
            }
        });
    }

    @Override
    public Future<Response> asyncDelete(Request r, HTTPCallback<R> callback) {
        return execute(r, callback, new CallbackOperation<R>() {
            public Future<Response> execute(Request r, HTTPCallback<R> callback) {
                return client.asyncDelete(r, callback);
            }
        });
    }

    @Override
    public Future<Response> asyncHead(Request r, HTTPCallback<R> callback) {
        return execute(r, callback, new CallbackOperation<R>() {
            public Future<Response> execute(Request r, HTTPCallback<R> callback) {
                return client.asyncHead(r, callback);
            }
        });
    }

    @Override
    public Future<Response> asyncOptions(Request r, HTTPCallback<R> callback) {
        return execute(r, callback, new CallbackOperation<R>() {
            public Future<Response> execute(Request r, HTTPCallback<R> callback) {
                return client.asyncOptions(r, callback);
            }
        });
    }

    @Override
    public Future<Response> asyncPurge(Request r, HTTPCallback<R> callback) {
        return execute(r, callback, new CallbackOperation<R>() {
            public Future<Response> execute(Request r, HTTPCallback<R> callback) {
                return client.asyncPurge(r, callback);
            }
        });
    }

    @Override
    public void asyncGet(Request r, final Callback<Response> callback) throws RestException {
        execute(r, new DirectCacheableOperation(new Operation() {
            public Future<Response> execute(Request r) throws RestException {
                return client.asyncGet(r, new DirectCallback<R>(r, callback));
            }
        }, callback));
    }

    @Override
    public void asyncPost(Request r, final Callback<Response> callback) throws RestException {
        execute(r, new Operation() {
            public Future<Response> execute(Request r) throws RestException {
                return client.asyncPost(r, new DirectCallback<R>(r, callback));
            }
        });
    }

    @Override
    public void asyncPut(Request r, final Callback<Response> callback) throws RestException {
        execute(r, new Operation() {
            public Future<Response> execute(Request r) throws RestException {
                return client.asyncPut(r, new DirectCallback<R>(r, callback));
            }
        });
    }

    @Override
    public void asyncDelete(Request r, final Callback<Response> callback) throws RestException {
        execute(r, new Operation() {
            public Future<Response> execute(Request r) throws RestException {
                return client.asyncDelete(r, new DirectCallback<R>(r, callback));
            }
        });
    }

    @Override
    public void asyncHead(Request r, final Callback<Response> callback) throws RestException {
        execute(r, new Operation() {
            public Future<Response> execute(Request r) throws RestException {
                return client.asyncHead(r, new DirectCallback<R>(r, callback));
            }
        });
    }

    @Override
    public void asyncOptions(Request r, final Callback<Response> callback) throws RestException {
        execute(r, new Operation() {
            public Future<Response> execute(Request r) throws RestException {
                return client.asyncOptions(r, new DirectCallback<R>(r, callback));
            }
        });
    }

    @Override
    public void asyncPurge(Request r, final Callback<Response> callback) throws RestException {
        execute(r, new Operation() {
            public Future<Response> execute(Request r) throws RestException {
                return client.asyncPurge(r, new DirectCallback<R>(r, callback));
            }
        });
    }
}