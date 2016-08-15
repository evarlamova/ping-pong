package ru.varlamova.http.util.reader;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import ru.varlamova.http.HttpRequest;
import ru.varlamova.http.enums.HttpMethod;
import ru.varlamova.http.exception.ReaderException;
import ru.varlamova.http.util.HttpConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;

import static ru.varlamova.http.util.HttpConstants.CONTENT_LENGTH_HEADER;

public class SimpleRequestReader implements RequestReader {

    private static Logger log = Logger.getLogger(SimpleRequestReader.class.getName());

    @Override
    public void read(HttpRequest request) throws IOException {
        BufferedReader httpReader = request.getReader();
        String str = httpReader.readLine();
        //Parse Starting line:
        Pair<HttpMethod, String> methodURIPair = parseStartingLine(str);
        request.setMethod(methodURIPair.getLeft());
        request.setUri(methodURIPair.getRight());
        //Parse Headers
        Map<String, String> headers = request.getHeaders();
        while (StringUtils.isNotEmpty(str = httpReader.readLine())) {
            Pair<String, String> headerNameValPair = parseHeader(str);
            headers.put(headerNameValPair.getLeft(), headerNameValPair.getRight());
        }
        //Parse Body
        if (headers.containsKey(CONTENT_LENGTH_HEADER)) {
            String contentLengthHeader = headers.get(CONTENT_LENGTH_HEADER);
            try {
                //TODO not implemented
                String body = parseBody(readContent(httpReader, Integer.parseInt(contentLengthHeader)));
            } catch (NumberFormatException ex) {
                log.warning("Can't read content, the content length header is NaN " + contentLengthHeader);
            }
        }
    }

    private String readContent(BufferedReader httpReader, int contentLength) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < contentLength; i++) {
            sb.append((char) httpReader.read());
        }
        return sb.toString();
    }

    private Pair<HttpMethod, String> parseStartingLine(String line) {
        if (StringUtils.isEmpty(line)) {
            throw new ReaderException("Wrong HTTP request, starting line is empty");
        }
        String[] parts = line.split(" ");
        if (parts.length != 3) {
            throw new ReaderException("Wrong HTTP request, starting line should be separated by space symbols, " +
                    "and have format [METHOD] [URI] [HTTPVER]");
        }
        HttpMethod method;
        try {
            method = HttpMethod.valueOf(parts[0]);
        } catch (IllegalArgumentException e) {
            throw new ReaderException("Not existing HTTP Method: use GET,HEAD,POST,PUT,PATCH,DELETE,OPTIONS," +
                    "TRACE");
        }
        try {
            URI uri = URI.create(parts[1]);
            String httpVersionSchema = parts[2];
            if (!HttpConstants.HTTP_VERSION_SCHEMA.matcher(httpVersionSchema).matches()) {
                throw new ReaderException("Wrong HTTP schema, supports only HTTP 1.1");
            }

            return Pair.of(method, uri.toString());
        } catch (IllegalArgumentException e) {
            throw new ReaderException("Wrong URI format. Use /** format");
        }
    }

    private Pair<String, String> parseHeader(String header) {
        Matcher matcher = HttpConstants.HEADER_PATTERN.matcher(header);
        if (!matcher.find()) {
            throw new ReaderException("Wrong header format: " + header);
        }
        String key = matcher.group(1).trim();
        String value = matcher.group(2).trim();

        return Pair.of(key, value);
    }

    private String parseBody(String body) {
        //TODO not implemented
        return body;
    }


}

