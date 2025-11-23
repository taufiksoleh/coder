# Gradle Wrapper Setup Guide

This guide explains how to set up and use the Gradle wrapper for this project.

## What is the Gradle Wrapper?

The Gradle wrapper is a script that allows you to run Gradle builds without having to install Gradle manually. It ensures everyone uses the same Gradle version (8.11.1 for this project).

## Initial Setup

### Option 1: Using the Setup Script (Recommended)

Run the provided setup script to download the Gradle wrapper automatically:

```bash
./setup-gradle.sh
```

This script will:
- Download the Gradle wrapper JAR (gradle-wrapper.jar)
- Make gradlew executable
- Verify the setup

### Option 2: Manual Download

If you prefer to download manually:

```bash
mkdir -p gradle/wrapper
curl -L https://raw.githubusercontent.com/gradle/gradle/v8.11.1/gradle/wrapper/gradle-wrapper.jar \
  -o gradle/wrapper/gradle-wrapper.jar
chmod +x gradlew
```

### Option 3: Using Gradle (if installed)

If you have Gradle installed globally:

```bash
gradle wrapper --gradle-version=8.11.1
```

## Verification

Verify the wrapper is set up correctly:

```bash
./gradlew --version
```

You should see output showing Gradle 8.11.1.

## Building the Project

Once the wrapper is set up, you can build the project:

```bash
# Build all modules
./gradlew build

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run tests
./gradlew test

# Run lint
./gradlew lintDebug

# Clean build
./gradlew clean build
```

## CI/CD Behavior

The CI/CD pipeline automatically downloads the Gradle wrapper JAR if it's missing, so you don't need to commit it to the repository. This keeps the repository size smaller and allows for easier Gradle version upgrades.

## Troubleshooting

### gradlew: command not found

Make sure the script is executable:

```bash
chmod +x gradlew
```

### Permission denied

On Windows, use `gradlew.bat` instead:

```cmd
gradlew.bat build
```

### Wrapper JAR not found

Run the setup script or manually download the wrapper JAR:

```bash
./setup-gradle.sh
```

### Download failed

If the download fails, you can:

1. Check your internet connection
2. Try downloading from a different mirror
3. Use a VPN if GitHub is blocked
4. Download the full Gradle distribution and extract the wrapper JAR manually

### Gradle version mismatch

If you see version conflicts, update the wrapper:

```bash
./gradlew wrapper --gradle-version=8.11.1
```

## Android Studio Integration

Android Studio automatically uses the Gradle wrapper. When you open the project:

1. Android Studio will detect the wrapper
2. It will sync the project using the specified Gradle version
3. You don't need to do anything manually

## Why the Wrapper JAR is Not Committed

The gradle-wrapper.jar file is not committed to version control for several reasons:

1. **Security**: Avoids committing potentially untrusted binary files
2. **Repository Size**: Keeps the repository smaller
3. **Flexibility**: Makes it easier to update Gradle versions
4. **CI/CD**: The pipeline automatically handles the wrapper setup

The Gradle wrapper properties file (`gradle/wrapper/gradle-wrapper.properties`) IS committed and specifies the Gradle version to use.

## Updating Gradle Version

To update to a new Gradle version:

1. Update `gradle/wrapper/gradle-wrapper.properties`:
   ```properties
   distributionUrl=https\://services.gradle.org/distributions/gradle-8.11.1-bin.zip
   ```

2. Update the version in `.github/workflows/*.yml` files

3. Run the setup script or rebuild the wrapper:
   ```bash
   ./setup-gradle.sh
   ```

## Additional Resources

- [Gradle Wrapper Documentation](https://docs.gradle.org/current/userguide/gradle_wrapper.html)
- [Gradle Build Scans](https://scans.gradle.com/)
- [Android Gradle Plugin Release Notes](https://developer.android.com/build/releases/gradle-plugin)

---

Last updated: 2025-01-23
