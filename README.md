# Code Editor IDE for Android

![Android CI](https://github.com/taufiksoleh/coder/workflows/Android%20CI/badge.svg)
![Release](https://github.com/taufiksoleh/coder/workflows/Android%20Release%20Build/badge.svg)

A modern, feature-rich code editor IDE for Android built entirely with Jetpack Compose.

## Features

### Code Editor
- Multi-language syntax support (Kotlin, Java, Python, JavaScript, TypeScript, HTML, CSS, and more)
- Line numbers display
- Undo/Redo functionality
- File operations (open, save, create)
- Horizontal and vertical scrolling
- Monospace font for better code readability

### File Browser
- Navigate filesystem on your Android device
- Create new files and folders
- Open files directly in the editor
- Sorted display (directories first, then alphabetically)

### Terminal Emulator
- Execute shell commands directly on your device
- Command history
- Built-in commands (cd, pwd, clear)
- Working directory management
- Process management

### Git Integration
- Repository management (init, clone, open)
- View git status and changed files
- Commit and push changes
- View commit history
- Branch management and switching
- Pull from remote repositories
- Powered by JGit

## Architecture

This project follows modern Android development best practices:

### Clean Architecture
- **Multi-module structure** for better separation of concerns
- **MVVM pattern** with ViewModels and UI State
- **Unidirectional data flow** using Kotlin StateFlow
- **Dependency Injection** with Hilt

### Module Structure

```
app/                        # Main application module
├── core/
│   ├── ui/                # Shared UI components
│   ├── common/            # Common utilities and models
│   └── data/              # Data layer
├── feature/
│   ├── editor/            # Code editor feature
│   ├── terminal/          # Terminal emulator feature
│   ├── git/               # Git integration feature
│   └── filebrowser/       # File browser feature
```

### Tech Stack

- **Jetpack Compose**: Modern declarative UI framework
- **Kotlin**: 100% Kotlin codebase
- **Coroutines & Flow**: For asynchronous operations
- **Hilt**: Dependency injection
- **JGit**: Pure Java Git implementation
- **Material 3**: Latest Material Design components
- **ViewModel**: Lifecycle-aware state management

## Requirements

- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 35 (Android 15)
- **Compile SDK**: 35
- **Kotlin**: 2.1.0
- **Gradle**: 8.11.1
- **AGP**: 8.7.3

## Permissions

The app requires the following permissions:
- `INTERNET`: For git clone/push/pull operations
- `READ_EXTERNAL_STORAGE`: To access files on device (API ≤ 32)
- `WRITE_EXTERNAL_STORAGE`: To save files on device (API ≤ 32)
- `MANAGE_EXTERNAL_STORAGE`: For full filesystem access
- `READ_MEDIA_*`: For media access on Android 13+

## Building the Project

1. Clone the repository
```bash
git clone <repository-url>
cd CodeEditorIDE
```

2. Open in Android Studio (Ladybug | 2024.2.1 or later)

3. Build and run
```bash
./gradlew assembleDebug
```

Or click the "Run" button in Android Studio.

## CI/CD Pipeline

The project includes a comprehensive GitHub Actions CI/CD pipeline:

### Automated Workflows

**Continuous Integration (android-ci.yml)**
- Triggered on push to `main` and `claude/**` branches
- Runs build, lint, and unit tests
- Generates debug APK
- Uploads artifacts and reports

**Release Build (android-release.yml)**
- Triggered on version tags (`v*`) or manual dispatch
- Builds signed release APK and AAB
- Creates GitHub releases automatically

**Pull Request Checks (pr-checks.yml)**
- Code quality validation with ktlint
- APK size monitoring (fails if > 100 MB)
- Automated PR comments with build status

**Dependency Review (dependency-review.yml)**
- Security vulnerability scanning
- Blocks PRs with moderate+ severity issues

### Automated Dependency Updates

**Dependabot** is configured to:
- Update Gradle dependencies weekly
- Update GitHub Actions weekly
- Auto-label and assign PRs

### Creating a Release

```bash
# Tag and push
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin v1.0.0
```

See [CI/CD Pipeline Documentation](docs/CI-CD-PIPELINE.md) for detailed setup instructions.

## Development

### Adding a New Language
To add syntax highlighting for a new language, update the `detectLanguage()` function in `EditorViewModel.kt`:

```kotlin
private fun detectLanguage(extension: String): String {
    return when (extension.lowercase()) {
        "your_ext" -> "your_language"
        // ... other languages
        else -> "plaintext"
    }
}
```

### Customizing Terminal
The terminal implementation is in `feature/terminal/`. To extend terminal functionality:
- Add new commands in `TerminalViewModel.kt`
- Implement native PTY support in `terminal.cpp` for advanced features

### Extending Git Features
Git operations are handled in `feature/git/repository/GitRepository.kt`. All operations use JGit API.

## Best Practices Applied

Based on 2025 Android development standards:

1. **Jetpack Compose**: Full Compose UI implementation
2. **Material 3**: Modern Material Design
3. **Clean Architecture**: Clear separation of concerns
4. **MVVM Pattern**: Lifecycle-aware components
5. **Modular Design**: Feature-based modules
6. **Kotlin Coroutines**: Async operations
7. **StateFlow**: Reactive state management
8. **Hilt DI**: Compile-time dependency injection

## Roadmap

- [ ] Syntax highlighting with TextMate grammars
- [ ] Code completion and IntelliSense
- [ ] Search and replace functionality
- [ ] Multiple file tabs
- [ ] Theme customization (dark/light mode)
- [ ] Keyboard shortcuts
- [ ] Git branch creation and merging
- [ ] SSH key management for Git
- [ ] Plugin system
- [ ] Language servers (LSP) integration

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

[Add your license here]

## Resources

- [Jetpack Compose Documentation](https://developer.android.com/compose)
- [Android Architecture Guide](https://developer.android.com/topic/architecture)
- [JGit Documentation](https://www.eclipse.org/jgit/)
- [Material Design 3](https://m3.material.io/)

## Acknowledgments

Built with inspiration from:
- Termux - Terminal emulator architecture
- JGit - Git operations
- Modern Android architecture patterns

---

**Note**: This is a full-featured IDE for Android. For production use, ensure you test thoroughly on various devices and Android versions.