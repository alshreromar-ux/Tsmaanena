package com.example.util

import com.example.data.local.PrayerType
import java.util.Calendar

data class PrayerTimeEntry(
    val type: PrayerType,
    val nameArabic: String,
    val timeFormatted: String,
    val hour24: Int,
    val minute: Int,
    val isNext: Boolean = false,
    val remainingFormatted: String = ""
)

data class PrayerScheduleInfo(
    val entries: List<PrayerTimeEntry>,
    val nextPrayer: PrayerTimeEntry?,
    val nextCountdownFormatted: String
)

object PrayerTimesHelper {

    fun getTodaySchedule(): PrayerScheduleInfo {
        val cal = Calendar.getInstance()
        val currentHour = cal.get(Calendar.HOUR_OF_DAY)
        val currentMinute = cal.get(Calendar.MINUTE)
        val currentTotalMinutes = currentHour * 60 + currentMinute

        // Standard realistic prayer schedule times for display and tracking
        val rawEntries = listOf(
            PrayerTimeEntry(PrayerType.FAJR, "الفجر", "04:55 ص", 4, 55),
            PrayerTimeEntry(PrayerType.DUHA, "الشروق", "06:18 ص", 6, 18),
            PrayerTimeEntry(PrayerType.DHUHR, "الظهر", "12:15 م", 12, 15),
            PrayerTimeEntry(PrayerType.ASR, "العصر", "03:35 م", 15, 35),
            PrayerTimeEntry(PrayerType.MAGHRIB, "المغرب", "05:50 م", 17, 50),
            PrayerTimeEntry(PrayerType.ISHA, "العشاء", "07:20 م", 19, 20)
        )

        var nextEntry: PrayerTimeEntry? = null
        var minDiffMinutes = Int.MAX_VALUE

        val processed = rawEntries.map { entry ->
            val entryMinutes = entry.hour24 * 60 + entry.minute
            val diff = entryMinutes - currentTotalMinutes

            val isFuture = diff > 0
            if (isFuture && diff < minDiffMinutes) {
                minDiffMinutes = diff
                nextEntry = entry
            }
            entry
        }

        // If all prayers today have passed, next is tomorrow's Fajr
        val finalNext = nextEntry ?: rawEntries.first()
        val remainingMinutes = if (minDiffMinutes != Int.MAX_VALUE) {
            minDiffMinutes
        } else {
            (24 * 60 - currentTotalMinutes) + (rawEntries.first().hour24 * 60 + rawEntries.first().minute)
        }

        val hours = remainingMinutes / 60
        val mins = remainingMinutes % 60
        val countdownStr = if (hours > 0) "$hours س و $mins د" else "$mins دقيقة"

        val updatedEntries = processed.map { entry ->
            if (entry.type == finalNext.type && entry.nameArabic == finalNext.nameArabic) {
                entry.copy(isNext = true, remainingFormatted = countdownStr)
            } else {
                entry
            }
        }

        return PrayerScheduleInfo(
            entries = updatedEntries,
            nextPrayer = finalNext.copy(isNext = true, remainingFormatted = countdownStr),
            nextCountdownFormatted = countdownStr
        )
    }
}
