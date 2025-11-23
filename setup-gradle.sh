#!/bin/bash

# Setup script for Gradle wrapper
# This script downloads the Gradle wrapper JAR if it doesn't exist

set -e

GRADLE_VERSION="8.11.1"
WRAPPER_JAR="gradle/wrapper/gradle-wrapper.jar"
WRAPPER_URL="https://raw.githubusercontent.com/gradle/gradle/v${GRADLE_VERSION}/gradle/wrapper/gradle-wrapper.jar"

echo "🔧 Setting up Gradle wrapper for version ${GRADLE_VERSION}..."

# Create wrapper directory if it doesn't exist
mkdir -p gradle/wrapper

# Check if wrapper JAR exists
if [ -f "$WRAPPER_JAR" ]; then
    echo "✅ Gradle wrapper JAR already exists"
else
    echo "📥 Downloading Gradle wrapper JAR..."
    curl -L "$WRAPPER_URL" -o "$WRAPPER_JAR"

    if [ -f "$WRAPPER_JAR" ]; then
        echo "✅ Gradle wrapper JAR downloaded successfully"
    else
        echo "❌ Failed to download Gradle wrapper JAR"
        exit 1
    fi
fi

# Make gradlew executable
if [ -f "gradlew" ]; then
    chmod +x gradlew
    echo "✅ Made gradlew executable"
fi

if [ -f "gradlew.bat" ]; then
    echo "✅ gradlew.bat is ready"
fi

echo ""
echo "🎉 Setup complete! You can now run:"
echo "   ./gradlew build"
echo ""
