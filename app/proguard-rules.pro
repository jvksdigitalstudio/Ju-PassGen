# ── JU Password Generator — ProGuard / R8 Rules ────────────────

-keepattributes SourceFile,LineNumberTable
-keepattributes Signature
-keepattributes *Annotation*

# ── AdMob / Google Mobile Ads ──────────────────────────────────
-keep class com.google.android.gms.ads.** { *; }
-keep class com.google.android.gms.common.** { *; }
-dontwarn com.google.android.gms.**

# ── Jetpack Compose ────────────────────────────────────────────
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# ── Coroutines ─────────────────────────────────────────────────
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-dontwarn kotlinx.coroutines.**

# ── Apache Commons Compress ────────────────────────────────────
-keep class org.apache.commons.compress.** { *; }
-dontwarn org.apache.commons.compress.**

# ── Tukaani XZ / LZMA2 ────────────────────────────────────────
-keep class org.tukaani.xz.** { *; }
-dontwarn org.tukaani.xz.**

# ── Crypto (javax.crypto) ──────────────────────────────────────
-keep class javax.crypto.** { *; }
-keep class java.security.** { *; }

# ── DataStore ──────────────────────────────────────────────────
-keep class androidx.datastore.** { *; }
-dontwarn androidx.datastore.**

# ── FileProvider ───────────────────────────────────────────────
-keep class androidx.core.content.FileProvider { *; }

# ── App classes principales ────────────────────────────────────
-keep class com.ju.passgen.JUApp { *; }
-keep class com.ju.passgen.MainActivity { *; }
-keep class com.ju.passgen.BuildConfig { *; }

# ── ViewModel / LiveData ───────────────────────────────────────
-keep class * extends androidx.lifecycle.ViewModel { *; }
-keep class androidx.lifecycle.** { *; }
-dontwarn androidx.lifecycle.**
