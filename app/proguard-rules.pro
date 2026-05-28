# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# ===========================
# Kotlin & Coroutines
# ===========================
-keepattributes *Annotation*
-dontwarn kotlinx.**
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# ===========================
# Hilt / Dagger
# ===========================
-dontwarn com.google.errorprone.annotations.**
-keep class dagger.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }
-keep class * extends dagger.hilt.android.internal.lifecycle.HiltViewModelFactory { *; }
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * { *; }
-keep class dagger.hilt.** { *; }
-keep class javax.annotation.** { *; }
-keepclasseswithmembers class * {
    @dagger.* <methods>;
}
-keepclasseswithmembers class * {
    @javax.inject.* <methods>;
}

# ===========================
# Room Database
# ===========================
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *
-keepclassmembers class * extends androidx.room.RoomDatabase {
    public static ** getDatabase(...);
}
-keep class com.ironcore.metrics.data.local.** { *; }
-keep class com.ironcore.metrics.data.local.entities.** { *; }
-keep class com.ironcore.metrics.data.local.dao.** { *; }

# ===========================
# Health Connect
# ===========================
-keep class androidx.health.connect.client.** { *; }
-keep class androidx.health.connect.client.records.** { *; }
-keep class androidx.health.connect.client.permission.** { *; }
-keep class androidx.health.connect.client.request.** { *; }
-keep class androidx.health.connect.client.response.** { *; }
-keep class androidx.health.connect.client.time.** { *; }
-keep class androidx.health.connect.client.units.** { *; }
-dontwarn androidx.health.connect.client.**

# ===========================
# Retrofit & OkHttp
# ===========================
-keepattributes Signature
-keepattributes Exceptions
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**

# ===========================
# Gson
# ===========================
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# Keep all DTOs and data models
-keep class com.ironcore.metrics.data.remote.dto.** { *; }
-keep class com.ironcore.metrics.data.model.** { *; }
-keep class com.ironcore.metrics.domain.model.** { *; }

# ===========================
# Jetpack Compose
# ===========================
-keep class androidx.compose.** { *; }
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.ui.** { *; }
-keep class androidx.compose.material3.** { *; }
-dontwarn androidx.compose.**

# ===========================
# Android Auto / Car App Library
# ===========================
-keep class androidx.car.app.** { *; }
-keep class com.ironcore.metrics.car.** { *; }
-keepclassmembers class * extends androidx.car.app.Screen {
    public <init>(...);
}
-keepclassmembers class * extends androidx.car.app.CarAppService {
    public <init>(...);
}

# ===========================
# Wear OS
# ===========================
-keep class androidx.wear.** { *; }
-keep class com.google.android.gms.wearable.** { *; }
-dontwarn androidx.wear.**

# ===========================
# WorkManager
# ===========================
-keep class * extends androidx.work.Worker
-keep class * extends androidx.work.CoroutineWorker
-keep class androidx.work.** { *; }
-keepclassmembers class * extends androidx.work.Worker {
    public <init>(android.content.Context,androidx.work.WorkerParameters);
}
-keepclassmembers class * extends androidx.work.CoroutineWorker {
    public <init>(android.content.Context,androidx.work.WorkerParameters);
}

# ===========================
# Speech Recognition & TTS
# ===========================
-keep class android.speech.** { *; }
-keep class com.ironcore.metrics.ui.workout.VoiceCommandHelper { *; }
-keep class com.ironcore.metrics.ui.workout.VoiceCommand { *; }

# ===========================
# ViewModels
# ===========================
-keep class * extends androidx.lifecycle.ViewModel {
    <init>();
}
-keep class * extends androidx.lifecycle.AndroidViewModel {
    <init>(android.app.Application);
}
-keep class com.ironcore.metrics.ui.**.*ViewModel { *; }

# ===========================
# Services
# ===========================
-keep class * extends android.app.Service
-keep class com.ironcore.metrics.data.health.IronCoreVitalsService { *; }
-keep class com.ironcore.metrics.data.sync.IronCoreWearListenerService { *; }

# ===========================
# Serialization
# ===========================
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# ===========================
# Parcelable
# ===========================
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# ===========================
# Enum
# ===========================
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ===========================
# Native Methods
# ===========================
-keepclasseswithmembernames class * {
    native <methods>;
}

# ===========================
# R8 Full Mode
# ===========================
-allowaccessmodification
-repackageclasses
