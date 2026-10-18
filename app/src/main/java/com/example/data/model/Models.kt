package com.example.data.model

import com.example.data.local.PrayerStatus
import com.example.data.local.PrayerType

enum class StoryCategory(val arabicTitle: String, val iconRes: String) {
    ALL("الكل", "all"),
    PROPHETS("قصص الأنبياء", "prophets"),
    COMPANIONS("قصص الصحابة", "companions"),
    RIGHTEOUS("قصص الصالحين والتابعين", "righteous")
}

data class IslamicStory(
    val id: String,
    val title: String,
    val characterName: String,
    val category: StoryCategory,
    val summary: String,
    val fullStory: String,
    val lessons: List<String>,
    val keyQuote: String,
    val isBookmarked: Boolean = false
)

enum class AthkarCategory(val arabicTitle: String, val badge: String) {
    SALAWAT("الصلاة على النبي ﷺ", "فضل عظيم"),
    MORNING("أذكار الصباح", "حصن الصباح"),
    EVENING("أذكار المساء", "حصن المساء"),
    POST_PRAYER("أذكار بعد الصلاة", "دبر الصلوات"),
    SLEEP("أذكار النوم والاستيقاظ", "سكينة"),
    UNIVERSAL_DUA("أدعية مأثورة جامعة", "دعاء مستجاب")
}

data class AthkarItem(
    val id: String,
    val category: AthkarCategory,
    val arabicText: String,
    val repeatCount: Int,
    val currentCount: Int = 0,
    val virtue: String,
    val reference: String
)

data class QuranSurahItem(
    val number: Int,
    val nameArabic: String,
    val englishName: String,
    val versesCount: Int,
    val revelationType: String,
    val virtues: String,
    val fullText: String
)

data class DailyVerse(
    val ayahText: String,
    val surahAndAyah: String,
    val reflection: String
)

data class DailyHadith(
    val matn: String,
    val narrator: String,
    val benefit: String
)

data class PrayerTimeDisplay(
    val type: PrayerType,
    val timeFormatted: String,
    val isNext: Boolean = false,
    val remainingFormatted: String = ""
)
