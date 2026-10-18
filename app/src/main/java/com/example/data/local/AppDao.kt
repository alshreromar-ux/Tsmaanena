package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PrayerDao {
    @Query("SELECT * FROM prayer_records WHERE date = :date ORDER BY timestamp ASC")
    fun getPrayersForDate(date: String): Flow<List<PrayerRecord>>

    @Query("SELECT * FROM prayer_records ORDER BY date DESC")
    fun getAllPrayerRecords(): Flow<List<PrayerRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePrayer(record: PrayerRecord)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePrayers(records: List<PrayerRecord>)

    @Query("SELECT COUNT(*) FROM prayer_records WHERE status IN ('CONGREGATION', 'ON_TIME', 'LATE')")
    fun countTotalPrayed(): Flow<Int>

    @Query("SELECT COUNT(*) FROM prayer_records WHERE date = :date AND status IN ('CONGREGATION', 'ON_TIME')")
    suspend fun countOnTimePrayersForDate(date: String): Int
}

@Dao
interface StoryBookmarkDao {
    @Query("SELECT * FROM story_bookmarks ORDER BY bookmarkedAt DESC")
    fun getAllBookmarks(): Flow<List<StoryBookmark>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBookmark(bookmark: StoryBookmark)

    @Query("DELETE FROM story_bookmarks WHERE storyId = :storyId")
    suspend fun removeBookmark(storyId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM story_bookmarks WHERE storyId = :storyId)")
    fun isBookmarked(storyId: String): Flow<Boolean>
}

@Dao
interface TasbihDao {
    @Query("SELECT * FROM tasbih_records")
    fun getAllTasbih(): Flow<List<TasbihRecord>>

    @Query("SELECT * FROM tasbih_records WHERE dhikrKey = :dhikrKey")
    suspend fun getTasbih(dhikrKey: String): TasbihRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveTasbih(record: TasbihRecord)
}
