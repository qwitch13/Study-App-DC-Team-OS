#!/bin/bash
# StudyApp Launcher Script for Linux/Mac
# This script compiles and runs the StudyApp

echo "=========================================="
echo "   StudyApp - Exam Preparation Tool"
echo "=========================================="
echo ""

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed or not in PATH"
    echo "Please install Java JDK 8 or higher"
    exit 1
fi

# Check if javac is installed
if ! command -v javac &> /dev/null; then
    echo "Error: Java compiler (javac) is not installed"
    echo "Please install Java JDK 8 or higher"
    exit 1
fi

# Display Java version
echo "Java version:"
java -version
echo ""

# Check if StudyApp.java exists
if [ -f "StudyApp.java" ]; then
    MAIN_FILE="StudyApp.java"
    CLASS_FILE="StudyApp"
    echo "Using: StudyApp.java"
else
    echo "Error: StudyApp.java not found!"
    exit 1
fi

echo ""
echo "Compiling StudyApp..."

# Compile the Java file
javac "$MAIN_FILE"

# Check if compilation was successful
if [ $? -eq 0 ]; then
    echo "Compilation successful!"
    echo ""
    echo "Starting StudyApp..."
    echo "=========================================="
    echo ""

    # Run the application
    java "$CLASS_FILE"

    # Clean up class files after execution
    echo ""
    echo "Cleaning up..."
    rm -f *.class

else
    echo "Compilation failed! Please check for errors."
    exit 1
fi

echo ""
echo "Thank you for using StudyApp!"
