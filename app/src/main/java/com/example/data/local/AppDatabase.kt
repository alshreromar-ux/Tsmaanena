package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

class Converters {
    @TypeConverter
    fun fromPrayerType(value: PrayerType): String = value.name

    @TypeConverter
    fun toPrayerType(value: String): PrayerType = try {
        PrayerType.valueOf(value)
    } catch (e: Exception) {
        PrayerType.FAJR
    }

    @TypeConverter
    fun fromPrayerStatus(value: PrayerStatus): String = value.name

    @TypeConverter
    fun toPrayerStatus(value: String): PrayerStatus = try {
        PrayerStatus.valueOf(value)
    } catch (e: Exception) {
        PrayerStatus.PENDING
    }
}

@Database(
    entities = [PrayerRecord::class, StoryBookmark::class, TasbihRecord::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun prayerDao(): PrayerDao
    abstract fun storyBookmarkDao(): StoryBookmarkDao
    abstract fun tasbihDao(): TasbihDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "zad_al_muslim.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
