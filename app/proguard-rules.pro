# Add project specific ProGuard rules here.

# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper
-dontwarn dagger.hilt.android.internal.lifecycle.**

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Database class *
-keep @androidx.room.Dao class *
-keepclassmembers class * extends androidx.room.RoomDatabase {
    public static ** getDatabase(...);
    abstract ** *Dao();
}

# DataStore
-keepclassmembers class * extends androidx.datastore.preferences.protobuf.GeneratedMessageLite {
    <fields>;
}

# Kotlin
-dontwarn kotlin.reflect.jvm.internal.**
-keep class kotlin.Metadata { *; }
-keep class kotlin.reflect.** { *; }

# Keep all model classes (for Room entities and domain models)
-keep class dev.dericbourg.firstclassmetronome.data.entity.** { *; }
-keep class dev.dericbourg.firstclassmetronome.domain.model.** { *; }

# Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**
