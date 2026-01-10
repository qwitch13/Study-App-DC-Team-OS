package export;

import content.FlashcardGenerator.Flashcard;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Export flashcards to Anki-compatible format
 * Format: Front[TAB]Back[TAB]Tags
 */
public class AnkiExporter {

    /**
     * Export flashcards to Anki format (tab-separated text file)
     */
    public void exportToAnki(List<Flashcard> flashcards, String outputPath) throws IOException {
        StringBuilder anki = new StringBuilder();

        // Header (optional, but helpful)
        anki.append("# Anki Import File\n");
        anki.append("# Format: Front[TAB]Back[TAB]Tags\n");
        anki.append("# Import into Anki: File > Import > Select this file > Set delimiter to Tab\n");
        anki.append("#\n\n");

        for (Flashcard card : flashcards) {
            String front = formatForAnki(card.front);
            String back = formatForAnki(card.back);
            String tags = generateTags(card);

            // Tab-separated format
            anki.append(front).append("\t").append(back).append("\t").append(tags).append("\n");
        }

        Files.write(Paths.get(outputPath), anki.toString().getBytes());
        System.out.println("✓ Anki export created: " + outputPath);
        System.out.println("  Total flashcards: " + flashcards.size());
    }

    /**
     * Format text for Anki (escape special characters, handle HTML)
     */
    public String formatForAnki(String text) {
        if (text == null) return "";

        // Replace newlines with HTML breaks for Anki
        text = text.replace("\n", "<br>");

        // Convert bullet points to HTML lists
        if (text.contains("• ")) {
            text = convertBulletsToHTML(text);
        }

        // Escape tabs (would break the format)
        text = text.replace("\t", "    ");

        // Preserve code formatting
        if (text.contains("```")) {
            text = convertCodeBlocksToHTML(text);
        }

        // Bold text formatting (if using markdown-style **)
        text = text.replaceAll("\\*\\*([^*]+)\\*\\*", "<b>$1</b>");

        return text.trim();
    }

    /**
     * Generate hierarchical tags for organization
     */
    public String generateTags(Flashcard card) {
        // Format: BSYS::Topic::Type
        String subject = card.subject;
        String topic = card.topic.replace(" ", "_").replace("/", "-").replace("&", "and");

        // Determine type from content
        String type = "General";
        String frontLower = card.front.toLowerCase();

        if (frontLower.startsWith("what is ")) {
            type = "Definition";
        } else if (frontLower.startsWith("how does ") || frontLower.startsWith("explain ")) {
            type = "Concept";
        } else if (frontLower.contains("command") || frontLower.contains("code")) {
            type = "Code";
        } else if (frontLower.contains("problem") || frontLower.contains("scenario")) {
            type = "Problem";
        }

        // Also add difficulty tag
        String difficultyTag = "Difficulty::";
        if (card.difficulty == 1) difficultyTag += "Easy";
        else if (card.difficulty == 2) difficultyTag += "Medium";
        else difficultyTag += "Hard";

        return String.format("%s::%s::%s %s", subject, topic, type, difficultyTag);
    }

    /**
     * Convert bullet points to HTML list
     */
    private String convertBulletsToHTML(String text) {
        String[] lines = text.split("<br>");
        StringBuilder html = new StringBuilder();
        boolean inList = false;

        for (String line : lines) {
            if (line.trim().startsWith("• ")) {
                if (!inList) {
                    html.append("<ul>");
                    inList = true;
                }
                html.append("<li>").append(line.trim().substring(2)).append("</li>");
            } else {
                if (inList) {
                    html.append("</ul>");
                    inList = false;
                }
                if (!line.trim().isEmpty()) {
                    html.append(line).append("<br>");
                }
            }
        }

        if (inList) {
            html.append("</ul>");
        }

        return html.toString();
    }

    /**
     * Convert markdown code blocks to HTML
     */
    private String convertCodeBlocksToHTML(String text) {
        // Simple code block conversion
        text = text.replaceAll("```([a-z]*)\\n([^`]+)```",
            "<pre><code>$2</code></pre>");

        // Inline code
        text = text.replaceAll("`([^`]+)`", "<code>$1</code>");

        return text;
    }

    /**
     * Export with additional metadata (optional enhanced format)
     */
    public void exportToAnkiEnhanced(List<Flashcard> flashcards, String outputPath) throws IOException {
        StringBuilder anki = new StringBuilder();

        // Enhanced header with deck structure
        anki.append("#separator:tab\n");
        anki.append("#html:true\n");
        anki.append("#deck:BSYS Operating Systems\n");
        anki.append("#\n");
        anki.append("Front\tBack\tTags\tDifficulty\tTopic\n");

        for (Flashcard card : flashcards) {
            String front = formatForAnki(card.front);
            String back = formatForAnki(card.back);
            String tags = generateTags(card);
            String difficulty = String.valueOf(card.difficulty);
            String topic = card.topic;

            anki.append(front).append("\t")
                .append(back).append("\t")
                .append(tags).append("\t")
                .append(difficulty).append("\t")
                .append(topic).append("\n");
        }

        // Save to enhanced filename
        String enhancedPath = outputPath.replace(".txt", "-enhanced.txt");
        Files.write(Paths.get(enhancedPath), anki.toString().getBytes());
        System.out.println("✓ Enhanced Anki export created: " + enhancedPath);
    }
}
