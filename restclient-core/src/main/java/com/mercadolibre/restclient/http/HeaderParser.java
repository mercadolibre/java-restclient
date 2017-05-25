package com.mercadolibre.restclient.http;

import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HeaderParser {

    public static final String ELEM_DELIM = ",";
    public static final String PARAM_DELIM = ";";
    public static final String VALUE_DELIM = "=";

    public static HeaderElement[] parse(String s) {
        if (StringUtils.isBlank(s)) throw new IllegalArgumentException("Header should not be blank");

        final String[] split = s.split(ELEM_DELIM);
        HeaderElement[] output = new HeaderElement[split.length];

        for (int i=0; i < split.length; i++) {
            if (StringUtils.isBlank(split[i])) throw new IllegalArgumentException("Header element should not be blank");

            final String[] elements = split[i].split(PARAM_DELIM);
            output[i] = new HeaderElement(elements[0].trim(), parseParameters(elements));
        }

        return output;
    }

    private static Map<String,String> parseParameters(final String[] elements) {
        if (elements.length == 1) return Collections.emptyMap();

        Map<String,String> output = new HashMap<>(elements.length-1);

        for (int i=1; i < elements.length; i++) {
            if (StringUtils.isBlank(elements[i])) throw new IllegalArgumentException("Header pair should not be blank");

            String[] split = elements[i].split(VALUE_DELIM);
            if (split.length == 0 || split.length > 2)
                throw new IllegalArgumentException("Invalid header pair format");
            else if (split.length == 2) {
                if (StringUtils.isBlank(split[0]) || StringUtils.isBlank(split[1]))
                    throw new IllegalArgumentException("Invalid header pair format");
                else
                    output.put(split[0].trim(), split[1].trim());
            } else if (StringUtils.isBlank(split[0]) || elements[i].contains(VALUE_DELIM))
                throw new IllegalArgumentException("Invalid header pair format");
            else
                output.put(split[0].trim(), null);
        }

        return output;
    }

}
