@echo off
REM StudyApp Launcher Script for Windows
REM This script compiles and runs the StudyApp

echo ==========================================
echo    StudyApp - Exam Preparation Tool
echo ==========================================
echo.

REM Check if Java is installed
java -version >nul 2>&1
if errorlevel 1 (
    echo Error: Java is not installed or not in PATH
    echo Please install Java JDK 8 or higher
    pause
    exit /b 1
)

REM Check if javac is installed
javac -version >nul 2>&1
if errorlevel 1 (
    echo Error: Java compiler ^(javac^) is not installed
    echo Please install Java JDK 8 or higher
    pause
    exit /b 1
)

REM Display Java version
echo Java version:
java -version
echo.

REM Check if StudyApp.java exists
if exist "StudyApp.java" (
    set MAIN_FILE=StudyApp.java
    set CLASS_FILE=StudyApp
    echo Using: StudyApp.java
) else (
    echo Error: StudyApp.java not found!
    pause
    exit /b 1
)

echo.
echo Compiling StudyApp...

REM Compile the Java file
javac "%MAIN_FILE%"

REM Check if compilation was successful
if errorlevel 1 (
    echo Compilation failed! Please check for errors.
    pause
    exit /b 1
)

echo Compilation successful!
echo.
echo Starting StudyApp...
echo ==========================================
echo.

REM Run the application
java "%CLASS_FILE%"

REM Clean up class files after execution
echo.
echo Cleaning up...
del /Q *.class 2>nul

echo.
echo Thank you for using StudyApp!
pause
