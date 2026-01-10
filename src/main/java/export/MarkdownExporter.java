package export;

import content.ContentProcessor.*;
import content.FlashcardGenerator.Flashcard;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Export content as Markdown scriptums (study guides)
 * Creates both single comprehensive file and multiple topic-based files
 */
public class MarkdownExporter {

    /**
     * Export single comprehensive markdown scriptum
     */
    public void exportSingleScriptum(ProcessedContent content, List<Flashcard> flashcards, String outputPath) throws IOException {
        StringBuilder md = new StringBuilder();

        // Title and TOC
        md.append("# Operating Systems - Complete Study Guide\n\n");
        md.append("*Generated from BSYS course materials*\n\n");
        md.append("---\n\n");
        md.append("## Table of Contents\n\n");

        List<String> topics = new ArrayList<>(content.getTopics());
        Collections.sort(topics);

        for (int i = 0; i < topics.size(); i++) {
            md.append(String.format("%d. [%s](#%s)\n", i + 1, topics.get(i),
                topics.get(i).toLowerCase().replace(" ", "-").replace("/", "-").replace("&", "and")));
        }
        md.append("\n---\n\n");

        // Generate content for each topic
        for (String topic : topics) {
            md.append(generateTopicSection(topic, content.getByTopic(topic), flashcards));
            md.append("\n---\n\n");
        }

        // Write to file
        Files.write(Paths.get(outputPath), md.toString().getBytes());
        System.out.println("✓ Single scriptum created: " + outputPath);
    }

    /**
     * Export multiple markdown scriptums by topic
     */
    public void exportMultipleScriptums(ProcessedContent content, List<Flashcard> flashcards, String outputDir) throws IOException {
        File dir = new File(outputDir);
        dir.mkdirs();

        List<String> topics = new ArrayList<>(content.getTopics());
        Collections.sort(topics);

        int fileNumber = 1;
        for (String topic : topics) {
            StringBuilder md = new StringBuilder();

            // Title
            md.append(String.format("# %s\n\n", topic));
            md.append("*BSYS - Operating Systems*\n\n");
            md.append("---\n\n");

            // Content
            md.append(generateTopicSection(topic, content.getByTopic(topic), flashcards));

            // Write file
            String filename = String.format("%02d-%s.md", fileNumber++,
                topic.toLowerCase().replace(" ", "-").replace("/", "-").replace("&", "and"));
            String filepath = Paths.get(outputDir, filename).toString();

            Files.write(Paths.get(filepath), md.toString().getBytes());
            System.out.println("✓ Topic scriptum created: " + filename);
        }
    }

    /**
     * Generate markdown section for a topic
     */
    private String generateTopicSection(String topic, List<ContentItem> items, List<Flashcard> flashcards) {
        StringBuilder md = new StringBuilder();

        md.append(String.format("## %s\n\n", topic));

        // Group items by type
        Map<ContentType, List<ContentItem>> byType = new HashMap<>();
        for (ContentItem item : items) {
            byType.computeIfAbsent(item.type, k -> new ArrayList<>()).add(item);
        }

        // Definitions
        if (byType.containsKey(ContentType.DEFINITION)) {
            md.append("### Key Definitions\n\n");
            for (ContentItem item : byType.get(ContentType.DEFINITION)) {
                md.append(formatDefinition(item.title, item.content));
            }
            md.append("\n");
        }

        // Concepts
        if (byType.containsKey(ContentType.CONCEPT)) {
            md.append("### Core Concepts\n\n");
            for (ContentItem item : byType.get(ContentType.CONCEPT).subList(0, Math.min(5, byType.get(ContentType.CONCEPT).size()))) {
                md.append(formatConcept(item.title, item.content));
            }
            md.append("\n");
        }

        // Code examples
        if (byType.containsKey(ContentType.CODE)) {
            md.append("### Code Examples & Commands\n\n");
            for (ContentItem item : byType.get(ContentType.CODE).subList(0, Math.min(5, byType.get(ContentType.CODE).size()))) {
                md.append(formatCodeExample(item.content));
            }
            md.append("\n");
        }

        // Study notes (flashcards for this topic)
        List<Flashcard> topicCards = new ArrayList<>();
        for (Flashcard card : flashcards) {
            if (card.topic.equals(topic)) {
                topicCards.add(card);
            }
        }

        if (!topicCards.isEmpty()) {
            md.append("### Study Notes\n\n");
            md.append("*Key points to remember:*\n\n");
            for (int i = 0; i < Math.min(10, topicCards.size()); i++) {
                Flashcard card = topicCards.get(i);
                md.append(formatStudyNote(card));
            }
            md.append("\n");
        }

        return md.toString();
    }

    /**
     * Format definition entry
     */
    private String formatDefinition(String term, String definition) {
        return String.format("**%s**\n\n%s\n\n", term, definition);
    }

    /**
     * Format concept entry
     */
    private String formatConcept(String title, String content) {
        return String.format("#### %s\n\n%s\n\n", title, formatText(content));
    }

    /**
     * Format code example
     */
    private String formatCodeExample(String code) {
        // Detect language
        String language = "";
        if (code.contains("#include") || code.contains("int main")) {
            language = "c";
        } else if (code.matches("^[$#]\\s+.*")) {
            language = "bash";
        }

        return String.format("```%s\n%s\n```\n\n", language, code.trim());
    }

    /**
     * Format study note from flashcard
     */
    private String formatStudyNote(Flashcard card) {
        return String.format("- **%s**\n  \n  %s\n\n",
            card.front.replace("\n", " "),
            card.back.replace("\n", "\n  "));
    }

    /**
     * Format text with proper markdown
     */
    private String formatText(String text) {
        // Convert bullet points
        text = text.replaceAll("(?m)^•\\s+", "- ");

        // Preserve code blocks
        text = text.replaceAll("(`[^`]+`)", "**$1**");

        return text;
    }
}
