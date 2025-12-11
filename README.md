# StudyApp - Interactive Exam Preparation Tool

A comprehensive, terminal-based study application for exam preparation covering BSYS (Operating Systems), DigiCom (Digital Communications), and TEAM (Teamarbeit/Teamwork).

## Features

- 🎴 **Flashcard Mode** - Study with flashcards and spaced repetition
- ❓ **Quiz Mode** - Test your knowledge with multiple choice questions
- 📋 **Topic Browser** - Browse and review content by subject and topic
- 📊 **Progress Tracking** - Monitor your learning progress across subjects
- ⚡ **Quick Review** - Focus on cards that need more practice
- 🎓 **Exam Simulation** - Timed practice exams with mixed questions
- ✏️ **Custom Content** - Add, edit, export, and import your own study materials
- 💾 **Multiple Import Formats** - Support for CSV, TSV, Anki, and custom formats

## System Requirements

- **Java JDK 8 or higher** (must include `javac` compiler)
- Terminal/Command Prompt with UTF-8 support for best display
- Works on: Linux, macOS, Windows

## Quick Start

### Linux / macOS

```bash
# Make the script executable (first time only)
chmod +x run.sh

# Run the application
./run.sh
```

### Windows

```batch
# Simply double-click run.bat or run from command prompt:
run.bat
```

### Manual Compilation

```bash
# Compile
javac StudyApp.java

# Run
java StudyApp
```

## Application Structure

```
Study-App-DC-Team-OS/
├── StudyApp.java               # Main application
├── run.sh                      # Launch script for Linux/Mac
├── run.bat                     # Launch script for Windows
├── README.md                   # This file
├── IMPORT_GUIDE.md            # Detailed import format guide
├── custom_flashcards.dat      # Your custom flashcards (auto-created)
├── custom_questions.dat       # Your custom quiz questions (auto-created)
├── study_progress.dat         # Your progress data (auto-created)
└── examples/                  # Example import files
    ├── example_flashcards.csv
    ├── example_questions.csv
    └── example_anki.txt
```

## Content Coverage

### BSYS (Operating Systems)
- Processes & Threads
- Inter-Process Communication (IPC)
- CPU Scheduling Algorithms
- Memory Management & Virtual Memory
- Page Replacement Algorithms
- File Systems
- RAID Configurations

### DigiCom (Digital Communications)
- OSI & TCP/IP Layer Models
- Network Devices (Switches, Routers)
- VLANs & 802.1Q Tagging
- Spanning Tree Protocol (STP/RSTP)
- Routing Protocols (RIPv2, OSPF)
- Network Configuration

### TEAM (Teamwork)
- Team Definitions & Development Models
- Tuckman's 5 Phases
- Belbin Team Roles
- Leadership Styles
- Conflict Management
- Agile & Scrum
- Feedback Techniques
- DEIB (Diversity, Equity, Inclusion, Belonging)

## Usage Guide

### Main Menu Options

1. **Flashcard Mode** - Study cards by subject with self-assessment
2. **Quiz Mode** - Take multiple-choice quizzes with immediate feedback
3. **Topic Browser** - Browse all cards organized by topic
4. **View Progress** - See your performance statistics by subject
5. **Quick Review** - Review cards you've struggled with
6. **Exam Simulation** - Timed practice exam (10/20/30 questions)
7. **Input Mode** - Manage your custom content
0. **Exit** - Save progress and close

### Input Mode Features

Within Input Mode (option 7), you can:

1. **Add Flashcard** - Create a new flashcard manually
2. **Add Quiz Question** - Create a new multiple-choice question
3. **View Custom Content** - Browse your custom cards and questions
4. **Edit Custom Content** - Modify existing custom items
5. **Delete Custom Content** - Remove custom items
6. **Export Custom Content** - Save your content to a file
7. **Import from File** - Load content from various formats
8. **Quick Add (Bulk Mode)** - Rapidly add multiple cards using `question;answer` format

## Importing Content

### Supported Import Formats

1. **Simple Format** (CSV-like)
2. **StudyApp Export Format** (native format)
3. **Anki Format** (tab-separated)
4. **Custom CSV/TSV**

### Quick Import Examples

#### 1. Simple Format (question;answer)

Create a text file with one card per line:

```
What is a Process?;An instance of an executing program
What is a Thread?;A lightweight process that shares address space
What is Deadlock?;Circular wait where processes hold resources and wait for others
```

#### 2. StudyApp Export Format

```
---CARD---
SUBJECT: BSYS
TOPIC: Processes
DIFFICULTY: 2
FRONT:
What is a zombie process?
BACK:
A process that has completed execution but still has an entry in the process table
```

#### 3. Anki Format (TSV)

Create a file with tab-separated values:

```
What is TCP?	Transmission Control Protocol - reliable, connection-oriented transport	BSYS
What is UDP?	User Datagram Protocol - unreliable, connectionless transport	DigiCom
```

**Format:** `Front[TAB]Back[TAB]Subject`

#### 4. CSV Format

```csv
Subject,Topic,Question,Answer,Difficulty
BSYS,Memory,What is paging?,Division of memory into fixed-size blocks,2
DigiCom,VLAN,What is 802.1Q?,VLAN tagging standard,2
TEAM,Agile,What is Scrum?,Agile framework with sprints,1
```

### Importing via Application

1. Launch StudyApp
2. Go to **Input Mode** (option 7)
3. Select **Import from File** (option 7)
4. Enter the filename (e.g., `my_cards.txt`, `anki_export.txt`)
5. The app will detect the format and import

### Importing via Manual File Editing

You can directly edit the data files:

- `custom_flashcards.dat` - Format: `Subject|||Topic|||Front|||Back|||Difficulty`
- `custom_questions.dat` - Format: `Subject|||Question|||OptionA|||OptionB|||OptionC|||OptionD|||CorrectIndex|||Explanation`

Use `|||` as the delimiter and `\n` for newlines within content.

## Creating Import Files

### Example: CSV Flashcards

Create `my_flashcards.csv`:

```csv
Subject,Topic,Question,Answer,Difficulty
BSYS,Processes,What is fork()?,System call that creates a new process,2
BSYS,Threads,Difference between user and kernel threads?,User threads managed by user library; kernel threads by OS,3
DigiCom,Routing,What is OSPF?,Open Shortest Path First - link-state routing protocol,2
```

### Example: Bulk Simple Format

Create `bulk_cards.txt`:

```
Process vs Program;Program is passive code; Process is executing program with state
Virtual Memory Benefit;Allows programs larger than physical RAM to run
VLAN Purpose;Logical network segmentation for security and performance
STP Purpose;Prevents network loops in redundant switched networks
```

Import in **Bulk Add Mode** (Input Mode → option 8)

### Example: Questions Format

Create `quiz_questions.txt`:

```
---QUESTION---
SUBJECT: BSYS
Q: Which scheduling algorithm is optimal for minimizing average wait time?
A: FCFS
B: SJF
C: Round Robin
D: Priority
CORRECT: B
EXPLANATION: SJF (Shortest Job First) provides optimal minimum average waiting time
```

## Anki Import Guide

### Exporting from Anki

1. Open Anki
2. Select your deck
3. Go to **File → Export**
4. Choose format: **Notes in Plain Text (.txt)**
5. Enable **Include HTML and media references**
6. Export to a file (e.g., `anki_export.txt`)

### Anki File Format

Anki exports use tab-separated values:

```
Front text[TAB]Back text[TAB]Tags (optional)
```

Example:

```
What is the OSI model?	7-layer network model: Physical, Data Link, Network, Transport, Session, Presentation, Application	networking, osi
TCP vs UDP	TCP: reliable, connection-oriented; UDP: unreliable, fast, connectionless	protocols, transport
```

### Converting Anki Export for StudyApp

The app automatically detects Anki format (tab-separated). You can also add a subject prefix:

```
BSYS: Process States	Running, Ready, Blocked, Terminated	os, processes
DigiCom: VLAN 802.1Q	TPID=0x8100, 12-bit VLAN ID (0-4095)	networking, vlan
```

The app will extract the subject prefix if present.

## Advanced Usage

### Filtering and Search

- In **Flashcard Mode** and **Quiz Mode**, you can filter by subject
- **Topic Browser** organizes content hierarchically
- **Quick Review** automatically selects cards you've struggled with

### Progress Tracking

Your progress is automatically saved:
- **Per-topic accuracy** for flashcards
- **Per-subject performance** for quizzes
- **Overall statistics** across all subjects

Progress is saved to `study_progress.dat` and persists between sessions.

### Custom Subjects

You can create your own subjects:
1. Go to **Input Mode**
2. Select **Add Flashcard** or **Add Quiz Question**
3. Choose option 4 (CUSTOM)
4. Enter your custom subject name

## Tips for Effective Studying

1. **Spaced Repetition**: Review cards multiple times over several days
2. **Active Recall**: Try to answer before revealing the solution
3. **Mix Subjects**: Study different topics in one session
4. **Use Quick Review**: Focus on weak areas identified by the app
5. **Exam Simulation**: Practice under timed conditions before the real exam
6. **Custom Content**: Add your own notes, lecture materials, and practice questions

## Troubleshooting

### "Java not found" error

- Install Java JDK 8 or higher
- Make sure `java` and `javac` are in your system PATH
- Verify: `java -version` and `javac -version`

### Unicode characters not displaying correctly

- Ensure your terminal supports UTF-8 encoding
- On Windows: Use Windows Terminal or enable UTF-8 in Command Prompt
  ```
  chcp 65001
  ```

### Script won't run on Linux/Mac

```bash
chmod +x run.sh
./run.sh
```

### Import fails

- Check file format matches one of the supported formats
- Ensure special characters are properly escaped
- Use UTF-8 encoding for the import file
- Check IMPORT_GUIDE.md for detailed format specifications

### Progress not saving

- Ensure the application has write permissions in its directory
- Check that you're exiting properly (option 0) to trigger save
- Look for `study_progress.dat`, `custom_flashcards.dat`, `custom_questions.dat`

## Data Files

The application creates and maintains these files:

- **custom_flashcards.dat** - Your custom flashcards
- **custom_questions.dat** - Your custom quiz questions
- **study_progress.dat** - Your learning progress and statistics

**Backup**: Copy these files to preserve your custom content and progress!

## Contributing Content

Want to share your study materials?

1. Create your content in StudyApp
2. Export via **Input Mode → Export Custom Content**
3. Share the exported file
4. Others can import via **Input Mode → Import from File**

## Version Information

- **Author**: Claude AI
- **Version**: 1.1 (Extended Content)
- **File**: `StudyApp.java` - Complete version with extended content

## License

This educational tool is provided as-is for study purposes.

## Support

For issues, questions, or contributions, check the git repository or create an issue.

---

**Good luck with your exams! Viel Erfolg bei der Prüfung! 🎓**
