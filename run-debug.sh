#!/bin/bash

echo "Starting Music Visualizer with enhanced debugging..."
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
echo "Starting application with JavaFX Maven plugin..."
echo "This will run for 30 seconds then automatically exit to capture logs..."

# Run with timeout and capture all output
timeout 30s mvn javafx:run 2>&1 | tee debug-output.log

echo ""
echo "Application run completed. Exit code: $?"
echo "Debug output saved to debug-output.log"

if [ -f debug-output.log ]; then
    echo ""
    echo "Last 20 lines of output:"
    tail -20 debug-output.log
fi