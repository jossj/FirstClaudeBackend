package com.example.firstclaudebackend.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class HtmlParsingServiceTest {

    @Spy
    private HtmlParsingService service;

    private static final String FAKE_URL = "https://example.com";

    private Document buildDocument(String html) {
        return Jsoup.parse(html, FAKE_URL);
    }

    @BeforeEach
    void setUp() throws IOException {
        // Default document used by most tests — override per-test as needed
        Document doc = buildDocument("""
                <html>
                  <head><title>Test Page</title></head>
                  <body>
                    <h1>Heading One</h1>
                    <h2>Heading Two</h2>
                    <a href="/page1">Page 1</a>
                    <a href="/page2">Page 2</a>
                    <a href="">Empty link</a>
                    <img src="/img/photo.jpg" />
                    <img src="/img/logo.png" />
                    <img src="" />
                  </body>
                </html>
                """);
        doReturn(doc).when(service).fetch(FAKE_URL);
    }

    // --- extractTitle ---

    @Test
    void extractTitle_returnsPageTitle() throws IOException {
        assertThat(service.extractTitle(FAKE_URL)).isEqualTo("Test Page");
    }

    @Test
    void extractTitle_returnsEmptyString_whenNoTitleTag() throws IOException {
        doReturn(buildDocument("<html><body></body></html>")).when(service).fetch(FAKE_URL);
        assertThat(service.extractTitle(FAKE_URL)).isEmpty();
    }

    // --- extractLinks ---

    @Test
    void extractLinks_returnsLinksWithTextAndHref() throws IOException {
        List<Map<String, String>> links = service.extractLinks(FAKE_URL);

        assertThat(links).hasSize(2); // empty href is filtered out
        assertThat(links.get(0)).containsEntry("text", "Page 1")
                                .containsEntry("href", "https://example.com/page1");
        assertThat(links.get(1)).containsEntry("text", "Page 2")
                                .containsEntry("href", "https://example.com/page2");
    }

    @Test
    void extractLinks_returnsEmptyList_whenNoLinks() throws IOException {
        doReturn(buildDocument("<html><body><p>No links</p></body></html>")).when(service).fetch(FAKE_URL);
        assertThat(service.extractLinks(FAKE_URL)).isEmpty();
    }

    // --- extractImages ---

    @Test
    void extractImages_returnsAbsoluteImageUrls() throws IOException {
        List<String> images = service.extractImages(FAKE_URL);

        assertThat(images).containsExactly(
                "https://example.com/img/photo.jpg",
                "https://example.com/img/logo.png"
        );
    }

    @Test
    void extractImages_returnsEmptyList_whenNoImages() throws IOException {
        doReturn(buildDocument("<html><body></body></html>")).when(service).fetch(FAKE_URL);
        assertThat(service.extractImages(FAKE_URL)).isEmpty();
    }

    // --- extractRunnerNames ---

    @Test
    void extractRunnerNames_returnsAllRunnerNames() throws IOException {
        doReturn(buildDocument("""
                <html><body>
                  <div class="runner-name">Alice</div>
                  <div class="runner-name">Bob</div>
                  <div class="runner-name">Charlie</div>
                </body></html>
                """)).when(service).fetch(FAKE_URL);

        assertThat(service.extractRunnerNames(FAKE_URL))
                .containsExactly("Alice", "Bob", "Charlie");
    }

    @Test
    void extractRunnerNames_filtersBlankEntries() throws IOException {
        doReturn(buildDocument("""
                <html><body>
                  <div class="runner-name">Alice</div>
                  <div class="runner-name">  </div>
                  <div class="runner-name">Bob</div>
                </body></html>
                """)).when(service).fetch(FAKE_URL);

        assertThat(service.extractRunnerNames(FAKE_URL))
                .containsExactly("Alice", "Bob");
    }

    @Test
    void extractRunnerNames_returnsEmptyList_whenNonePresent() throws IOException {
        doReturn(buildDocument("<html><body><p>No runners</p></body></html>"))
                .when(service).fetch(FAKE_URL);

        assertThat(service.extractRunnerNames(FAKE_URL)).isEmpty();
    }

    // --- extractBySelector ---

    @Test
    void extractBySelector_returnsMatchingElementText() throws IOException {
        List<String> headings = service.extractBySelector(FAKE_URL, "h1, h2");

        assertThat(headings).containsExactly("Heading One", "Heading Two");
    }

    @Test
    void extractBySelector_returnsEmptyList_whenSelectorMatchesNothing() throws IOException {
        assertThat(service.extractBySelector(FAKE_URL, "table")).isEmpty();
    }

    // --- stripHtml ---

    @Test
    void stripHtml_removesAllTags() {
        String result = service.stripHtml("<p>Hello <b>world</b></p>");
        assertThat(result).isEqualTo("Hello world");
    }

    @Test
    void stripHtml_returnsPlainString_whenNoTagsPresent() {
        assertThat(service.stripHtml("plain text")).isEqualTo("plain text");
    }

    @Test
    void stripHtml_returnsEmpty_whenOnlyTags() {
        assertThat(service.stripHtml("<div><span></span></div>")).isEmpty();
    }

    // --- sanitize ---

    @Test
    void sanitize_keepsBasicFormattingTags() {
        String result = service.sanitize("<p>Hello <b>world</b></p>");
        assertThat(result).contains("<b>world</b>");
    }

    @Test
    void sanitize_removesScriptTags() {
        String result = service.sanitize("<p>Safe</p><script>alert('xss')</script>");
        assertThat(result).doesNotContain("<script>");
        assertThat(result).doesNotContain("alert");
    }

    @Test
    void sanitize_removesInlineEventHandlers() {
        String result = service.sanitize("<p onclick=\"evil()\">Click me</p>");
        assertThat(result).doesNotContain("onclick");
    }

    @Test
    void sanitize_keepsImageTags() {
        String result = service.sanitize("<img src=\"https://example.com/img.png\" />");
        assertThat(result).contains("<img");
    }
}
