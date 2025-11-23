pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "CodeEditorIDE"

include(":app")
include(":core:ui")
include(":core:common")
include(":core:data")
include(":feature:editor")
include(":feature:terminal")
include(":feature:git")
include(":feature:filebrowser")
