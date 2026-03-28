# FitForge ProGuard Rules for Play Store Release

# ===== General Android Rules =====
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose
-dontoptimize
-dontpreverify

# Preserve line numbers for debugging stack traces
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ===== Kotlin =====
-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings {
    <fields>;
}
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# ===== Android Architecture Components =====
# ViewModel
-keep class * extends androidx.lifecycle.ViewModel {
    <init>();
}
-keep class * extends androidx.lifecycle.AndroidViewModel {
    <init>(android.app.Application);
}

# LiveData
-keep class androidx.lifecycle.LiveData { *; }
-keep class androidx.lifecycle.MutableLiveData { *; }

# Room Database
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Keep all Room DAOs
-keep interface * extends androidx.room.Dao {
    *;
}

# Keep Room entities and their fields
-keep @androidx.room.Entity class * {
    *;
}

# Keep TypeConverters
-keep class * {
    @androidx.room.TypeConverter <methods>;
}

# ===== Jetpack Compose =====
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Compose Runtime
-keepclassmembers class androidx.compose.** {
    *;
}

# ===== Hilt/Dagger =====
-dontwarn com.google.errorprone.annotations.**
-keep class dagger.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# Hilt generated classes
-keep class **_HiltModules** { *; }
-keep class **_Factory { *; }
-keep class **_MembersInjector { *; }
-keep class **_HiltWrapper { *; }
-keep class **Hilt** { *; }
-keep class dagger.hilt.** { *; }

# Keep application class
-keep class * extends android.app.Application {
    <init>();
}

# ===== Firebase =====
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# Firebase Firestore
-keep class com.google.firebase.firestore.** { *; }
-keepclassmembers class com.google.firebase.firestore.** {
    *;
}

# Firebase Auth
-keep class com.google.firebase.auth.** { *; }

# ===== Gson/JSON =====
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# ===== Retrofit/OkHttp =====
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Retrofit
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

# ===== FitForge App-Specific Rules =====
# Keep all entity classes
-keep class com.fitforge.app.data.local.db.entity.** { *; }

# Keep all repository interfaces and implementations
-keep interface com.fitforge.app.domain.repository.** { *; }
-keep class com.fitforge.app.data.repository.** { *; }

# Keep all use cases
-keep class com.fitforge.app.domain.usecase.** { *; }

# Keep ViewModels
-keep class com.fitforge.app.presentation.**.* extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# Keep UI State classes
-keep class com.fitforge.app.presentation.**.*UiState { *; }

# Keep Navigation Screen sealed classes
-keep class com.fitforge.app.navigation.Screen { *; }
-keep class com.fitforge.app.navigation.Screen$* { *; }

# ===== Parcelable =====
-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# ===== Serializable =====
-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# ===== Enum =====
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ===== Native Methods =====
-keepclasseswithmembernames class * {
    native <methods>;
}

# ===== View Constructors =====
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# ===== R Class =====
-keepclassmembers class **.R$* {
    public static <fields>;
}

# ===== Remove Logging =====
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# ===== Crashlytics (if added) =====
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception

# ===== Security =====
# Keep security-sensitive classes
-keep class javax.crypto.** { *; }
-keep class java.security.** { *; }
