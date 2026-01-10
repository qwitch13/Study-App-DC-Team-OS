package content;

import pdf.TextExtractor;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Process extracted PDF content and prepare for flashcard/question generation
 */
public class ContentProcessor {

    public enum ContentType {
        DEFINITION,  // Term definitions
        CONCEPT,     // Conceptual explanations
        CODE,        // Code examples and commands
        PROBLEM      // Problem scenarios
    }

    /**
     * Process all content from parsed directory
     */
    public ProcessedContent processAllContent(String parsedDir) throws IOException {
        File dir = new File(parsedDir);
        if (!dir.exists()) {
            throw new IOException("Parsed directory not found: " + parsedDir);
        }

        ProcessedContent content = new ProcessedContent();

        // Process main topic files
        File[] files = dir.listFiles((d, name) -> name.endsWith(".txt") && !name.equals("metadata.json"));
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    processFile(file, content);
                }
            }
        }

        // Process exercises subdirectory
        File exercisesDir = new File(dir, "exercises");
        if (exercisesDir.exists() && exercisesDir.isDirectory()) {
            File[] exerciseFiles = exercisesDir.listFiles((d, name) -> name.endsWith(".txt"));
            if (exerciseFiles != null) {
                for (File file : exerciseFiles) {
                    processFile(file, content);
                }
            }
        }

        return content;
    }

    /**
     * Process a single file
     */
    private void processFile(File file, ProcessedContent content) throws IOException {
        System.out.println("Processing content from: " + file.getName());

        String text = new String(Files.readAllBytes(file.toPath()));
        String topic = inferTopicFromFilename(file.getName());

        // Extract definitions
        Map<String, String> definitions = TextExtractor.extractDefinitions(text);
        for (Map.Entry<String, String> entry : definitions.entrySet()) {
            ContentItem item = new ContentItem(
                ContentType.DEFINITION,
                topic,
                entry.getKey(),
                entry.getValue(),
                assignDifficulty(entry.getValue(), ContentType.DEFINITION)
            );
            content.addItem(item);
        }

        // Extract code examples
        List<String> codeExamples = TextExtractor.extractCodeExamples(text);
        for (String code : codeExamples) {
            if (code.length() > 10 && code.length() < 500) {
                ContentItem item = new ContentItem(
                    ContentType.CODE,
                    topic,
                    "Code example",
                    code,
                    assignDifficulty(code, ContentType.CODE)
                );
                content.addItem(item);
            }
        }

        // Extract conceptual content (paragraphs)
        List<String> concepts = extractConcepts(text);
        for (String concept : concepts) {
            ContentItem item = new ContentItem(
                ContentType.CONCEPT,
                topic,
                extractConceptTitle(concept),
                concept,
                assignDifficulty(concept, ContentType.CONCEPT)
            );
            content.addItem(item);
        }

        // Extract problem scenarios
        List<String> problems = extractProblems(text);
        for (String problem : problems) {
            ContentItem item = new ContentItem(
                ContentType.PROBLEM,
                topic,
                "Problem scenario",
                problem,
                assignDifficulty(problem, ContentType.PROBLEM)
            );
            content.addItem(item);
        }

        System.out.println("  Found: " + definitions.size() + " definitions, " +
                           codeExamples.size() + " code examples, " +
                           concepts.size() + " concepts, " +
                           problems.size() + " problems");
    }

    /**
     * Extract conceptual content (explanatory paragraphs)
     */
    private List<String> extractConcepts(String text) {
        List<String> concepts = new ArrayList<>();
        String[] paragraphs = text.split("\n\n+");

        for (String para : paragraphs) {
            String trimmed = para.trim();

            // Look for explanatory paragraphs
            if (trimmed.length() > 100 && trimmed.length() < 800 &&
                !trimmed.matches("^\\d+\\s*$") && // Not just numbers
                !trimmed.startsWith("Figure") &&
                !trimmed.startsWith("Table") &&
                !trimmed.contains("©") &&
                trimmed.split("\\s+").length > 15) { // At least 15 words

                concepts.add(trimmed);
            }
        }

        return concepts.subList(0, Math.min(concepts.size(), 20)); // Limit per file
    }

    /**
     * Extract problem scenarios
     */
    private List<String> extractProblems(String text) {
        List<String> problems = new ArrayList<>();
        String[] lines = text.split("\n");

        Pattern problemPattern = Pattern.compile(
            "(?i)(problem|exercise|scenario|example|question|task)\\s*:?",
            Pattern.CASE_INSENSITIVE
        );

        StringBuilder currentProblem = null;

        for (String line : lines) {
            if (problemPattern.matcher(line).find()) {
                if (currentProblem != null && currentProblem.length() > 50) {
                    problems.add(currentProblem.toString().trim());
                }
                currentProblem = new StringBuilder(line).append("\n");
            } else if (currentProblem != null) {
                if (line.trim().isEmpty()) {
                    if (currentProblem.length() > 50) {
                        problems.add(currentProblem.toString().trim());
                    }
                    currentProblem = null;
                } else {
                    currentProblem.append(line).append("\n");
                }
            }
        }

        if (currentProblem != null && currentProblem.length() > 50) {
            problems.add(currentProblem.toString().trim());
        }

        return problems.subList(0, Math.min(problems.size(), 10)); // Limit per file
    }

    /**
     * Extract concept title from text
     */
    private String extractConceptTitle(String text) {
        String[] lines = text.split("\n");
        if (lines.length > 0) {
            String firstLine = lines[0].trim();
            if (firstLine.length() > 5 && firstLine.length() < 60) {
                return firstLine;
            }
        }

        // Fallback: extract first few words
        String[] words = text.trim().split("\\s+");
        if (words.length > 3) {
            return words[0] + " " + words[1] + " " + words[2] + "...";
        }
        return "Concept";
    }

    /**
     * Assign difficulty level (1-3) based on content complexity
     */
    public int assignDifficulty(String content, ContentType type) {
        int difficulty = 1; // Default: Easy

        // Length-based complexity
        if (content.length() > 300) difficulty++;
        if (content.length() > 600) difficulty++;

        // Keyword-based complexity
        String lower = content.toLowerCase();
        int complexityScore = 0;

        // Advanced terms
        String[] advancedTerms = {
            "algorithm", "complexity", "synchronization", "concurrent",
            "deadlock", "semaphore", "mutex", "critical section",
            "virtual memory", "paging", "segmentation", "scheduling"
        };
        for (String term : advancedTerms) {
            if (lower.contains(term)) complexityScore++;
        }

        if (complexityScore >= 3) difficulty = 3;
        else if (complexityScore >= 1) difficulty = Math.max(difficulty, 2);

        // Type-based adjustments
        if (type == ContentType.PROBLEM) {
            difficulty = Math.max(difficulty, 2); // Problems are at least medium
        } else if (type == ContentType.CODE && content.contains("#include")) {
            difficulty = Math.max(difficulty, 2); // C/C++ code is medium+
        }

        return Math.min(3, difficulty);
    }

    /**
     * Infer topic from filename
     */
    private String inferTopicFromFilename(String filename) {
        filename = filename.toLowerCase().replace(".txt", "");

        if (filename.contains("introduction") || filename.equals("01-introduction")) {
            return "Introduction";
        } else if (filename.contains("unix") || filename.contains("linux")) {
            return "Unix/Linux";
        } else if (filename.contains("history")) {
            return "OS History";
        } else if (filename.contains("process") || filename.contains("thread")) {
            return "Processes & Threads";
        } else if (filename.contains("memory")) {
            return "Memory Management";
        } else if (filename.contains("file")) {
            return "File Systems";
        } else if (filename.contains("deadlock")) {
            return "Deadlocks";
        } else if (filename.contains("exercise")) {
            return "Lab Exercises";
        }

        return "General";
    }

    /**
     * Container for a content item
     */
    public static class ContentItem {
        public final ContentType type;
        public final String topic;
        public final String title;
        public final String content;
        public final int difficulty;

        public ContentItem(ContentType type, String topic, String title, String content, int difficulty) {
            this.type = type;
            this.topic = topic;
            this.title = title;
            this.content = content;
            this.difficulty = difficulty;
        }
    }

    /**
     * Container for all processed content
     */
    public static class ProcessedContent {
        private List<ContentItem> items = new ArrayList<>();
        private Map<String, List<ContentItem>> byTopic = new HashMap<>();
        private Map<ContentType, List<ContentItem>> byType = new HashMap<>();

        public void addItem(ContentItem item) {
            items.add(item);

            byTopic.computeIfAbsent(item.topic, k -> new ArrayList<>()).add(item);
            byType.computeIfAbsent(item.type, k -> new ArrayList<>()).add(item);
        }

        public List<ContentItem> getAllItems() {
            return items;
        }

        public List<ContentItem> getByTopic(String topic) {
            return byTopic.getOrDefault(topic, new ArrayList<>());
        }

        public List<ContentItem> getByType(ContentType type) {
            return byType.getOrDefault(type, new ArrayList<>());
        }

        public Set<String> getTopics() {
            return byTopic.keySet();
        }

        public int getTotalCount() {
            return items.size();
        }
    }
}
