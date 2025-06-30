package org.example.handler;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.net.URI;

public class StaticFileHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        URI uri = exchange.getRequestURI();
        String path = uri.getPath();
        // "/" → "/index.html" 로 리다이렉트
        if ("/".equals(path)) {
            path = "/index.html";
        }

        // 클래스패스(resources/static)에서 파일 로드
        try (InputStream is = getClass()
                .getClassLoader()
                .getResourceAsStream("static" + path)) {
            if (is == null) {
                exchange.sendResponseHeaders(404, -1);
                exchange.close();
                return;
            }

            // Content-Type 추정
            String contentType = Files.probeContentType(Paths.get(path));
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            exchange.getResponseHeaders()
                    .set("Content-Type", contentType + "; charset=UTF-8");

            byte[] data = is.readAllBytes();
            exchange.sendResponseHeaders(200, data.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(data);
            }
        }
    }
}
