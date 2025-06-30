package org.example.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.model.Project;
import org.example.service.ProjectService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProjectHandler implements HttpHandler {

    private final ProjectService projectService;
    private static final Pattern ID_PATTERN = Pattern.compile("/project/(\\w+)");

    public ProjectHandler(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            if ("GET".equalsIgnoreCase(method) && "/project".equals(path)){
                // 전체 조회
                List<Project> list = projectService.findAll();
                String json = listToJson(list);
                sendJson(exchange, 200, json);

            } else if ("GET".equalsIgnoreCase(method)) {
                // 단건 조회
                Matcher m = ID_PATTERN.matcher(path);
                if (m.matches()) {
                    long id = Long.parseLong(m.group(1));
                    projectService.findById(id)
                            .ifPresentOrElse(
                                    p -> {
                                        try {
                                            sendJson(exchange, 200, projectToJson(p));
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    },
                                    () -> {
                                        try {
                                            sendStatus(exchange, 404);
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                            );
                } else {
                    sendStatus(exchange, 404);
                }


            } else if ("POST".equalsIgnoreCase(method) && "/project".equals(path)) {
                // 생성
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Project req = parseJsonToProject(body);
                Project created = projectService.save(req);
                sendJson(exchange, 201, projectToJson(created));

            } else if ("PUT".equalsIgnoreCase(method)) {
                // 수정
                Matcher m = ID_PATTERN.matcher(path);
                if (m.matches()) {
                    long id = Long.parseLong(m.group(1));
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    Project req = parseJsonToProject(body);
                    Project updated = projectService.update(id, req);
                    if (updated != null) {
                        sendJson(exchange, 200, projectToJson(updated));
                    } else  {
                        sendStatus(exchange, 404);
                    }
                } else {
                    sendStatus(exchange, 404);
                }

            } else if ("DELETE".equalsIgnoreCase(method)) {
                // 삭제
                Matcher m = ID_PATTERN.matcher(path);
                if (m.matches()) {
                    long id  = Long.parseLong(m.group(1));
                    boolean ok = projectService.deleteById(id);
                    sendStatus(exchange, ok ? 204 : 404);
                } else {
                    sendStatus(exchange,  404);
                }

            } else {
                // 지원하지 않는 메서드
                sendStatus(exchange, 405);
            }
        } finally {
            exchange.close();
        }

    }

    // ------------------------------------------------
    // JSON 직렬화·역직렬화 (외부 라이브러리 없이 직접 구현)

    private String projectToJson(Project p) {
        return "{"
                + "\"id\":"          + p.getId() + ","
                + "\"title\":\""     + escape(p.getTitle())       + "\","
                + "\"description\":\""+ escape(p.getDescription())+ "\","
                + "\"techStack\":\"" + escape(p.getTechStack())   + "\","
                + "\"githubUrl\":\"" + escape(p.getGithubUrl())   + "\","
                + "\"imageUrl\":\""  + escape(p.getImageUrl())    + "\""
                + "}";
    }

    private String listToJson(List<Project> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            sb.append(projectToJson(list.get(i)));
            if (i < list.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    private Project parseJsonToProject(String json) {
        Map<String,String> map = new HashMap<>();
        String body = json.trim();
        if (body.startsWith("{") && body.endsWith("}")) {
            body = body.substring(1, body.length() - 1);
            for (String pair : body.split(",")) {
                String[] kv = pair.split(":", 2);
                if (kv.length == 2) {
                    String key = kv[0].trim().replaceAll("^\"|\"$", "");
                    String val = kv[1].trim().replaceAll("^\"|\"$", "");
                    map.put(key, val);
                }
            }
        }
        Project p = new Project();
        p.setTitle(map.getOrDefault("title", ""));
        p.setDescription(map.getOrDefault("description", ""));
        p.setTechStack(map.getOrDefault("techStack", ""));
        p.setGithubUrl(map.getOrDefault("githubUrl", ""));
        p.setImageUrl(map.getOrDefault("imageUrl", ""));
        return p;
    }

    // ------------------------------------------------
    // HTTP 응답 헬퍼

    private void sendJson(HttpExchange ex, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        ex.sendResponseHeaders(status, bytes.length);
        ex.getResponseBody().write(bytes);
    }

    private void sendStatus(HttpExchange ex, int status) throws IOException {
        ex.sendResponseHeaders(status, -1);
    }

    private String escape(String s) {
        if (s == null) {
            return "";   // null이면 빈 문자열로 처리
        }
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }

}

