package ru.varlamova.http.util.writer;

import ru.varlamova.http.HttpResponse;
import ru.varlamova.http.enums.HttpStatus;
import ru.varlamova.http.util.HttpConstants;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;

public class SimpleResponseWriter implements ResponseWriter {

    @Override
    public void write(HttpResponse response, String content) throws IOException {
        BufferedWriter responseWriter = response.getWriter();
        //Write starting line
        responseWriter.write(convertStartingLineToResponse(response.getHttpStatus()));
        responseWriter.newLine();
        //Write headers
        for (Map.Entry<String, String> header : response.getResponseHeader().entrySet()) {
            responseWriter.write(convertHeaderToResponse(header.getKey(), header.getValue()));
            responseWriter.newLine();
        }
        responseWriter.newLine();
        responseWriter.write(content);
        responseWriter.newLine();
        responseWriter.flush();
    }

    private String convertStartingLineToResponse(HttpStatus httpStatus) {
        return HttpConstants.DEFAULT_PROTOCOL_VERSION + " "
                + httpStatus.getHttpCode() + " "
                + httpStatus.getMetssageString();
    }

    private String convertHeaderToResponse(String headerKey, String headerValue) {
        return headerKey.trim() + ": " + headerValue;
    }

}