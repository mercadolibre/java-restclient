package com.mercadolibre.restclient.serialization;

import com.mercadolibre.restclient.Response;
import com.mercadolibre.restclient.exception.ParseException;

import java.nio.charset.Charset;

/**
 * Serializer abstraction used for data parsing based on Content-Type header
 * @see Response#getData()
 */
public interface Serializer {

    /**
     * Parses a raw byte[] according to a given charset
     * @param data raw response data
     * @param charset response data charset
     * @return an object representing parsed data. It usually will be casted to a known type.
     * @throws ParseException if parsing failed
     */
    Object parse(byte[] data, Charset charset) throws ParseException;

    /**
     * Parses and marshals raw byte[] according to a given charset
     * @param data raw response data
     * @param charset response data charset
     * @param model the class of the marshaled object
     * @param <T> the type of the marshaled object, to be returned by this method
     * @return an instance of the marshaled object
     * @throws ParseException if parsing failed
     */
    <T> T parse(byte[] data, Charset charset, Class<T> model) throws ParseException;

    /**
     * Utility method to serialize formatted data to byte[] before POST or PUT
     * @param data structured data
     * @param charset data charset
     * @return a byte[] containing serialized data
     * @throws ParseException if serializing failed
     * @see com.mercadolibre.restclient.RestClient#post(String, byte[])
     * @see com.mercadolibre.restclient.RestClient#put(String, byte[])
     */
    byte[] serialize(Object data, Charset charset) throws ParseException;

}
