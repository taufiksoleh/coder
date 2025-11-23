# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep Hilt generated code
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# Keep JGit classes
-keep class org.eclipse.jgit.** { *; }
-dontwarn org.eclipse.jgit.**

# Keep Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**
