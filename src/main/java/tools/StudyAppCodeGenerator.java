package tools;

import content.FlashcardGenerator.Flashcard;
import content.QuestionGenerator.Question;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Generate Java code snippets for integrating content into StudyApp
 */
public class StudyAppCodeGenerator {

    /**
     * Generate complete Java code for StudyApp integration
     */
    public void generateCode(List<Flashcard> flashcards, List<Question> questions, String outputPath) throws IOException {
        StringBuilder code = new StringBuilder();

        // Header
        code.append("// ============================================================================\n");
        code.append("// GENERATED CODE FOR STUDYAPP INTEGRATION\n");
        code.append("// Generated: ").append(new Date()).append("\n");
        code.append("//\n");
        code.append("// This file contains Java code to add to StudyApp.java\n");
        code.append("// Add flashcards section to initializeContent() method around line 832+\n");
        code.append("// Add questions section to initializeContent() method around line 1958+\n");
        code.append("// ============================================================================\n\n");

        // Flashcards section
        code.append(generateFlashcardCode(flashcards));
        code.append("\n\n");

        // Questions section
        code.append(generateQuestionCode(questions));
        code.append("\n\n");

        // Statistics
        code.append("// ============================================================================\n");
        code.append("// GENERATION STATISTICS\n");
        code.append("// ============================================================================\n");
        code.append("// Total flashcards: ").append(flashcards.size()).append("\n");
        code.append("// Total questions: ").append(questions.size()).append("\n");
        code.append("//\n");
        code.append("// Flashcards by topic:\n");
        Map<String, Integer> flashcardsByTopic = countByTopic(flashcards);
        for (Map.Entry<String, Integer> entry : flashcardsByTopic.entrySet()) {
            code.append("//   ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        code.append("//\n");
        code.append("// Flashcards by difficulty:\n");
        code.append("//   Easy (1): ").append(countByDifficulty(flashcards, 1)).append("\n");
        code.append("//   Medium (2): ").append(countByDifficulty(flashcards, 2)).append("\n");
        code.append("//   Hard (3): ").append(countByDifficulty(flashcards, 3)).append("\n");
        code.append("// ============================================================================\n");

        Files.write(Paths.get(outputPath), code.toString().getBytes());
        System.out.println("✓ StudyApp integration code generated: " + outputPath);
        System.out.println("  Flashcards: " + flashcards.size());
        System.out.println("  Questions: " + questions.size());
    }

    /**
     * Generate flashcard code
     */
    public String generateFlashcardCode(List<Flashcard> flashcards) {
        StringBuilder code = new StringBuilder();

        code.append("// ==================== GENERATED BSYS FLASHCARDS ====================\n");
        code.append("// Add these lines to initializeContent() method after existing BSYS flashcards\n");
        code.append("// Location: Around line 832 in StudyApp.java\n\n");

        // Group by topic for better organization
        Map<String, List<Flashcard>> byTopic = new LinkedHashMap<>();
        for (Flashcard card : flashcards) {
            byTopic.computeIfAbsent(card.topic, k -> new ArrayList<>()).add(card);
        }

        for (Map.Entry<String, List<Flashcard>> entry : byTopic.entrySet()) {
            String topic = entry.getKey();
            List<Flashcard> cards = entry.getValue();

            code.append("        // ").append(topic).append(" (").append(cards.size()).append(" cards)\n");

            for (Flashcard card : cards) {
                code.append("        flashcards.add(new Flashcard(");
                code.append(escapeString(card.subject)).append(", ");
                code.append(escapeString(card.topic)).append(",\n");
                code.append("            ").append(escapeString(card.front)).append(",\n");
                code.append("            ").append(escapeString(card.back)).append(",\n");
                code.append("            ").append(card.difficulty);
                code.append("));\n\n");
            }
        }

        code.append("        // ==================== END GENERATED FLASHCARDS ====================\n");
        return code.toString();
    }

    /**
     * Generate question code
     */
    public String generateQuestionCode(List<Question> questions) {
        StringBuilder code = new StringBuilder();

        code.append("// ==================== GENERATED BSYS QUESTIONS ====================\n");
        code.append("// Add these lines to initializeContent() method after existing BSYS questions\n");
        code.append("// Location: Around line 1958 in StudyApp.java\n\n");

        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);

            code.append("        // Question ").append(i + 1).append("\n");
            code.append("        questions.add(new Question(");
            code.append(escapeString(q.subject)).append(",\n");
            code.append("            ").append(escapeString(q.question)).append(",\n");
            code.append("            new String[]{\n");

            for (int j = 0; j < q.options.length; j++) {
                code.append("                ").append(escapeString(q.options[j]));
                if (j < q.options.length - 1) {
                    code.append(",");
                }
                code.append("\n");
            }

            code.append("            },\n");
            code.append("            ").append(q.correctIndex).append(",\n");
            code.append("            ").append(escapeString(q.explanation));
            code.append("));\n\n");
        }

        code.append("        // ==================== END GENERATED QUESTIONS ====================\n");
        return code.toString();
    }

    /**
     * Escape Java string (handle quotes, newlines, etc.)
     */
    public String escapeString(String str) {
        if (str == null) return "\"\"";

        // Handle multi-line strings
        if (str.contains("\n")) {
            // Use Java text blocks for multi-line (Java 15+) or concatenation
            StringBuilder escaped = new StringBuilder("\"");
            String[] lines = str.split("\n");

            for (int i = 0; i < lines.length; i++) {
                String line = lines[i]
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\t", "\\t");

                escaped.append(line);

                if (i < lines.length - 1) {
                    escaped.append("\\n\" +\n                \"");
                }
            }

            escaped.append("\"");
            return escaped.toString();
        } else {
            // Single-line string
            return "\"" + str
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\t", "\\t")
                .replace("\r", "")
                + "\"";
        }
    }

    /**
     * Count flashcards by topic
     */
    private Map<String, Integer> countByTopic(List<Flashcard> flashcards) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (Flashcard card : flashcards) {
            counts.put(card.topic, counts.getOrDefault(card.topic, 0) + 1);
        }
        return counts;
    }

    /**
     * Count flashcards by difficulty
     */
    private int countByDifficulty(List<Flashcard> flashcards, int difficulty) {
        int count = 0;
        for (Flashcard card : flashcards) {
            if (card.difficulty == difficulty) count++;
        }
        return count;
    }
}
