package com.mercadolibre.restclient.http;

import com.mercadolibre.restclient.util.CoberturaIgnore;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Collections;
import java.util.Map;

public class HeaderElement {

    private Map<String,String> values;
    private String name;

    public HeaderElement(String name, Map<String,String> values) {
        this.name = name;
        this.values = Collections.unmodifiableMap(values);
    }

    public String getName() {
        return name;
    }

    public Map<String,String> getValues(){
        return values;
    }

    public String getValue(String name) {
        return values.get(name);
    }

    public boolean containsValue(String name) {
        return values.containsKey(name);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(name);

        if (values != null && !values.isEmpty()) {
            sb.append(HeaderParser.PARAM_DELIM);
            sb.append(" ");
            for (Map.Entry<String,String> e : values.entrySet()) {
                sb.append(e.getKey());

                if (StringUtils.isNotBlank(e.getValue())) {
                    sb.append(HeaderParser.VALUE_DELIM);
                    sb.append(e.getValue());
                }

                sb.append(HeaderParser.PARAM_DELIM);
                sb.append(" ");
            }

            sb.setLength(sb.length() - 2);
        }

        return sb.toString();
    }

    @Override @CoberturaIgnore
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        HeaderElement that = (HeaderElement) o;

        return new EqualsBuilder()
                .append(values, that.values)
                .append(name, that.name)
                .isEquals();
    }

    @Override @CoberturaIgnore
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(values)
                .append(name)
                .toHashCode();
    }

}
