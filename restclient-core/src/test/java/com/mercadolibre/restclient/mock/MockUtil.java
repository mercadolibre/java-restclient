package com.mercadolibre.restclient.mock;

import com.mercadolibre.restclient.Request;
import com.mercadolibre.restclient.http.HttpMethod;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class MockUtil {

    public static Object getAttribute(String name, Object target) throws IllegalAccessException, NoSuchFieldException {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);

        return field.get(target);
    }

    public static void setAttribute(String name, Object target, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);

        field.set(target, value);
    }

    public static Object call(String name, Object target, Object... args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?>[] argClasses = new Class<?>[args.length];

        for (int i = 0; i < args.length; i++)
            argClasses[i] = args[i].getClass();

        return call(name, target, args, argClasses);
    }

    public static Object call(String name, Object target, Object[] args, Class<?>[] argClasses) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = target.getClass().getDeclaredMethod(name, argClasses);
        method.setAccessible(true);

        return method.invoke(target, args);
    }

    public static Request mockRequest(String url, HttpMethod method) {
        try {
            Constructor<Request> constructor = Request.class.getDeclaredConstructor();
            constructor.setAccessible(true);

            Request request = constructor.newInstance();

            setAttribute("method", request, method);
            setAttribute("url", request, url);

            return request;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
