import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * StudyApp 2.0 - Bilingual Exam Preparation Tool for Team Development
 *
 * Hochschule Campus Wien - Winter Semester 2025/26
 * Course: Teamarbeit (Team Development)
 * Exam Date: January 14, 2026
 *
 * FEATURES:
 * - Bilingual support (German/English)
 * - Exam simulation matching FH Campus Wien format (60min, 70pts, T/F + fill-in)
 * - Enhanced progress tracking with streaks and session stats
 * - Spaced repetition system for optimal learning
 * - Multiple study modes: flashcards, quiz, practice problems
 * - CSV import/export for custom content
 * - Comprehensive Teamentwicklung content coverage
 *
 * Based on: MathEP1.0 architecture + StudyApp v1.1
 *
 * @version 2.0
 * @date 2026-01-12
 */
public class StudyApp {

    private static final Scanner scanner = new Scanner(System.in);
    private static final Random random = new Random();

    // Data files
    private static final String FLASHCARDS_FILE = "flashcards.dat";
    private static final String QUESTIONS_FILE = "questions.dat";
    private static final String PROGRESS_FILE = "progress.dat";
    private static final String SETTINGS_FILE = "settings.dat";
    private static final String EXAM_HISTORY_FILE = "exam_history.dat";

    // Current language setting
    private static Language currentLang = Language.DE;

    // Progress tracking - enhanced from MathEP1
    private static Map<String, int[]> cardProgress = new HashMap<>(); // cardId -> [correct, total, intervalDays]
    private static Map<String, int[]> quizProgress = new HashMap<>(); // topic -> [correct, total]
    private static Map<String, Long> lastReviewTime = new HashMap<>(); // cardId -> timestamp
    private static Map<String, List<Integer>> performanceHistory = new HashMap<>(); // topic -> scores
    private static Set<String> masteredCards = new HashSet<>();

    // Session tracking
    private static long sessionStartTime;
    private static int sessionQuestionsAnswered = 0;
    private static int sessionCorrect = 0;
    private static LocalDate lastActiveDate = LocalDate.now();
    private static int currentStreak = 0;
    private static long totalStudyTimeSeconds = 0;
    private static int dailyGoal = 20;
    private static int todayCompleted = 0;

    // Exam history
    private static List<String> examHistory = new ArrayList<>();

    // Content collections
    private static List<Flashcard> flashcards = new ArrayList<>();
    private static List<Question> questions = new ArrayList<>();
    private static List<Flashcard> customCards = new ArrayList<>();
    private static List<Question> customQuestions = new ArrayList<>();

    // Subject filter (null = all subjects)
    private static Subject subjectFilter = null;

    // ANSI Color codes
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String PURPLE = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";
    private static final String BOLD = "\u001B[1m";
    private static final String WHITE = "\u001B[37m";

    // ==================== MAIN ====================

    public static void main(String[] args) {
        sessionStartTime = System.currentTimeMillis();
        loadSettings();
        loadAllData();
        updateStreak();
        initializeContent();

        printWelcome();
        mainMenu();

        saveAllData();
        saveSettings();
        printSessionSummary();
    }

    // ==================== ENUMS AND DATA CLASSES ====================

    /**
     * Supported languages
     */
    enum Language {
        DE, EN;

        @Override
        public String toString() {
            return switch (this) {
                case DE -> "Deutsch";
                case EN -> "English";
            };
        }
    }

    /**
     * Subject areas for content organization
     */
    enum Subject {
        // Operating Systems
        BSYS("Betriebssysteme", "Operating Systems"),

        // Digital Communications
        DIGICOM("Digitale Kommunikation", "Digital Communications"),

        // Team Development
        TEAM("Teamentwicklung", "Team Development"),
        MODELS("Modelle", "Models"),
        PROCESSES("Prozesse", "Processes"),
        LEADERSHIP("Führung", "Leadership"),
        CONFLICT("Konflikt", "Conflict"),
        AGILE("Agile", "Agile"),
        ORGANIZATION("Organisation", "Organization");

        private final String nameDE;
        private final String nameEN;

        Subject(String nameDE, String nameEN) {
            this.nameDE = nameDE;
            this.nameEN = nameEN;
        }

        public String getName() {
            return currentLang == Language.DE ? nameDE : nameEN;
        }

        public String getName(Language lang) {
            return lang == Language.DE ? nameDE : nameEN;
        }
    }

    /**
     * Question types matching exam format
     */
    enum QuestionType {
        MULTIPLE_CHOICE,  // Standard quiz questions
        TRUE_FALSE,       // Part 1 of exam (19 questions, 2pts each)
        FILL_IN;          // Part 2 of exam (fill-in terms, 3-6pts each)

        @Override
        public String toString() {
            return switch (this) {
                case MULTIPLE_CHOICE -> currentLang == Language.DE ? "Multiple Choice" : "Multiple Choice";
                case TRUE_FALSE -> currentLang == Language.DE ? "Wahr/Falsch" : "True/False";
                case FILL_IN -> currentLang == Language.DE ? "Lückentext" : "Fill-in";
            };
        }
    }

    /**
     * Flashcard record with bilingual support
     */
    record Flashcard(
        Subject subject,
        String topic,
        Map<Language, String> front,
        Map<Language, String> back,
        int difficulty,
        Language sourceLang
    ) implements Serializable {
        @java.io.Serial private static final long serialVersionUID = 1L;

        // Helper constructor for single-language cards
        public Flashcard(Subject subject, String topic, String frontDE, String frontEN,
                        String backDE, String backEN, int difficulty) {
            this(subject, topic,
                Map.of(Language.DE, frontDE, Language.EN, frontEN),
                Map.of(Language.DE, backDE, Language.EN, backEN),
                difficulty, Language.DE);
        }

        public String getFront() {
            return front.getOrDefault(currentLang, front.get(Language.DE));
        }

        public String getBack() {
            return back.getOrDefault(currentLang, back.get(Language.DE));
        }
    }

    /**
     * Question record with bilingual support and multiple types
     */
    record Question(
        Subject subject,
        Map<Language, String> question,
        Map<Language, List<String>> options,  // Empty for fill-in questions
        Set<Integer> correctIndices,  // Support multiple correct answers
        Map<Language, String> explanation,
        QuestionType type,
        int points,  // For exam scoring
        Language sourceLang
    ) implements Serializable {
        @java.io.Serial private static final long serialVersionUID = 1L;

        // Helper constructor for single correct answer
        public Question(Subject subject,
                       String questionDE, String questionEN,
                       List<String> optionsDE, List<String> optionsEN,
                       int correctIndex,
                       String explanationDE, String explanationEN,
                       QuestionType type, int points) {
            this(subject,
                Map.of(Language.DE, questionDE, Language.EN, questionEN),
                Map.of(Language.DE, optionsDE, Language.EN, optionsEN),
                Set.of(correctIndex),
                Map.of(Language.DE, explanationDE, Language.EN, explanationEN),
                type, points, Language.DE);
        }

        // Constructor for True/False questions
        public static Question trueFalse(Subject subject,
                                        String statementDE, String statementEN,
                                        boolean isTrue,
                                        String explanationDE, String explanationEN) {
            List<String> optionsDE = List.of("Wahr (W)", "Falsch (F)");
            List<String> optionsEN = List.of("True (T)", "False (F)");
            int correctIndex = isTrue ? 0 : 1;

            return new Question(subject,
                Map.of(Language.DE, statementDE, Language.EN, statementEN),
                Map.of(Language.DE, optionsDE, Language.EN, optionsEN),
                Set.of(correctIndex),
                Map.of(Language.DE, explanationDE, Language.EN, explanationEN),
                QuestionType.TRUE_FALSE, 2, Language.DE);
        }

        // Constructor for fill-in questions
        public static Question fillIn(Subject subject,
                                     String questionDE, String questionEN,
                                     List<String> answersDE, List<String> answersEN,
                                     int points) {
            // For fill-in, we store correct answers in explanation field
            String explanationDE = String.join(", ", answersDE);
            String explanationEN = String.join(", ", answersEN);

            return new Question(subject,
                Map.of(Language.DE, questionDE, Language.EN, questionEN),
                Map.of(Language.DE, List.of(), Language.EN, List.of()),
                Set.of(),
                Map.of(Language.DE, explanationDE, Language.EN, explanationEN),
                QuestionType.FILL_IN, points, Language.DE);
        }

        public String getQuestion() {
            return question.getOrDefault(currentLang, question.get(Language.DE));
        }

        public List<String> getOptions() {
            return options.getOrDefault(currentLang, options.get(Language.DE));
        }

        public String getExplanation() {
            return explanation.getOrDefault(currentLang, explanation.get(Language.DE));
        }

        public int getCorrectIndex() {
            return correctIndices.isEmpty() ? -1 : correctIndices.iterator().next();
        }

        public boolean isCorrect(int index) {
            return correctIndices.contains(index);
        }
    }

    // ==================== TRANSLATION SYSTEM ====================

    /**
     * Get translated string for current language
     */
    private static String t(String keyDE, String keyEN) {
        return currentLang == Language.DE ? keyDE : keyEN;
    }

    // ==================== UI METHODS ====================

    private static void printWelcome() {
        clearScreen();

        int totalCards = flashcards.size() + customCards.size();
        int totalQuestions = questions.size() + customQuestions.size();

        System.out.println(CYAN + BOLD);
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.printf("║     %s - %s               ║%n",
            t("TEAMENTWICKLUNG", "TEAM DEVELOPMENT"),
            t("LERNPROGRAMM v2.0", "STUDY APP v2.0"));
        System.out.printf("║     Hochschule Campus Wien - %s               ║%n",
            t("WS 2025/26", "Winter 2025/26"));
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        System.out.printf("║  🌐 %s: %s     🎯 %s: %-25s ║%n",
            t("Sprache", "Language"), currentLang,
            t("Fach", "Subject"),
            subjectFilter == null ? t("Alle", "All") : subjectFilter.getName());
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        System.out.printf("║  📚 %d %s    ❓ %d %s                    ║%n",
            totalCards, t("Karteikarten", "Flashcards"),
            totalQuestions, t("Fragen", "Questions"));
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        System.out.printf("║  🔥 Streak: %d %s     📊 %s: %d/%d     ⏱️ %s     ║%n",
            currentStreak, t("Tage", "days"),
            t("Heute", "Today"), todayCompleted, dailyGoal,
            t("Gesamt", "Total") + ": " + formatTime(totalStudyTimeSeconds));
        System.out.println("╚════════════════════════════════════════════════════════════════╝" + RESET);

        // Show due reviews
        int dueReviews = countDueReviews();
        if (dueReviews > 0) {
            System.out.println(YELLOW + "\n⚠️  " + dueReviews + " " +
                t("Karten zur Wiederholung fällig!", "cards due for review!") + RESET);
        }

        // Show weak topics
        List<String> weakTopics = getWeakTopics();
        if (!weakTopics.isEmpty()) {
            System.out.println(RED + "📉 " + t("Schwache Themen", "Weak topics") +
                ": " + String.join(", ", weakTopics) + RESET);
        }

        System.out.println();
    }

    private static void mainMenu() {
        while (true) {
            printWelcome(); // Refresh stats each loop

            System.out.println(BOLD + "\n════════ " + subjectFilter.getName() + " " +
                t("HAUPTMENÜ", "MAIN MENU") + " ════════" + RESET);

            System.out.println("  " + CYAN + t("LERNEN:", "LEARN:") + RESET);
            System.out.println("    1. 📚 " + t("Karteikarten", "Flashcards") + " (" + getSubjectCardCount(subjectFilter) + ")");
            System.out.println("    2. ❓ Quiz (" + getSubjectQuestionCount(subjectFilter) + ")");
            System.out.println("    3. ⚡ " + t("Schnell-Review", "Quick Review"));

            System.out.println("\n  " + PURPLE + t("PRÜFUNG:", "EXAM:") + RESET);
            if (subjectFilter == Subject.TEAM) {
                 System.out.println("    4. 🎓 " + t("Prüfungssimulation (60 Min, 70 Pkt)", "Exam Simulation (60 Min, 70 Pts)"));
            } else {
                 System.out.println("    4. 🎓 " + t("Prüfungssimulation", "Exam Simulation"));
            }

            System.out.println("\n  " + YELLOW + t("TOOLS:", "TOOLS:") + RESET);
            System.out.println("    5. 📊 " + t("Statistiken & Analyse", "Statistics & Analysis"));
            System.out.println("    6. ✏️  " + t("Eigene Inhalte", "Custom Content"));
            System.out.println("    7. 💾 " + t("Import/Export", "Import/Export"));
            System.out.println("    8. ⚙️  " + t("Einstellungen", "Settings"));
            System.out.println();
            System.out.println("    0. 🚪 " + t("Beenden", "Exit"));
            System.out.println("════════════════════════════════════════════════════");

            int choice = readInt(t("Auswahl", "Choice") + ": ", 0, 8);

            switch (choice) {
                case 1 -> flashcardMode(subjectFilter);
                case 2 -> quizMode(subjectFilter);
                case 3 -> quickReview(subjectFilter);
                case 4 -> examSimulation(subjectFilter);
                case 5 -> showDetailedStats();
                case 6 -> inputMenu();
                case 7 -> importExportMenu();
                case 8 -> settingsMenu();
                case 0 -> {
                    System.out.println(GREEN + "\n" +
                        t("Viel Erfolg bei der Prüfung! 🎓", "Good luck on your exam! 🎓") + RESET);
                    return;
                }
            }
        }
    }

    // ==================== EXAM SIMULATION ====================

    private static void examSimulation(Subject subject) {
        System.out.println(PURPLE + BOLD + "\n╔══════════════════════════════════════════════╗");
        System.out.println("║     🎓 " + t("PRÜFUNGSSIMULATION", "EXAM SIMULATION") + " - " + subject.getName() + "     ║");
        System.out.println("╚══════════════════════════════════════════════╝" + RESET);

        System.out.println("\n" + t("Diese Simulation entspricht der echten Prüfung:",
            "This simulation matches the real exam:"));
        System.out.println("  • " + t("60 Minuten Zeitlimit", "60 minute time limit"));
        System.out.println("  • " + t("70 Punkte total (42 zum Bestehen = 60%)",
            "70 points total (42 to pass = 60%)"));
        System.out.println("  • " + t("Teil 1: 19 Wahr/Falsch Fragen (38 Punkte)",
            "Part 1: 19 True/False questions (38 points)"));
        System.out.println("  • " + t("Teil 2: Lückentextfragen (32 Punkte)",
            "Part 2: Fill-in questions (32 points)"));
        System.out.println("  • " + t("Keine Hilfsmittel", "No materials allowed"));
        System.out.println("  • " + t("Teilpunkte möglich, keine Minuspunkte",
            "Partial credit possible, no negative points"));

        System.out.println("\n" + t("Prüfungstyp wählen:", "Choose exam type:"));
        System.out.println("  1. 🎯 " + t("Übungsprüfung (10 Fragen, 10 Min)",
            "Practice Exam (10 questions, 10 min)"));
        System.out.println("  2. 📋 " + t("Teilprüfung (20 Fragen, 30 Min)",
            "Partial Exam (20 questions, 30 min)"));
        System.out.println("  3. 📚 " + t("VOLLSTÄNDIGE PRÜFUNG (60 Min, 70 Punkte)",
            "FULL EXAM (60 min, 70 points)"));
        System.out.println("  0. ← " + t("Zurück", "Back"));

        int type = readInt(t("Auswahl", "Choice") + ": ", 0, 3);
        if (type == 0) return;

        int timeMinutes;
        boolean fullExam = false;

        switch (type) {
            case 1 -> timeMinutes = 10;
            case 2 -> timeMinutes = 30;
            case 3 -> { timeMinutes = 60; fullExam = true; }
            default -> timeMinutes = 30;
        }

        System.out.println(YELLOW + "\n⚠️  " +
            t("Die Prüfung beginnt jetzt! Timer läuft...",
              "The exam starts now! Timer is running...") + RESET);
        System.out.print("[ENTER] " + t("um zu starten", "to start") + ": ");
        scanner.nextLine();

        runExam(subject, fullExam, timeMinutes);
    }

    private static void runExam(Subject subject, boolean fullExam, int timeMinutes) {
        // Select questions for exam - filter by subject
        List<Question> examQuestions = new ArrayList<>();

        // Filter questions by subject (for TEAM, include all subcategories)
        List<Question> subjectQuestions;
        if (subject == Subject.TEAM) {
            subjectQuestions = questions.stream()
                .filter(q -> q.subject() == Subject.TEAM ||
                           q.subject() == Subject.MODELS ||
                           q.subject() == Subject.PROCESSES ||
                           q.subject() == Subject.LEADERSHIP ||
                           q.subject() == Subject.CONFLICT ||
                           q.subject() == Subject.AGILE ||
                           q.subject() == Subject.ORGANIZATION)
                .toList();
        } else {
            subjectQuestions = questions.stream()
                .filter(q -> q.subject() == subject)
                .toList();
        }

        if (fullExam) {
            // Full exam: 19 True/False + fill-in questions
            // Get True/False questions
            List<Question> tfQuestions = new ArrayList<>(subjectQuestions.stream()
                .filter(q -> q.type == QuestionType.TRUE_FALSE)
                .toList());
            Collections.shuffle((List<?>)tfQuestions);
            examQuestions.addAll(tfQuestions.subList(0, Math.min(19, tfQuestions.size())));

            // Get fill-in questions
            List<Question> fillInQuestions = new ArrayList<>(subjectQuestions.stream()
                .filter(q -> q.type == QuestionType.FILL_IN)
                .toList());
            Collections.shuffle((List<?>)fillInQuestions);
            examQuestions.addAll(fillInQuestions.subList(0, Math.min(10, fillInQuestions.size())));
        } else {
            // Practice exam: mix of question types
            List<Question> allQuestions = new ArrayList<>(subjectQuestions);
            Collections.shuffle(allQuestions);
            int numQuestions = timeMinutes / 3; // Rough estimate: 3 min per question
            examQuestions.addAll(allQuestions.subList(0, Math.min(numQuestions, allQuestions.size())));
        }

        long startTime = System.currentTimeMillis();
        long endTime = startTime + (timeMinutes * 60 * 1000L);

        int totalPoints = 0;
        int earnedPoints = 0;
        int answered = 0;
        Map<Subject, int[]> subjectResults = new HashMap<>();

        for (int i = 0; i < examQuestions.size(); i++) {
            long remaining = endTime - System.currentTimeMillis();
            if (remaining <= 0) {
                System.out.println(RED + "\n⏰ " +
                    t("ZEIT ABGELAUFEN!", "TIME'S UP!") + RESET);
                break;
            }

            Question q = examQuestions.get(i);
            totalPoints += q.points;

            System.out.println(BOLD + "\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" + RESET);
            System.out.printf("%s[%s]%s " + t("Frage", "Question") + " %d/%d   [%d " +
                t("Punkte", "points") + "]   ⏱️ %s " + t("verbleibend", "remaining") + "%n",
                YELLOW, q.subject.getName(), RESET, i + 1, examQuestions.size(),
                q.points, formatTime(remaining / 1000));
            System.out.println(BOLD + "\n" + q.getQuestion() + RESET + "\n");

            int pointsEarned = 0;

            if (q.type == QuestionType.TRUE_FALSE || q.type == QuestionType.MULTIPLE_CHOICE) {
                List<String> options = q.getOptions();
                for (int j = 0; j < options.size(); j++) {
                    System.out.println("  " + (char)('A' + j) + ") " + options.get(j));
                }

                System.out.print("\n" + t("Antwort", "Answer") + " (A-" +
                    (char)('A' + options.size() - 1) + ", Enter=" +
                    t("überspringen", "skip") + "): ");
                String answer = scanner.nextLine().trim().toUpperCase();

                if (!answer.isEmpty()) {
                    int answerIndex = answer.charAt(0) - 'A';
                    if (answerIndex >= 0 && answerIndex < options.size()) {
                        answered++;
                        if (q.isCorrect(answerIndex)) {
                            pointsEarned = q.points;
                            System.out.println(GREEN + "✓ " + t("Richtig!", "Correct!") + RESET);
                        } else {
                            System.out.println(RED + "✗ " + t("Falsch", "Wrong") +
                                " → " + t("Richtig war", "Correct was") + ": " +
                                (char)('A' + q.getCorrectIndex()) + RESET);
                        }
                    }
                }
            } else if (q.type == QuestionType.FILL_IN) {
                System.out.println(CYAN + t("Geben Sie die Antwort(en) ein (durch Komma getrennt):",
                    "Enter the answer(s) (comma-separated):") + RESET);
                System.out.print("> ");
                String answer = scanner.nextLine().trim();

                if (!answer.isEmpty()) {
                    answered++;
                    // Simple matching: count how many terms are correct
                    String[] userAnswers = answer.split(",");
                    String[] correctAnswers = q.getExplanation().split(",");
                    int correctCount = 0;

                    for (String userAns : userAnswers) {
                        for (String correctAns : correctAnswers) {
                            if (userAns.trim().equalsIgnoreCase(correctAns.trim())) {
                                correctCount++;
                                break;
                            }
                        }
                    }

                    // Partial credit
                    pointsEarned = (q.points * correctCount) / correctAnswers.length;

                    if (correctCount == correctAnswers.length) {
                        System.out.println(GREEN + "✓ " + t("Vollständig richtig!",
                            "Completely correct!") + RESET);
                    } else if (correctCount > 0) {
                        System.out.println(YELLOW + "◐ " + t("Teilweise richtig",
                            "Partially correct") + ": " + correctCount + "/" +
                            correctAnswers.length + RESET);
                    } else {
                        System.out.println(RED + "✗ " + t("Falsch. Richtig:", "Wrong. Correct:") +
                            " " + q.getExplanation() + RESET);
                    }
                }
            }

            earnedPoints += pointsEarned;

            // Track by subject
            subjectResults.computeIfAbsent(q.subject, k -> new int[]{0, 0});
            subjectResults.get(q.subject)[0] += pointsEarned;
            subjectResults.get(q.subject)[1] += q.points;
        }

        // Calculate results
        long duration = System.currentTimeMillis() - startTime;
        double percentage = totalPoints > 0 ? (earnedPoints * 100.0) / totalPoints : 0;
        String grade = calculateGrade(percentage);
        boolean passed = earnedPoints >= (totalPoints * 0.6);

        // Show results
        System.out.println(PURPLE + BOLD + "\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║         📋 " + t("PRÜFUNGSERGEBNIS", "EXAM RESULT") + "                     ║");
        System.out.println("╚════════════════════════════════════════════════════════╝" + RESET);

        System.out.printf("\n  " + t("Punkte", "Points") + ":      %d / %d (%.1f%%)%n",
            earnedPoints, totalPoints, percentage);
        System.out.printf("  " + t("Benötigt", "Required") + ":   %d " +
            t("Punkte zum Bestehen (60%%)", "points to pass (60%%)") + "%n",
            (int)(totalPoints * 0.6));
        System.out.printf("  " + t("Zeit", "Time") + ":        %s " + t("von", "of") + " %d " +
            t("Minuten", "minutes") + "%n",
            formatTime(duration / 1000), timeMinutes);
        System.out.printf("  " + t("Note", "Grade") + ":        %s%s%s%n",
            passed ? GREEN : RED, grade, RESET);
        System.out.printf("  " + t("Status", "Status") + ":      %s%s%s%n",
            passed ? GREEN : RED,
            passed ? t("BESTANDEN ✓", "PASSED ✓") : t("NICHT BESTANDEN ✗", "FAILED ✗"),
            RESET);

        System.out.println(CYAN + "\n  📊 " + t("Ergebnisse nach Thema:", "Results by subject:") + RESET);
        for (Subject s : Subject.values()) {
            int[] results = subjectResults.get(s);
            if (results != null && results[1] > 0) {
                double subjectPct = (results[0] * 100.0) / results[1];
                String color = subjectPct >= 70 ? GREEN : (subjectPct >= 50 ? YELLOW : RED);
                System.out.printf("    %-30s %s%d/%d (%.0f%%)%s%n",
                    s.getName(), color, results[0], results[1], subjectPct, RESET);
            }
        }

        // Save to history
        String historyEntry = String.format("%s: %d/%d (%.1f%%) - %s - %s",
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
            earnedPoints, totalPoints, percentage, grade,
            passed ? t("BESTANDEN", "PASSED") : t("NICHT BESTANDEN", "FAILED"));
        examHistory.add(historyEntry);

        // Update progress
        todayCompleted += answered;
        sessionQuestionsAnswered += answered;
        sessionCorrect += earnedPoints;

        System.out.print("\n[ENTER] " + t("zum Fortfahren", "to continue") + ": ");
        scanner.nextLine();
    }

    private static String calculateGrade(double percentage) {
        if (percentage >= 90) return "1 (" + t("Sehr Gut", "Excellent") + ") 🌟";
        if (percentage >= 80) return "2 (" + t("Gut", "Good") + ") 👍";
        if (percentage >= 70) return "3 (" + t("Befriedigend", "Satisfactory") + ")";
        if (percentage >= 60) return "4 (" + t("Genügend", "Sufficient") + ")";
        return "5 (" + t("Nicht Genügend", "Fail") + ") ❌";
    }

    // ==================== FLASHCARD MODE ====================

    private static void flashcardMode(Subject filterSubject) {
        List<Flashcard> cards = new ArrayList<>(flashcards);
        cards.addAll(customCards);

        if (filterSubject != null) {
            cards = new ArrayList<>(cards.stream()
                .filter(c -> c.subject == filterSubject)
                .toList());
        }

        if (cards.isEmpty()) {
            System.out.println(RED + t("Keine Karten verfügbar!", "No cards available!") + RESET);
            return;
        }

        Collections.shuffle((List<?>)cards);

        System.out.println(CYAN + "\n══════════ 📚 " +
            t("KARTEIKARTEN", "FLASHCARDS") + " ══════════" + RESET);
        System.out.printf(t("%d Karten zum Lernen", "%d cards to study") + "%n", cards.size());
        System.out.print("[ENTER] " + t("zum Starten", "to start") + ", [q] " +
            t("zum Abbrechen", "to quit") + ": ");
        if (scanner.nextLine().trim().equalsIgnoreCase("q")) return;

        int studied = 0;
        for (int i = 0; i < cards.size() && i < 20; i++) {
            Flashcard card = cards.get(i);
            String cardId = "card_" + cards.indexOf(card);

            System.out.println(BOLD + "\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" + RESET);
            System.out.printf("%s[%s]%s " + t("Karte", "Card") + " %d/%d%n",
                YELLOW, card.subject.getName(), RESET, i + 1, Math.min(20, cards.size()));
            System.out.println(BOLD + "\n❓ " + card.getFront() + RESET);

            System.out.print("\n[ENTER] " + t("für Antwort", "for answer") + ": ");
            scanner.nextLine();

            System.out.println(BOLD + "\n✓ " + card.getBack() + RESET);

            System.out.print("\n" + t("Bewertung", "Rating") + " (1=" +
                t("Vergessen", "Forgot") + ", 2=" + t("Schwer", "Hard") +
                ", 3=" + t("Gut", "Good") + ", 4=" + t("Leicht", "Easy") +
                ", q=" + t("beenden", "quit") + "): ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("q")) break;

            int rating = 3;
            try { rating = Integer.parseInt(input); } catch (Exception e) {}
            rating = Math.max(1, Math.min(4, rating));

            // Update progress using spaced repetition algorithm
            updateCardProgress(cardId, rating);
            studied++;
        }

        todayCompleted += studied;
        sessionQuestionsAnswered += studied;

        System.out.println(GREEN + "\n✓ " + studied + " " +
            t("Karten studiert!", "cards studied!") + RESET);
        System.out.print("\n[ENTER] " + t("zum Fortfahren", "to continue") + ": ");
        scanner.nextLine();
    }

    private static void updateCardProgress(String cardId, int rating) {
        int[] progress = cardProgress.getOrDefault(cardId, new int[]{0, 0, 1});
        int timesCorrect = progress[0];
        int timesSeen = progress[1];
        int intervalDays = progress[2];

        timesSeen++;

        if (rating >= 3) {
            timesCorrect++;
            // Spaced repetition: double interval for good ratings
            intervalDays = Math.min(intervalDays * 2, 90);
        } else {
            // Reset interval for poor ratings
            intervalDays = 1;
        }

        cardProgress.put(cardId, new int[]{timesCorrect, timesSeen, intervalDays});
        lastReviewTime.put(cardId, System.currentTimeMillis());

        // Check for mastery (80%+ accuracy over 10+ reviews)
        if (timesSeen >= 10 && (timesCorrect * 100.0 / timesSeen) >= 80) {
            masteredCards.add(cardId);
        }
    }

    // ==================== QUIZ MODE ====================

    private static void quizMode(Subject filterSubject) {
        List<Question> quizQuestions = new ArrayList<>(questions);
        quizQuestions.addAll(customQuestions);

        // Filter to multiple choice and T/F only
        quizQuestions = new ArrayList<>(quizQuestions.stream()
            .filter(q -> q.type == QuestionType.MULTIPLE_CHOICE || q.type == QuestionType.TRUE_FALSE)
            .toList());

        if (filterSubject != null) {
            quizQuestions = new ArrayList<>(quizQuestions.stream()
                .filter(q -> q.subject == filterSubject)
                .toList());
        }

        if (quizQuestions.isEmpty()) {
            System.out.println(RED + t("Keine Fragen verfügbar!", "No questions available!") + RESET);
            return;
        }

        Collections.shuffle((List<?>)quizQuestions);

        System.out.println(CYAN + "\n══════════ ❓ QUIZ ══════════" + RESET);
        System.out.printf(t("%d Fragen verfügbar", "%d questions available") + "%n",
            quizQuestions.size());

        int numQuestions = Math.min(10, quizQuestions.size());
        System.out.print(t("Wie viele Fragen?", "How many questions?") + " (1-" +
            quizQuestions.size() + ", Enter=" + numQuestions + "): ");
        String input = scanner.nextLine().trim();
        if (!input.isEmpty()) {
            try {
                numQuestions = Integer.parseInt(input);
                numQuestions = Math.max(1, Math.min(numQuestions, quizQuestions.size()));
            } catch (Exception e) {}
        }

        int correct = 0;
        Map<Subject, int[]> subjectResults = new HashMap<>();

        for (int i = 0; i < numQuestions; i++) {
            Question q = quizQuestions.get(i);

            System.out.println(BOLD + "\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" + RESET);
            System.out.printf("%s[%s]%s " + t("Frage", "Question") + " %d/%d%n",
                YELLOW, q.subject.getName(), RESET, i + 1, numQuestions);
            System.out.println(BOLD + "\n" + q.getQuestion() + RESET + "\n");

            List<String> options = q.getOptions();
            for (int j = 0; j < options.size(); j++) {
                System.out.println("  " + (char)('A' + j) + ") " + options.get(j));
            }

            System.out.print("\n" + t("Antwort", "Answer") + " (A-" +
                (char)('A' + options.size() - 1) + "): ");
            String answer = scanner.nextLine().trim().toUpperCase();

            if (!answer.isEmpty()) {
                int answerIndex = answer.charAt(0) - 'A';

                subjectResults.computeIfAbsent(q.subject, k -> new int[]{0, 0});
                subjectResults.get(q.subject)[1]++;

                if (answerIndex >= 0 && answerIndex < options.size() && q.isCorrect(answerIndex)) {
                    correct++;
                    subjectResults.get(q.subject)[0]++;
                    System.out.println(GREEN + "✓ " + t("Richtig!", "Correct!") + RESET);
                } else {
                    System.out.println(RED + "✗ " + t("Falsch. Richtig war", "Wrong. Correct was") +
                        ": " + (char)('A' + q.getCorrectIndex()) + RESET);
                }

                if (!q.getExplanation().isEmpty()) {
                    System.out.println(CYAN + "💡 " + q.getExplanation() + RESET);
                }
            }

            System.out.print("\n[ENTER] " + t("für nächste Frage", "for next question") + ": ");
            scanner.nextLine();
        }

        // Show results
        double percentage = (correct * 100.0) / numQuestions;
        System.out.println(BOLD + "\n═══════════════════════════════════════" + RESET);
        System.out.printf(t("Ergebnis", "Result") + ": %d/%d (%.1f%%)%n",
            correct, numQuestions, percentage);

        String performance = percentage >= 80 ? GREEN + t("Ausgezeichnet!", "Excellent!") :
                           percentage >= 60 ? YELLOW + t("Gut!", "Good!") :
                           RED + t("Mehr üben!", "Practice more!");
        System.out.println(performance + RESET);

        todayCompleted += numQuestions;
        sessionQuestionsAnswered += numQuestions;
        sessionCorrect += correct;

        System.out.print("\n[ENTER] " + t("zum Fortfahren", "to continue") + ": ");
        scanner.nextLine();
    }

    // ==================== QUICK REVIEW (Spaced Repetition) ====================

    private static void quickReview(Subject filterSubject) {
        String title = t("SCHNELL-REVIEW", "QUICK REVIEW");
        if(filterSubject != null) {
            title += " - " + filterSubject.getName();
        }
        System.out.println(CYAN + BOLD + "\n══════════ ⚡ " + title + " ══════════" + RESET);

        List<Flashcard> allCards = new ArrayList<>(flashcards);
        allCards.addAll(customCards);
        final List<Flashcard> allCardsForId = new ArrayList<>(allCards);

        List<Flashcard> filteredCards = allCards;
        if (filterSubject != null) {
            // For TEAM, include all subcategories
            if (filterSubject == Subject.TEAM) {
                filteredCards = new ArrayList<>(allCards.stream().filter(c ->
                    c.subject() == Subject.TEAM ||
                    c.subject() == Subject.MODELS ||
                    c.subject() == Subject.PROCESSES ||
                    c.subject() == Subject.LEADERSHIP ||
                    c.subject() == Subject.CONFLICT ||
                    c.subject() == Subject.AGILE ||
                    c.subject() == Subject.ORGANIZATION
                ).toList());
            } else {
                 filteredCards = new ArrayList<>(allCards.stream()
                    .filter(c -> c.subject() == filterSubject)
                    .toList());
            }
        }

        List<Flashcard> dueCards = new ArrayList<>();
        long now = System.currentTimeMillis();

        for (Flashcard card : filteredCards) {
            String cardId = "card_" + allCardsForId.indexOf(card);
            if (allCardsForId.indexOf(card) == -1) continue; 

            int[] progress = cardProgress.getOrDefault(cardId, new int[]{0, 0, 1});
            Long lastReview = lastReviewTime.get(cardId);
            int intervalDays = progress[2];

            if (lastReview == null) {
                dueCards.add(card);
            } else {
                long daysSinceReview = (now - lastReview) / (1000 * 60 * 60 * 24);
                if (daysSinceReview >= intervalDays) {
                    dueCards.add(card);
                }
            }
        }

        if (dueCards.isEmpty()) {
            System.out.println(GREEN + "✓ " +
                t("Keine Karten zur Wiederholung fällig!", "No cards due for review!") + RESET);
            System.out.println("  " + t("Komm später wieder oder lerne neue Karten.",
                "Come back later or study new cards."));
            System.out.print("\n[ENTER] " + t("zum Fortfahren", "to continue") + ": ");
            scanner.nextLine();
            return;
        }
        
        System.out.printf("📚 %d " + t("Karten zur Wiederholung", "cards to review") + "%n",
            dueCards.size());
        
        // Re-using flashcard mode logic here
        Collections.shuffle(dueCards);
        System.out.print("[ENTER] " + t("zum Starten", "to start") + ", [q] " +
            t("zum Abbrechen", "to quit") + ": ");
        if (scanner.nextLine().trim().equalsIgnoreCase("q")) return;

        int studied = 0;
        for (int i = 0; i < dueCards.size(); i++) {
            Flashcard card = dueCards.get(i);
            String cardId = "card_" + allCardsForId.indexOf(card);

            System.out.println(BOLD + "\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" + RESET);
            System.out.printf("%s[%s]%s " + t("Karte", "Card") + " %d/%d%n",
                YELLOW, card.subject.getName(), RESET, i + 1, dueCards.size());
            System.out.println(BOLD + "\n❓ " + card.getFront() + RESET);

            System.out.print("\n[ENTER] " + t("für Antwort", "for answer") + ": ");
            scanner.nextLine();

            System.out.println(BOLD + "\n✓ " + card.getBack() + RESET);

            System.out.print("\n" + t("Bewertung", "Rating") + " (1=" +
                t("Vergessen", "Forgot") + ", 2=" + t("Schwer", "Hard") +
                ", 3=" + t("Gut", "Good") + ", 4=" + t("Leicht", "Easy") +
                ", q=" + t("beenden", "quit") + "): ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("q")) break;

            int rating = 3;
            try { rating = Integer.parseInt(input); } catch (Exception e) {}
            rating = Math.max(1, Math.min(4, rating));

            updateCardProgress(cardId, rating);
            studied++;
        }

        todayCompleted += studied;
        sessionQuestionsAnswered += studied;

        System.out.println(GREEN + "\n✓ " + studied + " " +
            t("Karten studiert!", "cards studied!") + RESET);
        System.out.print("\n[ENTER] " + t("zum Fortfahren", "to continue") + ": ");
        scanner.nextLine();
    }

    // ==================== WEAKNESS TRAINING ====================

    private static void weaknessTraining() {
        System.out.println(RED + BOLD + "\n══════════ 💪 " +
            t("SCHWÄCHEN TRAINIEREN", "TRAIN WEAKNESSES") + " ══════════" + RESET);

        List<String> weakTopics = getWeakTopics();

        if (weakTopics.isEmpty()) {
            System.out.println(GREEN + "✓ " +
                t("Keine schwachen Themen gefunden! Du bist gut vorbereitet!",
                  "No weak topics found! You're well prepared!") + RESET);
            System.out.print("\n[ENTER] " + t("zum Fortfahren", "to continue") + ": ");
            scanner.nextLine();
            return;
        }

        System.out.println(t("Schwache Themen identifiziert:", "Weak topics identified:"));
        for (int i = 0; i < weakTopics.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + weakTopics.get(i));
        }

        // Train on weak topics
        quizMode(null); // Could be enhanced to filter by weak topics
    }

    // ==================== SUBJECT QUICK ACCESS ====================

    private static void subjectQuickAccess(Subject subject) {
        System.out.println(CYAN + "\n══════════ " + subject.getName() + " ══════════" + RESET);
        System.out.println(t("Was möchtest du lernen?", "What do you want to study?"));
        System.out.println("  1. 📚 " + t("Karteikarten", "Flashcards") + " (" +
            getSubjectCardCount(subject) + " " + t("Karten", "cards") + ")");
        System.out.println("  2. ❓ Quiz (" +
            getSubjectQuestionCount(subject) + " " + t("Fragen", "questions") + ")");
        System.out.println("  3. 🔍 " + t("Als Filter setzen", "Set as filter"));
        System.out.println("  0. ← " + t("Zurück", "Back"));

        int choice = readInt(t("Auswahl", "Choice") + ": ", 0, 3);

        switch (choice) {
            case 1 -> flashcardMode(subject);
            case 2 -> quizMode(subject);
            case 3 -> {
                subjectFilter = subject;
                saveSettings();
                System.out.println(GREEN + "✓ " + t("Filter gesetzt auf:", "Filter set to:") + " " +
                    subject.getName() + RESET);
                System.out.print("\n[ENTER] " + t("zum Fortfahren", "to continue") + ": ");
                scanner.nextLine();
            }
        }
    }

    // ==================== TOPIC MENU ====================

    private static void topicMenu() {
        System.out.println(CYAN + "\n══════════ 🎯 " +
            t("THEMA AUSWÄHLEN", "SELECT TOPIC") + " ══════════" + RESET);

        System.out.println("\n" + BOLD + t("Hauptfächer:", "Main Subjects:") + RESET);
        System.out.println("  1. 💻 " + Subject.BSYS.getName() +
            " (" + getSubjectCardCount(Subject.BSYS) + " " +
            t("Karten", "cards") + ", " + getSubjectQuestionCount(Subject.BSYS) + " " +
            t("Fragen", "questions") + ")");
        System.out.println("  2. 🌐 " + Subject.DIGICOM.getName() +
            " (" + getSubjectCardCount(Subject.DIGICOM) + " " +
            t("Karten", "cards") + ", " + getSubjectQuestionCount(Subject.DIGICOM) + " " +
            t("Fragen", "questions") + ")");
        System.out.println("  3. 👥 " + Subject.TEAM.getName() +
            " (" + getSubjectCardCount(Subject.TEAM) + " " +
            t("Karten", "cards") + ", " + getSubjectQuestionCount(Subject.TEAM) + " " +
            t("Fragen", "questions") + ")");

        System.out.println("\n" + t("Weitere Themen:", "Additional Topics:") + RESET);
        System.out.println("  4. " + Subject.MODELS.getName());
        System.out.println("  5. " + Subject.PROCESSES.getName());
        System.out.println("  6. " + Subject.LEADERSHIP.getName());
        System.out.println("  7. " + Subject.CONFLICT.getName());
        System.out.println("  8. " + Subject.AGILE.getName());
        System.out.println("  9. " + Subject.ORGANIZATION.getName());

        System.out.println("\n  10. 🔍 " + t("Fach-Filter setzen", "Set subject filter") +
            " (" + t("Aktuell", "Current") + ": " +
            (subjectFilter == null ? t("Alle", "All") : subjectFilter.getName()) + ")");
        System.out.println("  0. ← " + t("Zurück", "Back"));

        int choice = readInt(t("Auswahl", "Choice") + ": ", 0, 10);
        if (choice == 0) return;

        if (choice == 10) {
            setSubjectFilter();
            return;
        }

        Subject selected = Subject.values()[choice - 1];

        System.out.println("\n" + t("Was möchtest du mit", "What do you want with") + " " +
            selected.getName() + " " + t("machen?", "do?"));
        System.out.println("  1. 📚 " + t("Karteikarten", "Flashcards"));
        System.out.println("  2. ❓ Quiz");
        System.out.println("  0. ← " + t("Zurück", "Back"));

        int modeChoice = readInt(t("Auswahl", "Choice") + ": ", 0, 2);

        switch (modeChoice) {
            case 1 -> flashcardMode(selected);
            case 2 -> quizMode(selected);
        }
    }

    private static void setSubjectFilter() {
        System.out.println(CYAN + "\n" + t("Fach-Filter setzen:", "Set subject filter:") + RESET);
        System.out.println("  1. 💻 " + Subject.BSYS.getName());
        System.out.println("  2. 🌐 " + Subject.DIGICOM.getName());
        System.out.println("  3. 👥 " + Subject.TEAM.getName());
        System.out.println("  4. 🌍 " + t("Alle Fächer (kein Filter)", "All subjects (no filter)"));
        System.out.println("  0. ← " + t("Zurück", "Back"));

        int choice = readInt(t("Auswahl", "Choice") + ": ", 0, 4);

        switch (choice) {
            case 1 -> subjectFilter = Subject.BSYS;
            case 2 -> subjectFilter = Subject.DIGICOM;
            case 3 -> subjectFilter = Subject.TEAM;
            case 4 -> subjectFilter = null;
            default -> { return; }
        }

        String filterName = subjectFilter == null ? t("Alle Fächer", "All subjects") : subjectFilter.getName();
        System.out.println(GREEN + "✓ " + t("Filter gesetzt auf:", "Filter set to:") + " " + filterName + RESET);
        System.out.print("\n[ENTER] " + t("zum Fortfahren", "to continue") + ": ");
        scanner.nextLine();
    }

    private static int getSubjectCardCount(Subject subject) {
        // For TEAM, include all TEAM-related subcategories
        if (subject == Subject.TEAM) {
            return (int) flashcards.stream().filter(c ->
                c.subject() == Subject.TEAM ||
                c.subject() == Subject.MODELS ||
                c.subject() == Subject.PROCESSES ||
                c.subject() == Subject.LEADERSHIP ||
                c.subject() == Subject.CONFLICT ||
                c.subject() == Subject.AGILE ||
                c.subject() == Subject.ORGANIZATION
            ).count() + (int) customCards.stream().filter(c ->
                c.subject() == Subject.TEAM ||
                c.subject() == Subject.MODELS ||
                c.subject() == Subject.PROCESSES ||
                c.subject() == Subject.LEADERSHIP ||
                c.subject() == Subject.CONFLICT ||
                c.subject() == Subject.AGILE ||
                c.subject() == Subject.ORGANIZATION
            ).count();
        }
        return (int) flashcards.stream().filter(c -> c.subject() == subject).count() +
               (int) customCards.stream().filter(c -> c.subject() == subject).count();
    }

    private static int getSubjectQuestionCount(Subject subject) {
        // For TEAM, include all TEAM-related subcategories
        if (subject == Subject.TEAM) {
            return (int) questions.stream().filter(q ->
                q.subject() == Subject.TEAM ||
                q.subject() == Subject.MODELS ||
                q.subject() == Subject.PROCESSES ||
                q.subject() == Subject.LEADERSHIP ||
                q.subject() == Subject.CONFLICT ||
                q.subject() == Subject.AGILE ||
                q.subject() == Subject.ORGANIZATION
            ).count() + (int) customQuestions.stream().filter(q ->
                q.subject() == Subject.TEAM ||
                q.subject() == Subject.MODELS ||
                q.subject() == Subject.PROCESSES ||
                q.subject() == Subject.LEADERSHIP ||
                q.subject() == Subject.CONFLICT ||
                q.subject() == Subject.AGILE ||
                q.subject() == Subject.ORGANIZATION
            ).count();
        }
        return (int) questions.stream().filter(q -> q.subject() == subject).count() +
               (int) customQuestions.stream().filter(q -> q.subject() == subject).count();
    }

    // ==================== STATISTICS ====================

    private static void showDetailedStats() {
        clearScreen();
        System.out.println(BLUE + BOLD + "\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║         📊 " + t("STATISTIKEN & ANALYSE", "STATISTICS & ANALYSIS") + "               ║");
        System.out.println("╚════════════════════════════════════════════════════════╝" + RESET);

        // Overall stats
        int totalCards = flashcards.size() + customCards.size();
        int totalQuestions = questions.size() + customQuestions.size();
        int masteredCount = masteredCards.size();

        System.out.println(CYAN + "\n📚 " + t("Gesamtübersicht:", "Overview:") + RESET);
        System.out.printf("  " + t("Verfügbare Karten:", "Available cards:") + " %d%n", totalCards);
        System.out.printf("  " + t("Verfügbare Fragen:", "Available questions:") + " %d%n", totalQuestions);
        System.out.printf("  " + t("Gemeisterte Karten:", "Mastered cards:") + " %s%d (%.1f%%)%s%n",
            GREEN, masteredCount, (masteredCount * 100.0 / totalCards), RESET);
        System.out.printf("  " + t("Heutige Aktivität:", "Today's activity:") + " %d/%d%n",
            todayCompleted, dailyGoal);
        System.out.printf("  " + t("Aktueller Streak:", "Current streak:") + " %s%d " +
            t("Tage", "days") + "%s 🔥%n",
            currentStreak >= 7 ? GREEN : YELLOW, currentStreak, RESET);
        System.out.printf("  " + t("Gesamte Lernzeit:", "Total study time:") + " %s%n",
            formatTime(totalStudyTimeSeconds));

        // Session stats
        long sessionDuration = (System.currentTimeMillis() - sessionStartTime) / 1000;
        double sessionAccuracy = sessionQuestionsAnswered > 0 ?
            (sessionCorrect * 100.0) / sessionQuestionsAnswered : 0;

        System.out.println(CYAN + "\n📈 " + t("Aktuelle Sitzung:", "Current session:") + RESET);
        System.out.printf("  " + t("Dauer:", "Duration:") + " %s%n", formatTime(sessionDuration));
        System.out.printf("  " + t("Beantwortete Fragen:", "Questions answered:") + " %d%n",
            sessionQuestionsAnswered);
        if (sessionQuestionsAnswered > 0) {
            System.out.printf("  " + t("Genauigkeit:", "Accuracy:") + " %.1f%%%n", sessionAccuracy);
        }

        // Exam history
        if (!examHistory.isEmpty()) {
            System.out.println(CYAN + "\n🎓 " + t("Prüfungshistorie:", "Exam history:") + RESET);
            int showCount = Math.min(5, examHistory.size());
            for (int i = examHistory.size() - showCount; i < examHistory.size(); i++) {
                System.out.println("  " + examHistory.get(i));
            }
        }

        System.out.print("\n[ENTER] " + t("zum Fortfahren", "to continue") + ": ");
        scanner.nextLine();
    }

    // ==================== INPUT MENU (Custom Content) ====================

    private static void inputMenu() {
        System.out.println(YELLOW + "\n══════════ ✏️  " +
            t("EIGENE INHALTE", "CUSTOM CONTENT") + " ══════════" + RESET);
        System.out.println("  1. ➕ " + t("Karteikarte hinzufügen", "Add flashcard"));
        System.out.println("  2. ➕ " + t("Frage hinzufügen", "Add question"));
        System.out.println("  3. 📋 " + t("Eigene Inhalte anzeigen", "View custom content"));
        System.out.println("  4. 🗑️  " + t("Eigene Inhalte löschen", "Delete custom content"));
        System.out.println("  0. ← " + t("Zurück", "Back"));

        int choice = readInt(t("Auswahl", "Choice") + ": ", 0, 4);

        switch (choice) {
            case 1 -> addCustomFlashcard();
            case 2 -> addCustomQuestion();
            case 3 -> viewCustomContent();
            case 4 -> deleteCustomContent();
        }
    }

    private static void addCustomFlashcard() {
        System.out.println("\n" + t("Neue Karteikarte", "New flashcard") + ":");

        // Select subject
        Subject[] subjects = Subject.values();
        for (int i = 0; i < subjects.length; i++) {
            System.out.println("  " + (i + 1) + ". " + subjects[i].getName());
        }
        int subjChoice = readInt(t("Thema", "Subject") + " (1-" + subjects.length + "): ",
            1, subjects.length);
        Subject subject = subjects[subjChoice - 1];

        System.out.print(t("Topic (z.B. 'Modelle'):", "Topic (e.g. 'Models'):") + " ");
        String topic = scanner.nextLine().trim();

        System.out.print(t("Vorderseite (Frage) [DE]:", "Front (question) [DE]:") + " ");
        String frontDE = scanner.nextLine().trim();

        System.out.print(t("Vorderseite (Frage) [EN]:", "Front (question) [EN]:") + " ");
        String frontEN = scanner.nextLine().trim();

        System.out.print(t("Rückseite (Antwort) [DE]:", "Back (answer) [DE]:") + " ");
        String backDE = scanner.nextLine().trim();

        System.out.print(t("Rückseite (Antwort) [EN]:", "Back (answer) [EN]:") + " ");
        String backEN = scanner.nextLine().trim();

        int difficulty = readInt(t("Schwierigkeit (1-3):", "Difficulty (1-3):") + " ", 1, 3);

        Flashcard card = new Flashcard(subject, topic, frontDE, frontEN, backDE, backEN, difficulty);
        customCards.add(card);
        saveCustomContent();

        System.out.println(GREEN + "✓ " + t("Karteikarte hinzugefügt!", "Flashcard added!") + RESET);
    }

    private static void addCustomQuestion() {
        System.out.println("\n" + t("Neue Frage", "New question") + ":");

        // Select subject
        Subject[] subjects = Subject.values();
        for (int i = 0; i < subjects.length; i++) {
            System.out.println("  " + (i + 1) + ". " + subjects[i].getName());
        }
        int subjChoice = readInt(t("Thema", "Subject") + " (1-" + subjects.length + "): ",
            1, subjects.length);
        Subject subject = subjects[subjChoice - 1];

        System.out.println(t("Fragetyp:", "Question type:"));
        System.out.println("  1. Multiple Choice");
        System.out.println("  2. " + t("Wahr/Falsch", "True/False"));
        int typeChoice = readInt(t("Auswahl", "Choice") + ": ", 1, 2);

        if (typeChoice == 2) {
            // True/False
            System.out.print(t("Aussage [DE]:", "Statement [DE]:") + " ");
            String statementDE = scanner.nextLine().trim();

            System.out.print(t("Aussage [EN]:", "Statement [EN]:") + " ");
            String statementEN = scanner.nextLine().trim();

            System.out.print(t("Ist die Aussage wahr? (j/n):", "Is the statement true? (y/n):") + " ");
            boolean isTrue = scanner.nextLine().trim().toLowerCase().startsWith("j") ||
                           scanner.nextLine().trim().toLowerCase().startsWith("y");

            System.out.print(t("Erklärung [DE]:", "Explanation [DE]:") + " ");
            String explanationDE = scanner.nextLine().trim();

            System.out.print(t("Erklärung [EN]:", "Explanation [EN]:") + " ");
            String explanationEN = scanner.nextLine().trim();

            Question q = Question.trueFalse(subject, statementDE, statementEN,
                isTrue, explanationDE, explanationEN);
            customQuestions.add(q);
        } else {
            // Multiple Choice
            System.out.print(t("Frage [DE]:", "Question [DE]:") + " ");
            String questionDE = scanner.nextLine().trim();

            System.out.print(t("Frage [EN]:", "Question [EN]:") + " ");
            String questionEN = scanner.nextLine().trim();

            List<String> optionsDE = new ArrayList<>();
            List<String> optionsEN = new ArrayList<>();

            for (int i = 0; i < 4; i++) {
                System.out.print(t("Option", "Option") + " " + (char)('A' + i) + " [DE]: ");
                optionsDE.add(scanner.nextLine().trim());
                System.out.print(t("Option", "Option") + " " + (char)('A' + i) + " [EN]: ");
                optionsEN.add(scanner.nextLine().trim());
            }

            int correctIndex = readInt(t("Richtige Option (0-3):", "Correct option (0-3):") + " ", 0, 3);

            System.out.print(t("Erklärung [DE]:", "Explanation [DE]:") + " ");
            String explanationDE = scanner.nextLine().trim();

            System.out.print(t("Erklärung [EN]:", "Explanation [EN]:") + " ");
            String explanationEN = scanner.nextLine().trim();

            Question q = new Question(subject, questionDE, questionEN,
                optionsDE, optionsEN, correctIndex, explanationDE, explanationEN,
                QuestionType.MULTIPLE_CHOICE, 2);
            customQuestions.add(q);
        }

        saveCustomContent();
        System.out.println(GREEN + "✓ " + t("Frage hinzugefügt!", "Question added!") + RESET);
    }

    private static void viewCustomContent() {
        System.out.println("\n" + t("Eigene Inhalte:", "Custom content:"));
        System.out.println(t("Karteikarten:", "Flashcards:") + " " + customCards.size());
        System.out.println(t("Fragen:", "Questions:") + " " + customQuestions.size());

        if (!customCards.isEmpty()) {
            System.out.println("\n" + t("Deine Karteikarten:", "Your flashcards:"));
            for (int i = 0; i < Math.min(5, customCards.size()); i++) {
                Flashcard c = customCards.get(i);
                System.out.println("  " + (i + 1) + ". " + c.getFront());
            }
            if (customCards.size() > 5) {
                System.out.println("  ... " + t("und", "and") + " " +
                    (customCards.size() - 5) + " " + t("weitere", "more"));
            }
        }

        System.out.print("\n[ENTER] " + t("zum Fortfahren", "to continue") + ": ");
        scanner.nextLine();
    }

    private static void deleteCustomContent() {
        if (customCards.isEmpty() && customQuestions.isEmpty()) {
            System.out.println(YELLOW + t("Keine eigenen Inhalte vorhanden.",
                "No custom content available.") + RESET);
            return;
        }

        System.out.print(RED + t("WARNUNG: Alle eigenen Inhalte löschen? (j/n):",
            "WARNING: Delete all custom content? (y/n):") + RESET + " ");
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (confirm.startsWith("j") || confirm.startsWith("y")) {
            customCards.clear();
            customQuestions.clear();
            saveCustomContent();
            System.out.println(GREEN + "✓ " + t("Eigene Inhalte gelöscht.", "Custom content deleted.") + RESET);
        }
    }

    // ==================== IMPORT/EXPORT ====================

    private static void importExportMenu() {
        System.out.println(CYAN + "\n══════════ 💾 IMPORT/EXPORT ══════════" + RESET);
        System.out.println("  1. 📥 " + t("Importieren (CSV)", "Import (CSV)"));
        System.out.println("  2. 📤 " + t("Exportieren", "Export"));
        System.out.println("  0. ← " + t("Zurück", "Back"));

        int choice = readInt(t("Auswahl", "Choice") + ": ", 0, 2);

        switch (choice) {
            case 1 -> importFromCSV();
            case 2 -> exportContent();
        }
    }

    private static void importFromCSV() {
        System.out.print(t("Dateiname (z.B. 'cards.csv'):", "Filename (e.g. 'cards.csv'):") + " ");
        String filename = scanner.nextLine().trim();

        try {
            List<String> lines = Files.readAllLines(Path.of(filename));
            int imported = 0;
            int skipped = 0;
            boolean isFirstLine = true;
            boolean isAlternateFormat = false;

            // Detect format from header
            if (!lines.isEmpty()) {
                String header = lines.get(0).toLowerCase();
                isAlternateFormat = header.contains("set name") || header.contains("front (question)");
            }

            for (String line : lines) {
                if (isFirstLine) {
                    isFirstLine = false;
                    // Skip header row
                    if (line.toLowerCase().contains("subject") || line.toLowerCase().contains("set name")
                        || line.toLowerCase().contains("topic")) {
                        continue;
                    }
                }

                List<String> parts = parseCSVLine(line);

                try {
                    if (isAlternateFormat && parts.size() >= 5) {
                        // Alternate format: Set #, Set Name, Topic, Front (Question), Back (Answer)
                        String setName = parts.get(1).trim();
                        String topic = parts.get(2).trim();
                        String front = parts.get(3).trim();
                        String back = parts.get(4).trim();

                        Subject subject = detectSubject(setName);
                        if (subject == null) {
                            skipped++;
                            continue;
                        }

                        // Single language - use for both DE and EN
                        Flashcard card = new Flashcard(subject, topic, front, front, back, back, 2);
                        customCards.add(card);
                        imported++;
                    } else if (parts.size() >= 6) {
                        // Standard format: Subject,Topic,FrontDE,FrontEN,BackDE,BackEN,Difficulty
                        Subject subject = parseSubject(parts.get(0).trim());
                        if (subject == null) {
                            skipped++;
                            continue;
                        }
                        String topic = parts.get(1).trim();
                        String frontDE = parts.get(2).trim();
                        String frontEN = parts.get(3).trim();
                        String backDE = parts.get(4).trim();
                        String backEN = parts.get(5).trim();
                        int difficulty = parts.size() > 6 ? Integer.parseInt(parts.get(6).trim()) : 2;

                        Flashcard card = new Flashcard(subject, topic, frontDE, frontEN,
                            backDE, backEN, difficulty);
                        customCards.add(card);
                        imported++;
                    } else {
                        skipped++;
                    }
                } catch (Exception e) {
                    skipped++;
                }
            }

            saveCustomContent();
            System.out.println();
            if (imported > 0) {
                System.out.println(GREEN + BOLD + "✓ " + t("Import erfolgreich!", "Import successful!") + RESET);
                System.out.println(GREEN + "  → " + imported + " " +
                    t("Karten importiert", "cards imported") + RESET);
            } else {
                System.out.println(YELLOW + "⚠ " + t("Keine Karten importiert.", "No cards imported.") + RESET);
            }
            if (skipped > 0) {
                System.out.println(YELLOW + "  → " + skipped + " " +
                    t("Zeilen übersprungen (ungültiges Format)", "lines skipped (invalid format)") + RESET);
            }
        } catch (IOException e) {
            System.out.println(RED + BOLD + "✗ " + t("Import fehlgeschlagen!", "Import failed!") + RESET);
            System.out.println(RED + "  → " + t("Fehler:", "Error:") + " " + e.getMessage() + RESET);
        }
    }

    private static List<String> parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        result.add(current.toString());
        return result;
    }

    private static Subject detectSubject(String setName) {
        String lower = setName.toLowerCase();
        if (lower.contains("digital") || lower.contains("communication") || lower.contains("network")
            || lower.contains("osi") || lower.contains("tcp") || lower.contains("vlan")
            || lower.contains("routing") || lower.contains("switching")) {
            return Subject.DIGICOM;
        } else if (lower.contains("betriebssystem") || lower.contains("operating system")
            || lower.contains("bsys") || lower.contains("process") || lower.contains("memory")
            || lower.contains("scheduling") || lower.contains("file system") || lower.contains("raid")) {
            return Subject.BSYS;
        } else if (lower.contains("team") || lower.contains("leadership") || lower.contains("agile")
            || lower.contains("conflict") || lower.contains("führung") || lower.contains("organisation")) {
            return Subject.TEAM;
        }
        return null;
    }

    private static Subject parseSubject(String value) {
        // Try direct enum match first
        try {
            return Subject.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Fall back to detection
            return detectSubject(value);
        }
    }

    private static void exportContent() {
        try {
            String filename = "export_" + LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv";

            try (PrintWriter pw = new PrintWriter(filename)) {
                pw.println("Subject,Topic,FrontDE,FrontEN,BackDE,BackEN,Difficulty");

                for (Flashcard card : customCards) {
                    pw.printf("%s,%s,\"%s\",\"%s\",\"%s\",\"%s\",%d%n",
                        card.subject,
                        card.topic,
                        card.front.get(Language.DE),
                        card.front.get(Language.EN),
                        card.back.get(Language.DE),
                        card.back.get(Language.EN),
                        card.difficulty);
                }
            }

            System.out.println(GREEN + "✓ " + t("Exportiert nach", "Exported to") + " " +
                filename + RESET);
        } catch (IOException e) {
            System.out.println(RED + "✗ " + t("Fehler beim Exportieren:", "Error exporting:") +
                " " + e.getMessage() + RESET);
        }
    }

    // ==================== SETTINGS ====================

    private static void settingsMenu() {
        System.out.println(CYAN + "\n══════════ ⚙️  " + t("EINSTELLUNGEN", "SETTINGS") + " ══════════" + RESET);
        System.out.println("  1. 🌐 " + t("Sprache ändern", "Change language") +
            " (" + t("Aktuell", "Current") + ": " + currentLang + ")");
        System.out.println("  2. 📚 " + t("Fach ändern", "Change subject") +
            " (" + t("Aktuell", "Current") + ": " + subjectFilter.getName() + ")");
        System.out.println("  3. 🎯 " + t("Tagesziel ändern", "Change daily goal") +
            " (" + t("Aktuell", "Current") + ": " + dailyGoal + ")");
        System.out.println("  4. 🗑️  " + t("Alle Daten zurücksetzen", "Reset all data"));
        System.out.println("  0. ← " + t("Zurück", "Back"));

        int choice = readInt(t("Auswahl", "Choice") + ": ", 0, 4);

        switch (choice) {
            case 1 -> selectLanguage();
            case 2 -> selectSubject();
            case 3 -> changeDailyGoal();
            case 4 -> resetAllData();
        }
    }

    private static void selectSubject() {
        System.out.println(CYAN + "\n" + t("Fach wählen:", "Select subject:") + RESET);
        System.out.println("  1. 💻 " + Subject.BSYS.getName());
        System.out.println("  2. 🌐 " + Subject.DIGICOM.getName());
        System.out.println("  3. 👥 " + Subject.TEAM.getName());
        System.out.println("  0. ← " + t("Zurück", "Back"));

        int choice = readInt(t("Auswahl", "Choice") + ": ", 0, 3);

        switch (choice) {
            case 1 -> subjectFilter = Subject.BSYS;
            case 2 -> subjectFilter = Subject.DIGICOM;
            case 3 -> subjectFilter = Subject.TEAM;
            default -> { return; }
        }

        saveSettings();
        System.out.println(GREEN + "✓ " + t("Fach geändert auf:", "Subject changed to:") + " " + subjectFilter.getName() + RESET);
        System.out.print("\n[ENTER] " + t("zum Fortfahren", "to continue") + ": ");
        scanner.nextLine();
    }


    private static void selectLanguage() {
        System.out.println("\n" + t("Sprache wählen:", "Select language:"));
        System.out.println("  1. Deutsch (DE)");
        System.out.println("  2. English (EN)");

        int choice = readInt(t("Auswahl", "Choice") + ": ", 1, 2);
        currentLang = choice == 1 ? Language.DE : Language.EN;

        saveSettings();
        System.out.println(GREEN + "✓ " + t("Sprache geändert!", "Language changed!") + RESET);
    }

    private static void changeDailyGoal() {
        dailyGoal = readInt(t("Neues Tagesziel (1-100):", "New daily goal (1-100):") + " ", 1, 100);
        saveSettings();
        System.out.println(GREEN + "✓ " + t("Tagesziel geändert!", "Daily goal changed!") + RESET);
    }

    private static void resetAllData() {
        System.out.print(RED + t("WARNUNG: Alle Daten (Fortschritt, eigene Inhalte) löschen? (j/n):",
            "WARNING: Delete all data (progress, custom content)? (y/n):") + RESET + " ");
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (confirm.startsWith("j") || confirm.startsWith("y")) {
            cardProgress.clear();
            quizProgress.clear();
            lastReviewTime.clear();
            performanceHistory.clear();
            masteredCards.clear();
            customCards.clear();
            customQuestions.clear();
            examHistory.clear();
            currentStreak = 0;
            todayCompleted = 0;
            totalStudyTimeSeconds = 0;

            saveAllData();
            saveSettings();

            System.out.println(GREEN + "✓ " + t("Alle Daten gelöscht.", "All data deleted.") + RESET);
        }
    }

    // ==================== HELPER METHODS ====================

    private static int countDueReviews() {
        int count = 0;
        long now = System.currentTimeMillis();
        List<Flashcard> allCards = new ArrayList<>(flashcards);
        allCards.addAll(customCards);

        for (int i = 0; i < allCards.size(); i++) {
            String cardId = "card_" + i;
            int[] progress = cardProgress.getOrDefault(cardId, new int[]{0, 0, 1});
            Long lastReview = lastReviewTime.get(cardId);

            if (lastReview == null) {
                count++;
            } else {
                long daysSinceReview = (now - lastReview) / (1000 * 60 * 60 * 24);
                if (daysSinceReview >= progress[2]) {
                    count++;
                }
            }
        }

        return count;
    }

    private static List<String> getWeakTopics() {
        List<String> weak = new ArrayList<>();

        for (Map.Entry<String, int[]> entry : quizProgress.entrySet()) {
            int[] progress = entry.getValue();
            if (progress[1] >= 5) { // At least 5 attempts
                double accuracy = (progress[0] * 100.0) / progress[1];
                if (accuracy < 60) {
                    weak.add(entry.getKey());
                }
            }
        }

        return weak;
    }

    private static void updateStreak() {
        LocalDate today = LocalDate.now();

        if (!today.equals(lastActiveDate)) {
            long daysBetween = ChronoUnit.DAYS.between(lastActiveDate, today);

            if (daysBetween == 1) {
                // Consecutive day
                currentStreak++;
            } else if (daysBetween > 1) {
                // Streak broken
                currentStreak = 1;
            }

            lastActiveDate = today;
            todayCompleted = 0;
        }
    }

    private static void printSessionSummary() {
        long sessionDuration = (System.currentTimeMillis() - sessionStartTime) / 1000;
        totalStudyTimeSeconds += sessionDuration;

        System.out.println(CYAN + "\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║         📊 " + t("SITZUNGSZUSAMMENFASSUNG", "SESSION SUMMARY") + "               ║");
        System.out.println("╚════════════════════════════════════════════════════════╝" + RESET);

        System.out.printf("  " + t("Dauer:", "Duration:") + " %s%n", formatTime(sessionDuration));
        System.out.printf("  " + t("Beantwortete Fragen:", "Questions answered:") + " %d%n",
            sessionQuestionsAnswered);

        if (sessionQuestionsAnswered > 0) {
            double accuracy = (sessionCorrect * 100.0) / sessionQuestionsAnswered;
            System.out.printf("  " + t("Genauigkeit:", "Accuracy:") + " %.1f%%%n", accuracy);
        }

        System.out.println("\n" + t("Bis zum nächsten Mal! 👋", "See you next time! 👋"));
    }

    private static int readInt(String prompt, int min, int max) {
        while (true) {
            try {
                System.out.print(prompt);
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println(RED + t("Bitte Zahl zwischen", "Please enter number between") +
                    " " + min + " " + t("und", "and") + " " + max + " " +
                    t("eingeben!", "!") + RESET);
            } catch (NumberFormatException e) {
                System.out.println(RED + t("Ungültige Eingabe!", "Invalid input!") + RESET);
            }
        }
    }

    private static String formatTime(long seconds) {
        if (seconds < 60) return seconds + "s";
        if (seconds < 3600) return (seconds / 60) + "m " + (seconds % 60) + "s";
        return (seconds / 3600) + "h " + ((seconds % 3600) / 60) + "m";
    }

    private static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    // ==================== DATA PERSISTENCE ====================

    private static boolean settingsExist() {
        return Files.exists(Path.of(SETTINGS_FILE));
    }

    private static void loadSettings() {
        try {
            if (Files.exists(Path.of(SETTINGS_FILE))) {
                List<String> lines = Files.readAllLines(Path.of(SETTINGS_FILE));
                for (String line : lines) {
                    String[] parts = line.split("=");
                    if (parts.length == 2) {
                        switch (parts[0]) {
                            case "language" -> {
                                // Handle both old format (Deutsch/English) and new format (DE/EN)
                                String langValue = parts[1].trim();
                                if ("Deutsch".equals(langValue) || "German".equals(langValue)) {
                                    currentLang = Language.DE;
                                } else if ("English".equals(langValue)) {
                                    currentLang = Language.EN;
                                } else {
                                    // Try parsing as enum (DE/EN)
                                    try {
                                        currentLang = Language.valueOf(langValue);
                                    } catch (IllegalArgumentException e) {
                                        // Default to DE if invalid
                                        currentLang = Language.DE;
                                    }
                                }
                            }
                            case "dailyGoal" -> dailyGoal = Integer.parseInt(parts[1]);
                            case "subjectFilter" -> {
                                String filterValue = parts[1].trim();
                                if (!"null".equals(filterValue)) {
                                    try {
                                        subjectFilter = Subject.valueOf(filterValue);
                                    } catch (IllegalArgumentException e) {
                                        // Ignore invalid value, will be handled by default below
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            // Settings file doesn't exist yet, will be handled by default below
        }

        // If no subject is set (first run or invalid), default to BSYS
        if (subjectFilter == null) {
            subjectFilter = Subject.BSYS;
        }
    }

    private static void saveSettings() {
        try (PrintWriter pw = new PrintWriter(SETTINGS_FILE)) {
            pw.println("language=" + currentLang);
            pw.println("dailyGoal=" + dailyGoal);
            pw.println("subjectFilter=" + (subjectFilter == null ? "null" : subjectFilter.name()));
        } catch (IOException e) {
            System.err.println("Error saving settings: " + e.getMessage());
        }
    }

    private static void loadAllData() {
        loadProgress();
        loadCustomContent();
        loadExamHistory();
    }

    private static void saveAllData() {
        saveProgress();
        saveCustomContent();
        saveExamHistory();
    }

    private static void loadProgress() {
        try {
            if (Files.exists(Path.of(PROGRESS_FILE))) {
                List<String> lines = Files.readAllLines(Path.of(PROGRESS_FILE));
                String section = "";

                for (String line : lines) {
                    if (line.startsWith("[")) {
                        section = line;
                        continue;
                    }

                    String[] parts = line.split("=");
                    if (parts.length < 2) continue;

                    switch (section) {
                        case "[CARD_PROGRESS]" -> {
                            String[] values = parts[1].split(",");
                            if (values.length >= 3) {
                                cardProgress.put(parts[0], new int[]{
                                    Integer.parseInt(values[0]),
                                    Integer.parseInt(values[1]),
                                    Integer.parseInt(values[2])
                                });
                            }
                        }
                        case "[QUIZ_PROGRESS]" -> {
                            String[] values = parts[1].split(",");
                            if (values.length >= 2) {
                                quizProgress.put(parts[0], new int[]{
                                    Integer.parseInt(values[0]),
                                    Integer.parseInt(values[1])
                                });
                            }
                        }
                        case "[LAST_REVIEW]" -> {
                            lastReviewTime.put(parts[0], Long.parseLong(parts[1]));
                        }
                        case "[MASTERED]" -> {
                            masteredCards.add(parts[0]);
                        }
                        case "[STATS]" -> {
                            switch (parts[0]) {
                                case "streak" -> currentStreak = Integer.parseInt(parts[1]);
                                case "lastActive" -> lastActiveDate = LocalDate.parse(parts[1]);
                                case "todayCompleted" -> todayCompleted = Integer.parseInt(parts[1]);
                                case "totalStudyTime" -> totalStudyTimeSeconds = Long.parseLong(parts[1]);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            // Progress file doesn't exist yet
        }
    }

    private static void saveProgress() {
        try (PrintWriter pw = new PrintWriter(PROGRESS_FILE)) {
            pw.println("[CARD_PROGRESS]");
            for (Map.Entry<String, int[]> entry : cardProgress.entrySet()) {
                int[] progress = entry.getValue();
                pw.printf("%s=%d,%d,%d%n", entry.getKey(), progress[0], progress[1], progress[2]);
            }

            pw.println("\n[QUIZ_PROGRESS]");
            for (Map.Entry<String, int[]> entry : quizProgress.entrySet()) {
                int[] progress = entry.getValue();
                pw.printf("%s=%d,%d%n", entry.getKey(), progress[0], progress[1]);
            }

            pw.println("\n[LAST_REVIEW]");
            for (Map.Entry<String, Long> entry : lastReviewTime.entrySet()) {
                pw.printf("%s=%d%n", entry.getKey(), entry.getValue());
            }

            pw.println("\n[MASTERED]");
            for (String cardId : masteredCards) {
                pw.println(cardId + "=1");
            }

            pw.println("\n[STATS]");
            pw.println("streak=" + currentStreak);
            pw.println("lastActive=" + lastActiveDate);
            pw.println("todayCompleted=" + todayCompleted);
            pw.println("totalStudyTime=" + totalStudyTimeSeconds);
        } catch (IOException e) {
            System.err.println("Error saving progress: " + e.getMessage());
        }
    }

    private static void loadCustomContent() {
        // Load custom flashcards
        try {
            if (Files.exists(Path.of(FLASHCARDS_FILE))) {
                List<String> lines = Files.readAllLines(Path.of(FLASHCARDS_FILE));
                for (String line : lines) {
                    String[] parts = line.split("\\|\\|\\|");
                    if (parts.length >= 7) {
                        try {
                            Subject subject = Subject.valueOf(parts[0]);
                            String topic = parts[1];
                            String frontDE = parts[2].replace("\\n", "\n");
                            String frontEN = parts[3].replace("\\n", "\n");
                            String backDE = parts[4].replace("\\n", "\n");
                            String backEN = parts[5].replace("\\n", "\n");
                            int difficulty = Integer.parseInt(parts[6]);

                            Flashcard card = new Flashcard(subject, topic, frontDE, frontEN,
                                backDE, backEN, difficulty);
                            customCards.add(card);
                        } catch (Exception e) {
                            // Skip malformed entries
                        }
                    }
                }
            }
        } catch (IOException e) {
            // File doesn't exist yet
        }

        // Load custom questions
        try {
            if (Files.exists(Path.of(QUESTIONS_FILE))) {
                List<String> lines = Files.readAllLines(Path.of(QUESTIONS_FILE));
                for (String line : lines) {
                    String[] parts = line.split("\\|\\|\\|");
                    if (parts.length >= 8) {
                        try {
                            Subject subject = Subject.valueOf(parts[0]);
                            String questionDE = parts[1].replace("\\n", "\n");
                            String questionEN = parts[2].replace("\\n", "\n");
                            String optionsStr = parts[3];
                            String[] optionsDE = optionsStr.split(";;;");
                            String optionsStr2 = parts[4];
                            String[] optionsEN = optionsStr2.split(";;;");
                            int correctIndex = Integer.parseInt(parts[5]);
                            String explanationDE = parts[6].replace("\\n", "\n");
                            String explanationEN = parts[7].replace("\\n", "\n");
                            QuestionType type = parts.length > 8 ?
                                QuestionType.valueOf(parts[8]) : QuestionType.MULTIPLE_CHOICE;
                            int points = parts.length > 9 ? Integer.parseInt(parts[9]) : 2;

                            Question q = new Question(subject, questionDE, questionEN,
                                Arrays.asList(optionsDE), Arrays.asList(optionsEN),
                                correctIndex, explanationDE, explanationEN, type, points);
                            customQuestions.add(q);
                        } catch (Exception e) {
                            // Skip malformed entries
                        }
                    }
                }
            }
        } catch (IOException e) {
            // File doesn't exist yet
        }
    }

    private static void saveCustomContent() {
        // Save custom flashcards
        try (PrintWriter pw = new PrintWriter(FLASHCARDS_FILE)) {
            for (Flashcard card : customCards) {
                pw.printf("%s|||%s|||%s|||%s|||%s|||%s|||%d%n",
                    card.subject,
                    card.topic,
                    card.front.get(Language.DE).replace("\n", "\\n"),
                    card.front.get(Language.EN).replace("\n", "\\n"),
                    card.back.get(Language.DE).replace("\n", "\\n"),
                    card.back.get(Language.EN).replace("\n", "\\n"),
                    card.difficulty);
            }
        } catch (IOException e) {
            System.err.println("Error saving flashcards: " + e.getMessage());
        }

        // Save custom questions
        try (PrintWriter pw = new PrintWriter(QUESTIONS_FILE)) {
            for (Question q : customQuestions) {
                String optionsDE = String.join(";;;", q.options.get(Language.DE));
                String optionsEN = String.join(";;;", q.options.get(Language.EN));

                pw.printf("%s|||%s|||%s|||%s|||%s|||%d|||%s|||%s|||%s|||%d%n",
                    q.subject,
                    q.question.get(Language.DE).replace("\n", "\\n"),
                    q.question.get(Language.EN).replace("\n", "\\n"),
                    optionsDE,
                    optionsEN,
                    q.getCorrectIndex(),
                    q.explanation.get(Language.DE).replace("\n", "\\n"),
                    q.explanation.get(Language.EN).replace("\n", "\\n"),
                    q.type,
                    q.points);
            }
        } catch (IOException e) {
            System.err.println("Error saving questions: " + e.getMessage());
        }
    }

    private static void loadExamHistory() {
        try {
            if (Files.exists(Path.of(EXAM_HISTORY_FILE))) {
                examHistory.addAll(Files.readAllLines(Path.of(EXAM_HISTORY_FILE)));
            }
        } catch (IOException e) {
            // File doesn't exist yet
        }
    }

    private static void saveExamHistory() {
        try (PrintWriter pw = new PrintWriter(EXAM_HISTORY_FILE)) {
            for (String entry : examHistory) {
                pw.println(entry);
            }
        } catch (IOException e) {
            System.err.println("Error saving exam history: " + e.getMessage());
        }
    }

    // ==================== CONTENT INITIALIZATION ====================

    private static void initializeContent() {
        // Load TEAM content from ContentData.java
        loadTeamContent();

        // Load BSYS and DigiCom content from BSYSDigiComContent.java
        loadBSYSDigiComContent();
    }

    /**
     * Load Team Development content
     */
    private static void loadTeamContent() {
        List<ContentData.Flashcard> sourceCards = ContentData.initializeFlashcards();
        List<ContentData.Question> sourceQuestions = ContentData.initializeQuestions();

        // Convert flashcards
        for (ContentData.Flashcard card : sourceCards) {
            Subject subject = convertTeamSubject(card.subject);
            Flashcard converted = new Flashcard(
                subject,
                card.topic,
                card.frontDE,
                card.frontEN,
                card.backDE,
                card.backEN,
                card.difficulty
            );
            flashcards.add(converted);
        }

        // Convert questions
        for (ContentData.Question q : sourceQuestions) {
            Subject subject = convertTeamSubject(q.subject);

            if ("true_false".equals(q.type)) {
                boolean isTrue = q.correctIndex == 0;
                Question converted = Question.trueFalse(
                    subject,
                    q.questionDE,
                    q.questionEN,
                    isTrue,
                    q.explanationDE,
                    q.explanationEN
                );
                questions.add(converted);
            } else if ("multiple_choice".equals(q.type)) {
                Question converted = new Question(
                    subject,
                    q.questionDE, q.questionEN,
                    Arrays.asList(q.optionsDE), Arrays.asList(q.optionsEN),
                    q.correctIndex,
                    q.explanationDE, q.explanationEN,
                    QuestionType.MULTIPLE_CHOICE, 2
                );
                questions.add(converted);
            } else if ("fill_in".equals(q.type)) {
                Question converted = Question.fillIn(
                    subject,
                    q.questionDE, q.questionEN,
                    Arrays.asList(q.optionsDE), Arrays.asList(q.optionsEN),
                    q.correctIndex // points
                );
                questions.add(converted);
            }
        }
    }

    /**
     * Load BSYS and DigiCom content
     */
    private static void loadBSYSDigiComContent() {
        List<BSYSDigiComContent.Flashcard> sourceCards = BSYSDigiComContent.initializeFlashcards();
        List<BSYSDigiComContent.Question> sourceQuestions = BSYSDigiComContent.initializeQuestions();

        // Convert flashcards
        for (BSYSDigiComContent.Flashcard card : sourceCards) {
            Subject subject = convertBSYSSubject(card.subject);
            Flashcard converted = new Flashcard(
                subject,
                card.topic,
                card.frontDE,
                card.frontEN,
                card.backDE,
                card.backEN,
                card.difficulty
            );
            flashcards.add(converted);
        }

        // Convert questions
        for (BSYSDigiComContent.Question q : sourceQuestions) {
            Subject subject = convertBSYSSubject(q.subject);

            if (q.type == BSYSDigiComContent.QuestionType.TRUE_FALSE) {
                boolean isTrue = q.correctIndex == 0;
                Question converted = Question.trueFalse(
                    subject,
                    q.questionDE,
                    q.questionEN,
                    isTrue,
                    q.explanationDE,
                    q.explanationEN
                );
                questions.add(converted);
            } else if (q.type == BSYSDigiComContent.QuestionType.MULTIPLE_CHOICE) {
                Question converted = new Question(
                    subject,
                    q.questionDE, q.questionEN,
                    q.optionsDE, q.optionsEN,
                    q.correctIndex,
                    q.explanationDE, q.explanationEN,
                    QuestionType.MULTIPLE_CHOICE, 2
                );
                questions.add(converted);
            }
        }
    }

    /**
     * Convert ContentData.Subject enum to StudyApp.Subject enum
     */
    private static Subject convertTeamSubject(ContentData.Subject source) {
        return switch (source) {
            case MODELS -> Subject.MODELS;
            case FUNDAMENTALS -> Subject.TEAM;
            case LEADERSHIP -> Subject.LEADERSHIP;
            case CONFLICT -> Subject.CONFLICT;
            case AGILE -> Subject.AGILE;
            case ORGANIZATION -> Subject.ORGANIZATION;
            case REFLECTION -> Subject.TEAM;
        };
    }

    /**
     * Convert BSYSDigiComContent.Subject enum to StudyApp.Subject enum
     */
    private static Subject convertBSYSSubject(BSYSDigiComContent.Subject source) {
        return switch (source) {
            case BSYS -> Subject.BSYS;
            case DIGICOM -> Subject.DIGICOM;
        };
    }
}
