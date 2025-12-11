# StudyApp Import Guide

Complete guide for importing flashcards and questions into StudyApp from various formats.

## Table of Contents

1. [Supported Formats](#supported-formats)
2. [Format Specifications](#format-specifications)
3. [Anki Import Guide](#anki-import-guide)
4. [Import Methods](#import-methods)
5. [Examples](#examples)
6. [Tips & Best Practices](#tips--best-practices)
7. [Troubleshooting](#troubleshooting)

---

## Supported Formats

StudyApp supports importing content in the following formats:

| Format | File Extension | Use Case |
|--------|---------------|----------|
| **Simple Format** | `.txt` | Quick bulk import, one-liners |
| **StudyApp Export** | `.txt` | Native format with full metadata |
| **Anki Format** | `.txt` | Import from Anki flashcard app |
| **CSV Format** | `.csv` | Spreadsheet-based import |
| **TSV Format** | `.txt`, `.tsv` | Tab-separated values |

---

## Format Specifications

### 1. Simple Format

**Best for:** Quick imports, simple flashcards

**Syntax:**
```
question;answer
```

**Example:**
```
What is a process?;An instance of a program in execution
What is a thread?;A lightweight execution unit within a process
What is deadlock?;Circular wait where processes block each other
```

**Features:**
- One card per line
- Use semicolon (`;`) as separator
- Automatically assigned to "CUSTOM" subject and "Imported" topic
- Medium difficulty (2) by default

**Limitations:**
- No subject/topic customization per card
- No difficulty levels
- No multi-line content

---

### 2. StudyApp Export Format

**Best for:** Full-featured imports, complex content, preserving structure

**Flashcard Syntax:**
```
---CARD---
SUBJECT: SubjectName
TOPIC: TopicName
DIFFICULTY: 1-3
FRONT:
Question text (can be multi-line)
BACK:
Answer text (can be multi-line)
```

**Question Syntax:**
```
---QUESTION---
SUBJECT: SubjectName
Q: Question text
A: Option A
B: Option B
C: Option C
D: Option D
CORRECT: A-D
EXPLANATION: Explanation text
```

**Example:**
```
---CARD---
SUBJECT: BSYS
TOPIC: Scheduling
DIFFICULTY: 3
FRONT:
Compare and contrast preemptive vs non-preemptive scheduling
BACK:
Preemptive:
• OS can interrupt running process
• Better for time-sharing systems
• Higher overhead

Non-preemptive:
• Process runs until completion/block
• Simpler implementation
• Risk of starvation

---QUESTION---
SUBJECT: DigiCom
Q: Which protocol prevents network loops?
A: OSPF
B: STP
C: RIP
D: BGP
CORRECT: B
EXPLANATION: Spanning Tree Protocol (STP) prevents loops by creating a loop-free logical topology
```

**Features:**
- Full metadata support
- Multi-line content
- Difficulty levels (1=Easy, 2=Medium, 3=Hard)
- Both flashcards and quiz questions
- Preserves formatting

---

### 3. Anki Format

**Best for:** Importing from Anki flashcard application

**Syntax:**
```
Front[TAB]Back[TAB]Tags
```

**Note:** Use actual TAB characters (not spaces)

**Example:**
```
What is TCP?	Transmission Control Protocol - reliable connection-oriented protocol	networking protocols
BSYS: Process states	New Ready Running Blocked Terminated	os processes
Virtual Memory	Technique using disk space to extend RAM	memory management
```

**Features:**
- Tab-separated values (TSV)
- Optional tags field (3rd column)
- Auto-detects subject from tags or front text
- Supports subject prefix (e.g., "BSYS: Question")

**Subject Detection:**
1. **Prefix in front text:** `BSYS: What is...` → Subject = BSYS
2. **In tags field:** Tags contain "BSYS", "DigiCom", or "TEAM"
3. **Default:** Subject = "CUSTOM"

**Recognized Subjects:**
- `BSYS` - Operating Systems
- `DigiCom` - Digital Communications
- `TEAM` - Teamwork

---

### 4. CSV Format

**Best for:** Creating in Excel/Sheets, bulk imports with full metadata

**Syntax:**
```csv
Subject,Topic,Question,Answer,Difficulty
```

**Example:**
```csv
Subject,Topic,Question,Answer,Difficulty
BSYS,Processes,What is a PCB?,Process Control Block containing process state and metadata,2
DigiCom,VLAN,What is 802.1Q?,VLAN tagging standard with 12-bit VLAN ID,3
TEAM,Agile,What is Scrum?,Agile framework with time-boxed sprints,1
```

**Features:**
- Easy to create in spreadsheet applications
- Header row (will be skipped during import)
- Full metadata per card
- Difficulty: 1 (Easy), 2 (Medium), 3 (Hard)

**CSV Rules:**
- Use commas as delimiters
- Enclose text containing commas in quotes
- First row should be the header
- Difficulty is optional (defaults to 2)

---

## Anki Import Guide

### Exporting from Anki

1. **Open Anki Desktop**
2. **Select your deck**
3. **File → Export**
4. **Configure export:**
   - Export format: **Notes in Plain Text (.txt)**
   - Include: **Notes** (not cards)
   - ✓ Include tags
   - ✓ Include HTML and media references
5. **Save the file** (e.g., `anki_export.txt`)
6. **Import to StudyApp:** Use "Import from File" option

### Anki Format Details

Anki exports create tab-separated files:

```
Front text[TAB]Back text[TAB]Tag1 Tag2 Tag3
```

**Example Anki Export:**
```
What is the OSI model?	7-layer network model: Physical Data-Link Network Transport Session Presentation Application	networking osi model
TCP characteristics	Connection-oriented Reliable Ordered delivery Flow control	networking tcp transport-layer
```

### Adding Subject Information for Anki Import

**Method 1: Prefix in Front Text**
```
BSYS: What is a process?	An instance of a program in execution	processes
DigiCom: What is OSPF?	Link-state routing protocol using Dijkstra	routing protocols
```

**Method 2: Tags**
```
What is virtual memory?	Using disk as RAM extension	memory bsys os
VLAN concept	Virtual LAN for network segmentation	networking digicom vlan
```

**Method 3: Edit in Anki Before Export**

Add a "Subject" field in Anki:
1. Tools → Manage Note Types
2. Select your note type → Fields
3. Add field: "Subject"
4. Add subject to each card
5. Export includes the subject field

### Converting Anki Decks

If you have multiple Anki decks for different subjects:

1. Export each deck separately
2. Add subject prefix or tags before export
3. Import each file to StudyApp
4. StudyApp will organize by detected subjects

---

## Import Methods

### Method 1: Via Application Menu

1. **Launch StudyApp**
   ```bash
   ./run.sh          # Linux/Mac
   run.bat           # Windows
   ```

2. **Navigate to Input Mode**
   - Main Menu → Option 7 (Input Mode)

3. **Select Import**
   - Input Mode → Option 7 (Import from File)

4. **Enter filename**
   - Can be relative: `examples/example_anki.txt`
   - Or absolute: `/Users/name/Documents/flashcards.txt`

5. **Review import results**
   - Shows count of cards and questions imported
   - Automatically saves to custom content

### Method 2: Bulk Add Mode (Simple Format Only)

For rapid entry of simple cards:

1. **Input Mode → Option 8 (Bulk Add Mode)**
2. **Choose subject and topic**
3. **Type cards as `question;answer`**
4. **Press Enter on empty line to finish**

**Example Session:**
```
Subject: [1] BSYS
Topic: Scheduling

1> What is FCFS?;First Come First Served scheduling algorithm
   ✓ Added
2> What is SJF?;Shortest Job First scheduling algorithm
   ✓ Added
3> [press Enter]

✓ Added 2 flashcards!
```

### Method 3: Direct File Editing

Advanced users can directly edit the data files:

**Custom Flashcards:** `custom_flashcards.dat`
```
Subject|||Topic|||Front|||Back|||Difficulty
```

**Custom Questions:** `custom_questions.dat`
```
Subject|||Question|||OptionA|||OptionB|||OptionC|||OptionD|||CorrectIndex|||Explanation
```

**Notes:**
- Use `|||` as field delimiter
- Use `\n` for newlines within fields
- Escape `|||` in content as `\|\|\|`
- Restart StudyApp to reload

---

## Examples

### Example 1: Import Simple Flashcards

**File: `my_cards.txt`**
```
What is a kernel?;Core component of OS managing system resources
What is a shell?;Command-line interface for interacting with OS
What is a daemon?;Background process running continuously
```

**Import:**
1. Input Mode → Import from File
2. Enter: `my_cards.txt`
3. ✓ 3 flashcards imported

---

### Example 2: Import CSV from Excel

**File: `exam_prep.csv`**
```csv
Subject,Topic,Question,Answer,Difficulty
BSYS,Memory,Page size trade-off,"Smaller pages: less internal fragmentation, larger page table; Larger pages: more internal fragmentation, smaller page table",3
BSYS,IPC,Shared memory vs message passing,"Shared memory: fast but needs synchronization; Message passing: slower but easier to use",2
DigiCom,Routing,Distance vector vs link state,"DV: periodic updates, count-to-infinity problem; LS: event-driven, converges faster",3
```

**Import:**
1. Save from Excel as CSV
2. Input Mode → Import from File
3. Enter: `exam_prep.csv`
4. ✓ 3 flashcards imported with full metadata

---

### Example 3: Import from Anki

**File: `anki_network.txt`** (exported from Anki)
```
OSI Layer 1	Physical - transmits raw bits over physical medium	networking osi
OSI Layer 2	Data Link - MAC addressing, frame error checking	networking osi digicom
OSI Layer 3	Network - IP addressing, routing	networking osi
DigiCom: STP purpose	Prevents loops in redundant switched networks	networking stp layer2
```

**Import:**
1. Export from Anki as plain text
2. Input Mode → Import from File
3. Enter: `anki_network.txt`
4. ✓ 4 flashcards imported
   - First 3 → Subject: CUSTOM
   - Last one → Subject: DigiCom (detected from prefix)

---

### Example 4: Full StudyApp Format

**File: `advanced_cards.txt`**
```
---CARD---
SUBJECT: BSYS
TOPIC: Deadlock
DIFFICULTY: 3
FRONT:
What are the four necessary conditions for deadlock?
List and explain each.
BACK:
1. Mutual Exclusion
   - Resources cannot be shared

2. Hold and Wait
   - Process holds resources while waiting for more

3. No Preemption
   - Resources cannot be forcibly taken

4. Circular Wait
   - Circular chain of processes waiting for resources

All four must be present for deadlock to occur.

---QUESTION---
SUBJECT: BSYS
Q: Which condition, if prevented, can stop deadlock?
A: Only Mutual Exclusion
B: Only Circular Wait
C: Any one of the four conditions
D: All four conditions must be prevented
CORRECT: C
EXPLANATION: Breaking any single condition prevents deadlock. Typically, preventing Circular Wait (through resource ordering) is the most practical approach.
```

**Import:**
1. Input Mode → Import from File
2. Enter: `advanced_cards.txt`
3. ✓ 1 flashcard, 1 question imported with full formatting

---

## Tips & Best Practices

### Content Creation

1. **Keep it concise:** Flashcards work best with focused, bite-sized information
2. **One concept per card:** Don't overload a single card
3. **Use formatting:** Take advantage of multi-line support in StudyApp format
4. **Include examples:** Especially for abstract concepts
5. **Add context:** Subject and topic help organize your study sessions

### Import Workflow

1. **Start simple:** Begin with simple format for quick content
2. **Upgrade gradually:** Use CSV/StudyApp format for complex content
3. **Test small batches:** Import 5-10 cards first to verify format
4. **Backup regularly:** Export your custom content periodically
5. **Use version control:** Keep import files in git for tracking changes

### File Organization

```
my-study-materials/
├── bsys/
│   ├── processes.csv
│   ├── memory.txt
│   └── scheduling.txt
├── digicom/
│   ├── routing.csv
│   └── switching.txt
├── team/
│   └── agile.txt
└── anki-exports/
    ├── deck1.txt
    └── deck2.txt
```

### UTF-8 Encoding

Always save import files as **UTF-8** encoding:
- **VS Code:** Bottom right → Select Encoding → UTF-8
- **Notepad++:** Encoding → UTF-8
- **Excel:** Save As → CSV UTF-8

### Special Characters

Most special characters work fine:
- ✓ Arrows: → ← ↔
- ✓ Bullets: • · ○
- ✓ Math: ≥ ≤ ≠ ±
- ✓ Symbols: ✓ ✗ ★ ☆

Escape these in CSV:
- Commas: Use quotes `"text, with, commas"`
- Quotes: Double them `"He said ""hello"""`

---

## Troubleshooting

### Import Fails

**Problem:** "Import failed: File not found"
- **Solution:** Check filename and path
- Use absolute path if needed
- Ensure file extension is correct

**Problem:** "Import failed: IOException"
- **Solution:** Close file in other applications
- Check file permissions
- Verify file is not corrupted

### No Cards Imported

**Problem:** "Import complete. Cards: 0"
- **Solution:** Check file format
- Verify delimiter (semicolon, comma, or tab)
- Look for extra spaces or empty lines
- Test with example files first

**Problem:** CSV not parsing correctly
- **Solution:**
  - Ensure header row exists: `Subject,Topic,Question,Answer,Difficulty`
  - Check for commas within fields (should be quoted)
  - Verify no extra columns

### Anki Import Issues

**Problem:** Cards imported but wrong subject
- **Solution:**
  - Add subject prefix: `BSYS: Question`
  - Or include subject in tags: `bsys`, `digicom`, `team`
  - Edit cards after import

**Problem:** Tabs converted to spaces
- **Solution:**
  - Don't open/edit Anki export in Excel (converts tabs)
  - Use plain text editor (VS Code, Notepad++)
  - Verify tabs with cat -A (Linux/Mac) or hex editor

### Character Encoding Issues

**Problem:** Special characters show as `?` or `�`
- **Solution:**
  - Save file as UTF-8
  - Terminal must support UTF-8
  - Windows: `chcp 65001`

**Problem:** Newlines not preserved
- **Solution:**
  - Use StudyApp export format for multi-line content
  - Simple/Anki formats are single-line only

### Content Issues

**Problem:** Cards imported but answers cut off
- **Solution:**
  - Check for delimiter characters in content
  - Semicolon in simple format: use different format
  - Comma in CSV: wrap field in quotes

**Problem:** Difficulty not being set correctly
- **Solution:**
  - Use values 1, 2, or 3 only
  - Invalid values default to 2 (Medium)
  - Leave empty for default

---

## Quick Reference

### Format Cheat Sheet

| Format | Delimiter | Multi-line | Metadata | Difficulty |
|--------|-----------|------------|----------|------------|
| Simple | `;` | No | No | No |
| CSV | `,` | No | Yes | Yes |
| Anki | `TAB` | No | Partial | No |
| StudyApp | Tags | Yes | Full | Yes |

### Import Checklist

- [ ] File saved as UTF-8
- [ ] Correct delimiter used
- [ ] No special characters in delimiters
- [ ] Tested with small sample
- [ ] Backup existing data
- [ ] File path accessible
- [ ] Proper format structure

### Command Summary

```bash
# Test import with example files
./run.sh
# → 7 (Input Mode)
# → 7 (Import from File)
# → examples/example_simple.txt

# Quick bulk add
./run.sh
# → 7 (Input Mode)
# → 8 (Bulk Add Mode)
# → Enter cards as: question;answer
```

---

## Additional Resources

### Example Files Location

All example files are in the `examples/` directory:
- `example_simple.txt` - Simple format examples
- `example_flashcards.csv` - CSV format examples
- `example_anki.txt` - Anki format examples
- `example_studyapp_format.txt` - Full StudyApp format examples

### Data Files

Your imported content is stored in:
- `custom_flashcards.dat` - Your custom flashcards
- `custom_questions.dat` - Your custom quiz questions
- `study_progress.dat` - Your learning progress

**Backup these files regularly!**

### Getting Help

- Check README.md for general usage
- Review example files in `examples/`
- Test with small files first
- Export existing content to see format

---

**Happy Studying! 📚**
