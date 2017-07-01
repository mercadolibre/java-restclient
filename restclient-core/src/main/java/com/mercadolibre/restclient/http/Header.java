package com.mercadolibre.restclient.http;


import com.mercadolibre.restclient.util.CoberturaIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Header {

    private final String name;
    private String value;
    private HeaderElement[] elements;

    public Header(String name, HeaderElement[] elements) {
        this.name = name;
        this.elements = elements;
    }

    public Header(String name, String raw) {
        this.name = name;
        this.value = raw;
    }

    public String getName() {
        return name;
    }

    public String getCanonicalName() {
        return name.toLowerCase();
    }

    public HeaderElement[] getElements() {
        if (elements == null) elements = HeaderParser.parse(value);

        return elements;
    }

    private String formatValue() {
        StringBuilder sb = new StringBuilder();
        for (HeaderElement e : elements) {
            sb.append(e.toString());
            sb.append(HeaderParser.ELEM_DELIM);
            sb.append(" ");
        }

        sb.setLength(sb.length() - 2);

        return sb.toString();
    }

    public String getValue() {
        if (value == null) value = formatValue();

        return value;
    }

    public String toString() {
        return name + ": " + getValue();
    }

    @Override @CoberturaIgnore
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Header header = (Header) o;

        return new EqualsBuilder()
                .append(name, header.name)
                .append(value, header.value)
                .append(elements, header.elements)
                .isEquals();
    }

    @Override @CoberturaIgnore
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .append(value)
                .append(elements)
                .toHashCode();
    }
}
