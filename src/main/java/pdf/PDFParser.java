package pdf;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * PDF Parser using Apache PDFBox
 * Extracts text from PDF files preserving structure and code blocks
 */
public class PDFParser {

    private static final Pattern CODE_BLOCK_PATTERN = Pattern.compile("(`{1,3}[^`]+`{1,3}|\\$[^$]+\\$)");
    private static final Pattern COMMAND_PATTERN = Pattern.compile("^\\s*[a-z]+\\s+[-a-zA-Z0-9]+");

    /**
     * Parse a single PDF file and extract all text
     */
    public static String parsePDF(File pdfFile) throws IOException {
        if (!pdfFile.exists() || !pdfFile.isFile()) {
            throw new IOException("PDF file not found: " + pdfFile.getAbsolutePath());
        }

        try (PDDocument document = Loader.loadPDF(pdfFile)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            String text = stripper.getText(document);
            return sanitizeText(text);
        }
    }

    /**
     * Extract text page by page for better structure preservation
     */
    public static List<String> extractTextByPage(File pdfFile) throws IOException {
        List<String> pages = new ArrayList<>();

        try (PDDocument document = Loader.loadPDF(pdfFile)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);

            int numberOfPages = document.getNumberOfPages();
            for (int i = 1; i <= numberOfPages; i++) {
                stripper.setStartPage(i);
                stripper.setEndPage(i);
                String pageText = stripper.getText(document);
                pages.add(sanitizeText(pageText));
            }
        }

        return pages;
    }

    /**
     * Identify potential code blocks in text
     */
    public static List<CodeBlock> identifyCodeBlocks(String text) {
        List<CodeBlock> codeBlocks = new ArrayList<>();
        String[] lines = text.split("\n");

        boolean inCodeBlock = false;
        StringBuilder currentBlock = new StringBuilder();
        int startLine = 0;

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];

            // Detect code block markers or command-like patterns
            if (line.trim().matches("^(```|~~~|\\$|#|>).*") ||
                COMMAND_PATTERN.matcher(line).find() ||
                line.contains("int main") ||
                line.contains("void ") ||
                line.contains("#include")) {

                if (!inCodeBlock) {
                    inCodeBlock = true;
                    startLine = i;
                    currentBlock = new StringBuilder();
                }
                currentBlock.append(line).append("\n");

            } else if (inCodeBlock) {
                // Continue accumulating if seems like code
                if (line.trim().isEmpty() ||
                    line.contains("{") ||
                    line.contains("}") ||
                    line.contains(";") ||
                    line.trim().startsWith("//")) {
                    currentBlock.append(line).append("\n");
                } else {
                    // End of code block
                    codeBlocks.add(new CodeBlock(currentBlock.toString(), startLine, i - 1));
                    inCodeBlock = false;
                }
            }
        }

        // Add final block if still in one
        if (inCodeBlock) {
            codeBlocks.add(new CodeBlock(currentBlock.toString(), startLine, lines.length - 1));
        }

        return codeBlocks;
    }

    /**
     * Clean and sanitize extracted text
     */
    public static String sanitizeText(String text) {
        if (text == null) return "";

        // Remove excessive whitespace but preserve structure
        text = text.replaceAll("\r\n", "\n");
        text = text.replaceAll("\\n{3,}", "\n\n");

        // Fix common PDF extraction issues
        text = text.replaceAll("\u00AD", ""); // Remove soft hyphens
        text = text.replaceAll("\uFEFF", ""); // Remove BOM
        text = text.replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F]", ""); // Remove control chars

        // Normalize Unicode quotes
        text = text.replace("\u201C", "\"").replace("\u201D", "\"");
        text = text.replace("\u2018", "'").replace("\u2019", "'");

        return text.trim();
    }

    /**
     * Represents a code block extracted from the PDF
     */
    public static class CodeBlock {
        public final String code;
        public final int startLine;
        public final int endLine;

        public CodeBlock(String code, int startLine, int endLine) {
            this.code = code;
            this.startLine = startLine;
            this.endLine = endLine;
        }

        @Override
        public String toString() {
            return "CodeBlock[lines " + startLine + "-" + endLine + "]: " +
                   code.substring(0, Math.min(50, code.length())) + "...";
        }
    }
}
