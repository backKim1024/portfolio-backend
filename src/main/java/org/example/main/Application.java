package org.example.main;

import com.sun.net.httpserver.*;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.security.*;

public class Application {
    public static void main(String[] args) throws Exception{
        // HttpsServer 생성
        HttpsServer server = HttpsServer.create(new InetSocketAddress(8443), 0);

        // SSL 설정
        char[] password = "password".toCharArray();
        KeyStore ks = KeyStore.getInstance("JKS");
        try (FileInputStream fis = new FileInputStream("testkey.jks")){
            ks.load(fis, password);
        }

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks,password);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), null, null);

        server.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
            public void configure(HttpsParameters params) {
                try {
                    SSLContext sc = getSSLContext();
                    SSLEngine se = sc.createSSLEngine();
                    params.setNeedClientAuth(false);
                    params.setCipherSuites(se.getEnabledCipherSuites());
                    params.setProtocols(se.getEnabledProtocols());
                    params.setSSLParameters(sc.getDefaultSSLParameters());
                } catch (Exception e) {
                    System.out.println("Failed to create HTTPS port");
                }
            }
        });

        // Context 등록 및 서버 시작
       server.createContext("/hello", new httpHandler());
       server.setExecutor(null);
       server.start();
       System.out.println("HTTPS Server Started on port 8443");
    }

    static class httpHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            String response = "Hello, Secure World!";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}