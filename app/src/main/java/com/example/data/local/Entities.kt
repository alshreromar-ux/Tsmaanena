package com.example.data.local

import androidx.room.Entity

enum class PrayerType(val arabicName: String, val orderIndex: Int, val isObligatory: Boolean) {
    FAJR("صلاة الفجر", 1, true),
    DHUHR("صلاة الظهر", 2, true),
    ASR("صلاة العصر", 3, true),
    MAGHRIB("صلاة المغرب", 4, true),
    ISHA("صلاة العشاء", 5, true),
    DUHA("صلاة الضحى (سنة)", 6, false),
    QIYAM("قيام الليل والوتر", 7, false)
}

enum class PrayerStatus(val arabicLabel: String) {
    PENDING("لم تُسجل"),
    CONGREGATION("جماعة"),
    ON_TIME("في وقتها"),
    LATE("قضاء"),
    MISSED("فاتتني")
}

@Entity(tableName = "prayer_records", primaryKeys = ["date", "prayerType"])
data class PrayerRecord(
    val date: String, // format: "yyyy-MM-dd"
    val prayerType: PrayerType,
    val status: PrayerStatus = PrayerStatus.PENDING,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "story_bookmarks")
data class StoryBookmark(
    @androidx.room.PrimaryKey val storyId: String,
    val bookmarkedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "tasbih_records")
data class TasbihRecord(
    @androidx.room.PrimaryKey val dhikrKey: String,
    val count: Int,
    val totalHistoricalCount: Long,
    val lastUpdated: Long = System.currentTimeMillis()
)
