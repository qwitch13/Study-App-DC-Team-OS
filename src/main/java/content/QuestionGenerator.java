package content;

import content.ContentProcessor.*;
import java.util.*;

/**
 * Generate multiple-choice quiz questions from processed content
 */
public class QuestionGenerator {

    private Random random = new Random(42);

    /**
     * Generate quiz questions from processed content
     */
    public List<Question> generateQuestions(ProcessedContent content) {
        List<Question> questions = new ArrayList<>();

        // Generate questions from definitions
        questions.addAll(generateDefinitionQuestions(content.getByType(ContentType.DEFINITION)));

        // Generate conceptual questions
        questions.addAll(generateConceptualQuestions(content.getByType(ContentType.CONCEPT)));

        // Generate code/command questions
        questions.addAll(generateCodeQuestions(content.getByType(ContentType.CODE)));

        // Add hardcoded high-quality questions for common topics
        questions.addAll(generateCommonQuestions());

        System.out.println("\nGenerated " + questions.size() + " quiz questions");

        return questions;
    }

    /**
     * Generate definition-based questions
     */
    private List<Question> generateDefinitionQuestions(List<ContentItem> definitions) {
        List<Question> questions = new ArrayList<>();

        for (int i = 0; i < Math.min(definitions.size(), 15); i++) {
            ContentItem item = definitions.get(i);

            String question = "What is " + item.title + "?";
            String[] options = new String[4];
            options[0] = item.content.substring(0, Math.min(item.content.length(), 150));

            // Generate distractors
            options[1] = generateDistractor(item.title, definitions, 0);
            options[2] = generateDistractor(item.title, definitions, 1);
            options[3] = generateDistractor(item.title, definitions, 2);

            String explanation = "Correct definition: " + item.content.substring(0, Math.min(item.content.length(), 200));

            questions.add(new Question(
                "BSYS",
                question,
                options,
                0,
                explanation
            ));
        }

        return questions;
    }

    /**
     * Generate conceptual questions
     */
    private List<Question> generateConceptualQuestions(List<ContentItem> concepts) {
        List<Question> questions = new ArrayList<>();

        String[] templates = {
            "Which statement about %s is TRUE?",
            "What is the primary purpose of %s?",
            "How does %s work?",
            "Which of the following describes %s?"
        };

        for (int i = 0; i < Math.min(concepts.size(), 10); i++) {
            ContentItem item = concepts.get(i);

            // Skip if content is too short
            if (item.content.length() < 50) continue;

            String concept = extractConceptName(item.title);
            String template = templates[random.nextInt(templates.length)];
            String question = String.format(template, concept);

            String[] options = new String[4];
            options[0] = extractTrueStatement(item.content);

            // Generate false statements as distractors
            options[1] = "It is primarily used for user interface management";
            options[2] = "It has no impact on system performance";
            options[3] = "It is only available in Windows operating systems";

            String explanation = "The correct answer relates to: " + item.content.substring(0, Math.min(150, item.content.length()));

            questions.add(new Question(
                "BSYS",
                question,
                options,
                0,
                explanation
            ));
        }

        return questions;
    }

    /**
     * Generate code/command questions
     */
    private List<Question> generateCodeQuestions(List<ContentItem> codeItems) {
        List<Question> questions = new ArrayList<>();

        for (int i = 0; i < Math.min(codeItems.size(), 8); i++) {
            ContentItem item = codeItems.get(i);
            String code = item.content.trim();

            if (code.length() < 5 || code.length() > 200) continue;

            String question = "What does this command/code do?\n\n" + code;
            String[] options = new String[4];

            // Identify command type and generate options
            if (code.contains("ps") || code.matches(".*\\bps\\b.*")) {
                options[0] = "Lists running processes";
                options[1] = "Prints system information";
                options[2] = "Performs string search";
                options[3] = "Powers off the system";
            } else if (code.contains("fork")) {
                options[0] = "Creates a child process";
                options[1] = "Terminates a process";
                options[2] = "Waits for process completion";
                options[3] = "Changes process priority";
            } else {
                options[0] = "Performs an operating system operation";
                options[1] = "Displays GUI window";
                options[2] = "Connects to network";
                options[3] = "Plays audio file";
            }

            String explanation = "This code/command is used in the context of " + item.topic;

            questions.add(new Question(
                "BSYS",
                question,
                options,
                0,
                explanation
            ));
        }

        return questions;
    }

    /**
     * Generate high-quality questions for common OS topics
     */
    private List<Question> generateCommonQuestions() {
        List<Question> questions = new ArrayList<>();

        // Process management questions
        questions.add(new Question(
            "BSYS",
            "What is the main difference between a process and a thread?",
            new String[]{
                "Threads share the same address space, processes have separate address spaces",
                "Processes are faster than threads",
                "Threads cannot run concurrently, processes can",
                "There is no difference, they are the same thing"
            },
            0,
            "Threads within the same process share memory (address space), while processes have separate memory spaces. This makes threads lighter-weight but requires careful synchronization."
        ));

        questions.add(new Question(
            "BSYS",
            "Which of the following is NOT a necessary condition for deadlock?",
            new String[]{
                "Preemption",
                "Mutual exclusion",
                "Hold and wait",
                "Circular wait"
            },
            0,
            "The four necessary conditions for deadlock are: mutual exclusion, hold and wait, no preemption, and circular wait. Preemption (ability to forcibly take resources) actually prevents deadlock."
        ));

        // Memory management questions
        questions.add(new Question(
            "BSYS",
            "What is the purpose of paging in memory management?",
            new String[]{
                "To eliminate external fragmentation and enable non-contiguous memory allocation",
                "To increase CPU speed",
                "To manage network packets",
                "To compress files on disk"
            },
            0,
            "Paging divides memory into fixed-size blocks (pages), allowing processes to use non-contiguous physical memory and eliminating external fragmentation."
        ));

        questions.add(new Question(
            "BSYS",
            "Which page replacement algorithm has the lowest page fault rate?",
            new String[]{
                "Optimal (OPT)",
                "First-In-First-Out (FIFO)",
                "Least Recently Used (LRU)",
                "Random replacement"
            },
            0,
            "The Optimal algorithm replaces the page that will not be used for the longest time in the future. It has the lowest page fault rate but is not implementable in practice since future page references are unknown."
        ));

        // CPU scheduling questions
        questions.add(new Question(
            "BSYS",
            "Which CPU scheduling algorithm may cause starvation?",
            new String[]{
                "Priority scheduling",
                "Round Robin",
                "First-Come-First-Served (FCFS)",
                "Shortest Job First (SJF) with aging"
            },
            0,
            "Priority scheduling can cause starvation when low-priority processes never get CPU time because high-priority processes keep arriving. Aging is a technique to prevent this."
        ));

        questions.add(new Question(
            "BSYS",
            "What is the main advantage of Round Robin scheduling?",
            new String[]{
                "Fair CPU time distribution and good response time",
                "Minimizes average waiting time",
                "No context switching overhead",
                "Prevents all types of starvation"
            },
            0,
            "Round Robin gives each process a fixed time quantum in a cyclic manner, ensuring fair CPU distribution and good response times, especially for time-sharing systems."
        ));

        // File systems questions
        questions.add(new Question(
            "BSYS",
            "In Unix/Linux, what does an i-node contain?",
            new String[]{
                "File metadata including size, permissions, and pointers to data blocks",
                "The actual file data",
                "The filename and directory path",
                "Network routing information"
            },
            0,
            "An i-node (index node) stores file metadata such as size, owner, permissions, timestamps, and pointers to data blocks. The filename is stored separately in the directory entry."
        ));

        questions.add(new Question(
            "BSYS",
            "What is the purpose of RAID 1?",
            new String[]{
                "Data mirroring for redundancy",
                "Data striping for performance",
                "Data compression",
                "Data encryption"
            },
            0,
            "RAID 1 (mirroring) duplicates data across two or more disks, providing redundancy and fault tolerance. If one disk fails, data is still available from the mirror."
        ));

        // Unix/Linux commands
        questions.add(new Question(
            "BSYS",
            "What does the command 'chmod 755 file.txt' do?",
            new String[]{
                "Sets read-write-execute for owner, read-execute for group and others",
                "Changes file ownership to user 755",
                "Copies file 755 times",
                "Compresses the file"
            },
            0,
            "chmod 755 sets permissions: 7 (rwx) for owner, 5 (r-x) for group, 5 (r-x) for others. The octal digits represent binary permission bits."
        ));

        questions.add(new Question(
            "BSYS",
            "What is the return value of fork() in the child process?",
            new String[]{
                "0",
                "The child's PID",
                "The parent's PID",
                "-1"
            },
            0,
            "fork() returns 0 to the child process, the child's PID to the parent process, and -1 if the fork fails."
        ));

        // Synchronization questions
        questions.add(new Question(
            "BSYS",
            "What is a semaphore?",
            new String[]{
                "A synchronization primitive that controls access to shared resources",
                "A type of memory allocation",
                "A file system structure",
                "A network protocol"
            },
            0,
            "A semaphore is an integer variable used for process synchronization. wait() decrements it (blocking if negative), signal() increments it."
        ));

        questions.add(new Question(
            "BSYS",
            "What problem does the Dining Philosophers Problem illustrate?",
            new String[]{
                "Deadlock and resource allocation issues",
                "Memory fragmentation",
                "CPU scheduling fairness",
                "File system corruption"
            },
            0,
            "The Dining Philosophers Problem demonstrates challenges in resource allocation, deadlock prevention, and concurrent programming with shared resources."
        ));

        // Virtual memory questions
        questions.add(new Question(
            "BSYS",
            "What is thrashing in virtual memory?",
            new String[]{
                "Excessive paging activity that degrades performance",
                "Fast memory access",
                "Disk encryption",
                "Network congestion"
            },
            0,
            "Thrashing occurs when a system spends more time swapping pages in and out of memory than executing instructions, usually due to insufficient physical memory."
        ));

        questions.add(new Question(
            "BSYS",
            "What is the Translation Lookaside Buffer (TLB)?",
            new String[]{
                "A cache for page table entries to speed up address translation",
                "A network buffer",
                "A disk cache",
                "A CPU register"
            },
            0,
            "The TLB is a small, fast cache that stores recent virtual-to-physical address translations, reducing the overhead of page table lookups."
        ));

        // Inter-process communication
        questions.add(new Question(
            "BSYS",
            "Which IPC mechanism is the fastest?",
            new String[]{
                "Shared memory",
                "Message passing",
                "Pipes",
                "Sockets"
            },
            0,
            "Shared memory is fastest because data doesn't need to be copied between processes. However, it requires explicit synchronization."
        ));

        return questions;
    }

    // ========== Helper Methods ==========

    private String generateDistractor(String term, List<ContentItem> allDefinitions, int offset) {
        // Find a different definition to use as distractor
        int index = (term.hashCode() + offset) % allDefinitions.size();
        if (index < 0) index = -index;

        ContentItem other = allDefinitions.get(Math.min(index, allDefinitions.size() - 1));
        if (other.title.equals(term)) {
            index = (index + 1) % allDefinitions.size();
            other = allDefinitions.get(index);
        }

        return other.content.substring(0, Math.min(other.content.length(), 150));
    }

    private String extractConceptName(String title) {
        if (title.length() < 50) {
            return title.replace("...", "").trim();
        }
        return "this concept";
    }

    private String extractTrueStatement(String content) {
        // Extract first sentence as true statement
        String[] sentences = content.split("[.!?]");
        if (sentences.length > 0) {
            return sentences[0].trim();
        }
        return content.substring(0, Math.min(100, content.length()));
    }

    /**
     * Question class matching StudyApp format
     */
    public static class Question {
        public final String subject;
        public final String question;
        public final String[] options;
        public final int correctIndex;
        public final String explanation;

        public Question(String subject, String question, String[] options, int correctIndex, String explanation) {
            this.subject = subject;
            this.question = question;
            this.options = options;
            this.correctIndex = correctIndex;
            this.explanation = explanation;
        }
    }
}
