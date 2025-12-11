# 📚 StudyApp - Exam Preparation Tool

Interactive terminal-based study application for your three courses:
- **BSYS** - Operating Systems (Prozesse, Threads, Memory, File Systems, RAID)
- **DigiCom** - Digital Communications (OSI/TCP-IP, VLANs, STP, Routing)
- **TEAM** - Teamarbeit (Tuckman, Belbin, Führung, Agile, DEIB)

## 🚀 Installation

### Option 1: Automatic Install (Recommended)
```bash
# Download all files to a folder, then:
chmod +x install.sh
./install.sh
```

This will:
- Compile the app
- Install to `~/StudyApp/`
- Create `study` command
- Add desktop shortcut (macOS)

After install, just type `study` to run!

### Option 2: Manual Quick Start
```bash
# Compile
javac StudyApp.java

# Run
java StudyApp
```

### Option 3: Double-Click (macOS)
1. Put all files in a folder
2. Double-click `StudyApp.command`

## 📁 Files Included

| File | Purpose |
|------|---------|
| `StudyApp.java` | Main application (compile this) |
| `install.sh` | Automated installer |
| `study.sh` | Portable launcher script |
| `uninstall.sh` | Clean removal |
| `StudyApp.command` | macOS double-click launcher |

## ✨ Features

### 1. 🎴 Flashcard Mode
- Study flashcards by subject or all mixed
- Self-assessment (did you know it?)
- Navigate with [n]ext, [p]revious, [q]uit

### 2. ❓ Quiz Mode
- Multiple choice questions
- Immediate feedback with explanations
- Choose number of questions

### 3. 📋 Topic Browser
- Browse all content organized by topic
- Quick reference while studying
- View all cards in a topic

### 4. 📊 Progress Tracking
- Tracks your correct/incorrect answers
- Progress bars per subject
- Saved between sessions (study_progress.dat)

### 5. ⚡ Quick Review
- Focuses on cards you got wrong
- Spaced repetition approach
- 10 random weak cards

### 6. 🎓 Exam Simulation
- Timed exam environment
- No hints during exam
- Austrian grading (1-5)
- Review wrong answers after

### 7. ✏️ Input Mode (NEW!)
Add your own flashcards and questions!

**Sub-features:**
- **Add Flashcard**: Create cards with subject, topic, Q&A, difficulty
- **Add Question**: Create multiple choice with 4 options + explanation
- **View Custom**: See all your added content
- **Edit**: Modify existing custom cards/questions
- **Delete**: Remove individual items or clear all
- **Export**: Save to shareable text file
- **Import**: Load from file (supports simple `question;answer` format)
- **Bulk Add**: Rapid entry mode for many cards at once

**Quick Add Format:**
```
What is a process?;An instance of an executing program
What does TLB stand for?;Translation Lookaside Buffer
```

**Custom content is saved automatically** to:
- `custom_flashcards.dat`
- `custom_questions.dat`

## 📖 Content Included

### BSYS (42 flashcards, 12 questions)
- Process model, states, creation
- Threads (user-level vs kernel-level)
- IPC (pipes, semaphores, mutexes)
- Scheduling (FCFS, SJF, SRTN, Round-Robin)
- Memory (paging, TLB, page faults)
- Page replacement (FIFO, LRU, Clock, NRU)
- File systems (i-nodes, links, allocation)
- RAID levels (0, 1, 4, 5, 6)

### DigiCom (24 flashcards, 12 questions)
- OSI 7-layer model with mnemonics
- TCP/IP 4-layer model
- PDU names per layer
- VLANs and 802.1Q tagging
- STP algorithm, ports, states, timers
- RSTP improvements
- RIPv2 and OSPF configuration

### TEAM (24 flashcards, 12 questions)
- Team definitions (West, Thompson, Mohrman)
- T-shaped qualification
- Tuckman 5 phases
- Belbin 9 roles (3 categories)
- Führungsstile
- Generationen X/Y/Z
- Konfliktmanagement
- Feedback rules
- Agile/Scrum basics
- DEIB concepts

## 🎨 Terminal Colors

The app uses ANSI colors for better readability:
- 🔵 Blue = BSYS
- 🟢 Green = DigiCom  
- 🟣 Purple = TEAM
- 🟡 Yellow = Menu options
- 🔴 Red = Errors/warnings

## 💾 Progress & Data Saving

Your progress is automatically saved to `study_progress.dat` when you exit.
Delete this file to reset your progress.

**Data files created by StudyApp:**

| File | Purpose |
|------|---------|
| `study_progress.dat` | Your quiz/flashcard performance stats |
| `custom_flashcards.dat` | Your custom flashcards |
| `custom_questions.dat` | Your custom quiz questions |

All files are saved in the same directory as the app.

## 📝 Tips for Effective Study

1. **Start with Topic Browser** - Get an overview of all content
2. **Use Flashcard Mode** - Active recall is powerful
3. **Take Quizzes** - Test yourself regularly
4. **Quick Review daily** - Focus on weak areas
5. **Exam Simulation** - Practice under pressure before the real exam

---

Viel Erfolg bei der Prüfung! 🎓
