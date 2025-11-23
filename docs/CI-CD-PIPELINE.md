# CI/CD Pipeline Documentation

This document describes the CI/CD pipeline setup for the Code Editor IDE Android project.

## Overview

The project uses **GitHub Actions** for continuous integration and deployment. The pipeline includes automated building, testing, code quality checks, and release management.

## Gradle Wrapper Handling

The Gradle wrapper JAR (`gradle/wrapper/gradle-wrapper.jar`) is **not committed** to the repository for security and repository size reasons. Instead:

- CI workflows automatically download the wrapper JAR if missing
- Local development uses the setup script: `./setup-gradle.sh`
- The wrapper properties file is committed to specify the Gradle version

See [Gradle Setup Guide](GRADLE-SETUP.md) for detailed information.

## Workflows

### 1. Android CI (`android-ci.yml`)

**Trigger:**
- Push to `main` or any `claude/**` branch
- Pull requests to `main`

**Jobs:**
- ✅ Checkout code
- ☕ Set up JDK 17
- 📦 Cache Gradle dependencies
- 🔍 Validate Gradle wrapper
- 🏗️ Build project with Gradle
- 🔎 Run Lint analysis
- 🧪 Run unit tests
- 📱 Generate debug APK
- 📤 Upload artifacts (APK, lint reports, test results)

**Artifacts:**
- `app-debug.apk` (retention: 14 days)
- Lint reports (retention: 7 days)
- Test results (retention: 7 days)

### 2. Android Release Build (`android-release.yml`)

**Trigger:**
- Push tags matching `v*` (e.g., `v1.0.0`)
- Manual workflow dispatch

**Jobs:**
- ✅ All CI checks
- 🏗️ Build release APK
- 📦 Build release AAB (App Bundle)
- ✍️ Sign APK (if secrets configured)
- 📤 Upload release artifacts
- 🚀 Create GitHub release (for tags)

**Artifacts:**
- `app-release.apk` (retention: 30 days)
- `app-release.aab` (retention: 30 days)

**Required Secrets:**
```
SIGNING_KEY          - Base64 encoded keystore
KEY_ALIAS            - Key alias
KEY_STORE_PASSWORD   - Keystore password
KEY_PASSWORD         - Key password
```

### 3. Pull Request Checks (`pr-checks.yml`)

**Trigger:**
- Pull request events (opened, synchronize, reopened)

**Jobs:**

#### Code Quality Check
- 🎨 Check code style (ktlint)
- 🔎 Run Lint
- 🧪 Run unit tests
- 🏗️ Build debug APK
- 💬 Comment build status on PR

#### APK Size Check
- 📏 Check APK size
- ⚠️ Fail if APK exceeds 100 MB

### 4. Dependency Review (`dependency-review.yml`)

**Trigger:**
- Pull requests to `main`

**Jobs:**
- 🔒 Review dependencies for security vulnerabilities
- 💬 Comment security findings in PR
- ❌ Fail on moderate or higher severity issues

## Dependabot Configuration

**Automated dependency updates:**
- **Gradle dependencies**: Weekly on Monday
- **GitHub Actions**: Weekly on Monday

**Settings:**
- Max 10 open PRs for Gradle
- Max 5 open PRs for GitHub Actions
- Auto-labeled with `dependencies`
- Commits prefixed with `chore:`

## Setting Up Signing for Release Builds

To enable APK signing in the release workflow:

### 1. Generate a Keystore

```bash
keytool -genkey -v -keystore release-keystore.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias release-key
```

### 2. Encode Keystore to Base64

```bash
base64 release-keystore.jks > keystore.base64
```

### 3. Add Secrets to GitHub

Go to: `Settings` → `Secrets and variables` → `Actions` → `New repository secret`

Add the following secrets:
- `SIGNING_KEY`: Contents of `keystore.base64`
- `KEY_ALIAS`: Your key alias (e.g., `release-key`)
- `KEY_STORE_PASSWORD`: Your keystore password
- `KEY_PASSWORD`: Your key password

### 4. Update gradle.properties (Optional)

For local signing, add to `~/.gradle/gradle.properties`:

```properties
RELEASE_STORE_FILE=/path/to/release-keystore.jks
RELEASE_STORE_PASSWORD=your_password
RELEASE_KEY_ALIAS=release-key
RELEASE_KEY_PASSWORD=your_password
```

## Triggering Workflows

### CI Build (Automatic)
```bash
git push origin claude/your-branch-name
```

### Release Build (Manual)
```bash
# Create and push a tag
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin v1.0.0
```

### Manual Workflow Dispatch
1. Go to `Actions` tab on GitHub
2. Select `Android Release Build`
3. Click `Run workflow`
4. Enter version number
5. Click `Run workflow`

## Monitoring Workflows

### Check Workflow Status
1. Go to the `Actions` tab in your GitHub repository
2. Select the workflow you want to monitor
3. View logs and artifacts

### Download Artifacts
1. Go to the completed workflow run
2. Scroll to the `Artifacts` section
3. Click on the artifact name to download

## Best Practices

### Branch Protection Rules

Recommended settings for `main` branch:
- ✅ Require pull request reviews
- ✅ Require status checks to pass before merging
  - Android CI
  - code-quality (from PR checks)
  - dependency-review
- ✅ Require branches to be up to date
- ✅ Include administrators

### Workflow Optimization

**Cache Strategy:**
- Gradle cache uses composite key based on all `*.gradle*` files
- Wrapper cache for faster setup

**Timeout Settings:**
- CI: 30 minutes
- Release: 45 minutes
- PR checks: 15-20 minutes

**Parallelization:**
- PR checks run code-quality and size-check in parallel

## Troubleshooting

### Build Fails with OutOfMemoryError
Increase heap size in `gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx4096m -Dfile.encoding=UTF-8
```

### Gradle Wrapper Validation Fails
Update wrapper:
```bash
./gradlew wrapper --gradle-version=8.11.1
```

### APK Signing Fails
1. Verify all secrets are set correctly
2. Check keystore password and alias
3. Ensure base64 encoding is correct

### Lint or Tests Fail
Run locally first:
```bash
./gradlew lintDebug testDebugUnitTest
```

## Local Testing

Test workflows locally using [act](https://github.com/nektos/act):

```bash
# Install act
brew install act  # macOS
# or
curl https://raw.githubusercontent.com/nektos/act/master/install.sh | sudo bash

# Run CI workflow locally
act push -W .github/workflows/android-ci.yml
```

## Workflow Badges

Add to your README:

```markdown
![Android CI](https://github.com/taufiksoleh/coder/workflows/Android%20CI/badge.svg)
![Release](https://github.com/taufiksoleh/coder/workflows/Android%20Release%20Build/badge.svg)
```

## Resources

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Android CI/CD Best Practices](https://developer.android.com/studio/build/building-cmdline)
- [Gradle Build Scans](https://scans.gradle.com/)
- [Sign your app](https://developer.android.com/studio/publish/app-signing)

## Support

For issues with the CI/CD pipeline:
1. Check the [Actions](https://github.com/taufiksoleh/coder/actions) tab
2. Review workflow logs
3. Open an issue with the `ci/cd` label

---

Last updated: 2025-01-23
