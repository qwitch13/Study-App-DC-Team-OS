package content;

import content.ContentProcessor.*;
import java.util.*;

/**
 * Generate flashcards from processed content
 * Creates 4 types: Definition, Conceptual, Code/Command, Problem-solving
 */
public class FlashcardGenerator {

    private Random random = new Random(42); // Fixed seed for reproducibility

    /**
     * Generate all flashcards from processed content
     * Target distribution: 30% definition, 35% conceptual, 20% code, 15% problem
     */
    public List<Flashcard> generateAll(ProcessedContent content) {
        List<Flashcard> flashcards = new ArrayList<>();

        // Generate each type
        flashcards.addAll(generateDefinitionCards(content.getByType(ContentType.DEFINITION)));
        flashcards.addAll(generateConceptualCards(content.getByType(ContentType.CONCEPT)));
        flashcards.addAll(generateCodeCards(content.getByType(ContentType.CODE)));
        flashcards.addAll(generateProblemCards(content.getByType(ContentType.PROBLEM)));

        System.out.println("\nGenerated flashcards:");
        System.out.println("  Definition cards: " + countByType(flashcards, "definition"));
        System.out.println("  Conceptual cards: " + countByType(flashcards, "concept"));
        System.out.println("  Code cards: " + countByType(flashcards, "code"));
        System.out.println("  Problem cards: " + countByType(flashcards, "problem"));
        System.out.println("  Total: " + flashcards.size());

        return flashcards;
    }

    /**
     * Generate definition flashcards
     * Format: "What is X?" → Clear definition
     */
    public List<Flashcard> generateDefinitionCards(List<ContentItem> definitions) {
        List<Flashcard> cards = new ArrayList<>();

        for (ContentItem item : definitions) {
            String front = "What is " + item.title + "?";
            String back = item.content;

            // Clean up and format
            back = cleanDefinition(back);

            if (back.length() > 20 && back.length() < 500) {
                cards.add(new Flashcard(
                    "BSYS",
                    item.topic,
                    front,
                    back,
                    item.difficulty
                ));
            }
        }

        // Enhance with common OS terms if we don't have enough
        if (cards.size() < 40) {
            cards.addAll(generateCommonDefinitions());
        }

        return cards;
    }

    /**
     * Generate conceptual flashcards
     * Format: "How does X work?" / "Why use X?" / "Explain X"
     */
    public List<Flashcard> generateConceptualCards(List<ContentItem> concepts) {
        List<Flashcard> cards = new ArrayList<>();

        String[] questionTemplates = {
            "How does %s work?",
            "Explain %s",
            "What is the purpose of %s?",
            "Describe %s",
            "Why is %s important?"
        };

        for (ContentItem item : concepts) {
            // Extract key concept from title
            String concept = extractKeyConcept(item.title, item.content);

            // Choose question format
            String template = questionTemplates[random.nextInt(questionTemplates.length)];
            String front = String.format(template, concept);

            String back = item.content;

            if (back.length() > 30 && back.length() < 800) {
                // Format back with bullet points if it contains lists
                back = formatExplanation(back);

                cards.add(new Flashcard(
                    "BSYS",
                    item.topic,
                    front,
                    back,
                    item.difficulty
                ));
            }
        }

        // Add process/concept explanation cards
        cards.addAll(generateProcessCards());

        return cards;
    }

    /**
     * Generate code/command flashcards
     * Format: "What does this command do?" / "What is the output?"
     */
    public List<Flashcard> generateCodeCards(List<ContentItem> codeItems) {
        List<Flashcard> cards = new ArrayList<>();

        for (ContentItem item : codeItems) {
            String code = item.content.trim();

            if (code.length() < 10 || code.length() > 500) continue;

            String front, back;

            // Identify type of code
            if (code.matches("^[$#]\\s+.*") || code.matches("^[a-z]+\\s+[-a-zA-Z0-9]+.*")) {
                // Shell command
                front = "What does this command do?\n\n" + code;
                back = generateCommandExplanation(code);
            } else if (code.contains("#include") || code.contains("int main")) {
                // C/C++ code
                front = "Explain this code:\n\n" + code;
                back = generateCodeExplanation(code, item.topic);
            } else {
                // Generic code
                front = "What does this code do?\n\n" + code;
                back = generateCodeExplanation(code, item.topic);
            }

            if (back != null && back.length() > 10) {
                cards.add(new Flashcard(
                    "BSYS",
                    item.topic,
                    front,
                    back,
                    item.difficulty
                ));
            }
        }

        // Add common Unix/Linux command cards
        cards.addAll(generateCommonCommandCards());

        return cards;
    }

    /**
     * Generate problem-solving flashcards
     * Format: "How would you solve X?" → Solution steps
     */
    public List<Flashcard> generateProblemCards(List<ContentItem> problems) {
        List<Flashcard> cards = new ArrayList<>();

        for (ContentItem item : problems) {
            String problem = item.content;

            // Create question
            String front = "Problem: " + extractProblemStatement(problem);

            // Extract or generate solution
            String back = extractSolution(problem);

            if (back == null || back.length() < 20) {
                back = "Solution approach:\n" + generateSolutionTemplate(item.topic);
            }

            cards.add(new Flashcard(
                "BSYS",
                item.topic,
                front,
                back,
                Math.max(2, item.difficulty) // Problems are at least medium
            ));
        }

        // Add scenario-based cards
        cards.addAll(generateScenarioCards());

        return cards;
    }

    // ========== Helper Methods ==========

    private String cleanDefinition(String def) {
        def = def.trim();
        // Remove leading articles
        def = def.replaceAll("^(A |An |The )", "");
        // Capitalize first letter
        if (def.length() > 0) {
            def = Character.toUpperCase(def.charAt(0)) + def.substring(1);
        }
        return def;
    }

    private String extractKeyConcept(String title, String content) {
        // Try to extract main subject from title or first sentence
        if (title.length() < 50 && !title.equals("Concept")) {
            return title.replace("...", "").trim();
        }

        // Extract from first sentence
        String[] sentences = content.split("[.!?]");
        if (sentences.length > 0) {
            String first = sentences[0].trim();
            if (first.length() < 60) {
                return first;
            }
        }

        return "this concept";
    }

    private String formatExplanation(String text) {
        // Convert numbered/bulleted lists to bullet format
        text = text.replaceAll("(?m)^\\d+\\.\\s+", "• ");
        text = text.replaceAll("(?m)^-\\s+", "• ");

        // Ensure proper spacing
        text = text.replaceAll("\n\n+", "\n\n");

        return text.trim();
    }

    private String generateCommandExplanation(String command) {
        command = command.replaceAll("^[$#]\\s+", "").trim();

        // Parse command
        String[] parts = command.split("\\s+");
        if (parts.length == 0) return null;

        String cmd = parts[0];

        // Common Unix/Linux commands
        Map<String, String> commandExplanations = new HashMap<>();
        commandExplanations.put("ps", "Lists running processes");
        commandExplanations.put("ls", "Lists directory contents");
        commandExplanations.put("chmod", "Changes file permissions");
        commandExplanations.put("kill", "Sends signal to a process");
        commandExplanations.put("fork", "Creates a child process");
        commandExplanations.put("grep", "Searches for patterns in text");
        commandExplanations.put("cat", "Displays file contents");
        commandExplanations.put("cd", "Changes current directory");
        commandExplanations.put("pwd", "Prints working directory");
        commandExplanations.put("mkdir", "Creates a directory");

        String explanation = commandExplanations.get(cmd);
        if (explanation != null) {
            return explanation + "\n\nCommand: " + command;
        }

        return "Command: " + command + "\n\n(Operating system command)";
    }

    private String generateCodeExplanation(String code, String topic) {
        if (code.contains("fork()")) {
            return "Creates a new child process that is a copy of the parent process";
        } else if (code.contains("wait()") || code.contains("waitpid()")) {
            return "Parent process waits for child process to complete";
        } else if (code.contains("pthread_create")) {
            return "Creates a new thread";
        } else if (code.contains("malloc") || code.contains("free")) {
            return "Dynamic memory allocation/deallocation";
        }

        return "Code related to " + topic;
    }

    private String extractProblemStatement(String problem) {
        // Get first 2-3 sentences
        String[] sentences = problem.split("[.!?]");
        if (sentences.length > 0) {
            String statement = sentences[0].trim();
            if (sentences.length > 1) {
                statement += ". " + sentences[1].trim();
            }
            return statement;
        }
        return problem.substring(0, Math.min(200, problem.length()));
    }

    private String extractSolution(String problem) {
        // Look for solution markers
        String lower = problem.toLowerCase();
        int solIdx = lower.indexOf("solution");
        if (solIdx == -1) solIdx = lower.indexOf("answer");

        if (solIdx != -1) {
            return problem.substring(solIdx).trim();
        }

        return null;
    }

    private String generateSolutionTemplate(String topic) {
        if (topic.contains("Deadlock")) {
            return "1. Identify the four conditions for deadlock\n" +
                   "2. Determine which condition to break\n" +
                   "3. Apply appropriate prevention/avoidance/detection strategy";
        } else if (topic.contains("Memory")) {
            return "1. Analyze memory requirements\n" +
                   "2. Choose appropriate algorithm (e.g., paging, segmentation)\n" +
                   "3. Calculate overhead and efficiency";
        } else if (topic.contains("Process")) {
            return "1. Understand process states and transitions\n" +
                   "2. Apply appropriate synchronization mechanisms\n" +
                   "3. Consider race conditions and critical sections";
        }

        return "Analyze the problem, identify key concepts, apply relevant OS principles";
    }

    private int countByType(List<Flashcard> cards, String type) {
        int count = 0;
        for (Flashcard card : cards) {
            if (card.front.toLowerCase().contains(type) ||
                card.topic.toLowerCase().contains(type)) {
                count++;
            }
        }
        return count;
    }

    // ========== Hardcoded Cards for Common Topics ==========

    private List<Flashcard> generateCommonDefinitions() {
        List<Flashcard> cards = new ArrayList<>();

        cards.add(new Flashcard("BSYS", "Processes & Threads", "What is a process?",
            "An instance of an executing program, including program code, data, stack, heap, and OS resources", 1));

        cards.add(new Flashcard("BSYS", "Processes & Threads", "What is a thread?",
            "A lightweight execution unit within a process that shares the process's address space but has its own stack and registers", 1));

        cards.add(new Flashcard("BSYS", "Memory Management", "What is virtual memory?",
            "A memory management technique that provides an abstraction of storage resources, allowing programs to use more memory than physically available", 2));

        cards.add(new Flashcard("BSYS", "Memory Management", "What is paging?",
            "A memory management scheme that eliminates the need for contiguous memory allocation by dividing memory into fixed-size blocks (pages)", 2));

        cards.add(new Flashcard("BSYS", "Deadlocks", "What is a deadlock?",
            "A situation where two or more processes are waiting indefinitely for resources held by each other, creating a circular dependency", 2));

        cards.add(new Flashcard("BSYS", "File Systems", "What is an i-node?",
            "An index node - a data structure containing file metadata such as size, owner, permissions, timestamps, and pointers to data blocks", 2));

        return cards;
    }

    private List<Flashcard> generateProcessCards() {
        List<Flashcard> cards = new ArrayList<>();

        cards.add(new Flashcard("BSYS", "Processes & Threads", "How does context switching work?",
            "1. Save current process state (registers, PC, stack pointer)\n" +
            "2. Update process control block (PCB)\n" +
            "3. Select next process to run\n" +
            "4. Restore new process state from its PCB\n" +
            "5. Resume execution", 2));

        cards.add(new Flashcard("BSYS", "Memory Management", "Explain the difference between internal and external fragmentation",
            "• Internal fragmentation: Wasted space within allocated memory blocks\n" +
            "• External fragmentation: Free memory scattered in small blocks between allocated regions\n" +
            "Paging eliminates external fragmentation but may cause internal fragmentation", 2));

        return cards;
    }

    private List<Flashcard> generateCommonCommandCards() {
        List<Flashcard> cards = new ArrayList<>();

        cards.add(new Flashcard("BSYS", "Unix/Linux", "What does 'ps aux' do?",
            "Lists ALL running processes with details:\n" +
            "• a = all users\n" +
            "• u = user-oriented format\n" +
            "• x = include processes without controlling terminal", 1));

        cards.add(new Flashcard("BSYS", "Unix/Linux", "What does 'chmod 755' mean?",
            "Sets file permissions:\n" +
            "• Owner: rwx (7 = read+write+execute)\n" +
            "• Group: r-x (5 = read+execute)\n" +
            "• Others: r-x (5 = read+execute)", 1));

        cards.add(new Flashcard("BSYS", "Unix/Linux", "What does 'fork()' system call do?",
            "Creates a new child process by duplicating the calling process\n" +
            "• Returns 0 to child process\n" +
            "• Returns child PID to parent\n" +
            "• Returns -1 on error", 2));

        return cards;
    }

    private List<Flashcard> generateScenarioCards() {
        List<Flashcard> cards = new ArrayList<>();

        cards.add(new Flashcard("BSYS", "Deadlocks", "Deadlock detection scenario: 3 processes, 3 resources. How to detect?",
            "Use resource allocation graph:\n" +
            "1. Create graph with processes (circles) and resources (squares)\n" +
            "2. Draw edges: P→R (request), R→P (allocation)\n" +
            "3. Check for cycles\n" +
            "4. If cycle exists with single-instance resources → deadlock", 3));

        cards.add(new Flashcard("BSYS", "Memory Management", "Which page replacement algorithm: minimize page faults for sequence 1,2,3,4,1,2,5,1,2,3,4,5?",
            "Compare algorithms:\n" +
            "• FIFO: Simple but Belady's anomaly possible\n" +
            "• LRU: Good performance, tracks recent usage\n" +
            "• Optimal: Theoretical best, not implementable\n" +
            "For this sequence, LRU typically performs best in practice", 3));

        return cards;
    }

    /**
     * Flashcard class matching StudyApp format
     */
    public static class Flashcard {
        public final String subject;
        public final String topic;
        public final String front;
        public final String back;
        public final int difficulty;

        public Flashcard(String subject, String topic, String front, String back, int difficulty) {
            this.subject = subject;
            this.topic = topic;
            this.front = front;
            this.back = back;
            this.difficulty = Math.max(1, Math.min(3, difficulty));
        }
    }
}
