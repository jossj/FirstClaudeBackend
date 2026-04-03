package com.example.firstclaudebackend.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class HtmlParsingService {

    private static final int TIMEOUT_MS = 10_000;

    /**
     * Fetch a URL and return the parsed Document.
     */
    public Document fetch(String url) throws IOException {
        return Jsoup.connect(url)
                .timeout(TIMEOUT_MS)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .header("Accept-Language", "en-AU,en;q=0.9")
                .ignoreHttpErrors(true)
                .get();
    }

    /**
     * Extract the page title from a URL.
     */
    public String extractTitle(String url) throws IOException {
        return fetch(url).title();
    }

    /**
     * Extract all anchor links from a URL (href + link text).
     */
    public List<Map<String, String>> extractLinks(String url) throws IOException {
        return fetch(url).select("a[href]").stream()
                .map(a -> Map.of(
                        "text", a.text(),
                        "href", a.absUrl("href")
                ))
                .filter(m -> !m.get("href").isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * Extract all image URLs from a page.
     */
    public List<String> extractImages(String url) throws IOException {
        return fetch(url).select("img[src]").stream()
                .map(img -> img.absUrl("src"))
                .filter(src -> !src.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * Extract the text content of elements matching a CSS selector.
     */
    public List<String> extractBySelector(String url, String cssSelector) throws IOException {
        return fetch(url).select(cssSelector).stream()
                .map(Element::text)
                .collect(Collectors.toList());
    }

    /**
     * Extract all runner names from elements with class "runner-name".
     */
    public List<String> extractRunnerNames(String url) throws IOException {
        return fetch(url).select("div.runner-name").stream()
                .map(Element::text)
                .filter(text -> !text.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * Strip all HTML tags from a raw HTML string, returning plain text.
     */
    public String stripHtml(String html) {
        return Jsoup.parse(html).text();
    }

    /**
     * Sanitize an HTML string, keeping only safe tags (no scripts, no inline styles).
     */
    public String sanitize(String html) {
        return Jsoup.clean(html, Safelist.basicWithImages());
    }
}
