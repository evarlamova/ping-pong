package ru.varlamova.http.util;

import java.util.regex.Pattern;

public final class HttpConstants {

    private HttpConstants() {
    }

    public static final Pattern HTTP_VERSION_SCHEMA = Pattern.compile("HTTP/1.1");
    public static final Pattern HEADER_PATTERN = Pattern.compile("(.+):(.+)");
    public static final String DEFAULT_PROTOCOL_VERSION = "HTTP/1.1";
    public static final String CONTENT_LENGTH_HEADER = "Content-Length";
    public static final String DATE_HEADER = "Date";
    public static final String SERVER_HEADER = "Server";
    public static final String CONNECTION_HEADER = "Connection";
    public static final String CONTENT_TYPE_HEADER = "Content-Type";
}


