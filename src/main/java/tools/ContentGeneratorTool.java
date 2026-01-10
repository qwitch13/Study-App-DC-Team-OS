package tools;

import pdf.TextExtractor;
import content.*;
import content.ContentProcessor.*;
import content.FlashcardGenerator.Flashcard;
import content.QuestionGenerator.Question;
import export.*;

import java.io.File;
import java.util.List;

/**
 * Main tool to generate all study content from PDFs
 * Orchestrates the entire pipeline: PDF parsing → content processing → flashcard/question generation → export
 */
public class ContentGeneratorTool {

    public static void main(String[] args) {
        try {
            System.out.println("===============================================");
            System.out.println("  BSYS Content Generator Tool");
            System.out.println("  Operating Systems Study Materials Generator");
            System.out.println("===============================================\n");

            // Configure paths
            String pdfDir = "/Users/qwitch13/Downloads/BSYS CSDC28VZ WS2025_26_20251230_2210";
            String parsedDir = pdfDir + "/parsed-os";

            // Allow command-line override
            if (args.length >= 1) {
                pdfDir = args[0];
                parsedDir = pdfDir + "/parsed-os";
            }
            if (args.length >= 2) {
                parsedDir = args[1];
            }

            System.out.println("Configuration:");
            System.out.println("  PDF source directory: " + pdfDir);
            System.out.println("  Output directory: " + parsedDir);
            System.out.println();

            // ========== STEP 1: Parse PDFs ==========
            System.out.println("========== STEP 1: Parsing PDFs ==========");
            File parsedDirFile = new File(parsedDir);
            parsedDirFile.mkdirs();

            TextExtractor.extractAllPDFs(pdfDir, parsedDir);
            System.out.println();

            // ========== STEP 2: Process Content ==========
            System.out.println("========== STEP 2: Processing Content ==========");
            ContentProcessor processor = new ContentProcessor();
            ProcessedContent content = processor.processAllContent(parsedDir);
            System.out.println("Total content items extracted: " + content.getTotalCount());
            System.out.println("Topics found: " + content.getTopics());
            System.out.println();

            // ========== STEP 3: Generate Flashcards ==========
            System.out.println("========== STEP 3: Generating Flashcards ==========");
            FlashcardGenerator flashcardGen = new FlashcardGenerator();
            List<Flashcard> flashcards = flashcardGen.generateAll(content);
            System.out.println();

            // ========== STEP 4: Generate Quiz Questions ==========
            System.out.println("========== STEP 4: Generating Quiz Questions ==========");
            QuestionGenerator questionGen = new QuestionGenerator();
            List<Question> questions = questionGen.generateQuestions(content);
            System.out.println();

            // ========== STEP 5: Export Markdown Scriptums ==========
            System.out.println("========== STEP 5: Exporting Markdown Scriptums ==========");
            MarkdownExporter mdExporter = new MarkdownExporter();

            // Single comprehensive scriptum
            String singleScriptumPath = parsedDir + "/BSYS-Complete-Scriptum.md";
            mdExporter.exportSingleScriptum(content, flashcards, singleScriptumPath);

            // Multiple topic-based scriptums
            String scriptumsDir = parsedDir + "/scriptums";
            mdExporter.exportMultipleScriptums(content, flashcards, scriptumsDir);
            System.out.println();

            // ========== STEP 6: Generate HTML Website ==========
            System.out.println("========== STEP 6: Generating HTML Website ==========");
            HTMLGenerator htmlGen = new HTMLGenerator();
            String websiteDir = parsedDir + "/website";
            htmlGen.generateWebsite(scriptumsDir, websiteDir);
            System.out.println();

            // ========== STEP 7: Export to Anki ==========
            System.out.println("========== STEP 7: Exporting to Anki Format ==========");
            AnkiExporter ankiExporter = new AnkiExporter();
            String ankiPath = parsedDir + "/BSYS-Anki-Export.txt";
            ankiExporter.exportToAnki(flashcards, ankiPath);

            // Also create enhanced version
            ankiExporter.exportToAnkiEnhanced(flashcards, ankiPath);
            System.out.println();

            // ========== STEP 8: Generate StudyApp Integration Code ==========
            System.out.println("========== STEP 8: Generating StudyApp Integration Code ==========");
            StudyAppCodeGenerator codeGen = new StudyAppCodeGenerator();
            String codeOutputPath = parsedDir + "/studyapp_additions.txt";
            codeGen.generateCode(flashcards, questions, codeOutputPath);
            System.out.println();

            // ========== Summary ==========
            System.out.println("===============================================");
            System.out.println("  GENERATION COMPLETE!");
            System.out.println("===============================================\n");

            System.out.println("Summary:");
            System.out.println("  ✓ " + flashcards.size() + " flashcards generated");
            System.out.println("  ✓ " + questions.size() + " quiz questions generated");
            System.out.println("  ✓ 1 comprehensive markdown scriptum");
            System.out.println("  ✓ " + content.getTopics().size() + " topic-specific markdown files");
            System.out.println("  ✓ HTML website with " + content.getTopics().size() + " pages");
            System.out.println("  ✓ Anki export files (standard + enhanced)");
            System.out.println("  ✓ StudyApp integration code generated");

            System.out.println("\nGenerated files in: " + parsedDir);
            System.out.println("  📄 BSYS-Complete-Scriptum.md - Single comprehensive study guide");
            System.out.println("  📁 scriptums/ - Individual topic markdown files");
            System.out.println("  🌐 website/ - HTML website (open website/index.html)");
            System.out.println("  📇 BSYS-Anki-Export.txt - Anki flashcard import file");
            System.out.println("  💻 studyapp_additions.txt - Java code for StudyApp integration");

            System.out.println("\nNext steps:");
            System.out.println("  1. Review studyapp_additions.txt");
            System.out.println("  2. Copy flashcard/question code into StudyApp.java initializeContent() method");
            System.out.println("  3. Compile and test StudyApp");
            System.out.println("  4. Import BSYS-Anki-Export.txt into Anki");
            System.out.println("  5. Open website/index.html in a browser");

            System.out.println("\n✨ All content generated successfully! ✨\n");

        } catch (Exception e) {
            System.err.println("Error during content generation:");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
