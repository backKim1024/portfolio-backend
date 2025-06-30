package org.example.handler;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.example.model.Project;
import org.example.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectHandlerTest {

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ProjectHandler handler;

    private HttpExchange exchange;
    private Headers responseHeaders;
    private ByteArrayOutputStream responseBody;

    @BeforeEach
    void setUp() throws Exception {
        exchange = mock(HttpExchange.class);
        responseHeaders = new Headers();
        responseBody = new ByteArrayOutputStream();

        // stub response
        lenient().when(exchange.getResponseHeaders()).thenReturn(responseHeaders);
        lenient().doReturn(responseBody).when(exchange).getResponseBody();
    }

    @Test
    void getAllProjects_returnsListAnd200() throws Exception {
        // GIVEN
        when(exchange.getRequestMethod()).thenReturn("GET");
        doReturn(new URI("/project")).when(exchange).getRequestURI();

        Project a = new Project(); a.setId(1L); a.setTitle("A");
        Project b = new Project(); b.setId(2L); b.setTitle("B");
        when(projectService.findAll()).thenReturn(Arrays.asList(a,b));

        // WHEN
        handler.handle(exchange);

        // THEN
        verify(exchange).sendResponseHeaders(eq(200), anyLong());
        String json = responseBody.toString(StandardCharsets.UTF_8);
        assertTrue(json.contains("\"id\":1") && json.contains("\"id\":2"));
    }

    @Test
    void getProjectById_found_returns200() throws Exception {
        // GIVEN
        when(exchange.getRequestMethod()).thenReturn("GET");
        doReturn(new URI("/project/42")).when(exchange).getRequestURI();

        Project p = new Project();
        p.setId(42L);
        p.setTitle("Answer");
        when(projectService.findById(42L)).thenReturn(Optional.of(p));

        // WHEN
        handler.handle(exchange);

        // THEN
        verify(exchange).sendResponseHeaders(eq(200), anyLong());
        String json = responseBody.toString(StandardCharsets.UTF_8);
        assertTrue(json.contains("\"id\":42") && json.contains("Answer"));
    }

    @Test
    void getProjectById_notFound_returns404() throws Exception {
        // GIVEN
        when(exchange.getRequestMethod()).thenReturn("GET");
        doReturn(new URI("/project/99")).when(exchange).getRequestURI();
        when(projectService.findById(99L)).thenReturn(Optional.empty());

        // WHEN
        handler.handle(exchange);

        // THEN
        verify(exchange).sendResponseHeaders(404, -1);
    }

    @Test
    void createProject_returns201_andBody() throws Exception {
        // GIVEN
        when(exchange.getRequestMethod()).thenReturn("POST");
        doReturn(new URI("/project")).when(exchange).getRequestURI();
        String reqJson = "{\"title\":\"X\",\"description\":\"D\",\"techStack\":\"T\",\"githubUrl\":\"G\",\"imageUrl\":\"I\"}";
        doReturn(new ByteArrayInputStream(reqJson.getBytes(StandardCharsets.UTF_8)))
                .when(exchange).getRequestBody();

        Project saved = new Project();
        saved.setId(7L);
        saved.setTitle("X");
        when(projectService.save(any())).thenReturn(saved);

        // WHEN
        handler.handle(exchange);

        // THEN
        verify(exchange).sendResponseHeaders(eq(201), anyLong());
        String json = responseBody.toString(StandardCharsets.UTF_8);
        assertTrue(json.contains("\"id\":7") && json.contains("\"title\":\"X\""));
    }

    @Test
    void updateProject_found_returns200() throws Exception {
        // GIVEN
        when(exchange.getRequestMethod()).thenReturn("PUT");
        doReturn(new URI("/project/5")).when(exchange).getRequestURI();
        String reqJson = "{\"title\":\"U\",\"description\":\"D\",\"techStack\":\"T\",\"githubUrl\":\"G\",\"imageUrl\":\"I\"}";
        doReturn(new ByteArrayInputStream(reqJson.getBytes(StandardCharsets.UTF_8)))
                .when(exchange).getRequestBody();

        Project updated = new Project();
        updated.setId(5L);
        updated.setTitle("U");
        when(projectService.update(eq(5L), any())).thenReturn(updated);

        // WHEN
        handler.handle(exchange);

        // THEN
        verify(exchange).sendResponseHeaders(eq(200), anyLong());
        String json = responseBody.toString(StandardCharsets.UTF_8);
        assertTrue(json.contains("\"id\":5") && json.contains("\"title\":\"U\""));
    }

    @Test
    void updateProject_notFound_returns404() throws Exception {
        // GIVEN
        when(exchange.getRequestMethod()).thenReturn("PUT");
        doReturn(new URI("/project/123")).when(exchange).getRequestURI();
        String reqJson = "{\"title\":\"U\",\"description\":\"D\",\"techStack\":\"T\",\"githubUrl\":\"G\",\"imageUrl\":\"I\"}";
        doReturn(new ByteArrayInputStream(reqJson.getBytes(StandardCharsets.UTF_8)))
                .when(exchange).getRequestBody();

        when(projectService.update(eq(123L), any())).thenReturn(null);

        // WHEN
        handler.handle(exchange);

        // THEN
        verify(exchange).sendResponseHeaders(404, -1);
    }

    @Test
    void deleteProject_found_returns204() throws Exception {
        // GIVEN
        when(exchange.getRequestMethod()).thenReturn("DELETE");
        doReturn(new URI("/project/3")).when(exchange).getRequestURI();
        when(projectService.deleteById(3L)).thenReturn(true);

        // WHEN
        handler.handle(exchange);

        // THEN
        verify(exchange).sendResponseHeaders(204, -1);
    }

    @Test
    void deleteProject_notFound_returns404() throws Exception {
        // GIVEN
        when(exchange.getRequestMethod()).thenReturn("DELETE");
        doReturn(new URI("/project/8")).when(exchange).getRequestURI();
        when(projectService.deleteById(8L)).thenReturn(false);

        // WHEN
        handler.handle(exchange);

        // THEN
        verify(exchange).sendResponseHeaders(404, -1);
    }

    @Test
    void unsupportedMethod_returns405() throws Exception {
        // GIVEN
        when(exchange.getRequestMethod()).thenReturn("PATCH");
        doReturn(new URI("/project")).when(exchange).getRequestURI();

        // WHEN
        handler.handle(exchange);

        // THEN
        verify(exchange).sendResponseHeaders(405, -1);
    }
}
