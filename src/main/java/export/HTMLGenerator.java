package export;

import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Generate HTML website from markdown scriptums
 */
public class HTMLGenerator {

    private Parser parser = Parser.builder().build();
    private HtmlRenderer renderer = HtmlRenderer.builder().build();

    /**
     * Generate complete website from markdown files
     */
    public void generateWebsite(String markdownDir, String outputDir) throws IOException {
        File mdDir = new File(markdownDir);
        File outDir = new File(outputDir);

        // Create output directories
        outDir.mkdirs();
        new File(outDir, "topics").mkdirs();
        new File(outDir, "css").mkdirs();
        new File(outDir, "js").mkdirs();

        // Generate CSS
        generateCSS(new File(outDir, "css/style.css"));

        // Generate JavaScript
        generateJavaScript(new File(outDir, "js/navigation.js"));

        // Find all markdown files
        File[] mdFiles = mdDir.listFiles((dir, name) -> name.endsWith(".md"));
        if (mdFiles == null) mdFiles = new File[0];
        Arrays.sort(mdFiles, Comparator.comparing(File::getName));

        // Generate topic pages
        List<TopicPage> pages = new ArrayList<>();
        for (File mdFile : mdFiles) {
            String topic = extractTopicName(mdFile.getName());
            String content = new String(Files.readAllBytes(mdFile.toPath()));
            String html = convertMarkdownToHTML(content);

            String htmlFilename = mdFile.getName().replace(".md", ".html");
            String outputPath = Paths.get(outDir.getPath(), "topics", htmlFilename).toString();

            String fullHtml = applyTemplate(html, topic, pages.size(), mdFiles.length);
            Files.write(Paths.get(outputPath), fullHtml.getBytes());

            pages.add(new TopicPage(topic, "topics/" + htmlFilename));
            System.out.println("✓ Generated HTML: " + htmlFilename);
        }

        // Generate index page
        generateIndex(new File(outDir, "index.html"), pages);

        System.out.println("✓ Website generated in: " + outputDir);
    }

    /**
     * Convert markdown to HTML
     */
    public String convertMarkdownToHTML(String markdown) {
        Node document = parser.parse(markdown);
        return renderer.render(document);
    }

    /**
     * Apply HTML template to content
     */
    public String applyTemplate(String content, String title, int pageNum, int totalPages) {
        String prevLink = pageNum > 0 ? String.format("../%s.html", String.format("%02d", pageNum)) : "#";
        String nextLink = pageNum < totalPages - 1 ? String.format("../%s.html", String.format("%02d", pageNum + 2)) : "#";

        return String.format("""
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>%s - BSYS Study Guide</title>
                <link rel="stylesheet" href="../css/style.css">
            </head>
            <body>
                <nav class="navbar">
                    <div class="nav-container">
                        <a href="../index.html" class="nav-brand">BSYS Study Guide</a>
                        <div class="nav-links">
                            <a href="../index.html">Home</a>
                            <a href="#" onclick="toggleSearch()">Search</a>
                        </div>
                    </div>
                </nav>

                <div class="container">
                    <aside class="sidebar">
                        <h3>Navigation</h3>
                        <div class="nav-section">
                            <a href="../index.html">← All Topics</a>
                        </div>
                    </aside>

                    <main class="content">
                        %s

                        <div class="page-navigation">
                            <a href="%s" class="nav-btn">← Previous</a>
                            <a href="%s" class="nav-btn">Next →</a>
                        </div>
                    </main>
                </div>

                <footer>
                    <p>BSYS Operating Systems Study Guide | Generated from course materials</p>
                </footer>

                <script src="../js/navigation.js"></script>
            </body>
            </html>
            """, title, content, prevLink, nextLink);
    }

    /**
     * Generate index page
     */
    private void generateIndex(File outputFile, List<TopicPage> pages) throws IOException {
        StringBuilder html = new StringBuilder();
        html.append("""
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>BSYS Study Guide - Operating Systems</title>
                <link rel="stylesheet" href="css/style.css">
            </head>
            <body>
                <nav class="navbar">
                    <div class="nav-container">
                        <a href="index.html" class="nav-brand">BSYS Study Guide</a>
                    </div>
                </nav>

                <div class="container">
                    <main class="content">
                        <h1>Operating Systems Study Guide</h1>
                        <p class="subtitle">Comprehensive study materials for BSYS course</p>

                        <div class="topic-grid">
            """);

        for (TopicPage page : pages) {
            html.append(String.format("""
                            <div class="topic-card">
                                <h3>%s</h3>
                                <a href="%s" class="card-link">Study this topic →</a>
                            </div>
                """, page.title, page.url));
        }

        html.append("""
                        </div>

                        <div class="info-section">
                            <h2>About This Guide</h2>
                            <p>This study guide covers key concepts in Operating Systems including:</p>
                            <ul>
                                <li>Process and thread management</li>
                                <li>Memory management and virtual memory</li>
                                <li>File systems and I/O</li>
                                <li>Deadlocks and synchronization</li>
                                <li>Unix/Linux commands and system calls</li>
                            </ul>
                        </div>
                    </main>
                </div>

                <footer>
                    <p>BSYS Operating Systems Study Guide | Generated from course materials</p>
                </footer>

                <script src="js/navigation.js"></script>
            </body>
            </html>
            """);

        Files.write(outputFile.toPath(), html.toString().getBytes());
        System.out.println("✓ Generated index.html");
    }

    /**
     * Generate CSS file
     */
    private void generateCSS(File outputFile) throws IOException {
        String css = """
            * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
            }

            body {
                font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
                line-height: 1.6;
                color: #333;
                background: #f5f5f5;
            }

            .navbar {
                background: #2c3e50;
                color: white;
                padding: 1rem 0;
                box-shadow: 0 2px 5px rgba(0,0,0,0.1);
            }

            .nav-container {
                max-width: 1200px;
                margin: 0 auto;
                padding: 0 2rem;
                display: flex;
                justify-content: space-between;
                align-items: center;
            }

            .nav-brand {
                font-size: 1.5rem;
                font-weight: bold;
                color: white;
                text-decoration: none;
            }

            .nav-links a {
                color: white;
                text-decoration: none;
                margin-left: 2rem;
            }

            .nav-links a:hover {
                color: #3498db;
            }

            .container {
                max-width: 1200px;
                margin: 2rem auto;
                padding: 0 2rem;
                display: grid;
                grid-template-columns: 250px 1fr;
                gap: 2rem;
            }

            .sidebar {
                background: white;
                padding: 1.5rem;
                border-radius: 8px;
                box-shadow: 0 2px 5px rgba(0,0,0,0.1);
                height: fit-content;
                position: sticky;
                top: 2rem;
            }

            .sidebar h3 {
                margin-bottom: 1rem;
                color: #2c3e50;
            }

            .sidebar a {
                display: block;
                padding: 0.5rem;
                color: #3498db;
                text-decoration: none;
                border-radius: 4px;
            }

            .sidebar a:hover {
                background: #ecf0f1;
            }

            .content {
                background: white;
                padding: 2rem;
                border-radius: 8px;
                box-shadow: 0 2px 5px rgba(0,0,0,0.1);
            }

            .content h1 {
                color: #2c3e50;
                margin-bottom: 1rem;
                border-bottom: 3px solid #3498db;
                padding-bottom: 0.5rem;
            }

            .content h2 {
                color: #34495e;
                margin-top: 2rem;
                margin-bottom: 1rem;
            }

            .content h3 {
                color: #7f8c8d;
                margin-top: 1.5rem;
                margin-bottom: 0.75rem;
            }

            .content p {
                margin-bottom: 1rem;
            }

            .content ul, .content ol {
                margin-left: 2rem;
                margin-bottom: 1rem;
            }

            .content code {
                background: #ecf0f1;
                padding: 0.2rem 0.4rem;
                border-radius: 3px;
                font-family: 'Monaco', 'Courier New', monospace;
                font-size: 0.9em;
            }

            .content pre {
                background: #2c3e50;
                color: #ecf0f1;
                padding: 1rem;
                border-radius: 5px;
                overflow-x: auto;
                margin-bottom: 1rem;
            }

            .content pre code {
                background: transparent;
                padding: 0;
                color: #ecf0f1;
            }

            .subtitle {
                font-size: 1.2rem;
                color: #7f8c8d;
                margin-bottom: 2rem;
            }

            .topic-grid {
                display: grid;
                grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
                gap: 1.5rem;
                margin: 2rem 0;
            }

            .topic-card {
                background: #ecf0f1;
                padding: 1.5rem;
                border-radius: 8px;
                border-left: 4px solid #3498db;
            }

            .topic-card h3 {
                color: #2c3e50;
                margin-bottom: 0.5rem;
            }

            .card-link {
                color: #3498db;
                text-decoration: none;
                font-weight: 500;
            }

            .card-link:hover {
                text-decoration: underline;
            }

            .page-navigation {
                display: flex;
                justify-content: space-between;
                margin-top: 3rem;
                padding-top: 2rem;
                border-top: 2px solid #ecf0f1;
            }

            .nav-btn {
                padding: 0.75rem 1.5rem;
                background: #3498db;
                color: white;
                text-decoration: none;
                border-radius: 5px;
                font-weight: 500;
            }

            .nav-btn:hover {
                background: #2980b9;
            }

            .info-section {
                margin-top: 3rem;
                padding: 2rem;
                background: #ecf0f1;
                border-radius: 8px;
            }

            footer {
                text-align: center;
                padding: 2rem;
                color: #7f8c8d;
                margin-top: 3rem;
            }

            @media (max-width: 768px) {
                .container {
                    grid-template-columns: 1fr;
                }

                .sidebar {
                    position: static;
                }

                .topic-grid {
                    grid-template-columns: 1fr;
                }
            }
            """;

        Files.write(outputFile.toPath(), css.getBytes());
        System.out.println("✓ Generated style.css");
    }

    /**
     * Generate JavaScript file
     */
    private void generateJavaScript(File outputFile) throws IOException {
        String js = """
            function toggleSearch() {
                alert('Search functionality - to be implemented');
            }

            // Smooth scrolling for anchor links
            document.addEventListener('DOMContentLoaded', function() {
                const links = document.querySelectorAll('a[href^="#"]');
                links.forEach(link => {
                    link.addEventListener('click', function(e) {
                        const href = this.getAttribute('href');
                        if (href !== '#') {
                            e.preventDefault();
                            const target = document.querySelector(href);
                            if (target) {
                                target.scrollIntoView({ behavior: 'smooth' });
                            }
                        }
                    });
                });
            });
            """;

        Files.write(outputFile.toPath(), js.getBytes());
        System.out.println("✓ Generated navigation.js");
    }

    /**
     * Extract topic name from filename
     */
    private String extractTopicName(String filename) {
        return filename
            .replaceAll("^\\d+-", "")
            .replace(".md", "")
            .replace("-", " ")
            .replace("_", " ");
    }

    /**
     * Represents a topic page
     */
    private static class TopicPage {
        final String title;
        final String url;

        TopicPage(String title, String url) {
            this.title = title;
            this.url = url;
        }
    }
}
