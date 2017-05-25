package com.mercadolibre.restclient.async;

/**
 * Interface intended to handle completion of async calls.
 * @param <T> The type of the response that will be handled on success
 */
public interface Callback<T> {

    /**
     * Method to be called upon a succesful request
     * @param response a response representation
     */
    void success(T response);

    /**
     * Method to be called upon failure of the current call.
     * @param t a Throwable received during request execution
     */
    void failure(Throwable t);

    /**
     * Cancels current request execution
     */
    void cancel();

}