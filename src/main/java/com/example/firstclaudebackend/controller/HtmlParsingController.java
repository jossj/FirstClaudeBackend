package com.example.firstclaudebackend.controller;

import com.example.firstclaudebackend.service.HtmlParsingService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/html")
public class HtmlParsingController {

    private final HtmlParsingService service;

    public HtmlParsingController(HtmlParsingService service) {
        this.service = service;
    }

    @GetMapping("/title")
    public Map<String, String> title(@RequestParam String url) throws IOException {
        return Map.of("title", service.extractTitle(url));
    }

    @GetMapping("/links")
    public List<Map<String, String>> links(@RequestParam String url) throws IOException {
        return service.extractLinks(url);
    }

    @GetMapping("/images")
    public List<String> images(@RequestParam String url) throws IOException {
        return service.extractImages(url);
    }

    @GetMapping("/select")
    public List<String> select(@RequestParam String url,
                               @RequestParam String selector) throws IOException {
        return service.extractBySelector(url, selector);
    }

    @PostMapping("/strip")
    public Map<String, String> strip(@RequestBody Map<String, String> body) {
        return Map.of("text", service.stripHtml(body.get("html")));
    }

    @PostMapping("/sanitize")
    public Map<String, String> sanitize(@RequestBody Map<String, String> body) {
        return Map.of("html", service.sanitize(body.get("html")));
    }
}
