#!/bin/bash

echo "Starting Music Visualizer CRASH TEST - Will run for 45 seconds to capture 20-30s crash..."
echo "Compiling project..."

# Compile the project
mvn clean compile

if [ $? -ne 0 ]; then
    echo "Compilation failed!"
    exit 1
fi

echo "Compilation successful!"
echo "Java version:"
java --version

echo ""
echo "Starting application - monitoring for crash in 20-30 second range..."

# Run with longer timeout and capture all output
timeout 45s mvn javafx:run 2>&1 | tee crash-test-output.log

echo ""
echo "Application run completed. Exit code: $?"
echo "Full output saved to crash-test-output.log"

if [ -f crash-test-output.log ]; then
    echo ""
    echo "=== CRASH ANALYSIS ==="
    echo "Last 30 lines of output:"
    tail -30 crash-test-output.log
    
    echo ""
    echo "Looking for ERROR patterns:"
    grep -n "ERROR\|Exception\|CRASH ZONE" crash-test-output.log | tail -10
    
    echo ""
    echo "Timeline of events:"
    grep -n "Total runtime:" crash-test-output.log | tail -10
fi