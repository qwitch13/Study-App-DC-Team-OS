package pdf;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Batch PDF text extraction and organization
 * Processes all PDF files and organizes content by topic
 */
public class TextExtractor {

    private static final Pattern DEFINITION_PATTERN = Pattern.compile(
        "(?:^|\\n)\\s*([A-Z][A-Za-z\\s]{2,30})\\s*[:–-]\\s*(.+?)(?=\\n\\s*[A-Z]|\\n\\n|$)",
        Pattern.MULTILINE
    );

    /**
     * Extract text from all PDFs in source directory and organize in target directory
     */
    public static void extractAllPDFs(String sourceDir, String targetDir) throws IOException {
        File source = new File(sourceDir);
        File target = new File(targetDir);

        if (!source.exists()) {
            throw new IOException("Source directory not found: " + sourceDir);
        }

        // Create target directories
        target.mkdirs();
        new File(target, "exercises").mkdirs();

        // Find all PDF files
        List<File> pdfFiles = findPDFFiles(source);
        System.out.println("Found " + pdfFiles.size() + " PDF files");

        Map<String, TopicMetadata> metadata = new HashMap<>();
        int fileCount = 0;

        for (File pdfFile : pdfFiles) {
            System.out.println("Processing: " + pdfFile.getName());

            try {
                // Extract text
                String text = PDFParser.parsePDF(pdfFile);

                // Determine topic and output filename
                TopicInfo topicInfo = identifyTopic(pdfFile.getName(), text);
                String outputPath = determineOutputPath(target, pdfFile.getName(), topicInfo);

                // Save extracted text
                saveTextFile(outputPath, text);

                // Extract structured data
                Map<String, String> definitions = extractDefinitions(text);
                List<String> codeExamples = extractCodeExamples(text);

                // Store metadata
                metadata.put(topicInfo.topic, new TopicMetadata(
                    pdfFile.getName(),
                    topicInfo.topic,
                    topicInfo.topicNumber,
                    definitions.size(),
                    codeExamples.size(),
                    text.length()
                ));

                fileCount++;
                System.out.println("  ✓ Saved to: " + outputPath);
                System.out.println("    Definitions: " + definitions.size() +
                                   ", Code examples: " + codeExamples.size());

            } catch (Exception e) {
                System.err.println("  ✗ Error processing " + pdfFile.getName() + ": " + e.getMessage());
            }
        }

        // Save metadata
        saveMetadata(new File(target, "metadata.json"), metadata);
        System.out.println("\n" + fileCount + " files processed successfully");
    }

    /**
     * Identify topic from filename and content
     */
    public static TopicInfo identifyTopic(String filename, String text) {
        String topic = "unknown";
        int topicNumber = 99;

        // Parse from filename
        filename = filename.toLowerCase().replace(".pdf", "");

        if (filename.contains("introduction") || filename.contains("intro")) {
            topic = "Introduction";
            topicNumber = 1;
        } else if (filename.contains("unix") || filename.contains("linux")) {
            topic = "Unix-Linux";
            topicNumber = 2;
        } else if (filename.contains("history")) {
            topic = "History";
            topicNumber = 3;
        } else if (filename.contains("process") || filename.contains("thread")) {
            topic = "Processes-Threads";
            topicNumber = 4;
        } else if (filename.contains("memory")) {
            topic = "Memory";
            topicNumber = 5;
        } else if (filename.contains("file") && filename.contains("system")) {
            topic = "File-Systems";
            topicNumber = 6;
        } else if (filename.contains("deadlock")) {
            topic = "Deadlocks";
            topicNumber = 7;
        } else if (filename.contains("exercise")) {
            topic = "Exercise";
            topicNumber = extractNumberFromFilename(filename);
        } else if (filename.contains("distance")) {
            topic = "Distance-Learning";
            topicNumber = extractNumberFromFilename(filename);
        }

        return new TopicInfo(topic, topicNumber);
    }

    /**
     * Extract definitions from text
     */
    public static Map<String, String> extractDefinitions(String text) {
        Map<String, String> definitions = new LinkedHashMap<>();
        Matcher matcher = DEFINITION_PATTERN.matcher(text);

        while (matcher.find()) {
            String term = matcher.group(1).trim();
            String definition = matcher.group(2).trim();

            // Filter out false positives
            if (definition.length() > 10 && definition.length() < 500 &&
                !term.matches("^(Page|Figure|Table|Slide|Lecture).*")) {
                definitions.put(term, definition);
            }
        }

        return definitions;
    }

    /**
     * Extract code examples and commands from text
     */
    public static List<String> extractCodeExamples(String text) {
        List<String> examples = new ArrayList<>();

        // Extract code blocks identified by PDFParser
        List<PDFParser.CodeBlock> codeBlocks = PDFParser.identifyCodeBlocks(text);
        for (PDFParser.CodeBlock block : codeBlocks) {
            if (block.code.trim().length() > 5) {
                examples.add(block.code.trim());
            }
        }

        // Also extract command-like lines
        String[] lines = text.split("\n");
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.matches("^\\$\\s+.*") || // Shell prompt
                trimmed.matches("^#\\s+.*") ||   // Root prompt
                trimmed.matches("^[a-z]+\\s+-[a-zA-Z].*")) { // Command with flag
                if (!examples.contains(trimmed)) {
                    examples.add(trimmed);
                }
            }
        }

        return examples;
    }

    /**
     * Find all PDF files recursively
     */
    private static List<File> findPDFFiles(File directory) {
        List<File> pdfFiles = new ArrayList<>();
        findPDFFilesRecursive(directory, pdfFiles);
        pdfFiles.sort(Comparator.comparing(File::getName));
        return pdfFiles;
    }

    private static void findPDFFilesRecursive(File directory, List<File> pdfFiles) {
        File[] files = directory.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                findPDFFilesRecursive(file, pdfFiles);
            } else if (file.getName().toLowerCase().endsWith(".pdf")) {
                pdfFiles.add(file);
            }
        }
    }

    /**
     * Determine output file path based on topic
     */
    private static String determineOutputPath(File targetDir, String filename, TopicInfo topicInfo) {
        String basename = filename.replace(".pdf", "").replaceAll("^\\d+_", "");

        if (topicInfo.topic.equals("Exercise")) {
            return new File(targetDir, "exercises/exercise" + topicInfo.topicNumber + ".txt").getAbsolutePath();
        } else if (topicInfo.topic.equals("Distance-Learning")) {
            return new File(targetDir, "distance-learning-" + topicInfo.topicNumber + ".txt").getAbsolutePath();
        } else {
            String safeName = String.format("%02d-%s.txt",
                topicInfo.topicNumber,
                topicInfo.topic.toLowerCase().replace(" ", "-"));
            return new File(targetDir, safeName).getAbsolutePath();
        }
    }

    /**
     * Extract number from filename
     */
    private static int extractNumberFromFilename(String filename) {
        Pattern p = Pattern.compile("(\\d+)");
        Matcher m = p.matcher(filename);
        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }
        return 0;
    }

    /**
     * Save text to file
     */
    private static void saveTextFile(String filepath, String content) throws IOException {
        Files.write(Paths.get(filepath), content.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Save metadata as JSON
     */
    private static void saveMetadata(File file, Map<String, TopicMetadata> metadata) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(metadata);
        Files.write(file.toPath(), json.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Topic information
     */
    static class TopicInfo {
        final String topic;
        final int topicNumber;

        TopicInfo(String topic, int topicNumber) {
            this.topic = topic;
            this.topicNumber = topicNumber;
        }
    }

    /**
     * Metadata for a topic
     */
    static class TopicMetadata {
        final String sourceFile;
        final String topic;
        final int topicNumber;
        final int definitionCount;
        final int codeExampleCount;
        final int textLength;

        TopicMetadata(String sourceFile, String topic, int topicNumber,
                     int definitionCount, int codeExampleCount, int textLength) {
            this.sourceFile = sourceFile;
            this.topic = topic;
            this.topicNumber = topicNumber;
            this.definitionCount = definitionCount;
            this.codeExampleCount = codeExampleCount;
            this.textLength = textLength;
        }
    }
}
