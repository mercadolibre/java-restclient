package com.mercadolibre.restclient.http;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * A representation of HTTP Content-Type header, containing MIME type and charset.
 */
public final class ContentType {

    public static final String HEADER_NAME = "Content-Type";

    /**
     * application/atom+xml; charset=utf-8
     */
    public static final ContentType APPLICATION_ATOM_XML = new ContentType("application/atom+xml", StandardCharsets.UTF_8);

    /**
     * application/x-www-form-urlencoded; charset=utf-8
     */
    public static final ContentType APPLICATION_FORM_URLENCODED = new ContentType("application/x-www-form-urlencoded", StandardCharsets.UTF_8);

    /**
     * application/json; charset=utf-8
     */
    public static final ContentType APPLICATION_JSON = new ContentType("application/json", StandardCharsets.UTF_8);

    /**
     * application/octet-stream
     */
    public static final ContentType APPLICATION_OCTET_STREAM = new ContentType("application/octet-stream", null);

    /**
     * application/svg+xml; charset=utf-8
     */
    public static final ContentType APPLICATION_SVG_XML = new ContentType("application/svg+xml", StandardCharsets.UTF_8);

    /**
     * application/xhtml+xml; charset=utf-8
     */
    public static final ContentType APPLICATION_XHTML_XML = new ContentType("application/xhtml+xml", StandardCharsets.UTF_8);

    /**
     * application/xml; charset=utf-8
     */
    public static final ContentType APPLICATION_XML = new ContentType("application/xml", StandardCharsets.UTF_8);

    /**
     * multipart/form-data; charset=utf-8
     */
    public static final ContentType MULTIPART_FORM_DATA = new ContentType("multipart/form-data", StandardCharsets.UTF_8);

    /**
     * text/html; charset=utf-8
     */
    public static final ContentType TEXT_HTML = new ContentType("text/html", StandardCharsets.UTF_8);

    /**
     * text/plain; charset=utf-8
     */
    public static final ContentType TEXT_PLAIN = new ContentType("text/plain", StandardCharsets.UTF_8);

    /**
     * text/xml; charset=utf-8
     */
    public static final ContentType TEXT_XML = new ContentType("text/xml", StandardCharsets.UTF_8);

    /**
     * *&#47;*
     */
    public static final ContentType WILDCARD = new ContentType("*/*", null);

    private String mimeType;
    private Charset charset;
    
    private static final Map<String,ContentType> types = new HashMap<>(12);
    
    static {
        types.put(APPLICATION_ATOM_XML.getMimeType(),APPLICATION_ATOM_XML);
        types.put(APPLICATION_FORM_URLENCODED.getMimeType(),APPLICATION_FORM_URLENCODED);
        types.put(APPLICATION_JSON.getMimeType(),APPLICATION_JSON);
        types.put(APPLICATION_OCTET_STREAM.getMimeType(),APPLICATION_OCTET_STREAM);
        types.put(APPLICATION_SVG_XML.getMimeType(),APPLICATION_SVG_XML);
        types.put(APPLICATION_XHTML_XML.getMimeType(),APPLICATION_XHTML_XML);
        types.put(APPLICATION_XML.getMimeType(),APPLICATION_XML);
        types.put(MULTIPART_FORM_DATA.getMimeType(),MULTIPART_FORM_DATA);
        types.put(TEXT_HTML.getMimeType(),TEXT_HTML);
        types.put(TEXT_PLAIN.getMimeType(),TEXT_PLAIN);
        types.put(TEXT_XML.getMimeType(),TEXT_XML);
        types.put(WILDCARD.getMimeType(),WILDCARD);
    }

    private ContentType(String mimeType, Charset charset) {
        this.mimeType = mimeType;
        this.charset = charset;
    }

    /**
     * Builds a ContentType based on a MIME type and a charset
     * @param mimeType a string representing MIME type
     * @param charset a {@link Charset} instance
     * @return a ContentType instance
     */
    public static ContentType get(String mimeType, Charset charset) {
        ContentType type = types.get(mimeType.toLowerCase());
        if (type != null)
            return charset == null || type.charset.equals(charset) ? type : new ContentType(mimeType,charset);
        else
            return WILDCARD;
    }

    /**
     * Builds a ContentType based on a MIME type and a charset
     * @param mimeType a string representing MIME type
     * @param charset a string representing charset
     * @return a ContentType instance
     */
    public static ContentType get(String mimeType, String charset) {
        if (!Charset.isSupported(charset))
            throw new IllegalArgumentException("Charset " + charset + " not supported");

        return get(mimeType, Charset.forName(charset));
    }

    /**
     * Extract a content type based on a {@link Headers} instance, looking for Content-Type header
     * @param headers a {@link Headers} instance
     * @return a ContentType instance
     */
    public static ContentType get(Headers headers) {
        Header h = headers.getHeader(HEADER_NAME);

        if (h != null) {
            HeaderElement e = h.getElements()[0];
            String charset = e.getValue("charset");
            return charset != null ? get(e.getName(), charset) : get(e.getName(), Charset.defaultCharset());
        } else
            return WILDCARD;
    }

    /**
     * @return a string representation of this ContentType MIME type
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * @return this ContentType charset
     */
    public Charset getCharset() {
        return charset;
    }

    public String toString() {
        return charset != null ? mimeType + HeaderParser.PARAM_DELIM + " charset=" + charset : mimeType;
    }

}
