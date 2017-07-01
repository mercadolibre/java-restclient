package com.mercadolibre.restclient;

import com.mercadolibre.restclient.async.CallbackProcessor;
import com.mercadolibre.restclient.http.ContentType;
import com.mercadolibre.restclient.mock.MockBuilder;
import com.mercadolibre.restclient.mock.MockCallbackProcessor;
import com.mercadolibre.restclient.serialization.Serializer;
import com.mercadolibre.restclient.serialization.SerializerRegistry;
import com.mercadolibre.restclient.serialization.Serializers;

import java.util.Map;
import java.util.ServiceLoader;


public final class Engine {

    private static Builder<?,?> builder;
    private static CallbackProcessor<?> callbackProcessor;

    @SuppressWarnings("rawtypes")
	private static ServiceLoader<Builder> builderLoader = ServiceLoader.load(Builder.class);
    private static ServiceLoader<SerializerRegistry> serializerLoader = ServiceLoader.load(SerializerRegistry.class);

    @SuppressWarnings("rawtypes")
    private static ServiceLoader<CallbackProcessor> callbackLoader = ServiceLoader.load(CallbackProcessor.class);

    static {
        loadEngine();
        loadSerializers();
    }

    @SuppressWarnings("rawtypes")
	private static void loadEngine() {
        for (Builder b : builderLoader) {
            if (b instanceof MockBuilder) {
                builder = b;
                break;
            }

            if (builder != null) throw new IllegalStateException("Many Rest Clients implementations found");

            builder = b;
        }

        loadCallbackFactory();
    }

    @SuppressWarnings("rawtypes")
    private static void loadCallbackFactory() {
        for (CallbackProcessor f : callbackLoader) {
            if (f instanceof MockCallbackProcessor) {
                callbackProcessor = f;
                break;
            }

            if (callbackProcessor != null) throw new IllegalStateException("Many Rest Clients implementations found");

            callbackProcessor = f;
        }
    }

    private static void loadSerializers() {
       for (SerializerRegistry s : serializerLoader) {
            Map<ContentType, Serializer> serializers = s.getMappings();
            for (Map.Entry<ContentType,Serializer> e : serializers.entrySet())
                Serializers.register(e.getKey(), e.getValue());
       }
    }

    public static Builder<?,?> newBuilder(RestClient client) {
        if (builder == null)
            throw new IllegalStateException("No engine has been registered");

        return builder.newInstance(client);
    }

    @SuppressWarnings("unchecked")
    public static <R> CallbackProcessor<R> callbackProcessor() {
        return (CallbackProcessor<R>) callbackProcessor;
    }

}