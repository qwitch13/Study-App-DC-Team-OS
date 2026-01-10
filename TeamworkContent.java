import java.util.ArrayList;
import java.util.List;

/**
 * Teamwork Content Module for StudyApp
 * Contains flashcards and quiz questions for the Teamarbeit (Teamwork) course
 * Topics: Team development models, roles, communication, and organizational culture
 *
 * @author Extended based on lecture materials WS2025/26
 */
public class TeamworkContent {

    /**
     * Initialize all TEAM flashcards
     * @return List of Flashcard objects
     */
    public static List<StudyApp.Flashcard> getTeamFlashcards() {
        List<StudyApp.Flashcard> flashcards = new ArrayList<>();

        // ==================== RANGDYNAMIKMODELL ====================

        flashcards.add(new StudyApp.Flashcard("TEAM", "Rangdynamik",
            "What is the Rangdynamikmodell?",
            "A model by Raul Schindler describing power and rank dynamics in groups.\n\n" +
            "Four central roles:\n" +
            "• Alpha (Leader) - highest authority\n" +
            "• Beta (Advisor) - expert supporting Alpha\n" +
            "• Gamma (Followers) - majority of group\n" +
            "• Omega (Critic) - counterpart/opposition",
            2));

        flashcards.add(new StudyApp.Flashcard("TEAM", "Rangdynamik",
            "Describe the ALPHA role in Rangdynamik",
            "ALPHA (Führer/Leader):\n" +
            "• Central figure with highest authority/influence\n" +
            "• Accepted as leader by the group\n" +
            "• Provides orientation and structure\n" +
            "• Can have formal or informal leadership",
            1));

        flashcards.add(new StudyApp.Flashcard("TEAM", "Rangdynamik",
            "Describe the BETA role in Rangdynamik",
            "BETA (Berater/Advisor):\n" +
            "• Supports Alpha as expert/consultant\n" +
            "• High reputation for specific knowledge or skills\n" +
            "• Has influence without direct responsibility of Alpha\n" +
            "• Takes on advisory role",
            1));

        flashcards.add(new StudyApp.Flashcard("TEAM", "Rangdynamik",
            "Describe the GAMMA role in Rangdynamik",
            "GAMMA (Mitläufer/Followers):\n" +
            "• Majority of group members\n" +
            "• Support Alpha and Beta without leading\n" +
            "• Seek belonging and security\n" +
            "• Stabilize the Alpha-Beta system",
            1));

        flashcards.add(new StudyApp.Flashcard("TEAM", "Rangdynamik",
            "Describe the OMEGA role in Rangdynamik",
            "OMEGA (Gegenpol/Critic):\n" +
            "• Takes role of counterpart/critic\n" +
            "• Points out tensions or problems\n" +
            "• In opposition to Alpha\n" +
            "• Brings challenges & critical perspectives\n" +
            "• Forces group reflection",
            2));

        flashcards.add(new StudyApp.Flashcard("TEAM", "Rangdynamik",
            "What are 4 applications of Rangdynamikmodell?",
            "1. GROUP DYNAMICS ANALYSIS:\n   Identify who holds which role\n\n" +
            "2. CONFLICT MANAGEMENT:\n   Understand Alpha-Omega tensions\n\n" +
            "3. TEAM COHESION:\n   Recognize all roles as valuable\n\n" +
            "4. ROLE FLEXIBILITY:\n   Enable switching between roles",
            2));

        // ==================== EISBERGMODELL ====================

        flashcards.add(new StudyApp.Flashcard("TEAM", "Communication",
            "What is the Eisbergmodell (Iceberg Model)?",
            "Communication and team dynamics have two levels:\n\n" +
            "SURFACE (~20%) - Visible:\n" +
            "• Words and statements\n" +
            "• Actions and behaviors\n" +
            "• Facts and numbers\n\n" +
            "BELOW SURFACE (~80%) - Invisible:\n" +
            "• Emotions and needs\n" +
            "• Attitudes and motives\n" +
            "• Values and beliefs\n" +
            "• Personal experiences",
            2));

        flashcards.add(new StudyApp.Flashcard("TEAM", "Communication",
            "Eisberg: What is Sachebene vs Beziehungsebene?",
            "SACHEBENE (Content Level) - Surface:\n" +
            "• What is openly communicated\n" +
            "• Facts, actions, words\n\n" +
            "BEZIEHUNGSEBENE (Relationship Level) - Deep:\n" +
            "• Emotions, values, beliefs\n" +
            "• Influences how we interact\n" +
            "• Often causes hidden conflicts",
            2));

        flashcards.add(new StudyApp.Flashcard("TEAM", "Communication",
            "What are 4 applications of Eisbergmodell?",
            "1. CONFLICT RESOLUTION:\n   Address hidden causes (values, emotions)\n\n" +
            "2. TEAM BUILDING & TRUST:\n   Share needs and values openly\n\n" +
            "3. REFLECTION & SELF-AWARENESS:\n   Understand own 'invisible' aspects\n\n" +
            "4. FEEDBACK CULTURE:\n   Recognize emotional impact of feedback",
            2));

        // ==================== SEEROSENMODELL ====================

        flashcards.add(new StudyApp.Flashcard("TEAM", "Behavior",
            "What is the Seerosenmodell (Water Lily Model)?",
            "Describes 3 levels of human behavior:\n\n" +
            "1. SEEROSENBLATT (Leaf) - Visible Behavior:\n   Observable actions, communication\n\n" +
            "2. STENGEL (Stem) - Attitude/Mindset:\n   Partially visible, basic attitudes\n\n" +
            "3. WURZEL (Root) - Values/Core Beliefs:\n   Invisible, deeply rooted from childhood",
            2));

        flashcards.add(new StudyApp.Flashcard("TEAM", "Behavior",
            "Seerosenmodell: Intervention levels",
            "BEHAVIOR (Leaf):\n" +
            "• Quick intervention, immediate effects\n" +
            "• Superficial changes (rules, training)\n\n" +
            "ATTITUDE (Stem):\n" +
            "• Medium-term, stronger impact\n" +
            "• Reflection, perspective change\n\n" +
            "VALUES (Root):\n" +
            "• Long-term, most sustainable\n" +
            "• Requires intensive work (coaching)\n" +
            "• Fundamental transformation",
            3));

        // ==================== GRPI-MODELL ====================

        flashcards.add(new StudyApp.Flashcard("TEAM", "GRPI",
            "What does GRPI stand for?",
            "Richard Beckhard's model for effective teamwork:\n\n" +
            "G = GOALS (Ziele)\n" +
            "R = ROLES (Rollen)\n" +
            "P = PROCESSES (Prozesse)\n" +
            "I = INTERPERSONAL RELATIONSHIPS (Beziehungen)",
            1));

        flashcards.add(new StudyApp.Flashcard("TEAM", "GRPI",
            "GRPI: Goals - what matters?",
            "GOALS (Ziele):\n" +
            "• Clear, specific, shared objectives\n" +
            "• Common understanding what we're working toward\n" +
            "• Everyone knows WHY goal is important\n" +
            "• Measurable and achievable (SMART)",
            2));

        flashcards.add(new StudyApp.Flashcard("TEAM", "GRPI",
            "GRPI: Roles - what matters?",
            "ROLES (Rollen):\n" +
            "• Clear definition of responsibilities\n" +
            "• Each member knows their tasks\n" +
            "• Understand contribution to overall success\n" +
            "• Reduces misunderstandings and conflicts",
            2));

        flashcards.add(new StudyApp.Flashcard("TEAM", "GRPI",
            "GRPI: Processes - what matters?",
            "PROCESSES (Prozesse):\n" +
            "• Work methods and decision-making structures\n" +
            "• Communication patterns\n" +
            "• Efficient, goal-oriented collaboration\n" +
            "• Established conflict resolution processes",
            2));

        flashcards.add(new StudyApp.Flashcard("TEAM", "GRPI",
            "GRPI: Relationships - what matters?",
            "INTERPERSONAL RELATIONSHIPS:\n" +
            "• Atmosphere and trust\n" +
            "• Conflict resolution capability\n" +
            "• Positive relationships promote motivation\n" +
            "• Open communication culture\n" +
            "• Mutual support",
            2));

        // ==================== TZI-MODELL ====================

        flashcards.add(new StudyApp.Flashcard("TEAM", "TZI",
            "What is TZI (Themenzentrierte Interaktion)?",
            "Theme-Centered Interaction by Ruth Cohn\n\n" +
            "Triangle with 3 elements:\n" +
            "• ICH (I) - Individual needs/feelings\n" +
            "• WIR (We) - Group/relationships\n" +
            "• THEMA (Theme) - Common task/goal\n\n" +
            "+ GLOBE (Umfeld) - External environment",
            2));

        flashcards.add(new StudyApp.Flashcard("TEAM", "TZI",
            "TZI: ICH dimension",
            "ICH (I - Individual):\n" +
            "• Personal thoughts, feelings, needs, goals\n" +
            "• Own perspectives and resources\n" +
            "• Self-reflection\n" +
            "• Authentic interaction\n" +
            "• Personal responsibility",
            2));

        flashcards.add(new StudyApp.Flashcard("TEAM", "TZI",
            "TZI: WIR dimension",
            "WIR (We - Group):\n" +
            "• Relationships and dynamics between members\n" +
            "• Collaboration and communication\n" +
            "• Mutual trust and support\n" +
            "• Group cohesion\n" +
            "• Social interaction",
            2));

        flashcards.add(new StudyApp.Flashcard("TEAM", "TZI",
            "TZI: THEMA dimension",
            "THEMA (Theme - Task):\n" +
            "• Common task or goal\n" +
            "• What team is working on\n" +
            "• Clear focus promotes productivity\n" +
            "• Goal orientation\n" +
            "• Task accomplishment",
            2));

        flashcards.add(new StudyApp.Flashcard("TEAM", "TZI",
            "TZI: Balance principle",
            "EFFECTIVE TEAMWORK requires BALANCE:\n\n" +
            "ICH (Personal responsibility) +\n" +
            "WIR (Social interaction) +\n" +
            "THEMA (Task orientation)\n\n" +
            "= Integrative team culture\n\n" +
            "All three dimensions must be in harmony!",
            3));

        // ==================== T-SHAPED QUALIFICATIONS ====================

        flashcards.add(new StudyApp.Flashcard("TEAM", "Skills",
            "What is T-shaped qualification?",
            "Modern IT professional profile:\n\n" +
            "HORIZONTAL (top of T):\n" +
            "• Broad generalist knowledge\n" +
            "• Understand multiple disciplines\n" +
            "• Can collaborate across specialties\n\n" +
            "VERTICAL (depth of T):\n" +
            "• Deep specialist expertise\n" +
            "• Expert in 1-2 specific areas\n" +
            "• Technical mastery",
            2));

        flashcards.add(new StudyApp.Flashcard("TEAM", "Skills",
            "Why T-shaped in software teams?",
            "Benefits:\n" +
            "• Teams combine diverse specialists\n" +
            "• Each understands others' work\n" +
            "• Enables spontaneous collaboration\n" +
            "• Facilitates complex problem-solving\n" +
            "• Breaks down silos\n" +
            "• Better than isolated 'I-shaped' experts",
            2));

        // ==================== TEAM DEFINITIONS ====================

        flashcards.add(new StudyApp.Flashcard("TEAM", "Definitions",
            "Team definition by M. West",
            "A group of people who:\n" +
            "• Work together toward SHARED GOALS\n" +
            "• Take on DIFFERENT ROLES\n" +
            "• COMMUNICATE with each other\n" +
            "• Successfully COORDINATE their efforts",
            1));

        flashcards.add(new StudyApp.Flashcard("TEAM", "Definitions",
            "Team definition by Thompson",
            "A group of individuals who are:\n" +
            "• MUTUALLY DEPENDENT on each other\n" +
            "• JOINTLY RESPONSIBLE\n" +
            "• Achieve SPECIFIC GOALS for organization\n" +
            "• Share accountability",
            1));

        flashcards.add(new StudyApp.Flashcard("TEAM", "Definitions",
            "Key team characteristics (Mohrman)",
            "Teams have:\n" +
            "• SHARED GOALS with joint responsibility\n" +
            "• MUTUAL DEPENDENCE on performance\n" +
            "• INFLUENCE RESULTS through interaction\n\n" +
            "Central task: NETWORKING with other members\n" +
            "Responsible as a whole for end result",
            2));

        // ==================== MODERN KNOWLEDGE WORK ====================

        flashcards.add(new StudyApp.Flashcard("TEAM", "Modern Work",
            "Knowledge work transformation",
            "OLD: 'Knowledge is power' (hoard it)\n" +
            "• Keep information to yourself\n" +
            "• Individual expertise as advantage\n\n" +
            "NEW: Knowledge sharing\n" +
            "• Knowledge one click away (ChatGPT, StackOverflow)\n" +
            "• Share to solve complex problems together\n" +
            "• From KNOWING to DOING (Kennen → Können)\n" +
            "• Co-creation and collaboration",
            2));

        // ==================== TUCKMAN PHASE MODEL ====================

        flashcards.add(new StudyApp.Flashcard("TEAM", "Development Phases",
            "Tuckman's 5 Phases of Team Development",
            "1. FORMING - Getting to know each other\n" +
            "2. STORMING - Conflict and power struggles\n" +
            "3. NORMING - Establishing rules and cohesion\n" +
            "4. PERFORMING - High productivity\n" +
            "5. ADJOURNING - Team dissolution (temporary teams)",
            1));

        flashcards.add(new StudyApp.Flashcard("TEAM", "Development Phases",
            "Tuckman: FORMING phase",
            "Characteristics:\n" +
            "• Different individual goals, interests, abilities\n" +
            "• Uncertainty and dependency\n" +
            "• Need for orientation\n" +
            "• Members 'feel each other out'\n" +
            "• Try out behavioral patterns",
            2));

        flashcards.add(new StudyApp.Flashcard("TEAM", "Development Phases",
            "Tuckman: STORMING phase",
            "Characteristics:\n" +
            "• Conflicts between persons/subgroups\n" +
            "• Rebellion against leaders\n" +
            "• Testing hierarchy\n" +
            "• Establishing commonality of goals\n" +
            "• Boundaries tested and drawn",
            2));

        flashcards.add(new StudyApp.Flashcard("TEAM", "Development Phases",
            "Tuckman: NORMING phase",
            "Characteristics:\n" +
            "• Development of group cohesion\n" +
            "• Different goals subordinated to common task\n" +
            "• Creation of accepted rules\n" +
            "• Mutual acceptance\n" +
            "• Care for group continuity",
            2));

        flashcards.add(new StudyApp.Flashcard("TEAM", "Development Phases",
            "Tuckman: PERFORMING phase",
            "Characteristics:\n" +
            "• Energy bundled for actual task fulfillment\n" +
            "• Personal problems take back seat\n" +
            "• Flexible, functional role understanding\n" +
            "• High productivity and effectiveness\n" +
            "• Optimal team performance",
            2));

        flashcards.add(new StudyApp.Flashcard("TEAM", "Development Phases",
            "Why stable teams in Agile?",
            "Problem with frequent team changes:\n" +
            "• Constantly cycling through formation phases\n" +
            "• Costs energy\n" +
            "• Prevents reaching 'performing'\n\n" +
            "AGILE APPROACH:\n" +
            "• Work with STABLE TEAMS\n" +
            "• On CHANGING PROJECTS\n" +
            "• (Not new teams per project!)",
            3));

        // ==================== BELBIN TEAM ROLES ====================

        flashcards.add(new StudyApp.Flashcard("TEAM", "Belbin Roles",
            "Belbin's 9 Team Roles - Overview",
            "COMMUNICATION: Resource Investigator, Team Worker, Coordinator\n\n" +
            "KNOWLEDGE: Specialist, Plant/Innovator, Monitor Evaluator\n\n" +
            "ACTION: Completer Finisher, Shaper, Implementer",
            1));

        flashcards.add(new StudyApp.Flashcard("TEAM", "Belbin Roles",
            "Belbin: Coordinator",
            "KOORDINATOR/IN:\n" +
            "✓ Employee-oriented leadership\n" +
            "✓ Committed to team goals\n" +
            "✓ Dominant, trustworthy\n" +
            "✓ Accepted by team\n\n" +
            "✗ Not necessarily idea generator",
            2));

        flashcards.add(new StudyApp.Flashcard("TEAM", "Belbin Roles",
            "Belbin: Shaper",
            "MACHER/IN:\n" +
            "✓ Task-oriented leadership\n" +
            "✓ Highly performance-motivated\n" +
            "✓ Achieves goals, overcomes obstacles\n" +
            "✓ Challenges and provokes\n\n" +
            "✗ Tends toward aggression\n" +
            "✗ 2-3 Shapers can cause conflicts",
            2));

        flashcards.add(new StudyApp.Flashcard("TEAM", "Belbin Roles",
            "Belbin: Plant/Innovator",
            "NEUERER/ERFINDER/IN:\n" +
            "✓ Highly intelligent, dominant\n" +
            "✓ Creative, original ideas\n" +
            "✓ Radical, unconventional problem-solving\n\n" +
            "✗ Sometimes neglects practical aspects\n" +
            "✗ Weak in leadership and communication",
            2));

        flashcards.add(new StudyApp.Flashcard("TEAM", "Belbin Roles",
            "Belbin: Resource Investigator",
            "WEICHENSTELLER/IN:\n" +
            "✓ Creates connections and networks\n" +
            "✓ Explores possibilities\n" +
            "✓ Sociable, enthusiastic\n\n" +
            "✗ Not necessarily idea generator\n" +
            "✗ Loses interest after initial enthusiasm",
            2));

        flashcards.add(new StudyApp.Flashcard("TEAM", "Belbin Roles",
            "Belbin: Monitor Evaluator",
            "BEOBACHTER/IN:\n" +
            "✓ Judges deliberately and accurately\n" +
            "✓ Weighs all arguments\n" +
            "✓ Important in significant decisions\n\n" +
            "✗ Often seems dry and boring\n" +
            "✗ Sometimes overcritical\n" +
            "✗ Cannot inspire",
            2));

        flashcards.add(new StudyApp.Flashcard("TEAM", "Belbin Roles",
            "Belbin: Specialist",
            "SPEZIALIST/IN:\n" +
            "✓ Has important specialized knowledge\n" +
            "✓ Introverted and solitary\n" +
            "✓ Very engaged in their area\n\n" +
            "✗ Only interested in narrow work area\n" +
            "✗ Little concern for others' interests",
            2));

        flashcards.add(new StudyApp.Flashcard("TEAM", "Belbin Roles",
            "Belbin: Team Worker",
            "TEAMARBEITER/IN:\n" +
            "✓ Maintains team spirit\n" +
            "✓ Diplomatic, humorous\n" +
            "✓ Good listener\n" +
            "✓ Handles difficult people\n\n" +
            "✗ Often indecisive\n" +
            "✗ Sometimes too considerate",
            2));

        flashcards.add(new StudyApp.Flashcard("TEAM", "Belbin Roles",
            "Belbin: Implementer",
            "UMSETZER/IN:\n" +
            "✓ Reliable, disciplined\n" +
            "✓ Practically oriented\n" +
            "✓ Trustworthy, takes responsibility\n" +
            "✓ Implements ideas\n\n" +
            "✗ Not innovative\n" +
            "✗ Inflexible\n" +
            "✗ Slow to embrace new ideas",
            2));

        flashcards.add(new StudyApp.Flashcard("TEAM", "Belbin Roles",
            "Belbin: Completer Finisher",
            "PERFEKTIONIST/IN:\n" +
            "✓ Carefully attends to details\n" +
            "✓ Completes things\n" +
            "✓ Punctual and persistent\n\n" +
            "✗ Sometimes over-anxious\n" +
            "✗ Poor at delegating",
            2));

        // ==================== CULTURE ====================

        flashcards.add(new StudyApp.Flashcard("TEAM", "Culture",
            "What is Team Culture? (Wohland)",
            "DEFINITION:\n" +
            "'Culture is the coupling of behavior and values'\n" +
            "(G. Wohland 2006)\n\n" +
            "KEY POINTS:\n" +
            "• Observable but not causally controllable\n" +
            "• It is an EFFECT, not a cause\n" +
            "• Collective memory\n" +
            "• Provides orientation",
            2));

        flashcards.add(new StudyApp.Flashcard("TEAM", "Culture",
            "Schein's 3 Levels of Culture",
            "LEVEL 1: ARTIFACTS\n" +
            "• Visible daily behavior\n" +
            "• 'What do I see, hear, feel?'\n\n" +
            "LEVEL 2: PUBLICLY PROPAGATED VALUES\n" +
            "• Official statements\n" +
            "• 'What is proclaimed?'\n\n" +
            "LEVEL 3: UNSPOKEN SHARED ASSUMPTIONS\n" +
            "• Deep collective beliefs\n" +
            "• 'Which values made us successful?'",
            3));

        return flashcards;
    }

    /**
     * Initialize all TEAM quiz questions
     * @return List of Question objects
     */
    public static List<StudyApp.Question> getTeamQuestions() {
        List<StudyApp.Question> questions = new ArrayList<>();

        questions.add(new StudyApp.Question("TEAM",
            "In the Rangdynamikmodell, which role represents the critic or opposition?",
            new String[]{
                "Alpha",
                "Beta",
                "Gamma",
                "Omega"
            },
            3,
            "Omega is the counterpart/critic who points out problems and challenges Alpha, forcing group reflection."));

        questions.add(new StudyApp.Question("TEAM",
            "According to the Eisbergmodell, approximately what percentage of communication is INVISIBLE (below surface)?",
            new String[]{
                "20%",
                "50%",
                "80%",
                "95%"
            },
            2,
            "About 80% is invisible (emotions, values, beliefs) while only 20% is visible (words, actions, facts)."));

        questions.add(new StudyApp.Question("TEAM",
            "In the Seerosenmodell, which level represents deeply rooted values from childhood?",
            new String[]{
                "Seerosenblatt (Leaf)",
                "Stengel (Stem)",
                "Wurzel (Root)",
                "Blüte (Flower)"
            },
            2,
            "Wurzel (Root) represents deep, invisible values and core beliefs formed in childhood."));

        questions.add(new StudyApp.Question("TEAM",
            "What does the 'G' in GRPI stand for?",
            new String[]{
                "Groups",
                "Goals",
                "Growth",
                "Guidelines"
            },
            1,
            "GRPI = Goals, Roles, Processes, Interpersonal Relationships (Richard Beckhard's model)."));

        questions.add(new StudyApp.Question("TEAM",
            "In TZI (Theme-Centered Interaction), what are the three main elements of the triangle?",
            new String[]{
                "I, We, It",
                "Individual, Team, Task",
                "ICH, WIR, THEMA",
                "Alpha, Beta, Gamma"
            },
            2,
            "TZI has ICH (I - individual), WIR (We - group), THEMA (Theme - task), plus Globe (environment)."));

        questions.add(new StudyApp.Question("TEAM",
            "What shape describes the modern IT professional qualification profile?",
            new String[]{
                "I-shaped (specialist only)",
                "T-shaped (specialist + generalist)",
                "V-shaped (two specialties)",
                "O-shaped (all-rounder)"
            },
            1,
            "T-shaped: horizontal bar = broad generalist knowledge, vertical bar = deep specialist expertise in 1-2 areas."));

        questions.add(new StudyApp.Question("TEAM",
            "According to Tuckman, in which phase do teams experience the highest productivity?",
            new String[]{
                "Forming",
                "Storming",
                "Norming",
                "Performing"
            },
            3,
            "Performing phase has highest productivity - energy bundled for task fulfillment, flexible roles."));

        questions.add(new StudyApp.Question("TEAM",
            "Which Belbin role is characterized as highly creative with original ideas but weak in leadership?",
            new String[]{
                "Coordinator",
                "Plant/Innovator",
                "Shaper",
                "Monitor Evaluator"
            },
            1,
            "Plant/Innovator brings creative, radical ideas but often neglects practical aspects and leadership."));

        questions.add(new StudyApp.Question("TEAM",
            "How many team roles does Belbin's model identify?",
            new String[]{
                "5 roles",
                "7 roles",
                "9 roles",
                "12 roles"
            },
            2,
            "Belbin identifies 9 team roles across 3 categories: Communication (3), Knowledge (3), Action (3)."));

        questions.add(new StudyApp.Question("TEAM",
            "Why do Agile organizations prefer stable teams on changing projects?",
            new String[]{
                "It's cheaper",
                "Prevents energy loss from repeated team formation cycles",
                "Easier to manage",
                "Required by Scrum"
            },
            1,
            "Constantly forming new teams costs energy and prevents reaching 'performing' phase. Stable teams are more effective."));

        questions.add(new StudyApp.Question("TEAM",
            "Which Belbin role is most likely to cause conflict if there are 2-3 of them in a team?",
            new String[]{
                "Team Worker",
                "Specialist",
                "Shaper",
                "Coordinator"
            },
            2,
            "Shapers are highly performance-motivated and task-oriented but tend toward aggression. Multiple Shapers can clash."));

        questions.add(new StudyApp.Question("TEAM",
            "In Schein's culture model, which level represents 'what is proclaimed' officially?",
            new String[]{
                "Artifacts",
                "Publicly Propagated Values",
                "Unspoken Shared Assumptions",
                "Core Beliefs"
            },
            1,
            "Level 2 (Publicly Propagated Values) represents what is officially stated and proclaimed."));

        questions.add(new StudyApp.Question("TEAM",
            "What is the transformation timeline benchmark for becoming a team-based organization?",
            new String[]{
                "3-6 months",
                "1.5 years",
                "3 years",
                "5 years"
            },
            1,
            "West & Markiewicz (2004): 1.5 years is a good benchmark for transforming to team-based organization."));

        questions.add(new StudyApp.Question("TEAM",
            "Modern knowledge work shifts from 'knowledge is power' to what principle?",
            new String[]{
                "Knowledge is money",
                "Knowledge sharing for complex problem-solving",
                "Knowledge is temporary",
                "Knowledge is individual"
            },
            1,
            "Shift to knowledge sharing - knowledge is one click away, focus on collaboration and doing (not just knowing)."));

        questions.add(new StudyApp.Question("TEAM",
            "In the GRPI model, which element focuses on work methods and decision-making?",
            new String[]{
                "Goals",
                "Roles",
                "Processes",
                "Interpersonal Relationships"
            },
            2,
            "Processes (P) covers work methods, decision-making structures, and communication patterns."));

        return questions;
    }
}
