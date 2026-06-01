package com.example.firstclaudebackend.controller;

import com.example.firstclaudebackend.service.HtmlParsingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HtmlParsingController.class)
class HtmlParsingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HtmlParsingService service;

    private static final String URL_PARAM = "https://example.com";

    // --- GET /api/html/title ---

    @Test
    void title_returnsPageTitle() throws Exception {
        when(service.extractTitle(URL_PARAM)).thenReturn("Test Page");

        mockMvc.perform(get("/api/html/title").param("url", URL_PARAM))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Page"));
    }

    @Test
    void title_returns502_onIOException() throws Exception {
        when(service.extractTitle(URL_PARAM)).thenThrow(new IOException("Connection refused"));

        mockMvc.perform(get("/api/html/title").param("url", URL_PARAM))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.error").value("Failed to fetch URL: Connection refused"));
    }

    // --- GET /api/html/links ---

    @Test
    void links_returnsLinkList() throws Exception {
        when(service.extractLinks(URL_PARAM)).thenReturn(List.of(
                Map.of("text", "Page 1", "href", "https://example.com/page1"),
                Map.of("text", "Page 2", "href", "https://example.com/page2")
        ));

        mockMvc.perform(get("/api/html/links").param("url", URL_PARAM))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].text").value("Page 1"))
                .andExpect(jsonPath("$[0].href").value("https://example.com/page1"))
                .andExpect(jsonPath("$[1].text").value("Page 2"));
    }

    @Test
    void links_returnsEmptyList_whenNoLinks() throws Exception {
        when(service.extractLinks(URL_PARAM)).thenReturn(List.of());

        mockMvc.perform(get("/api/html/links").param("url", URL_PARAM))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    // --- GET /api/html/images ---

    @Test
    void images_returnsImageUrlList() throws Exception {
        when(service.extractImages(URL_PARAM)).thenReturn(List.of(
                "https://example.com/img/photo.jpg",
                "https://example.com/img/logo.png"
        ));

        mockMvc.perform(get("/api/html/images").param("url", URL_PARAM))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("https://example.com/img/photo.jpg"))
                .andExpect(jsonPath("$[1]").value("https://example.com/img/logo.png"));
    }

    // --- GET /api/html/runner-names ---

    @Test
    void runnerNames_returnsRunnerNameList() throws Exception {
        when(service.extractRunnerNames(URL_PARAM)).thenReturn(List.of("Alice", "Bob", "Charlie"));

        mockMvc.perform(get("/api/html/runner-names").param("url", URL_PARAM))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("Alice"))
                .andExpect(jsonPath("$[1]").value("Bob"))
                .andExpect(jsonPath("$[2]").value("Charlie"));
    }

    // --- GET /api/html/select ---

    @Test
    void select_returnsMatchingElements() throws Exception {
        when(service.extractBySelector(URL_PARAM, "h1")).thenReturn(List.of("Heading One"));

        mockMvc.perform(get("/api/html/select")
                        .param("url", URL_PARAM)
                        .param("selector", "h1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("Heading One"));
    }

    @Test
    void select_returnsEmptyList_whenNothingMatches() throws Exception {
        when(service.extractBySelector(URL_PARAM, "table")).thenReturn(List.of());

        mockMvc.perform(get("/api/html/select")
                        .param("url", URL_PARAM)
                        .param("selector", "table"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    // --- POST /api/html/strip ---

    @Test
    void strip_returnsPlainText() throws Exception {
        when(service.stripHtml("<p>Hello <b>world</b></p>")).thenReturn("Hello world");

        mockMvc.perform(post("/api/html/strip")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"html\": \"<p>Hello <b>world</b></p>\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Hello world"));
    }

    @Test
    void strip_returnsEmptyText_forEmptyHtml() throws Exception {
        when(service.stripHtml("<div></div>")).thenReturn("");

        mockMvc.perform(post("/api/html/strip")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"html\": \"<div></div>\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value(""));
    }

    // --- POST /api/html/sanitize ---

    @Test
    void sanitize_returnsSanitizedHtml() throws Exception {
        when(service.sanitize("<p>Safe</p><script>xss</script>")).thenReturn("<p>Safe</p>");

        mockMvc.perform(post("/api/html/sanitize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"html\": \"<p>Safe</p><script>xss</script>\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.html").value("<p>Safe</p>"));
    }
}
