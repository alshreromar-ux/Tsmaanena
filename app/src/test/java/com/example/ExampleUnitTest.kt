package com.example

import com.example.data.model.StoryCategory
import com.example.data.repository.IslamicDataRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testStoriesContentAndLessonsIntegrity() {
        val stories = IslamicDataRepository.storiesList
        assertTrue("Should have multiple stories available", stories.size >= 5)

        // Verify each story has non-empty title, summary, full story, and lessons
        stories.forEach { story ->
            assertNotNull(story.id)
            assertTrue("Story title should not be empty", story.title.isNotBlank())
            assertTrue("Story characterName should not be empty", story.characterName.isNotBlank())
            assertTrue("Story should have extracted lessons", story.lessons.isNotEmpty())
            assertTrue("Story full narrative should be detailed", story.fullStory.length > 50)
        }

        // Verify categories exist
        val prophets = stories.filter { it.category == StoryCategory.PROPHETS }
        val companions = stories.filter { it.category == StoryCategory.COMPANIONS }
        val righteous = stories.filter { it.category == StoryCategory.RIGHTEOUS }

        assertTrue("Should contain Prophet stories", prophets.isNotEmpty())
        assertTrue("Should contain Companion stories", companions.isNotEmpty())
        assertTrue("Should contain Righteous stories", righteous.isNotEmpty())
    }

    @Test
    fun testAthkarAndSalawatFormulas() {
        val athkar = IslamicDataRepository.athkarList
        assertTrue("Should have diverse Athkar items", athkar.size >= 10)

        val salawat = IslamicDataRepository.salawatFormulas
        assertTrue("Should have multiple Salawat formulas", salawat.size >= 3)
    }

    @Test
    fun testQuranSurahs() {
        val surahs = IslamicDataRepository.quranSurahs
        assertTrue("Should contain key Surahs", surahs.size >= 4)
        val fatihah = surahs.firstOrNull { it.number == 1 }
        assertNotNull("Should contain Al-Fatihah", fatihah)
        assertEquals(7, fatihah?.versesCount)
    }

    @Test
    fun testPrayerTimesHelper() {
        val schedule = com.example.util.PrayerTimesHelper.getTodaySchedule()
        assertEquals(6, schedule.entries.size)
        assertNotNull("Should detect next prayer", schedule.nextPrayer)
        assertTrue("Countdown should not be blank", schedule.nextCountdownFormatted.isNotBlank())
    }
}
