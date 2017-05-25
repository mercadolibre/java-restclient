package com.mercadolibre.restclient.log;

import com.mercadolibre.restclient.Request;
import com.mercadolibre.restclient.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.NOPLogger;

public class LogUtil {

    public static final String LOGGER_NAME = "restClientLogger";
    public static final Logger log;

    static {
        log = LoggerFactory.getLogger(LOGGER_NAME) != null ? LoggerFactory.getLogger(LOGGER_NAME) : NOPLogger.NOP_LOGGER;
    }

    public static String makeTimeLogLine(Request r, Response response, long delta) {
        return String.valueOf(r.getMethod()) +
                " to " +
                r.getURL() +
                " returned " +
                response.getStatus() +
                ". Took " +
                delta +
                " ms";
    }

    public static String makeRetryLogLine(Request r) {
        return String.valueOf(r.getMethod()) +
                " to " +
                r.getURL() +
                " retry";
    }

    public static String makeExceptionLogLine(Request r) {
        return String.valueOf(r.getMethod()) +
                " to " +
                r.getURL() +
                " threw exception";
    }


}
