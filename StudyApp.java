import java.util.*;
import java.io.*;

/**
 * StudyApp - Interactive Exam Preparation Tool
 * Covers: BSYS (Operating Systems), DigiCom (Digital Communications), TEAM (Teamarbeit)
 * 
 * Features:
 * - Flashcard mode for memorization
 * - Quiz mode with multiple choice
 * - Topic browser for review
 * - Progress tracking
 * - Spaced repetition support
 * - Custom content input mode
 * 
 * @author Claude AI
 * @version 1.1
 */
public class StudyApp {
    
    private static final Scanner scanner = new Scanner(System.in);
    private static final Random random = new Random();
    
    // Progress tracking
    private static Map<String, Integer> correctAnswers = new HashMap<>();
    private static Map<String, Integer> totalAttempts = new HashMap<>();
    private static Set<String> masteredCards = new HashSet<>();
    private static int totalStudyTime = 0;
    
    // Custom content tracking
    private static List<Flashcard> customFlashcards = new ArrayList<>();
    private static List<Question> customQuestions = new ArrayList<>();
    private static final String CUSTOM_CARDS_FILE = "custom_flashcards.dat";
    private static final String CUSTOM_QUESTIONS_FILE = "custom_questions.dat";
    
    // Color codes for terminal
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String PURPLE = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";
    private static final String BOLD = "\u001B[1m";
    
    // ==================== FLASHCARDS DATA ====================
    
    static class Flashcard {
        String subject;
        String topic;
        String front;
        String back;
        int difficulty; // 1-3
        
        Flashcard(String subject, String topic, String front, String back, int difficulty) {
            this.subject = subject;
            this.topic = topic;
            this.front = front;
            this.back = back;
            this.difficulty = difficulty;
        }
    }
    
    static class Question {
        String subject;
        String question;
        String[] options;
        int correctIndex;
        String explanation;
        
        Question(String subject, String question, String[] options, int correctIndex, String explanation) {
            this.subject = subject;
            this.question = question;
            this.options = options;
            this.correctIndex = correctIndex;
            this.explanation = explanation;
        }
    }
    
    // All flashcards
    private static List<Flashcard> flashcards = new ArrayList<>();
    private static List<Question> questions = new ArrayList<>();
    
    public static void main(String[] args) {
        initializeContent();
        loadProgress();
        loadCustomContent();
        
        printWelcome();
        
        boolean running = true;
        while (running) {
            printMainMenu();
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1": flashcardMode(); break;
                case "2": quizMode(); break;
                case "3": topicBrowser(); break;
                case "4": viewProgress(); break;
                case "5": quickReview(); break;
                case "6": examSimulation(); break;
                case "7": inputMode(); break;
                case "0": 
                    running = false;
                    saveProgress();
                    saveCustomContent();
                    printGoodbye();
                    break;
                default:
                    System.out.println(RED + "Invalid choice. Try again." + RESET);
            }
        }
    }
    
    private static void initializeContent() {
        // ==================== BSYS FLASHCARDS ====================
        
        // Processes
        flashcards.add(new Flashcard("BSYS", "Processes", 
            "What is a Process?",
            "An instance of an executing program, including current values of:\n" +
            "• Program Counter\n• Registers\n• Variables\n" +
            "Only ONE program active at once on single-core CPU.",
            1));
            
        flashcards.add(new Flashcard("BSYS", "Processes",
            "What are the 4 sections of process memory?",
            "1. STACK - temporary data (parameters, return addresses, local vars)\n" +
            "2. HEAP - dynamically allocated memory at runtime\n" +
            "3. TEXT - program code, PC value, register contents\n" +
            "4. DATA - global and static variables",
            2));
            
        flashcards.add(new Flashcard("BSYS", "Processes",
            "What are the 3 process states?",
            "1. RUNNING - actually using CPU right now\n" +
            "2. READY - runnable, waiting for CPU time\n" +
            "3. BLOCKED - waiting for external event (I/O)\n\n" +
            "Transitions: Running→Blocked (I/O wait), Running→Ready (preempted),\n" +
            "Ready→Running (scheduled), Blocked→Ready (I/O complete)",
            1));
            
        flashcards.add(new Flashcard("BSYS", "Processes",
            "What are the 4 events that create processes?",
            "1. System initialization\n" +
            "2. Process creation system call (fork)\n" +
            "3. User request (clicking icon)\n" +
            "4. Batch job initiation",
            2));
            
        // Threads
        flashcards.add(new Flashcard("BSYS", "Threads",
            "What is a Thread?",
            "A 'lightweight process' - flow of execution with own:\n" +
            "• Program Counter\n• Stack\n• Register set\n\n" +
            "Shares with process: address space, open files, memory\n" +
            "Creation is 10-100x FASTER than processes!",
            1));
            
        flashcards.add(new Flashcard("BSYS", "Threads",
            "Process vs Thread - Key Differences",
            "PROCESS:\n• Heavy weight, resource intensive\n• Switching needs OS\n" +
            "• Own memory/files\n• Independent operation\n\n" +
            "THREAD:\n• Light weight\n• No OS needed for switching\n" +
            "• Share files/memory\n• Can access other thread's data",
            2));
            
        flashcards.add(new Flashcard("BSYS", "Threads",
            "User-Level vs Kernel-Level Threads",
            "USER-LEVEL:\n+ Fast creation, no kernel calls\n+ Can run on any OS\n" +
            "- Blocking call blocks entire process\n- No multiprocessor advantage\n\n" +
            "KERNEL-LEVEL:\n+ Multiple threads on multiple CPUs\n+ One blocked, others run\n" +
            "- Slower to create\n- Mode switch overhead",
            2));
            
        // IPC
        flashcards.add(new Flashcard("BSYS", "IPC",
            "What is IPC and its 3 issues?",
            "Inter-Process Communication - how processes communicate\n\n" +
            "3 Issues:\n" +
            "1. How to PASS information between processes\n" +
            "2. How to AVOID interference (critical sections)\n" +
            "3. How to handle DEPENDENCIES (sequencing)",
            1));
            
        flashcards.add(new Flashcard("BSYS", "IPC",
            "Semaphore vs Mutex",
            "SEMAPHORE:\n• Signaling mechanism (integer 0,1,2...)\n" +
            "• P(down): if >0 continue & decrement, else sleep\n" +
            "• V(up): increment value\n• ANY process can modify\n\n" +
            "MUTEX:\n• Locking mechanism (binary: locked/unlocked)\n" +
            "• OWNED by process that locks it\n• Only OWNER can unlock\n" +
            "• Faster (no kernel calls)",
            3));
            
        flashcards.add(new Flashcard("BSYS", "IPC",
            "Ordinary Pipes vs Named Pipes",
            "ORDINARY PIPES:\n• Unidirectional (one-way)\n• Require parent-child relationship\n" +
            "• Producer-consumer model\n• Cannot be accessed externally\n\n" +
            "NAMED PIPES:\n• Bidirectional (two-way)\n• No parent-child needed\n" +
            "• Multiple processes can use\n• Persist in filesystem",
            2));
            
        // Scheduling
        flashcards.add(new Flashcard("BSYS", "Scheduling",
            "FCFS (First-Come First-Served)",
            "• Non-preemptive\n• Simple FIFO queue\n• Easy to implement\n" +
            "• Poor for I/O-bound processes\n• Convoy effect possible\n\n" +
            "Example: P1(8ms), P2(4ms), P3(2ms) arriving at 0\n" +
            "Order: P1→P2→P3\nAvg waiting: (0+8+12)/3 = 6.67ms",
            2));
            
        flashcards.add(new Flashcard("BSYS", "Scheduling",
            "SJF (Shortest Job First)",
            "• Non-preemptive\n• Minimum average waiting time (optimal)\n" +
            "• Must know job length in advance\n• Starvation possible for long jobs\n\n" +
            "Example: P1(8ms), P2(4ms), P3(2ms) all at time 0\n" +
            "Order: P3→P2→P1\nAvg waiting: (0+2+6)/3 = 2.67ms",
            2));
            
        flashcards.add(new Flashcard("BSYS", "Scheduling",
            "Round-Robin Scheduling",
            "• Preemptive with time quantum (20-50ms typical)\n" +
            "• Each process gets equal CPU time slice\n" +
            "• Context switching overhead\n• Good for interactive systems\n\n" +
            "Quantum too small → too much switching overhead\n" +
            "Quantum too large → degenerates to FCFS",
            2));
            
        flashcards.add(new Flashcard("BSYS", "Scheduling",
            "SRTN (Shortest Remaining Time Next)",
            "• Preemptive version of SJF\n" +
            "• New shorter job can preempt current\n" +
            "• Optimal average waiting time\n" +
            "• Requires knowing remaining time\n" +
            "• Higher overhead than SJF",
            2));
            
        // Memory Management
        flashcards.add(new Flashcard("BSYS", "Memory",
            "Base and Limit Registers",
            "Hardware registers for memory protection:\n\n" +
            "BASE: Starting address of process memory\n" +
            "LIMIT: Size of memory region\n\n" +
            "Every memory access checked:\n" +
            "if (address >= base && address < base + limit)\n" +
            "   allow access\nelse\n   trap to OS (protection fault)",
            2));
            
        flashcards.add(new Flashcard("BSYS", "Memory",
            "Memory Allocation Algorithms",
            "FIRST FIT: Use first hole big enough (fast)\n" +
            "NEXT FIT: Like first fit, but start from last position\n" +
            "BEST FIT: Use smallest hole that fits (minimal waste)\n" +
            "WORST FIT: Use largest hole (leaves bigger fragments)\n" +
            "BUDDY: Split into power-of-2 blocks (fast coalescing)",
            2));
            
        flashcards.add(new Flashcard("BSYS", "Memory",
            "Virtual Memory - Key Concepts",
            "• Each process has own virtual address space\n" +
            "• Divided into fixed-size PAGES\n" +
            "• Physical memory divided into PAGE FRAMES\n" +
            "• MMU translates virtual → physical addresses\n" +
            "• Page Table maps pages to frames\n" +
            "• TLB caches recent translations",
            2));
            
        flashcards.add(new Flashcard("BSYS", "Memory",
            "What is a Page Fault?",
            "Trap when accessing unmapped/absent page:\n\n" +
            "1. MMU detects page not in memory\n" +
            "2. CPU traps to OS\n" +
            "3. OS finds page frame (may evict another)\n" +
            "4. Loads page from disk\n" +
            "5. Updates page table\n" +
            "6. Restarts instruction",
            2));
            
        flashcards.add(new Flashcard("BSYS", "Memory",
            "TLB (Translation Lookaside Buffer)",
            "Hardware cache for page table entries:\n\n" +
            "• Speeds up virtual→physical translation\n" +
            "• Hit: No memory access needed (fast!)\n" +
            "• Miss: Must access page table in memory\n\n" +
            "Soft miss: Page in memory, not in TLB\n" +
            "Hard miss: Page not in memory (page fault)",
            2));
            
        flashcards.add(new Flashcard("BSYS", "Memory",
            "Page Table Entry Bits",
            "PRESENT/ABSENT: Is page in physical memory?\n" +
            "PROTECTION: Read/Write/Execute permissions\n" +
            "MODIFIED (Dirty): Has page been written?\n" +
            "REFERENCED: Has page been accessed?\n\n" +
            "Used by replacement algorithms to decide\n" +
            "which page to evict.",
            2));
            
        // Page Replacement
        flashcards.add(new Flashcard("BSYS", "Page Replacement",
            "FIFO Page Replacement",
            "• Replace oldest page (first in, first out)\n" +
            "• Simple to implement\n" +
            "• May throw out heavily used pages!\n" +
            "• Belady's anomaly: more frames can cause more faults",
            1));
            
        flashcards.add(new Flashcard("BSYS", "Page Replacement",
            "LRU (Least Recently Used)",
            "• Replace page unused for longest time\n" +
            "• Based on locality principle\n" +
            "• Expensive to implement exactly\n" +
            "• Requires hardware support or approximation\n" +
            "• No Belady's anomaly",
            2));
            
        flashcards.add(new Flashcard("BSYS", "Page Replacement",
            "Clock Algorithm",
            "• Approximation of LRU\n" +
            "• Circular list with 'clock hand'\n" +
            "• Check R bit of page at hand:\n" +
            "  - If R=0: evict this page\n" +
            "  - If R=1: clear R, advance hand\n" +
            "• More efficient than Second Chance",
            2));
            
        flashcards.add(new Flashcard("BSYS", "Page Replacement",
            "NRU (Not Recently Used)",
            "Uses R (referenced) and M (modified) bits:\n\n" +
            "Class 0: R=0, M=0 (best to evict)\n" +
            "Class 1: R=0, M=1\n" +
            "Class 2: R=1, M=0\n" +
            "Class 3: R=1, M=1 (worst to evict)\n\n" +
            "R bit cleared periodically by OS.",
            2));
            
        // File Systems
        flashcards.add(new Flashcard("BSYS", "File Systems",
            "File Types in Unix",
            "1. REGULAR FILES: User data (text, binary)\n" +
            "2. DIRECTORIES: System files for FS structure\n" +
            "3. CHARACTER SPECIAL: Serial I/O devices\n" +
            "4. BLOCK SPECIAL: Disks, memory devices",
            1));
            
        flashcards.add(new Flashcard("BSYS", "File Systems",
            "MBR vs GPT",
            "MBR (Master Boot Record):\n• 1983 standard\n• 32-bit addresses\n" +
            "• Max 2TB partitions\n• Max 4 primary partitions\n\n" +
            "GPT (GUID Partition Table):\n• Part of UEFI standard\n" +
            "• 64-bit addresses\n• Much larger partitions\n" +
            "• 128 partitions typical\n• Has protective MBR",
            2));
            
        flashcards.add(new Flashcard("BSYS", "File Systems",
            "What is an i-node?",
            "Index-node: Data structure containing file metadata:\n\n" +
            "• File size\n• Owner/permissions\n• Timestamps\n" +
            "• Direct block pointers (12 typical)\n" +
            "• Single indirect pointer\n" +
            "• Double indirect pointer\n" +
            "• Triple indirect pointer\n\n" +
            "Only loaded when file is open!",
            2));
            
        flashcards.add(new Flashcard("BSYS", "File Systems",
            "Hard Link vs Symbolic Link",
            "HARD LINK:\n• Another directory entry pointing to same i-node\n" +
            "• Cannot cross filesystems\n• Cannot link directories\n" +
            "• File deleted when last link removed\n\n" +
            "SYMBOLIC (SOFT) LINK:\n• Separate file containing path\n" +
            "• Can cross filesystems\n• Can link directories\n" +
            "• Can become 'dangling' if target deleted",
            2));
            
        // RAID
        flashcards.add(new Flashcard("BSYS", "RAID",
            "RAID Levels Overview",
            "RAID 0: Striping only, NO redundancy, best performance\n" +
            "RAID 1: Mirroring, 50% capacity, highest redundancy\n" +
            "RAID 4: Striping + dedicated parity disk (bottleneck)\n" +
            "RAID 5: Striping + distributed parity (popular)\n" +
            "RAID 6: Like 5 but dual parity (tolerates 2 failures)",
            2));
            
        flashcards.add(new Flashcard("BSYS", "RAID",
            "RAID 5 Details",
            "Block Interleaved Distributed Parity:\n\n" +
            "• Data AND parity striped across all disks\n" +
            "• Parity rotates (no bottleneck disk)\n" +
            "• Can survive 1 disk failure\n" +
            "• Capacity: (N-1)/N\n" +
            "• Read: excellent, Write: good\n" +
            "• Most popular for servers",
            2));
            
        // ==================== DIGICOM FLASHCARDS ====================
        
        flashcards.add(new Flashcard("DigiCom", "Layers",
            "OSI Model - 7 Layers",
            "7. Application - User interface (HTTP, FTP)\n" +
            "6. Presentation - Data format, encryption\n" +
            "5. Session - Connection management\n" +
            "4. Transport - End-to-end delivery (TCP/UDP)\n" +
            "3. Network - Routing (IP)\n" +
            "2. Data Link - Local delivery (Ethernet)\n" +
            "1. Physical - Bits on wire\n\n" +
            "Mnemonic: All People Seem To Need Data Processing",
            1));
            
        flashcards.add(new Flashcard("DigiCom", "Layers",
            "TCP/IP Model - 4 Layers",
            "4. Application (OSI 5-7)\n   HTTP, FTP, SMTP, DNS\n\n" +
            "3. Transport (OSI 4)\n   TCP (reliable), UDP (fast)\n\n" +
            "2. Internet (OSI 3)\n   IP, ICMP, ARP\n\n" +
            "1. Network Interface (OSI 1-2)\n   Ethernet, WiFi",
            1));
            
        flashcards.add(new Flashcard("DigiCom", "Layers",
            "PDU Names per Layer",
            "Application: Data/Message\n" +
            "Transport: Segment (TCP) / Datagram (UDP)\n" +
            "Internet/Network: Packet\n" +
            "Data Link: Frame\n" +
            "Physical: Bits",
            1));
            
        flashcards.add(new Flashcard("DigiCom", "Layers",
            "Encapsulation vs Deencapsulation",
            "ENCAPSULATION (sender, top-down):\n" +
            "• Add header (and/or trailer) to data\n" +
            "• Creates PDU\n• Data becomes payload\n\n" +
            "DEENCAPSULATION (receiver, bottom-up):\n" +
            "• Remove and interpret header/trailer\n" +
            "• Extract payload\n• Pass up to next layer",
            1));
            
        flashcards.add(new Flashcard("DigiCom", "Devices",
            "Switch vs Router",
            "SWITCH (Layer 2):\n• Forwards FRAMES\n• Uses MAC addresses\n" +
            "• Same broadcast domain\n• Learns addresses (CAM table)\n\n" +
            "ROUTER (Layer 3):\n• Forwards PACKETS\n• Uses IP addresses\n" +
            "• Separates broadcast domains\n• Uses routing table",
            1));
            
        // VLANs
        flashcards.add(new Flashcard("DigiCom", "VLAN",
            "What is a VLAN?",
            "Virtual LAN - logical network segmentation:\n\n" +
            "• Switch assigns ports to virtual broadcast domains\n" +
            "• Hosts in different VLANs can't 'see' each other (L2)\n" +
            "• Reduces broadcast traffic\n" +
            "• Group by function, not location\n" +
            "• Adds security through isolation",
            1));
            
        flashcards.add(new Flashcard("DigiCom", "VLAN",
            "802.1Q Tag Structure",
            "4-byte tag inserted after source MAC:\n\n" +
            "TPID (2 bytes): 0x8100 (identifies 802.1Q)\n" +
            "PCP (3 bits): Priority (QoS)\n" +
            "DEI (1 bit): Drop eligible indicator\n" +
            "VLAN ID (12 bits): 0-4095 (0,4095 reserved)\n\n" +
            "Tagged frame max: 1522 bytes (vs 1518 untagged)",
            2));
            
        flashcards.add(new Flashcard("DigiCom", "VLAN",
            "Special VLAN Types",
            "DEFAULT VLAN (1):\n• All ports initially assigned\n• Allows switch to work out of box\n\n" +
            "NATIVE VLAN:\n• Untagged traffic on trunk\n• MUST match on both ends!\n\n" +
            "MANAGEMENT VLAN:\n• For switch admin (SSH, SNMP)\n• Security isolation\n\n" +
            "Best practice: Change all from default VLAN 1!",
            2));
            
        flashcards.add(new Flashcard("DigiCom", "VLAN",
            "Inter-VLAN Routing",
            "VLANs = separate broadcast domains\n→ Routing needed between them!\n\n" +
            "Options:\n1. Layer 3 Switch (routing + switching)\n" +
            "2. Router on a Stick (single trunk to router)\n" +
            "3. Multiple router interfaces (expensive)\n\n" +
            "Router rewrites 802.1Q tag when forwarding between VLANs",
            2));
            
        // STP
        flashcards.add(new Flashcard("DigiCom", "STP",
            "Why do we need STP?",
            "Redundant links create LOOPS:\n\n" +
            "• Frames duplicated infinitely\n• Broadcast storms\n" +
            "• CAM table instability\n• Network saturation\n\n" +
            "Ethernet has NO TTL mechanism!\n\n" +
            "STP creates loop-free spanning tree by blocking\n" +
            "redundant ports while keeping them as backup.",
            1));
            
        flashcards.add(new Flashcard("DigiCom", "STP",
            "Bridge ID and Port ID",
            "BRIDGE ID (8 bytes):\n• Priority (2 bytes): default 32768\n" +
            "• MAC Address (6 bytes)\n• Lowest = Root Bridge\n\n" +
            "PORT ID (2 bytes):\n• Priority (1 byte): default 128\n" +
            "• Port Number (1 byte)\n• Lowest wins tie-breakers",
            2));
            
        flashcards.add(new Flashcard("DigiCom", "STP",
            "STP Port Roles",
            "ROOT PORT:\n• One per non-root switch\n• Lowest path cost to Root Bridge\n\n" +
            "DESIGNATED PORT:\n• One per LAN segment\n• On switch closest to Root\n• Forwards traffic\n\n" +
            "ALTERNATE (BLOCKED) PORT:\n• Neither root nor designated\n• Blocks to prevent loops",
            2));
            
        flashcards.add(new Flashcard("DigiCom", "STP",
            "STP Port States",
            "BLOCKING: No data, receives BPDUs only\n" +
            "LISTENING: Preparing, BPDUs only (15s)\n" +
            "LEARNING: Learning MACs, no forward (15s)\n" +
            "FORWARDING: Full operation\n" +
            "DISABLED: Admin shutdown\n\n" +
            "Transition to forwarding: 30-50 seconds!",
            2));
            
        flashcards.add(new Flashcard("DigiCom", "STP",
            "STP Timers",
            "HELLO TIME: 2 seconds\n   Interval between BPDUs from Root\n\n" +
            "MAX AGE: 20 seconds\n   How long to keep BPDU info\n\n" +
            "FORWARD DELAY: 15 seconds\n   Time in Listening AND Learning states\n\n" +
            "Convergence time: 30-50 seconds total",
            2));
            
        flashcards.add(new Flashcard("DigiCom", "STP",
            "RSTP Improvements",
            "Rapid Spanning Tree Protocol (802.1w):\n\n" +
            "• Convergence in SECONDS (vs 30-50s)\n" +
            "• Each switch sends own BPDUs\n" +
            "• New role: Backup Port\n" +
            "• Combined state: Discarding\n" +
            "• Proposal/Agreement handshake\n" +
            "• Edge Ports → instant forwarding",
            2));
            
        // Routing
        flashcards.add(new Flashcard("DigiCom", "Routing",
            "RIPv2 Basics",
            "Routing Information Protocol v2:\n\n" +
            "• Distance-vector protocol\n" +
            "• Metric: Hop count (max 15, 16=unreachable)\n" +
            "• Updates every 30 seconds\n" +
            "• Supports VLSM (unlike RIPv1)\n" +
            "• Simple but slow convergence\n" +
            "• Good for small networks",
            1));
            
        flashcards.add(new Flashcard("DigiCom", "Routing",
            "RIPv2 Configuration",
            "router rip\n" +
            "version 2\n" +
            "no auto-summary\n" +
            "network 10.0.0.0\n" +
            "network 192.168.1.0\n\n" +
            "Verification:\n" +
            "show ip protocols\n" +
            "show ip route\n" +
            "debug ip rip",
            2));
            
        flashcards.add(new Flashcard("DigiCom", "Routing",
            "OSPF Basics",
            "Open Shortest Path First:\n\n" +
            "• Link-state protocol\n" +
            "• Metric: Cost (based on bandwidth)\n" +
            "• Cost = Reference BW / Interface BW\n" +
            "• Default reference: 100 Mbps\n" +
            "• Fast convergence\n" +
            "• Hierarchical (areas)",
            1));
            
        flashcards.add(new Flashcard("DigiCom", "Routing",
            "OSPF Key Concepts",
            "ROUTER ID: Unique identifier (IP format)\n\n" +
            "AREA: Logical grouping\n   Area 0 = Backbone (required)\n\n" +
            "WILDCARD MASK: Inverse of subnet mask\n   /24 subnet = 0.0.0.255 wildcard\n\n" +
            "COST: Lower = preferred path",
            2));
            
        flashcards.add(new Flashcard("DigiCom", "Routing",
            "OSPF Configuration",
            "router ospf 1\n" +
            "router-id 1.1.1.1\n" +
            "network 10.0.0.0 0.0.0.255 area 0\n" +
            "passive-interface g0/1\n\n" +
            "Verification:\n" +
            "show ip ospf neighbor\n" +
            "show ip route ospf",
            2));
            
        flashcards.add(new Flashcard("DigiCom", "Routing",
            "OSPF MD5 Authentication",
            "In router config:\n" +
            "area 0 authentication message-digest\n\n" +
            "On interface:\n" +
            "ip ospf message-digest-key 1 md5 PASSWORD\n\n" +
            "Must match on ALL neighbors!",
            2));
            
        // ==================== TEAM FLASHCARDS ====================
        
        flashcards.add(new Flashcard("TEAM", "Grundlagen",
            "Team Definition (West)",
            "A team is a group of people who:\n\n" +
            "• Work TOGETHER toward SHARED GOALS\n" +
            "• Take on DIFFERENT ROLES\n" +
            "• COMMUNICATE to coordinate efforts\n\n" +
            "Key: gemeinsame Ziele, Rollen, Kommunikation",
            1));
            
        flashcards.add(new Flashcard("TEAM", "Grundlagen",
            "Team Definition (Thompson)",
            "A team is a group of individuals who are:\n\n" +
            "• MUTUALLY DEPENDENT on each other\n" +
            "• JOINTLY RESPONSIBLE for outcomes\n" +
            "• Working toward SPECIFIC ORGANIZATIONAL GOALS\n\n" +
            "Key: wechselseitige Abhängigkeit, gemeinsame Verantwortung",
            1));
            
        flashcards.add(new Flashcard("TEAM", "Grundlagen",
            "T-förmige Qualifikation",
            "Modern professional profile:\n\n" +
            "VERTICAL (|): Deep expertise in 1-2 areas\n   → Specialist knowledge\n\n" +
            "HORIZONTAL (―): Broad knowledge across fields\n   → Can collaborate with other disciplines\n\n" +
            "Combine specialist depth with generalist breadth!",
            1));
            
        flashcards.add(new Flashcard("TEAM", "Grundlagen",
            "Wissen ist Macht → Teilen",
            "FRÜHER: 'Wissen ist Macht!'\n   Knowledge hoarding, individual success\n\n" +
            "HEUTE: Wissen TEILEN für komplexe Aufgaben\n   • StackOverflow, ChatGPT\n   • Co-creation\n   • Vom Kennen zum KÖNNEN\n\n" +
            "Knowledge is just a click away - applying it is the skill!",
            1));
            
        // Tuckman
        flashcards.add(new Flashcard("TEAM", "Teamentwicklung",
            "Tuckman Model - 5 Phases",
            "1. FORMING: Orientation, getting to know, polite\n" +
            "2. STORMING: Conflict, power struggles, role clarity\n" +
            "3. NORMING: Cooperation, rules, consensus\n" +
            "4. PERFORMING: High productivity, self-organization\n" +
            "5. ADJOURNING: Dissolution, reflection, farewell",
            1));
            
        flashcards.add(new Flashcard("TEAM", "Teamentwicklung",
            "Forming Phase Details",
            "FORMING (Orientierungsphase):\n\n" +
            "• Getting to know each other\n" +
            "• Uncertainty about roles and norms\n" +
            "• Polite, cautious behavior\n" +
            "• Looking to leader for guidance\n" +
            "• Testing boundaries",
            1));
            
        flashcards.add(new Flashcard("TEAM", "Teamentwicklung",
            "Storming Phase Details",
            "STORMING (Konfrontationsphase):\n\n" +
            "• Conflicts emerge\n" +
            "• Power struggles and competition\n" +
            "• Resistance to group influence\n" +
            "• Role clarification needed\n" +
            "• CRITICAL: Must be managed, not avoided!\n\n" +
            "Many teams fail here - push through!",
            2));
            
        flashcards.add(new Flashcard("TEAM", "Teamentwicklung",
            "Norming Phase Details",
            "NORMING (Kooperationsphase):\n\n" +
            "• Conflicts resolved\n" +
            "• Group norms established\n" +
            "• Consensus building\n" +
            "• Team cohesion develops\n" +
            "• Roles accepted\n" +
            "• Open communication begins",
            1));
            
        flashcards.add(new Flashcard("TEAM", "Teamentwicklung",
            "Performing Phase Details",
            "PERFORMING (Wachstumsphase):\n\n" +
            "• High productivity\n" +
            "• Self-organization\n" +
            "• Flexible roles\n" +
            "• Focus on task completion\n" +
            "• Constructive problem-solving\n" +
            "• Team identity strong",
            1));
            
        // Belbin Roles
        flashcards.add(new Flashcard("TEAM", "Teamrollen",
            "Belbin - 3 Categories of Roles",
            "ACTION-ORIENTED:\n• Shaper, Implementer, Completer Finisher\n\n" +
            "COMMUNICATION-ORIENTED:\n• Coordinator, Team Worker, Resource Investigator\n\n" +
            "KNOWLEDGE-ORIENTED:\n• Plant, Monitor Evaluator, Specialist\n\n" +
            "9 roles total - balanced teams need mix!",
            2));
            
        flashcards.add(new Flashcard("TEAM", "Teamrollen",
            "Belbin - Action Roles",
            "SHAPER (Macher):\n   Dynamic, drives progress, overcomes obstacles\n\n" +
            "IMPLEMENTER (Umsetzer):\n   Disciplined, reliable, turns ideas into action\n\n" +
            "COMPLETER FINISHER (Perfektionist):\n   Conscientious, finds errors, polishes work",
            2));
            
        flashcards.add(new Flashcard("TEAM", "Teamrollen",
            "Belbin - Communication Roles",
            "COORDINATOR (Koordinator):\n   Confident, delegates, clarifies goals\n\n" +
            "TEAM WORKER (Teamarbeiter):\n   Cooperative, diplomatic, mediates\n\n" +
            "RESOURCE INVESTIGATOR (Wegbereiter):\n   Extrovert, explores opportunities, networks",
            2));
            
        flashcards.add(new Flashcard("TEAM", "Teamrollen",
            "Belbin - Knowledge Roles",
            "PLANT (Neuerer/Erfinder):\n   Creative, unorthodox, solves difficult problems\n\n" +
            "MONITOR EVALUATOR (Beobachter):\n   Strategic, analytical, makes judgments\n\n" +
            "SPECIALIST (Spezialist):\n   Single-minded, expert in specific field",
            2));
            
        // Führung
        flashcards.add(new Flashcard("TEAM", "Führung",
            "Führungsstile",
            "AUTORITÄR:\n   Clear hierarchy, decisions from top\n\n" +
            "KOOPERATIV:\n   Shared decisions, open communication\n\n" +
            "LAISSEZ-FAIRE:\n   Little intervention, high autonomy\n\n" +
            "SITUATIV:\n   Adapts style to situation and people",
            1));
            
        flashcards.add(new Flashcard("TEAM", "Führung",
            "Generationen Y und Z",
            "GENERATION Y (Millennials, 1981-1996):\n" +
            "• Digital natives\n• Seek meaning in work\n" +
            "• Team-oriented\n• Work-life balance\n\n" +
            "GENERATION Z (1997-2012):\n" +
            "• Mobile first\n• Pragmatic\n" +
            "• Value diversity\n• Entrepreneurial",
            1));
            
        // Konfliktmanagement
        flashcards.add(new Flashcard("TEAM", "Konflikte",
            "Konfliktarten",
            "SACHKONFLIKTE:\n   Different views on facts/goals\n\n" +
            "BEZIEHUNGSKONFLIKTE:\n   Personal tensions between members\n\n" +
            "PROZESSKONFLIKTE:\n   Disagreement on how to work\n\n" +
            "ROLLENKONFLIKTE:\n   Unclear/overlapping responsibilities",
            1));
            
        flashcards.add(new Flashcard("TEAM", "Konflikte",
            "Konfliktlösungsstrategien",
            "VERMEIDUNG: Ignore conflict (temporary)\n\n" +
            "ANPASSUNG: Give in for relationship\n\n" +
            "DURCHSETZUNG: Push own interests\n\n" +
            "KOMPROMISS: Both sides give something\n\n" +
            "KOLLABORATION: Win-win solution (optimal!)",
            2));
            
        // Feedback
        flashcards.add(new Flashcard("TEAM", "Feedback",
            "Feedback Geben - Regeln",
            "• Use ICH-BOTSCHAFTEN ('I observed...')\n" +
            "• Be SPECIFIC and descriptive, not judgmental\n" +
            "• Give TIMELY feedback (not weeks later)\n" +
            "• Describe BEHAVIOR, not character\n" +
            "• Include positives AND improvements\n" +
            "• Offer suggestions, not commands",
            2));
            
        flashcards.add(new Flashcard("TEAM", "Feedback",
            "Feedback Annehmen - Regeln",
            "• LISTEN actively, don't immediately defend\n" +
            "• ASK questions if unclear\n" +
            "• THANK the person for feedback\n" +
            "• DECIDE what to accept\n" +
            "• Don't take it personally\n" +
            "• View as opportunity for growth",
            2));
            
        // Agile
        flashcards.add(new Flashcard("TEAM", "Agile",
            "User Story Format",
            "Als <ROLLE>\nmöchte ich <FUNKTION>\ndamit <NUTZEN>\n\n" +
            "English:\nAs a <ROLE>\nI want <FEATURE>\nso that <BENEFIT>\n\n" +
            "Example:\nAls Student möchte ich Karteikarten durchgehen,\ndamit ich für die Prüfung lernen kann.",
            1));
            
        flashcards.add(new Flashcard("TEAM", "Agile",
            "User Story Mapping",
            "Jeff Patton's technique:\n\n" +
            "BACKBONE (horizontal):\n   Main activities/epics across top\n\n" +
            "USER STORIES (vertical):\n   Prioritized under each activity\n\n" +
            "RELEASES (horizontal lines):\n   Define scope of each release\n\n" +
            "Visual overview of entire product!",
            2));
            
        flashcards.add(new Flashcard("TEAM", "Agile",
            "Scrum Roles",
            "PRODUCT OWNER:\n   • Owns product vision\n   • Manages backlog\n   • Stakeholder liaison\n\n" +
            "SCRUM MASTER:\n   • Facilitator\n   • Removes impediments\n   • Coaches team\n\n" +
            "DEVELOPMENT TEAM:\n   • Self-organizing\n   • Cross-functional\n   • Delivers increment",
            2));
            
        flashcards.add(new Flashcard("TEAM", "Agile",
            "Scrum Events",
            "SPRINT: Fixed iteration (usually 2 weeks)\n\n" +
            "SPRINT PLANNING: What to build this sprint?\n\n" +
            "DAILY STANDUP: 15min sync\n   What did I do? What will I do? Blockers?\n\n" +
            "SPRINT REVIEW: Demo to stakeholders\n\n" +
            "SPRINT RETROSPECTIVE: What to improve?",
            2));
            
        // DEIB
        flashcards.add(new Flashcard("TEAM", "DEIB",
            "DEIB Explained",
            "DIVERSITY: Variety of people\n   (background, gender, age, abilities)\n\n" +
            "EQUITY: Fairness, equal opportunities\n   (individual support where needed)\n\n" +
            "INCLUSION: All voices heard and valued\n   (active participation enabled)\n\n" +
            "BELONGING: Feel part of team\n   (can be authentic self)",
            1));
            
        initializeQuestions();
    }
    
    private static void initializeQuestions() {
        // ==================== BSYS QUESTIONS ====================
        
        questions.add(new Question("BSYS",
            "Which memory section contains local variables and return addresses?",
            new String[]{"Heap", "Stack", "Text", "Data"},
            1,
            "The STACK contains temporary data including function parameters, return addresses, and local variables."));
            
        questions.add(new Question("BSYS",
            "A process that is waiting for I/O is in which state?",
            new String[]{"Running", "Ready", "Blocked", "Terminated"},
            2,
            "BLOCKED state means the process is unable to run until some external event (like I/O completion) happens."));
            
        questions.add(new Question("BSYS",
            "Thread creation is typically how much faster than process creation?",
            new String[]{"2-5x faster", "10-100x faster", "Same speed", "Actually slower"},
            1,
            "Threads are 'lightweight processes' - creation is typically 10-100x faster because they share address space."));
            
        questions.add(new Question("BSYS",
            "Which synchronization mechanism is 'owned' and can only be released by owner?",
            new String[]{"Semaphore", "Mutex", "Spinlock", "Monitor"},
            1,
            "A MUTEX is owned by the process that locks it - only the owner can unlock it. Semaphores can be modified by any process."));
            
        questions.add(new Question("BSYS",
            "In Round-Robin scheduling, what is a typical time quantum?",
            new String[]{"1-5ms", "20-50ms", "100-200ms", "1-2 seconds"},
            1,
            "Typical quantum is 20-50ms - too small causes overhead, too large degenerates to FCFS."));
            
        questions.add(new Question("BSYS",
            "Which scheduling algorithm gives minimum average waiting time?",
            new String[]{"FCFS", "SJF", "Round-Robin", "Priority"},
            1,
            "SJF (Shortest Job First) gives optimal minimum average waiting time when all jobs are available."));
            
        questions.add(new Question("BSYS",
            "What happens during a page fault?",
            new String[]{"CPU executes faster", "Page is loaded from disk", "Process terminates", "TLB is cleared"},
            1,
            "A page fault occurs when accessing an unmapped page - the OS loads the required page from disk into memory."));
            
        questions.add(new Question("BSYS",
            "What does TLB stand for?",
            new String[]{"Transfer Load Buffer", "Translation Lookaside Buffer", "Table Lookup Block", "Thread Local Buffer"},
            1,
            "TLB = Translation Lookaside Buffer - a hardware cache for page table entries to speed up address translation."));
            
        questions.add(new Question("BSYS",
            "In NRU page replacement, which class of pages is best to evict?",
            new String[]{"Class 3 (R=1, M=1)", "Class 2 (R=1, M=0)", "Class 1 (R=0, M=1)", "Class 0 (R=0, M=0)"},
            3,
            "Class 0 (not referenced, not modified) is best to evict - not recently used and doesn't need to be written back."));
            
        questions.add(new Question("BSYS",
            "Which RAID level provides striping with distributed parity?",
            new String[]{"RAID 0", "RAID 1", "RAID 4", "RAID 5"},
            3,
            "RAID 5 uses block interleaved distributed parity - parity is spread across all disks to avoid bottleneck."));
            
        questions.add(new Question("BSYS",
            "An i-node contains all EXCEPT:",
            new String[]{"File size", "File name", "Block pointers", "Permissions"},
            1,
            "The file NAME is stored in the directory entry, not the i-node. The i-node contains metadata and block pointers."));
            
        questions.add(new Question("BSYS",
            "What is the maximum number of primary partitions in MBR?",
            new String[]{"2", "4", "8", "Unlimited"},
            1,
            "MBR supports maximum 4 primary partitions. GPT allows many more (typically 128)."));
            
        // ==================== DIGICOM QUESTIONS ====================
        
        questions.add(new Question("DigiCom",
            "At which OSI layer does a router operate?",
            new String[]{"Layer 1 - Physical", "Layer 2 - Data Link", "Layer 3 - Network", "Layer 4 - Transport"},
            2,
            "Routers operate at Layer 3 (Network) - they forward packets based on IP addresses."));
            
        questions.add(new Question("DigiCom",
            "What is the PDU at the Transport layer called?",
            new String[]{"Frame", "Packet", "Segment", "Data"},
            2,
            "At Transport layer: Segment (TCP) or Datagram (UDP). Frame is L2, Packet is L3."));
            
        questions.add(new Question("DigiCom",
            "What value indicates an 802.1Q tagged frame?",
            new String[]{"0x0800", "0x8100", "0x8600", "0x86DD"},
            1,
            "TPID = 0x8100 in the 802.1Q tag indicates a tagged VLAN frame."));
            
        questions.add(new Question("DigiCom",
            "How many usable VLAN IDs are there with 12 bits?",
            new String[]{"4096", "4095", "4094", "4093"},
            2,
            "12 bits = 4096 values, but VLAN 0 and 4095 are reserved, leaving 4094 usable IDs."));
            
        questions.add(new Question("DigiCom",
            "What is the default STP bridge priority?",
            new String[]{"0", "128", "32768", "65535"},
            2,
            "Default bridge priority is 32768. Lower priority = higher chance of becoming Root Bridge."));
            
        questions.add(new Question("DigiCom",
            "How long is the STP Forward Delay timer?",
            new String[]{"2 seconds", "15 seconds", "20 seconds", "50 seconds"},
            1,
            "Forward Delay = 15 seconds each for Listening and Learning states (30s total transition)."));
            
        questions.add(new Question("DigiCom",
            "Which STP port role has the lowest path cost to Root Bridge?",
            new String[]{"Designated Port", "Root Port", "Alternate Port", "Backup Port"},
            1,
            "ROOT PORT is the port with lowest path cost to Root Bridge - one per non-root switch."));
            
        questions.add(new Question("DigiCom",
            "What metric does RIPv2 use?",
            new String[]{"Bandwidth", "Hop count", "Delay", "Cost"},
            1,
            "RIPv2 uses hop count as metric. Maximum is 15 hops; 16 = unreachable."));
            
        questions.add(new Question("DigiCom",
            "What is the wildcard mask for a /24 network?",
            new String[]{"255.255.255.0", "0.0.0.255", "255.255.255.255", "0.0.0.0"},
            1,
            "Wildcard mask is inverse of subnet mask. /24 = 255.255.255.0 → wildcard = 0.0.0.255"));
            
        questions.add(new Question("DigiCom",
            "What is the default OSPF reference bandwidth?",
            new String[]{"10 Mbps", "100 Mbps", "1 Gbps", "10 Gbps"},
            1,
            "Default reference bandwidth is 100 Mbps. Cost = Reference BW / Interface BW."));
            
        questions.add(new Question("DigiCom",
            "RSTP convergence time is typically:",
            new String[]{"30-50 seconds", "15-20 seconds", "Under 10 seconds", "1-2 minutes"},
            2,
            "RSTP achieves convergence in seconds (under 10s) vs 30-50s for classic STP."));
            
        questions.add(new Question("DigiCom",
            "Native VLAN traffic on a trunk is:",
            new String[]{"Always tagged", "Never tagged (untagged)", "Encrypted", "Blocked"},
            1,
            "Native VLAN traffic is UNTAGGED on trunk links. Must be same on both ends!"));
            
        // ==================== TEAM QUESTIONS ====================
        
        questions.add(new Question("TEAM",
            "In the T-shaped qualification model, what does the vertical line represent?",
            new String[]{"Broad general knowledge", "Deep specialist knowledge", "Communication skills", "Leadership ability"},
            1,
            "The vertical line (|) represents deep expertise/specialization in 1-2 areas."));
            
        questions.add(new Question("TEAM",
            "Which Tuckman phase involves power struggles and conflict?",
            new String[]{"Forming", "Storming", "Norming", "Performing"},
            1,
            "STORMING phase involves conflicts, power struggles, and role clarification. Teams must push through this!"));
            
        questions.add(new Question("TEAM",
            "In Belbin's model, which role is the creative problem solver?",
            new String[]{"Coordinator", "Implementer", "Plant", "Specialist"},
            2,
            "PLANT (Neuerer/Erfinder) is the creative, unorthodox thinker who solves difficult problems."));
            
        questions.add(new Question("TEAM",
            "Which Belbin role is the diplomatic mediator?",
            new String[]{"Shaper", "Team Worker", "Monitor Evaluator", "Resource Investigator"},
            1,
            "TEAM WORKER (Teamarbeiter) is cooperative, diplomatic, and mediates between team members."));
            
        questions.add(new Question("TEAM",
            "What type of conflict involves disagreement about how to work?",
            new String[]{"Sachkonflikt", "Beziehungskonflikt", "Prozesskonflikt", "Rollenkonflikt"},
            2,
            "PROZESSKONFLIKT involves disagreement on working methods and task distribution."));
            
        questions.add(new Question("TEAM",
            "Which conflict resolution strategy aims for win-win?",
            new String[]{"Vermeidung", "Kompromiss", "Kollaboration", "Durchsetzung"},
            2,
            "KOLLABORATION seeks a win-win solution where both parties' needs are fully met - the optimal strategy."));
            
        questions.add(new Question("TEAM",
            "Good feedback should use:",
            new String[]{"Du-Botschaften (You-messages)", "Ich-Botschaften (I-messages)", "General statements", "Assumptions"},
            1,
            "ICH-BOTSCHAFTEN ('I observed...' 'I felt...') are less accusatory and more effective than 'You-messages'."));
            
        questions.add(new Question("TEAM",
            "In User Story format, what comes after 'Als <Rolle>'?",
            new String[]{"damit <Nutzen>", "möchte ich <Funktion>", "weil <Grund>", "wenn <Bedingung>"},
            1,
            "User Story format: Als <Rolle> möchte ich <Funktion>, damit <Nutzen>."));
            
        questions.add(new Question("TEAM",
            "Which Scrum role owns the product backlog?",
            new String[]{"Scrum Master", "Development Team", "Product Owner", "Stakeholder"},
            2,
            "PRODUCT OWNER is responsible for the product vision and managing the backlog priorities."));
            
        questions.add(new Question("TEAM",
            "How long is a typical Sprint?",
            new String[]{"1 day", "1 week", "2 weeks", "1 month"},
            2,
            "Typical Sprint length is 2 weeks, though 1-4 weeks is common depending on the team."));
            
        questions.add(new Question("TEAM",
            "In DEIB, what does the 'E' stand for?",
            new String[]{"Engagement", "Equity", "Excellence", "Empathy"},
            1,
            "E = EQUITY (Gerechtigkeit) - fairness and equal opportunities, with individual support where needed."));
            
        questions.add(new Question("TEAM",
            "Generation Y (Millennials) were born approximately:",
            new String[]{"1965-1980", "1981-1996", "1997-2012", "After 2012"},
            1,
            "Generation Y (Millennials): 1981-1996. Gen X: 1965-1980. Gen Z: 1997-2012."));
    }
    
    // ==================== UI METHODS ====================
    
    private static void printWelcome() {
        clearScreen();
        System.out.println(CYAN + BOLD);
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                               ║");
        System.out.println("║   ███████╗████████╗██╗   ██╗██████╗ ██╗   ██╗                ║");
        System.out.println("║   ██╔════╝╚══██╔══╝██║   ██║██╔══██╗╚██╗ ██╔╝                ║");
        System.out.println("║   ███████╗   ██║   ██║   ██║██║  ██║ ╚████╔╝                 ║");
        System.out.println("║   ╚════██║   ██║   ██║   ██║██║  ██║  ╚██╔╝                  ║");
        System.out.println("║   ███████║   ██║   ╚██████╔╝██████╔╝   ██║                   ║");
        System.out.println("║   ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝    ╚═╝                   ║");
        System.out.println("║                                                               ║");
        System.out.println("║            📚 EXAM PREPARATION STUDY APP 📚                  ║");
        System.out.println("║                                                               ║");
        System.out.println("║   Subjects:                                                   ║");
        System.out.println("║   " + BLUE + "● BSYS" + CYAN + " - Operating Systems                                ║");
        System.out.println("║   " + GREEN + "● DigiCom" + CYAN + " - Digital Communications                        ║");
        System.out.println("║   " + PURPLE + "● TEAM" + CYAN + " - Teamarbeit                                       ║");
        System.out.println("║                                                               ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝" + RESET);
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
    
    private static void printMainMenu() {
        System.out.println("\n" + CYAN + "═══════════════════════════════════════════════════════════════" + RESET);
        System.out.println(BOLD + "                      📖 MAIN MENU" + RESET);
        System.out.println(CYAN + "═══════════════════════════════════════════════════════════════" + RESET);
        System.out.println("\n  " + YELLOW + "[1]" + RESET + " 🎴 Flashcard Mode - Study with flashcards");
        System.out.println("  " + YELLOW + "[2]" + RESET + " ❓ Quiz Mode - Test your knowledge");
        System.out.println("  " + YELLOW + "[3]" + RESET + " 📋 Topic Browser - Review all topics");
        System.out.println("  " + YELLOW + "[4]" + RESET + " 📊 View Progress - Check your stats");
        System.out.println("  " + YELLOW + "[5]" + RESET + " ⚡ Quick Review - Random cards from all subjects");
        System.out.println("  " + YELLOW + "[6]" + RESET + " 🎓 Exam Simulation - Timed test");
        System.out.println("  " + YELLOW + "[7]" + RESET + " ✏️  Input Mode - Add your own content");
        System.out.println("\n  " + RED + "[0]" + RESET + " 🚪 Exit");
        System.out.println(CYAN + "\n═══════════════════════════════════════════════════════════════" + RESET);
        int customCount = customFlashcards.size() + customQuestions.size();
        if (customCount > 0) {
            System.out.println("  " + GREEN + "Custom content: " + customFlashcards.size() + " cards, " + customQuestions.size() + " questions" + RESET);
        }
        System.out.print("Choose option: ");
    }
    
    private static void flashcardMode() {
        while (true) {
            clearScreen();
            System.out.println("\n" + YELLOW + "═══════════════════════════════════════════════════════════════" + RESET);
            System.out.println(BOLD + "                   🎴 FLASHCARD MODE" + RESET);
            System.out.println(YELLOW + "═══════════════════════════════════════════════════════════════" + RESET);
            System.out.println("\n  Select Subject:");
            System.out.println("  " + BLUE + "[1]" + RESET + " BSYS - Operating Systems (" + countCards("BSYS") + " cards)");
            System.out.println("  " + GREEN + "[2]" + RESET + " DigiCom - Digital Communications (" + countCards("DigiCom") + " cards)");
            System.out.println("  " + PURPLE + "[3]" + RESET + " TEAM - Teamarbeit (" + countCards("TEAM") + " cards)");
            System.out.println("  " + CYAN + "[4]" + RESET + " All Subjects Mixed (" + flashcards.size() + " cards)");
            System.out.println("\n  " + RED + "[0]" + RESET + " Back to Main Menu");
            System.out.print("\nChoice: ");
            
            String choice = scanner.nextLine().trim();
            
            List<Flashcard> selectedCards;
            switch (choice) {
                case "1": selectedCards = filterCards("BSYS"); break;
                case "2": selectedCards = filterCards("DigiCom"); break;
                case "3": selectedCards = filterCards("TEAM"); break;
                case "4": selectedCards = new ArrayList<>(flashcards); break;
                case "0": return;
                default: continue;
            }
            
            if (selectedCards.isEmpty()) {
                System.out.println(RED + "No cards available!" + RESET);
                continue;
            }
            
            studyFlashcards(selectedCards);
        }
    }
    
    private static void studyFlashcards(List<Flashcard> cards) {
        Collections.shuffle(cards);
        int index = 0;
        int correct = 0;
        int total = 0;
        
        while (index < cards.size()) {
            Flashcard card = cards.get(index);
            clearScreen();
            
            String subjectColor = getSubjectColor(card.subject);
            
            System.out.println("\n" + subjectColor + "═══════════════════════════════════════════════════════════════" + RESET);
            System.out.println("  Card " + (index + 1) + " of " + cards.size() + " | " + subjectColor + card.subject + RESET + " | " + card.topic);
            System.out.println(subjectColor + "═══════════════════════════════════════════════════════════════" + RESET);
            
            System.out.println("\n" + BOLD + "  QUESTION:" + RESET);
            System.out.println("  " + card.front);
            System.out.println("\n" + YELLOW + "  [Press Enter to reveal answer]" + RESET);
            System.out.println("  [n] Next  [p] Previous  [q] Quit");
            
            String input = scanner.nextLine().trim().toLowerCase();
            
            if (input.equals("q")) break;
            if (input.equals("p") && index > 0) { index--; continue; }
            if (input.equals("n")) { index++; continue; }
            
            // Show answer
            System.out.println("\n" + GREEN + "  ANSWER:" + RESET);
            for (String line : card.back.split("\n")) {
                System.out.println("  " + line);
            }
            
            System.out.println("\n  Did you know this? " + GREEN + "[y]" + RESET + " Yes  " + RED + "[n]" + RESET + " No");
            input = scanner.nextLine().trim().toLowerCase();
            
            total++;
            if (input.equals("y")) {
                correct++;
                updateProgress(card.subject + "-" + card.topic, true);
                System.out.println(GREEN + "  ✓ Great job!" + RESET);
            } else {
                updateProgress(card.subject + "-" + card.topic, false);
                System.out.println(YELLOW + "  Keep practicing!" + RESET);
            }
            
            index++;
            pause(1000);
        }
        
        // Show session summary
        if (total > 0) {
            clearScreen();
            System.out.println("\n" + CYAN + "═══════════════════════════════════════════════════════════════" + RESET);
            System.out.println(BOLD + "               📊 SESSION SUMMARY" + RESET);
            System.out.println(CYAN + "═══════════════════════════════════════════════════════════════" + RESET);
            System.out.println("\n  Cards reviewed: " + total);
            System.out.println("  Correct: " + GREEN + correct + RESET);
            System.out.println("  Need review: " + YELLOW + (total - correct) + RESET);
            System.out.println("  Score: " + (correct * 100 / total) + "%");
            System.out.println("\n  Press Enter to continue...");
            scanner.nextLine();
        }
    }
    
    private static void quizMode() {
        while (true) {
            clearScreen();
            System.out.println("\n" + GREEN + "═══════════════════════════════════════════════════════════════" + RESET);
            System.out.println(BOLD + "                    ❓ QUIZ MODE" + RESET);
            System.out.println(GREEN + "═══════════════════════════════════════════════════════════════" + RESET);
            System.out.println("\n  Select Subject:");
            System.out.println("  " + BLUE + "[1]" + RESET + " BSYS - Operating Systems");
            System.out.println("  " + GREEN + "[2]" + RESET + " DigiCom - Digital Communications");
            System.out.println("  " + PURPLE + "[3]" + RESET + " TEAM - Teamarbeit");
            System.out.println("  " + CYAN + "[4]" + RESET + " All Subjects Mixed");
            System.out.println("\n  " + RED + "[0]" + RESET + " Back to Main Menu");
            System.out.print("\nChoice: ");
            
            String choice = scanner.nextLine().trim();
            
            List<Question> selectedQuestions;
            switch (choice) {
                case "1": selectedQuestions = filterQuestions("BSYS"); break;
                case "2": selectedQuestions = filterQuestions("DigiCom"); break;
                case "3": selectedQuestions = filterQuestions("TEAM"); break;
                case "4": selectedQuestions = new ArrayList<>(questions); break;
                case "0": return;
                default: continue;
            }
            
            System.out.print("\n  How many questions? (max " + selectedQuestions.size() + "): ");
            int numQuestions;
            try {
                numQuestions = Integer.parseInt(scanner.nextLine().trim());
                numQuestions = Math.min(numQuestions, selectedQuestions.size());
                numQuestions = Math.max(numQuestions, 1);
            } catch (Exception e) {
                numQuestions = Math.min(10, selectedQuestions.size());
            }
            
            runQuiz(selectedQuestions, numQuestions);
        }
    }
    
    private static void runQuiz(List<Question> allQuestions, int numQuestions) {
        Collections.shuffle(allQuestions);
        List<Question> quizQuestions = allQuestions.subList(0, numQuestions);
        
        int correct = 0;
        int questionNum = 1;
        List<Question> wrongAnswers = new ArrayList<>();
        
        for (Question q : quizQuestions) {
            clearScreen();
            String subjectColor = getSubjectColor(q.subject);
            
            System.out.println("\n" + subjectColor + "═══════════════════════════════════════════════════════════════" + RESET);
            System.out.println("  Question " + questionNum + " of " + numQuestions + " | " + subjectColor + q.subject + RESET);
            System.out.println(subjectColor + "═══════════════════════════════════════════════════════════════" + RESET);
            
            System.out.println("\n  " + BOLD + q.question + RESET + "\n");
            
            for (int i = 0; i < q.options.length; i++) {
                System.out.println("  " + YELLOW + "[" + (char)('A' + i) + "]" + RESET + " " + q.options[i]);
            }
            
            System.out.print("\n  Your answer (A-D): ");
            String answer = scanner.nextLine().trim().toUpperCase();
            
            int answerIndex = -1;
            if (answer.length() == 1 && answer.charAt(0) >= 'A' && answer.charAt(0) <= 'D') {
                answerIndex = answer.charAt(0) - 'A';
            }
            
            if (answerIndex == q.correctIndex) {
                correct++;
                System.out.println("\n  " + GREEN + "✓ CORRECT!" + RESET);
                updateProgress(q.subject + "-quiz", true);
            } else {
                wrongAnswers.add(q);
                System.out.println("\n  " + RED + "✗ INCORRECT!" + RESET);
                System.out.println("  Correct answer: " + GREEN + (char)('A' + q.correctIndex) + ") " + q.options[q.correctIndex] + RESET);
                updateProgress(q.subject + "-quiz", false);
            }
            
            System.out.println("\n  " + CYAN + "Explanation: " + RESET + q.explanation);
            System.out.println("\n  Press Enter to continue...");
            scanner.nextLine();
            questionNum++;
        }
        
        // Quiz Results
        clearScreen();
        int percentage = (correct * 100) / numQuestions;
        String grade = percentage >= 90 ? "A" : percentage >= 80 ? "B" : percentage >= 70 ? "C" : percentage >= 60 ? "D" : "F";
        String gradeColor = percentage >= 70 ? GREEN : percentage >= 60 ? YELLOW : RED;
        
        System.out.println("\n" + CYAN + "═══════════════════════════════════════════════════════════════" + RESET);
        System.out.println(BOLD + "                   🏆 QUIZ RESULTS" + RESET);
        System.out.println(CYAN + "═══════════════════════════════════════════════════════════════" + RESET);
        System.out.println("\n  Score: " + correct + "/" + numQuestions + " (" + percentage + "%)");
        System.out.println("  Grade: " + gradeColor + BOLD + grade + RESET);
        
        if (percentage >= 90) {
            System.out.println("\n  " + GREEN + "🌟 Excellent work! You've mastered this material!" + RESET);
        } else if (percentage >= 70) {
            System.out.println("\n  " + GREEN + "👍 Good job! Keep practicing!" + RESET);
        } else {
            System.out.println("\n  " + YELLOW + "📚 Keep studying! Review the topics you missed." + RESET);
        }
        
        if (!wrongAnswers.isEmpty()) {
            System.out.println("\n  Topics to review:");
            for (Question q : wrongAnswers) {
                System.out.println("  • " + q.subject + ": " + q.question.substring(0, Math.min(50, q.question.length())) + "...");
            }
        }
        
        System.out.println("\n  Press Enter to continue...");
        scanner.nextLine();
    }
    
    private static void topicBrowser() {
        while (true) {
            clearScreen();
            System.out.println("\n" + PURPLE + "═══════════════════════════════════════════════════════════════" + RESET);
            System.out.println(BOLD + "                   📋 TOPIC BROWSER" + RESET);
            System.out.println(PURPLE + "═══════════════════════════════════════════════════════════════" + RESET);
            System.out.println("\n  Select Subject:");
            System.out.println("  " + BLUE + "[1]" + RESET + " BSYS - Operating Systems");
            System.out.println("  " + GREEN + "[2]" + RESET + " DigiCom - Digital Communications");
            System.out.println("  " + PURPLE + "[3]" + RESET + " TEAM - Teamarbeit");
            System.out.println("\n  " + RED + "[0]" + RESET + " Back to Main Menu");
            System.out.print("\nChoice: ");
            
            String choice = scanner.nextLine().trim();
            String subject;
            switch (choice) {
                case "1": subject = "BSYS"; break;
                case "2": subject = "DigiCom"; break;
                case "3": subject = "TEAM"; break;
                case "0": return;
                default: continue;
            }
            
            browseTopic(subject);
        }
    }
    
    private static void browseTopic(String subject) {
        List<Flashcard> cards = filterCards(subject);
        Map<String, List<Flashcard>> byTopic = new LinkedHashMap<>();
        
        for (Flashcard card : cards) {
            byTopic.computeIfAbsent(card.topic, k -> new ArrayList<>()).add(card);
        }
        
        while (true) {
            clearScreen();
            String color = getSubjectColor(subject);
            System.out.println("\n" + color + "═══════════════════════════════════════════════════════════════" + RESET);
            System.out.println("                   📋 " + subject + " TOPICS");
            System.out.println(color + "═══════════════════════════════════════════════════════════════" + RESET);
            
            List<String> topics = new ArrayList<>(byTopic.keySet());
            for (int i = 0; i < topics.size(); i++) {
                String topic = topics.get(i);
                int cardCount = byTopic.get(topic).size();
                System.out.println("  " + YELLOW + "[" + (i + 1) + "]" + RESET + " " + topic + " (" + cardCount + " cards)");
            }
            System.out.println("\n  " + RED + "[0]" + RESET + " Back");
            System.out.print("\nChoice: ");
            
            String choice = scanner.nextLine().trim();
            if (choice.equals("0")) return;
            
            try {
                int topicIndex = Integer.parseInt(choice) - 1;
                if (topicIndex >= 0 && topicIndex < topics.size()) {
                    String selectedTopic = topics.get(topicIndex);
                    showTopicCards(byTopic.get(selectedTopic), subject, selectedTopic);
                }
            } catch (Exception e) {
                // Invalid input, continue
            }
        }
    }
    
    private static void showTopicCards(List<Flashcard> cards, String subject, String topic) {
        int index = 0;
        while (index < cards.size()) {
            Flashcard card = cards.get(index);
            clearScreen();
            String color = getSubjectColor(subject);
            
            System.out.println("\n" + color + "═══════════════════════════════════════════════════════════════" + RESET);
            System.out.println("  " + subject + " > " + topic + " | Card " + (index + 1) + "/" + cards.size());
            System.out.println(color + "═══════════════════════════════════════════════════════════════" + RESET);
            
            System.out.println("\n" + BOLD + "  Q: " + RESET + card.front);
            System.out.println("\n" + GREEN + "  A: " + RESET);
            for (String line : card.back.split("\n")) {
                System.out.println("     " + line);
            }
            
            System.out.println("\n  [n] Next  [p] Previous  [q] Back to topics");
            String input = scanner.nextLine().trim().toLowerCase();
            
            if (input.equals("q")) break;
            if (input.equals("p") && index > 0) index--;
            else if (input.equals("n") || input.isEmpty()) index++;
            else if (index < cards.size() - 1) index++;
            else break;
        }
    }
    
    private static void viewProgress() {
        clearScreen();
        System.out.println("\n" + CYAN + "═══════════════════════════════════════════════════════════════" + RESET);
        System.out.println(BOLD + "                   📊 YOUR PROGRESS" + RESET);
        System.out.println(CYAN + "═══════════════════════════════════════════════════════════════" + RESET);
        
        // Calculate stats per subject
        Map<String, int[]> subjectStats = new HashMap<>();
        subjectStats.put("BSYS", new int[]{0, 0});
        subjectStats.put("DigiCom", new int[]{0, 0});
        subjectStats.put("TEAM", new int[]{0, 0});
        
        for (String key : correctAnswers.keySet()) {
            for (String subject : subjectStats.keySet()) {
                if (key.startsWith(subject)) {
                    subjectStats.get(subject)[0] += correctAnswers.get(key);
                    subjectStats.get(subject)[1] += totalAttempts.get(key);
                }
            }
        }
        
        System.out.println("\n  " + BOLD + "Subject Statistics:" + RESET);
        System.out.println("  ─────────────────────────────────────────");
        
        for (String subject : new String[]{"BSYS", "DigiCom", "TEAM"}) {
            int[] stats = subjectStats.get(subject);
            String color = getSubjectColor(subject);
            int pct = stats[1] > 0 ? (stats[0] * 100 / stats[1]) : 0;
            String bar = generateProgressBar(pct, 20);
            System.out.println("  " + color + subject + RESET + "\t" + bar + " " + pct + "% (" + stats[0] + "/" + stats[1] + ")");
        }
        
        int totalCorrect = correctAnswers.values().stream().mapToInt(Integer::intValue).sum();
        int totalAttempt = totalAttempts.values().stream().mapToInt(Integer::intValue).sum();
        int overallPct = totalAttempt > 0 ? (totalCorrect * 100 / totalAttempt) : 0;
        
        System.out.println("  ─────────────────────────────────────────");
        System.out.println("  " + BOLD + "OVERALL" + RESET + "\t" + generateProgressBar(overallPct, 20) + " " + overallPct + "%");
        
        System.out.println("\n  " + BOLD + "Cards Mastered: " + RESET + masteredCards.size() + "/" + flashcards.size());
        System.out.println("  " + BOLD + "Total Cards Available: " + RESET + flashcards.size());
        System.out.println("  " + BOLD + "Total Questions Available: " + RESET + questions.size());
        
        System.out.println("\n  Press Enter to continue...");
        scanner.nextLine();
    }
    
    private static void quickReview() {
        List<Flashcard> weakCards = new ArrayList<>();
        
        // Find cards with low performance
        for (Flashcard card : flashcards) {
            String key = card.subject + "-" + card.topic;
            int correct = correctAnswers.getOrDefault(key, 0);
            int total = totalAttempts.getOrDefault(key, 0);
            if (total == 0 || (total > 0 && correct * 100 / total < 70)) {
                weakCards.add(card);
            }
        }
        
        if (weakCards.isEmpty()) {
            weakCards = new ArrayList<>(flashcards);
        }
        
        Collections.shuffle(weakCards);
        List<Flashcard> reviewCards = weakCards.subList(0, Math.min(10, weakCards.size()));
        
        clearScreen();
        System.out.println("\n" + YELLOW + "═══════════════════════════════════════════════════════════════" + RESET);
        System.out.println(BOLD + "                   ⚡ QUICK REVIEW" + RESET);
        System.out.println(YELLOW + "═══════════════════════════════════════════════════════════════" + RESET);
        System.out.println("\n  Reviewing " + reviewCards.size() + " cards that need attention...");
        System.out.println("\n  Press Enter to start...");
        scanner.nextLine();
        
        studyFlashcards(reviewCards);
    }
    
    private static void examSimulation() {
        clearScreen();
        System.out.println("\n" + RED + "═══════════════════════════════════════════════════════════════" + RESET);
        System.out.println(BOLD + "                   🎓 EXAM SIMULATION" + RESET);
        System.out.println(RED + "═══════════════════════════════════════════════════════════════" + RESET);
        System.out.println("\n  This simulates a real exam environment!");
        System.out.println("  • Mixed questions from all subjects");
        System.out.println("  • No explanations until the end");
        System.out.println("  • Timed (30 seconds per question)");
        System.out.println("\n  " + YELLOW + "[1]" + RESET + " Short exam (10 questions)");
        System.out.println("  " + YELLOW + "[2]" + RESET + " Medium exam (20 questions)");
        System.out.println("  " + YELLOW + "[3]" + RESET + " Full exam (30 questions)");
        System.out.println("\n  " + RED + "[0]" + RESET + " Back");
        System.out.print("\nChoice: ");
        
        String choice = scanner.nextLine().trim();
        int numQuestions;
        switch (choice) {
            case "1": numQuestions = 10; break;
            case "2": numQuestions = 20; break;
            case "3": numQuestions = 30; break;
            default: return;
        }
        
        numQuestions = Math.min(numQuestions, questions.size());
        
        System.out.println("\n  Starting exam in 3...");
        pause(1000);
        System.out.println("  2...");
        pause(1000);
        System.out.println("  1...");
        pause(1000);
        System.out.println("  GO!");
        pause(500);
        
        List<Question> examQuestions = new ArrayList<>(questions);
        Collections.shuffle(examQuestions);
        examQuestions = examQuestions.subList(0, numQuestions);
        
        int correct = 0;
        int[] answers = new int[numQuestions];
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < examQuestions.size(); i++) {
            Question q = examQuestions.get(i);
            clearScreen();
            
            System.out.println("\n  Question " + (i + 1) + "/" + numQuestions + " | " + q.subject);
            System.out.println("  ─────────────────────────────────────────");
            System.out.println("\n  " + q.question + "\n");
            
            for (int j = 0; j < q.options.length; j++) {
                System.out.println("  [" + (char)('A' + j) + "] " + q.options[j]);
            }
            
            System.out.print("\n  Answer (A-D): ");
            String answer = scanner.nextLine().trim().toUpperCase();
            
            int answerIndex = -1;
            if (answer.length() == 1 && answer.charAt(0) >= 'A' && answer.charAt(0) <= 'D') {
                answerIndex = answer.charAt(0) - 'A';
            }
            
            answers[i] = answerIndex;
            if (answerIndex == q.correctIndex) correct++;
        }
        
        long endTime = System.currentTimeMillis();
        int totalSeconds = (int)((endTime - startTime) / 1000);
        
        // Show results
        clearScreen();
        int pct = (correct * 100) / numQuestions;
        String grade = pct >= 90 ? "Sehr Gut (1)" : pct >= 80 ? "Gut (2)" : pct >= 70 ? "Befriedigend (3)" : pct >= 60 ? "Genügend (4)" : "Nicht Genügend (5)";
        String gradeColor = pct >= 60 ? GREEN : RED;
        
        System.out.println("\n" + CYAN + "═══════════════════════════════════════════════════════════════" + RESET);
        System.out.println(BOLD + "                   🎓 EXAM RESULTS" + RESET);
        System.out.println(CYAN + "═══════════════════════════════════════════════════════════════" + RESET);
        System.out.println("\n  Score: " + correct + "/" + numQuestions + " (" + pct + "%)");
        System.out.println("  Grade: " + gradeColor + BOLD + grade + RESET);
        System.out.println("  Time: " + (totalSeconds / 60) + "m " + (totalSeconds % 60) + "s");
        System.out.println("  Average: " + (totalSeconds / numQuestions) + "s per question");
        
        System.out.println("\n  " + YELLOW + "Review incorrect answers? [y/n]" + RESET);
        if (scanner.nextLine().trim().toLowerCase().equals("y")) {
            for (int i = 0; i < examQuestions.size(); i++) {
                Question q = examQuestions.get(i);
                if (answers[i] != q.correctIndex) {
                    System.out.println("\n  " + RED + "Q" + (i+1) + ": " + q.question + RESET);
                    if (answers[i] >= 0) {
                        System.out.println("  Your answer: " + q.options[answers[i]]);
                    }
                    System.out.println("  " + GREEN + "Correct: " + q.options[q.correctIndex] + RESET);
                    System.out.println("  " + q.explanation);
                }
            }
        }
        
        System.out.println("\n  Press Enter to continue...");
        scanner.nextLine();
    }
    
    // ==================== INPUT MODE ====================
    
    private static void inputMode() {
        while (true) {
            clearScreen();
            System.out.println("\n" + PURPLE + "═══════════════════════════════════════════════════════════════" + RESET);
            System.out.println(BOLD + "                   ✏️  INPUT MODE" + RESET);
            System.out.println(PURPLE + "═══════════════════════════════════════════════════════════════" + RESET);
            System.out.println("\n  Add your own study content!\n");
            System.out.println("  " + YELLOW + "[1]" + RESET + " 📝 Add Flashcard");
            System.out.println("  " + YELLOW + "[2]" + RESET + " ❓ Add Quiz Question");
            System.out.println("  " + YELLOW + "[3]" + RESET + " 📋 View Custom Content (" + customFlashcards.size() + " cards, " + customQuestions.size() + " questions)");
            System.out.println("  " + YELLOW + "[4]" + RESET + " ✏️  Edit Custom Content");
            System.out.println("  " + YELLOW + "[5]" + RESET + " 🗑️  Delete Custom Content");
            System.out.println("  " + YELLOW + "[6]" + RESET + " 📤 Export Custom Content");
            System.out.println("  " + YELLOW + "[7]" + RESET + " 📥 Import from File");
            System.out.println("  " + YELLOW + "[8]" + RESET + " ⚡ Quick Add (Bulk Mode)");
            System.out.println("\n  " + RED + "[0]" + RESET + " Back to Main Menu");
            System.out.print("\nChoice: ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1": addFlashcard(); break;
                case "2": addQuestion(); break;
                case "3": viewCustomContent(); break;
                case "4": editCustomContent(); break;
                case "5": deleteCustomContent(); break;
                case "6": exportCustomContent(); break;
                case "7": importFromFile(); break;
                case "8": bulkAddMode(); break;
                case "0": 
                    saveCustomContent();
                    return;
                default:
                    System.out.println(RED + "Invalid choice." + RESET);
                    pause(1000);
            }
        }
    }
    
    private static void addFlashcard() {
        clearScreen();
        System.out.println("\n" + GREEN + "═══════════════════════════════════════════════════════════════" + RESET);
        System.out.println(BOLD + "                   📝 ADD FLASHCARD" + RESET);
        System.out.println(GREEN + "═══════════════════════════════════════════════════════════════" + RESET);
        
        // Select subject
        System.out.println("\n  Select Subject:");
        System.out.println("  " + BLUE + "[1]" + RESET + " BSYS - Operating Systems");
        System.out.println("  " + GREEN + "[2]" + RESET + " DigiCom - Digital Communications");
        System.out.println("  " + PURPLE + "[3]" + RESET + " TEAM - Teamarbeit");
        System.out.println("  " + CYAN + "[4]" + RESET + " CUSTOM - New custom subject");
        System.out.print("\n  Subject [1-4]: ");
        
        String subjectChoice = scanner.nextLine().trim();
        String subject;
        switch (subjectChoice) {
            case "1": subject = "BSYS"; break;
            case "2": subject = "DigiCom"; break;
            case "3": subject = "TEAM"; break;
            case "4":
                System.out.print("  Enter custom subject name: ");
                subject = scanner.nextLine().trim();
                if (subject.isEmpty()) subject = "CUSTOM";
                break;
            default: subject = "CUSTOM";
        }
        
        // Get topic
        System.out.print("\n  Topic (e.g., 'Processes', 'VLANs'): ");
        String topic = scanner.nextLine().trim();
        if (topic.isEmpty()) topic = "General";
        
        // Get question (front of card)
        System.out.println("\n  " + BOLD + "Question/Front of card:" + RESET);
        System.out.println("  (Press Enter twice when done)");
        String front = readMultilineInput();
        
        if (front.isEmpty()) {
            System.out.println(RED + "  Card cancelled - no question entered." + RESET);
            pause(1500);
            return;
        }
        
        // Get answer (back of card)
        System.out.println("\n  " + BOLD + "Answer/Back of card:" + RESET);
        System.out.println("  (Press Enter twice when done, use • for bullet points)");
        String back = readMultilineInput();
        
        if (back.isEmpty()) {
            System.out.println(RED + "  Card cancelled - no answer entered." + RESET);
            pause(1500);
            return;
        }
        
        // Get difficulty
        System.out.print("\n  Difficulty [1=Easy, 2=Medium, 3=Hard]: ");
        int difficulty;
        try {
            difficulty = Integer.parseInt(scanner.nextLine().trim());
            difficulty = Math.max(1, Math.min(3, difficulty));
        } catch (Exception e) {
            difficulty = 2;
        }
        
        // Create and add the flashcard
        Flashcard newCard = new Flashcard(subject, topic, front, back, difficulty);
        customFlashcards.add(newCard);
        flashcards.add(newCard);
        
        // Preview
        clearScreen();
        System.out.println("\n" + GREEN + "═══════════════════════════════════════════════════════════════" + RESET);
        System.out.println(BOLD + "                   ✓ FLASHCARD ADDED!" + RESET);
        System.out.println(GREEN + "═══════════════════════════════════════════════════════════════" + RESET);
        System.out.println("\n  " + BOLD + "Subject:" + RESET + " " + subject);
        System.out.println("  " + BOLD + "Topic:" + RESET + " " + topic);
        System.out.println("  " + BOLD + "Difficulty:" + RESET + " " + "★".repeat(difficulty) + "☆".repeat(3-difficulty));
        System.out.println("\n  " + CYAN + "Q: " + RESET + front.replace("\n", "\n     "));
        System.out.println("\n  " + GREEN + "A: " + RESET + back.replace("\n", "\n     "));
        
        System.out.println("\n  " + YELLOW + "Add another? [y/N]" + RESET);
        if (scanner.nextLine().trim().toLowerCase().equals("y")) {
            addFlashcard();
        }
    }
    
    private static void addQuestion() {
        clearScreen();
        System.out.println("\n" + GREEN + "═══════════════════════════════════════════════════════════════" + RESET);
        System.out.println(BOLD + "                   ❓ ADD QUIZ QUESTION" + RESET);
        System.out.println(GREEN + "═══════════════════════════════════════════════════════════════" + RESET);
        
        // Select subject
        System.out.println("\n  Select Subject:");
        System.out.println("  " + BLUE + "[1]" + RESET + " BSYS");
        System.out.println("  " + GREEN + "[2]" + RESET + " DigiCom");
        System.out.println("  " + PURPLE + "[3]" + RESET + " TEAM");
        System.out.println("  " + CYAN + "[4]" + RESET + " CUSTOM");
        System.out.print("\n  Subject [1-4]: ");
        
        String subjectChoice = scanner.nextLine().trim();
        String subject;
        switch (subjectChoice) {
            case "1": subject = "BSYS"; break;
            case "2": subject = "DigiCom"; break;
            case "3": subject = "TEAM"; break;
            case "4":
                System.out.print("  Enter custom subject name: ");
                subject = scanner.nextLine().trim();
                if (subject.isEmpty()) subject = "CUSTOM";
                break;
            default: subject = "CUSTOM";
        }
        
        // Get question
        System.out.print("\n  " + BOLD + "Question:" + RESET + " ");
        String questionText = scanner.nextLine().trim();
        
        if (questionText.isEmpty()) {
            System.out.println(RED + "  Cancelled - no question entered." + RESET);
            pause(1500);
            return;
        }
        
        // Get options (A-D)
        String[] options = new String[4];
        System.out.println("\n  Enter 4 answer options:");
        for (int i = 0; i < 4; i++) {
            System.out.print("  " + YELLOW + "[" + (char)('A' + i) + "]" + RESET + " ");
            options[i] = scanner.nextLine().trim();
            if (options[i].isEmpty()) {
                options[i] = "Option " + (char)('A' + i);
            }
        }
        
        // Get correct answer
        System.out.print("\n  Correct answer [A/B/C/D]: ");
        String correctStr = scanner.nextLine().trim().toUpperCase();
        int correctIndex = 0;
        if (correctStr.length() == 1 && correctStr.charAt(0) >= 'A' && correctStr.charAt(0) <= 'D') {
            correctIndex = correctStr.charAt(0) - 'A';
        }
        
        // Get explanation
        System.out.print("\n  Explanation (optional): ");
        String explanation = scanner.nextLine().trim();
        if (explanation.isEmpty()) {
            explanation = "The correct answer is " + (char)('A' + correctIndex) + ": " + options[correctIndex];
        }
        
        // Create and add the question
        Question newQ = new Question(subject, questionText, options, correctIndex, explanation);
        customQuestions.add(newQ);
        questions.add(newQ);
        
        // Preview
        clearScreen();
        System.out.println("\n" + GREEN + "═══════════════════════════════════════════════════════════════" + RESET);
        System.out.println(BOLD + "                   ✓ QUESTION ADDED!" + RESET);
        System.out.println(GREEN + "═══════════════════════════════════════════════════════════════" + RESET);
        System.out.println("\n  " + BOLD + "Subject:" + RESET + " " + subject);
        System.out.println("\n  " + CYAN + "Q: " + RESET + questionText);
        for (int i = 0; i < 4; i++) {
            String marker = (i == correctIndex) ? GREEN + "✓ " : "  ";
            System.out.println("  " + marker + "[" + (char)('A' + i) + "] " + options[i] + RESET);
        }
        System.out.println("\n  " + YELLOW + "Explanation: " + RESET + explanation);
        
        System.out.println("\n  " + YELLOW + "Add another? [y/N]" + RESET);
        if (scanner.nextLine().trim().toLowerCase().equals("y")) {
            addQuestion();
        }
    }
    
    private static void viewCustomContent() {
        clearScreen();
        System.out.println("\n" + CYAN + "═══════════════════════════════════════════════════════════════" + RESET);
        System.out.println(BOLD + "                   📋 CUSTOM CONTENT" + RESET);
        System.out.println(CYAN + "═══════════════════════════════════════════════════════════════" + RESET);
        
        if (customFlashcards.isEmpty() && customQuestions.isEmpty()) {
            System.out.println("\n  " + YELLOW + "No custom content yet!" + RESET);
            System.out.println("  Use options 1 or 2 to add flashcards and questions.");
            System.out.println("\n  Press Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        // Show flashcards
        if (!customFlashcards.isEmpty()) {
            System.out.println("\n  " + BOLD + "FLASHCARDS (" + customFlashcards.size() + "):" + RESET);
            System.out.println("  ─────────────────────────────────────────");
            int idx = 1;
            for (Flashcard card : customFlashcards) {
                String color = getSubjectColor(card.subject);
                String preview = card.front.length() > 40 ? card.front.substring(0, 40) + "..." : card.front;
                preview = preview.replace("\n", " ");
                System.out.println("  " + YELLOW + idx + "." + RESET + " " + color + "[" + card.subject + "]" + RESET + " " + card.topic + ": " + preview);
                idx++;
            }
        }
        
        // Show questions
        if (!customQuestions.isEmpty()) {
            System.out.println("\n  " + BOLD + "QUESTIONS (" + customQuestions.size() + "):" + RESET);
            System.out.println("  ─────────────────────────────────────────");
            int idx = 1;
            for (Question q : customQuestions) {
                String color = getSubjectColor(q.subject);
                String preview = q.question.length() > 40 ? q.question.substring(0, 40) + "..." : q.question;
                System.out.println("  " + YELLOW + idx + "." + RESET + " " + color + "[" + q.subject + "]" + RESET + " " + preview);
                idx++;
            }
        }
        
        System.out.println("\n  Press Enter to continue...");
        scanner.nextLine();
    }
    
    private static void editCustomContent() {
        if (customFlashcards.isEmpty() && customQuestions.isEmpty()) {
            System.out.println("\n  " + YELLOW + "No custom content to edit!" + RESET);
            pause(1500);
            return;
        }
        
        clearScreen();
        System.out.println("\n" + CYAN + "═══════════════════════════════════════════════════════════════" + RESET);
        System.out.println(BOLD + "                   ✏️  EDIT CONTENT" + RESET);
        System.out.println(CYAN + "═══════════════════════════════════════════════════════════════" + RESET);
        
        System.out.println("\n  " + YELLOW + "[1]" + RESET + " Edit Flashcard");
        System.out.println("  " + YELLOW + "[2]" + RESET + " Edit Question");
        System.out.println("  " + RED + "[0]" + RESET + " Cancel");
        System.out.print("\n  Choice: ");
        
        String choice = scanner.nextLine().trim();
        
        if (choice.equals("1") && !customFlashcards.isEmpty()) {
            // List flashcards
            System.out.println("\n  Select flashcard to edit:");
            for (int i = 0; i < customFlashcards.size(); i++) {
                Flashcard card = customFlashcards.get(i);
                String preview = card.front.length() > 35 ? card.front.substring(0, 35) + "..." : card.front;
                preview = preview.replace("\n", " ");
                System.out.println("  " + YELLOW + (i+1) + "." + RESET + " [" + card.subject + "] " + preview);
            }
            System.out.print("\n  Number (0 to cancel): ");
            
            try {
                int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
                if (idx >= 0 && idx < customFlashcards.size()) {
                    editFlashcard(idx);
                }
            } catch (Exception e) {}
            
        } else if (choice.equals("2") && !customQuestions.isEmpty()) {
            // List questions
            System.out.println("\n  Select question to edit:");
            for (int i = 0; i < customQuestions.size(); i++) {
                Question q = customQuestions.get(i);
                String preview = q.question.length() > 35 ? q.question.substring(0, 35) + "..." : q.question;
                System.out.println("  " + YELLOW + (i+1) + "." + RESET + " [" + q.subject + "] " + preview);
            }
            System.out.print("\n  Number (0 to cancel): ");
            
            try {
                int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
                if (idx >= 0 && idx < customQuestions.size()) {
                    editQuestion(idx);
                }
            } catch (Exception e) {}
        }
    }
    
    private static void editFlashcard(int index) {
        Flashcard card = customFlashcards.get(index);
        
        clearScreen();
        System.out.println("\n" + CYAN + "═══════════════════════════════════════════════════════════════" + RESET);
        System.out.println(BOLD + "                   ✏️  EDIT FLASHCARD" + RESET);
        System.out.println(CYAN + "═══════════════════════════════════════════════════════════════" + RESET);
        
        System.out.println("\n  Current content:");
        System.out.println("  " + BOLD + "Subject:" + RESET + " " + card.subject);
        System.out.println("  " + BOLD + "Topic:" + RESET + " " + card.topic);
        System.out.println("  " + BOLD + "Q:" + RESET + " " + card.front.replace("\n", "\n     "));
        System.out.println("  " + BOLD + "A:" + RESET + " " + card.back.replace("\n", "\n     "));
        
        System.out.println("\n  What to edit?");
        System.out.println("  " + YELLOW + "[1]" + RESET + " Topic");
        System.out.println("  " + YELLOW + "[2]" + RESET + " Question");
        System.out.println("  " + YELLOW + "[3]" + RESET + " Answer");
        System.out.println("  " + RED + "[0]" + RESET + " Cancel");
        System.out.print("\n  Choice: ");
        
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                System.out.print("  New topic: ");
                String newTopic = scanner.nextLine().trim();
                if (!newTopic.isEmpty()) {
                    card.topic = newTopic;
                    System.out.println(GREEN + "  ✓ Topic updated!" + RESET);
                }
                break;
            case "2":
                System.out.println("  New question (Enter twice when done):");
                String newFront = readMultilineInput();
                if (!newFront.isEmpty()) {
                    card.front = newFront;
                    System.out.println(GREEN + "  ✓ Question updated!" + RESET);
                }
                break;
            case "3":
                System.out.println("  New answer (Enter twice when done):");
                String newBack = readMultilineInput();
                if (!newBack.isEmpty()) {
                    card.back = newBack;
                    System.out.println(GREEN + "  ✓ Answer updated!" + RESET);
                }
                break;
        }
        
        pause(1500);
    }
    
    private static void editQuestion(int index) {
        Question q = customQuestions.get(index);
        
        clearScreen();
        System.out.println("\n" + CYAN + "═══════════════════════════════════════════════════════════════" + RESET);
        System.out.println(BOLD + "                   ✏️  EDIT QUESTION" + RESET);
        System.out.println(CYAN + "═══════════════════════════════════════════════════════════════" + RESET);
        
        System.out.println("\n  Current content:");
        System.out.println("  " + BOLD + "Q:" + RESET + " " + q.question);
        for (int i = 0; i < 4; i++) {
            String marker = (i == q.correctIndex) ? GREEN + "✓" : " ";
            System.out.println("  " + marker + " [" + (char)('A'+i) + "] " + q.options[i] + RESET);
        }
        
        System.out.println("\n  What to edit?");
        System.out.println("  " + YELLOW + "[1]" + RESET + " Question text");
        System.out.println("  " + YELLOW + "[2]" + RESET + " Answer options");
        System.out.println("  " + YELLOW + "[3]" + RESET + " Correct answer");
        System.out.println("  " + YELLOW + "[4]" + RESET + " Explanation");
        System.out.println("  " + RED + "[0]" + RESET + " Cancel");
        System.out.print("\n  Choice: ");
        
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                System.out.print("  New question: ");
                String newQ = scanner.nextLine().trim();
                if (!newQ.isEmpty()) {
                    q.question = newQ;
                    System.out.println(GREEN + "  ✓ Question updated!" + RESET);
                }
                break;
            case "2":
                System.out.println("  Enter new options (press Enter to keep current):");
                for (int i = 0; i < 4; i++) {
                    System.out.print("  [" + (char)('A'+i) + "] (" + q.options[i] + "): ");
                    String newOpt = scanner.nextLine().trim();
                    if (!newOpt.isEmpty()) {
                        q.options[i] = newOpt;
                    }
                }
                System.out.println(GREEN + "  ✓ Options updated!" + RESET);
                break;
            case "3":
                System.out.print("  Correct answer [A/B/C/D]: ");
                String correct = scanner.nextLine().trim().toUpperCase();
                if (correct.length() == 1 && correct.charAt(0) >= 'A' && correct.charAt(0) <= 'D') {
                    q.correctIndex = correct.charAt(0) - 'A';
                    System.out.println(GREEN + "  ✓ Correct answer updated!" + RESET);
                }
                break;
            case "4":
                System.out.print("  New explanation: ");
                String newExp = scanner.nextLine().trim();
                if (!newExp.isEmpty()) {
                    q.explanation = newExp;
                    System.out.println(GREEN + "  ✓ Explanation updated!" + RESET);
                }
                break;
        }
        
        pause(1500);
    }
    
    private static void deleteCustomContent() {
        if (customFlashcards.isEmpty() && customQuestions.isEmpty()) {
            System.out.println("\n  " + YELLOW + "No custom content to delete!" + RESET);
            pause(1500);
            return;
        }
        
        clearScreen();
        System.out.println("\n" + RED + "═══════════════════════════════════════════════════════════════" + RESET);
        System.out.println(BOLD + "                   🗑️  DELETE CONTENT" + RESET);
        System.out.println(RED + "═══════════════════════════════════════════════════════════════" + RESET);
        
        System.out.println("\n  " + YELLOW + "[1]" + RESET + " Delete a Flashcard");
        System.out.println("  " + YELLOW + "[2]" + RESET + " Delete a Question");
        System.out.println("  " + RED + "[3]" + RESET + " Delete ALL Custom Content");
        System.out.println("  " + CYAN + "[0]" + RESET + " Cancel");
        System.out.print("\n  Choice: ");
        
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                if (customFlashcards.isEmpty()) {
                    System.out.println(YELLOW + "  No flashcards to delete." + RESET);
                    pause(1500);
                    return;
                }
                System.out.println("\n  Select flashcard to delete:");
                for (int i = 0; i < customFlashcards.size(); i++) {
                    Flashcard card = customFlashcards.get(i);
                    String preview = card.front.length() > 35 ? card.front.substring(0, 35) + "..." : card.front;
                    preview = preview.replace("\n", " ");
                    System.out.println("  " + YELLOW + (i+1) + "." + RESET + " [" + card.subject + "] " + preview);
                }
                System.out.print("\n  Number (0 to cancel): ");
                try {
                    int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
                    if (idx >= 0 && idx < customFlashcards.size()) {
                        Flashcard toRemove = customFlashcards.remove(idx);
                        flashcards.remove(toRemove);
                        System.out.println(GREEN + "  ✓ Flashcard deleted!" + RESET);
                    }
                } catch (Exception e) {}
                break;
                
            case "2":
                if (customQuestions.isEmpty()) {
                    System.out.println(YELLOW + "  No questions to delete." + RESET);
                    pause(1500);
                    return;
                }
                System.out.println("\n  Select question to delete:");
                for (int i = 0; i < customQuestions.size(); i++) {
                    Question q = customQuestions.get(i);
                    String preview = q.question.length() > 35 ? q.question.substring(0, 35) + "..." : q.question;
                    System.out.println("  " + YELLOW + (i+1) + "." + RESET + " [" + q.subject + "] " + preview);
                }
                System.out.print("\n  Number (0 to cancel): ");
                try {
                    int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
                    if (idx >= 0 && idx < customQuestions.size()) {
                        Question toRemove = customQuestions.remove(idx);
                        questions.remove(toRemove);
                        System.out.println(GREEN + "  ✓ Question deleted!" + RESET);
                    }
                } catch (Exception e) {}
                break;
                
            case "3":
                System.out.print("\n  " + RED + "Delete ALL custom content? Type 'DELETE' to confirm: " + RESET);
                if (scanner.nextLine().trim().equals("DELETE")) {
                    flashcards.removeAll(customFlashcards);
                    questions.removeAll(customQuestions);
                    customFlashcards.clear();
                    customQuestions.clear();
                    System.out.println(GREEN + "  ✓ All custom content deleted!" + RESET);
                } else {
                    System.out.println(YELLOW + "  Cancelled." + RESET);
                }
                break;
        }
        
        pause(1500);
    }
    
    private static void exportCustomContent() {
        if (customFlashcards.isEmpty() && customQuestions.isEmpty()) {
            System.out.println("\n  " + YELLOW + "No custom content to export!" + RESET);
            pause(1500);
            return;
        }
        
        clearScreen();
        System.out.println("\n" + CYAN + "═══════════════════════════════════════════════════════════════" + RESET);
        System.out.println(BOLD + "                   📤 EXPORT CONTENT" + RESET);
        System.out.println(CYAN + "═══════════════════════════════════════════════════════════════" + RESET);
        
        System.out.print("\n  Filename (default: study_export.txt): ");
        String filename = scanner.nextLine().trim();
        if (filename.isEmpty()) filename = "study_export.txt";
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("# StudyApp Export");
            writer.println("# Generated: " + new java.util.Date());
            writer.println();
            
            // Export flashcards
            if (!customFlashcards.isEmpty()) {
                writer.println("## FLASHCARDS");
                writer.println();
                for (Flashcard card : customFlashcards) {
                    writer.println("---CARD---");
                    writer.println("SUBJECT: " + card.subject);
                    writer.println("TOPIC: " + card.topic);
                    writer.println("DIFFICULTY: " + card.difficulty);
                    writer.println("FRONT:");
                    writer.println(card.front);
                    writer.println("BACK:");
                    writer.println(card.back);
                    writer.println();
                }
            }
            
            // Export questions
            if (!customQuestions.isEmpty()) {
                writer.println("## QUESTIONS");
                writer.println();
                for (Question q : customQuestions) {
                    writer.println("---QUESTION---");
                    writer.println("SUBJECT: " + q.subject);
                    writer.println("Q: " + q.question);
                    writer.println("A: " + q.options[0]);
                    writer.println("B: " + q.options[1]);
                    writer.println("C: " + q.options[2]);
                    writer.println("D: " + q.options[3]);
                    writer.println("CORRECT: " + (char)('A' + q.correctIndex));
                    writer.println("EXPLANATION: " + q.explanation);
                    writer.println();
                }
            }
            
            System.out.println(GREEN + "\n  ✓ Exported to: " + filename + RESET);
            System.out.println("  " + customFlashcards.size() + " flashcards, " + customQuestions.size() + " questions");
            
        } catch (Exception e) {
            System.out.println(RED + "\n  ✗ Export failed: " + e.getMessage() + RESET);
        }
        
        System.out.println("\n  Press Enter to continue...");
        scanner.nextLine();
    }
    
    private static void importFromFile() {
        clearScreen();
        System.out.println("\n" + CYAN + "═══════════════════════════════════════════════════════════════" + RESET);
        System.out.println(BOLD + "                   📥 IMPORT FROM FILE" + RESET);
        System.out.println(CYAN + "═══════════════════════════════════════════════════════════════" + RESET);
        
        System.out.println("\n  Supported formats:");
        System.out.println("  • StudyApp export files (.txt)");
        System.out.println("  • Simple format: question;answer (one per line)");
        
        System.out.print("\n  Filename: ");
        String filename = scanner.nextLine().trim();
        
        if (filename.isEmpty()) {
            System.out.println(RED + "  Cancelled." + RESET);
            pause(1500);
            return;
        }
        
        File file = new File(filename);
        if (!file.exists()) {
            System.out.println(RED + "  File not found: " + filename + RESET);
            pause(1500);
            return;
        }
        
        int cardsImported = 0;
        int questionsImported = 0;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            String currentSection = "";
            
            // Variables for parsing
            String subject = "CUSTOM", topic = "Imported", front = "", back = "";
            int difficulty = 2;
            String question = "";
            String[] options = new String[4];
            int correctIndex = 0;
            String explanation = "";
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                // Check for StudyApp format markers
                if (line.equals("---CARD---")) {
                    currentSection = "CARD";
                    front = ""; back = "";
                    continue;
                }
                if (line.equals("---QUESTION---")) {
                    currentSection = "QUESTION";
                    question = "";
                    options = new String[]{"", "", "", ""};
                    continue;
                }
                
                if (currentSection.equals("CARD")) {
                    if (line.startsWith("SUBJECT: ")) subject = line.substring(9);
                    else if (line.startsWith("TOPIC: ")) topic = line.substring(7);
                    else if (line.startsWith("DIFFICULTY: ")) difficulty = Integer.parseInt(line.substring(12));
                    else if (line.startsWith("FRONT:")) { /* next lines are front */ }
                    else if (line.startsWith("BACK:")) {
                        // Save front, now collecting back
                    }
                    else if (line.isEmpty() && !front.isEmpty() && !back.isEmpty()) {
                        // End of card
                        Flashcard card = new Flashcard(subject, topic, front.trim(), back.trim(), difficulty);
                        customFlashcards.add(card);
                        flashcards.add(card);
                        cardsImported++;
                        front = ""; back = "";
                        currentSection = "";
                    }
                    // Simple format: question;answer
                } else if (line.contains(";") && currentSection.isEmpty()) {
                    String[] parts = line.split(";", 2);
                    if (parts.length == 2) {
                        Flashcard card = new Flashcard("CUSTOM", "Imported", parts[0].trim(), parts[1].trim(), 2);
                        customFlashcards.add(card);
                        flashcards.add(card);
                        cardsImported++;
                    }
                }
                
                if (currentSection.equals("QUESTION")) {
                    if (line.startsWith("SUBJECT: ")) subject = line.substring(9);
                    else if (line.startsWith("Q: ")) question = line.substring(3);
                    else if (line.startsWith("A: ")) options[0] = line.substring(3);
                    else if (line.startsWith("B: ")) options[1] = line.substring(3);
                    else if (line.startsWith("C: ")) options[2] = line.substring(3);
                    else if (line.startsWith("D: ")) options[3] = line.substring(3);
                    else if (line.startsWith("CORRECT: ")) correctIndex = line.charAt(9) - 'A';
                    else if (line.startsWith("EXPLANATION: ")) explanation = line.substring(13);
                    else if (line.isEmpty() && !question.isEmpty()) {
                        Question q = new Question(subject, question, options, correctIndex, explanation);
                        customQuestions.add(q);
                        questions.add(q);
                        questionsImported++;
                        currentSection = "";
                    }
                }
            }
            
            System.out.println(GREEN + "\n  ✓ Import complete!" + RESET);
            System.out.println("  Flashcards imported: " + cardsImported);
            System.out.println("  Questions imported: " + questionsImported);
            
        } catch (Exception e) {
            System.out.println(RED + "\n  ✗ Import failed: " + e.getMessage() + RESET);
        }
        
        System.out.println("\n  Press Enter to continue...");
        scanner.nextLine();
    }
    
    private static void bulkAddMode() {
        clearScreen();
        System.out.println("\n" + YELLOW + "═══════════════════════════════════════════════════════════════" + RESET);
        System.out.println(BOLD + "                   ⚡ BULK ADD MODE" + RESET);
        System.out.println(YELLOW + "═══════════════════════════════════════════════════════════════" + RESET);
        
        System.out.println("\n  Quick format: Enter cards as 'question;answer'");
        System.out.println("  One per line. Empty line to finish.\n");
        
        // Select subject
        System.out.println("  Subject:");
        System.out.println("  [1] BSYS  [2] DigiCom  [3] TEAM  [4] CUSTOM");
        System.out.print("  Choice: ");
        String subjectChoice = scanner.nextLine().trim();
        String subject;
        switch (subjectChoice) {
            case "1": subject = "BSYS"; break;
            case "2": subject = "DigiCom"; break;
            case "3": subject = "TEAM"; break;
            default: 
                System.out.print("  Custom subject name: ");
                subject = scanner.nextLine().trim();
                if (subject.isEmpty()) subject = "CUSTOM";
        }
        
        System.out.print("  Topic: ");
        String topic = scanner.nextLine().trim();
        if (topic.isEmpty()) topic = "General";
        
        System.out.println("\n  " + GREEN + "Start entering cards (question;answer):" + RESET);
        System.out.println("  ─────────────────────────────────────────");
        
        int count = 0;
        while (true) {
            System.out.print("  " + YELLOW + (count + 1) + ">" + RESET + " ");
            String line = scanner.nextLine().trim();
            
            if (line.isEmpty()) break;
            
            String[] parts = line.split(";", 2);
            if (parts.length == 2) {
                Flashcard card = new Flashcard(subject, topic, parts[0].trim(), parts[1].trim(), 2);
                customFlashcards.add(card);
                flashcards.add(card);
                count++;
                System.out.println("     " + GREEN + "✓ Added" + RESET);
            } else {
                System.out.println("     " + RED + "Invalid format. Use: question;answer" + RESET);
            }
        }
        
        System.out.println("\n  " + GREEN + "✓ Added " + count + " flashcards!" + RESET);
        pause(1500);
    }
    
    private static String readMultilineInput() {
        StringBuilder sb = new StringBuilder();
        String lastLine = null;
        
        while (true) {
            String line = scanner.nextLine();
            if (line.isEmpty() && (lastLine == null || lastLine.isEmpty())) {
                break;
            }
            if (sb.length() > 0) sb.append("\n");
            sb.append(line);
            lastLine = line;
        }
        
        return sb.toString().trim();
    }
    
    private static void saveCustomContent() {
        // Save flashcards
        try (PrintWriter writer = new PrintWriter(new FileWriter(CUSTOM_CARDS_FILE))) {
            for (Flashcard card : customFlashcards) {
                // Use ||| as delimiter since content might have commas/newlines
                String front = card.front.replace("\n", "\\n").replace("|||", "\\|\\|\\|");
                String back = card.back.replace("\n", "\\n").replace("|||", "\\|\\|\\|");
                writer.println(card.subject + "|||" + card.topic + "|||" + front + "|||" + back + "|||" + card.difficulty);
            }
        } catch (Exception e) {
            // Silent fail
        }
        
        // Save questions
        try (PrintWriter writer = new PrintWriter(new FileWriter(CUSTOM_QUESTIONS_FILE))) {
            for (Question q : customQuestions) {
                String exp = q.explanation.replace("\n", "\\n").replace("|||", "\\|\\|\\|");
                writer.println(q.subject + "|||" + q.question + "|||" + 
                    q.options[0] + "|||" + q.options[1] + "|||" + q.options[2] + "|||" + q.options[3] + "|||" +
                    q.correctIndex + "|||" + exp);
            }
        } catch (Exception e) {
            // Silent fail
        }
    }
    
    private static void loadCustomContent() {
        // Load flashcards
        try (BufferedReader reader = new BufferedReader(new FileReader(CUSTOM_CARDS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|\\|\\|");
                if (parts.length >= 5) {
                    String front = parts[2].replace("\\n", "\n").replace("\\|\\|\\|", "|||");
                    String back = parts[3].replace("\\n", "\n").replace("\\|\\|\\|", "|||");
                    Flashcard card = new Flashcard(parts[0], parts[1], front, back, Integer.parseInt(parts[4]));
                    customFlashcards.add(card);
                    flashcards.add(card);
                }
            }
        } catch (Exception e) {
            // File doesn't exist yet
        }
        
        // Load questions
        try (BufferedReader reader = new BufferedReader(new FileReader(CUSTOM_QUESTIONS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|\\|\\|");
                if (parts.length >= 8) {
                    String[] options = {parts[2], parts[3], parts[4], parts[5]};
                    String exp = parts[7].replace("\\n", "\n").replace("\\|\\|\\|", "|||");
                    Question q = new Question(parts[0], parts[1], options, Integer.parseInt(parts[6]), exp);
                    customQuestions.add(q);
                    questions.add(q);
                }
            }
        } catch (Exception e) {
            // File doesn't exist yet
        }
    }
    
    // ==================== HELPER METHODS ====================
    
    private static List<Flashcard> filterCards(String subject) {
        List<Flashcard> result = new ArrayList<>();
        for (Flashcard card : flashcards) {
            if (card.subject.equals(subject)) {
                result.add(card);
            }
        }
        return result;
    }
    
    private static List<Question> filterQuestions(String subject) {
        List<Question> result = new ArrayList<>();
        for (Question q : questions) {
            if (q.subject.equals(subject)) {
                result.add(q);
            }
        }
        return result;
    }
    
    private static int countCards(String subject) {
        int count = 0;
        for (Flashcard card : flashcards) {
            if (card.subject.equals(subject)) count++;
        }
        return count;
    }
    
    private static String getSubjectColor(String subject) {
        switch (subject) {
            case "BSYS": return BLUE;
            case "DigiCom": return GREEN;
            case "TEAM": return PURPLE;
            default: return RESET;
        }
    }
    
    private static void updateProgress(String key, boolean correct) {
        correctAnswers.put(key, correctAnswers.getOrDefault(key, 0) + (correct ? 1 : 0));
        totalAttempts.put(key, totalAttempts.getOrDefault(key, 0) + 1);
    }
    
    private static String generateProgressBar(int percent, int width) {
        int filled = (percent * width) / 100;
        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < width; i++) {
            if (i < filled) bar.append(GREEN + "█" + RESET);
            else bar.append("░");
        }
        bar.append("]");
        return bar.toString();
    }
    
    private static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
    
    private static void pause(int ms) {
        try { Thread.sleep(ms); } catch (Exception e) {}
    }
    
    private static void saveProgress() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("study_progress.dat"))) {
            for (String key : correctAnswers.keySet()) {
                writer.println(key + "," + correctAnswers.get(key) + "," + totalAttempts.get(key));
            }
        } catch (Exception e) {
            // Silently fail
        }
    }
    
    private static void loadProgress() {
        try (BufferedReader reader = new BufferedReader(new FileReader("study_progress.dat"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    correctAnswers.put(parts[0], Integer.parseInt(parts[1]));
                    totalAttempts.put(parts[0], Integer.parseInt(parts[2]));
                }
            }
        } catch (Exception e) {
            // File doesn't exist yet, start fresh
        }
    }
    
    private static void printGoodbye() {
        clearScreen();
        System.out.println(CYAN + "\n\n");
        System.out.println("  ╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("  ║                                                               ║");
        System.out.println("  ║      🎓 Good luck with your exams! Study hard! 🎓            ║");
        System.out.println("  ║                                                               ║");
        System.out.println("  ║              Viel Erfolg bei der Prüfung!                     ║");
        System.out.println("  ║                                                               ║");
        System.out.println("  ╚═══════════════════════════════════════════════════════════════╝" + RESET);
        System.out.println("\n\n");
    }
}
