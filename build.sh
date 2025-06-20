#!/bin/bash

# HexClient Build Script
# Builds the HexClient Minecraft mod

echo "Building HexClient..."

# Check if Java is available
if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed or not in PATH"
    exit 1
fi

# Check Java version
java_version=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
if [ "$java_version" -lt 21 ]; then
    echo "Error: Java 21 or higher is required"
    exit 1
fi

# Clean previous builds
echo "Cleaning previous builds..."
./gradlew clean

# Build the mod
echo "Building HexClient mod..."
./gradlew build

# Check if build was successful
if [ $? -eq 0 ]; then
    echo "✅ HexClient build completed successfully!"
    echo "📦 Mod file location: build/libs/"
    ls -la build/libs/*.jar
else
    echo "❌ Build failed!"
    exit 1
fi