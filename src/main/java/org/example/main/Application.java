package org.example.main;

import com.sun.net.httpserver.*;
import org.example.handler.*;
import org.example.repository.passwordRepository.MemoryPasswrodRepository;
import org.example.repository.passwordRepository.PasswordRepository;
import org.example.repository.projectRepository.MemoryProjectRepository;
import org.example.service.ProjectService;
import org.example.service.ProjectServiceImpl;
import org.example.sesstion.SessionManager;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;

public class Application {
    public static void main(String[] args) throws Exception {
        // 1. 서버 설정 값
        int port = 8443;    // HTTPS 기본 포트(필요시 443으로 변경)
        long sessionTimeoutMillis = 30 * 60 * 1000; // 30분
        long cleanupIntervalMillis = 5 * 60 * 1000; // 5분

        // 2. SSLContext 설정
        String keystorePath = "testkey.jks";
        char[] keystorePassword = "password".toCharArray();
        KeyStore keyStore = KeyStore.getInstance("JKS");
        try (InputStream fileInputStream = new FileInputStream(keystorePath)) {
            keyStore.load(fileInputStream, keystorePassword);
        }

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
        keyManagerFactory.init(keyStore, keystorePassword);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), null, null);


        // 3. HTTPS 서버 생성 및 SSL 설정
        HttpsServer server = HttpsServer.create(new InetSocketAddress(port), 0);
        server.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
            @Override
            public void configure(HttpsParameters params) {
                try {
                    // 기본 SSL 파라미터 설정
                    SSLContext c = getSSLContext();
                    params.setSSLParameters(c.getDefaultSSLParameters());
                } catch (Exception e) {
                    System.err.println("Failed to configure HTTPS parameters: " + e.getMessage());
                }
            }
        }) ;

        // 4. 세션 / 인증 핸들러 초기화
        SessionManager sessionManager =
                new SessionManager(sessionTimeoutMillis, cleanupIntervalMillis);
        PasswordRepository passwordRepository = new MemoryPasswrodRepository();
        AuthHandler authHandler =
                new AuthHandler(passwordRepository, sessionManager);

        // 5. 로그인 / 로그아웃 (필터 없이 공개)
        server.createContext("/login", new LoginHandler(authHandler));
        server.createContext("/logout", new LogoutHandler(authHandler));

        // 정적 콘텐츠 추가
        server.createContext("/", new StaticFileHandler());

//        HttpContext staticCtx = server.createContext("/", new HttpHandler() {
//            @Override
//            public void handle(HttpExchange ex) throws IOException {
//                URI uri = ex.getRequestURI();
//                String path = uri.getPath().equals("/") ? "/index.html" : uri.getPath();
//                Path file = Paths.get("static" + path).normalize();
//                // 프로젝트 루트/static/index.html 등
//                if (!Files.exists(file) || Files.isDirectory(file)) {
//                    ex.sendResponseHeaders(404, -1);
//                } else {
//                    String ct = Files.probeContentType(file);
//                    byte[] bytes = Files.readAllBytes(file);
//                    ex.getResponseHeaders().add("Content-Type", ct + "; charset=UTF-8");
//                    ex.sendResponseHeaders(200, bytes.length);
//                    try (OutputStream os = ex.getResponseBody()) {
//                        os.write(bytes);
//                    }
//                }
//            }
//        });

        // 프로젝트 CRUD 핸들러 등록
        ProjectService projectService = new ProjectServiceImpl((new MemoryProjectRepository()));
        HttpContext projectCtc = server.createContext("/project", new ProjectHandler(projectService));



        server.setExecutor(null);
        server.start();
        System.out.println("HTTPS Server started on port " + port);

    }
}