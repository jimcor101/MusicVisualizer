#!/bin/bash

echo "Starting Music Visualizer..."
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

# Use the JavaFX Maven plugin to run the application
# This properly handles all JavaFX modules, dependencies, and classpath
mvn javafx:run