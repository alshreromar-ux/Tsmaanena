package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.PrayerRecord
import com.example.data.local.PrayerStatus
import com.example.data.local.PrayerType
import com.example.data.local.StoryBookmark
import com.example.data.local.TasbihRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class PrayerRepository(private val database: AppDatabase) {

    private val prayerDao = database.prayerDao()
    private val bookmarkDao = database.storyBookmarkDao()
    private val tasbihDao = database.tasbihDao()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

    fun getTodayDateString(): String {
        return dateFormat.format(Date())
    }

    fun formatDateForDisplay(dateStr: String): String {
        return try {
            val date = dateFormat.parse(dateStr) ?: Date()
            val outFormat = SimpleDateFormat("EEEE ، d MMMM yyyy", Locale("ar"))
            outFormat.format(date)
        } catch (e: Exception) {
            dateStr
        }
    }

    fun getPrayersForDate(date: String): Flow<List<PrayerRecord>> {
        return prayerDao.getPrayersForDate(date)
    }

    suspend fun ensurePrayersInitializedForDate(date: String) {
        val existing = prayerDao.getPrayersForDate(date).firstOrNull() ?: emptyList()
        val existingTypes = existing.map { it.prayerType }.toSet()

        val allTypes = PrayerType.values()
        val missing = allTypes.filter { it !in existingTypes }.map { type ->
            PrayerRecord(
                date = date,
                prayerType = type,
                status = PrayerStatus.PENDING,
                notes = "",
                timestamp = System.currentTimeMillis()
            )
        }
        if (missing.isNotEmpty()) {
            prayerDao.insertOrUpdatePrayers(missing)
        }
    }

    suspend fun updatePrayerStatus(
        date: String,
        prayerType: PrayerType,
        status: PrayerStatus,
        notes: String? = null
    ) {
        val existingList = prayerDao.getPrayersForDate(date).firstOrNull() ?: emptyList()
        val current = existingList.firstOrNull { it.prayerType == prayerType }
        val updatedRecord = PrayerRecord(
            date = date,
            prayerType = prayerType,
            status = status,
            notes = notes ?: current?.notes ?: "",
            timestamp = System.currentTimeMillis()
        )
        prayerDao.insertOrUpdatePrayer(updatedRecord)
    }

    suspend fun updatePrayerNotes(date: String, prayerType: PrayerType, notes: String) {
        val existingList = prayerDao.getPrayersForDate(date).firstOrNull() ?: emptyList()
        val current = existingList.firstOrNull { it.prayerType == prayerType }
        val updatedRecord = PrayerRecord(
            date = date,
            prayerType = prayerType,
            status = current?.status ?: PrayerStatus.PENDING,
            notes = notes,
            timestamp = System.currentTimeMillis()
        )
        prayerDao.insertOrUpdatePrayer(updatedRecord)
    }

    fun getAllBookmarks(): Flow<List<StoryBookmark>> = bookmarkDao.getAllBookmarks()

    suspend fun toggleBookmark(storyId: String, isCurrentlyBookmarked: Boolean) {
        if (isCurrentlyBookmarked) {
            bookmarkDao.removeBookmark(storyId)
        } else {
            bookmarkDao.addBookmark(StoryBookmark(storyId = storyId))
        }
    }

    fun getAllTasbih(): Flow<List<TasbihRecord>> = tasbihDao.getAllTasbih()

    suspend fun getTasbih(dhikrKey: String): TasbihRecord? = tasbihDao.getTasbih(dhikrKey)

    suspend fun incrementTasbih(dhikrKey: String) {
        val current = tasbihDao.getTasbih(dhikrKey)
        val newCount = (current?.count ?: 0) + 1
        val newTotal = (current?.totalHistoricalCount ?: 0L) + 1L
        tasbihDao.saveTasbih(
            TasbihRecord(
                dhikrKey = dhikrKey,
                count = newCount,
                totalHistoricalCount = newTotal,
                lastUpdated = System.currentTimeMillis()
            )
        )
    }

    suspend fun resetTasbih(dhikrKey: String) {
        val current = tasbihDao.getTasbih(dhikrKey)
        tasbihDao.saveTasbih(
            TasbihRecord(
                dhikrKey = dhikrKey,
                count = 0,
                totalHistoricalCount = current?.totalHistoricalCount ?: 0L,
                lastUpdated = System.currentTimeMillis()
            )
        )
    }

    fun calculatePastDates(daysCount: Int = 14): List<String> {
        val list = mutableListOf<String>()
        val cal = Calendar.getInstance()
        for (i in 0 until daysCount) {
            list.add(dateFormat.format(cal.time))
            cal.add(Calendar.DAY_OF_YEAR, -1)
        }
        return list
    }
}
