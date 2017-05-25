package com.mercadolibre.restclient.serialization;

import com.mercadolibre.restclient.exception.ParseException;
import org.junit.Test;
import static org.junit.Assert.*;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

public class JSONParserTest {

    @Test @SuppressWarnings("unchecked")
    public void shouldParseComplexObject() throws ParseException {
        String data = "{\"user_id\":123,\"scopes\":[\"read\"],\"status\":\"active\"}";

        Map<String,Object> json = DefaultJSONSerializer.INSTANCE.parse(data.getBytes(), Charset.defaultCharset(), Map.class);

        assertEquals(3, json.size());
        assertEquals(123, json.get("user_id"));
        assertEquals(1, ((List<String>) json.get("scopes")).size());
        assertEquals("read", ((List<String>) json.get("scopes")).get(0));
        assertEquals("active", json.get("status"));
    }

}
