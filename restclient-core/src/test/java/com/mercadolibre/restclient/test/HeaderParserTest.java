package com.mercadolibre.restclient.test;

import com.google.common.collect.ImmutableMap;
import com.mercadolibre.restclient.http.Header;
import com.mercadolibre.restclient.http.HeaderElement;
import com.mercadolibre.restclient.http.HeaderParser;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class HeaderParserTest {

    @Test
    public void shouldParseWellFormedHeader() {
        String value = "e1; k1 = v1, e2;k2=v2;k3 = v3; k4";
        HeaderElement[] elements = HeaderParser.parse(value);

        assertEquals(2, elements.length);

        assertEquals("e1", elements[0].getName());
        assertEquals(1, elements[0].getValues().size());
        assertEquals("v1", elements[0].getValue("k1"));

        assertEquals("e2", elements[1].getName());
        assertEquals(3, elements[1].getValues().size());
        assertEquals("v2", elements[1].getValue("k2"));
        assertEquals("v3", elements[1].getValue("k3"));
        assertTrue(elements[1].containsValue("k4"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailOnBlankHeader() {
        String value = " ";
        HeaderParser.parse(value);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailOnEmptyElement() {
        String value = "e1,,e2";
        HeaderParser.parse(value);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailOnEmptyPairKey() {
        String value = "e1;=v1";
        HeaderParser.parse(value);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailOnEmptyPair() {
        String value = "e1;=";
        HeaderParser.parse(value);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailOnEmptyPairValue() {
        String value = "e1;k1=";
        HeaderParser.parse(value);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailOnMissingPair() {
        String value = "e1;k1=v1;;k2=v2";
        HeaderParser.parse(value);
    }

    @Test
    public void shouldMakeHeaderString() {
        HeaderElement h = new HeaderElement("application/json", ImmutableMap.of("charset","utf-8"));
        assertEquals("application/json; charset=utf-8", h.toString());
    }

    @Test
    public void shouldParseHeaderFromElement() {
        Header h = new Header("Content-Type", new HeaderElement[]{new HeaderElement("application/json", ImmutableMap.of("charset","utf-8"))});
        HeaderElement[] elements = h.getElements();

        assertEquals(elements.length, 1);
        assertEquals("application/json; charset=utf-8", h.getValue());
        assertEquals("Content-Type: application/json; charset=utf-8", h.toString());
    }

}
