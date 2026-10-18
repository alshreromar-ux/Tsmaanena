package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.PrayerRecord
import com.example.data.local.PrayerStatus
import com.example.data.local.PrayerType
import com.example.data.model.AthkarCategory
import com.example.data.model.AthkarItem
import com.example.data.model.IslamicStory
import com.example.data.model.QuranSurahItem
import com.example.data.model.StoryCategory
import com.example.data.repository.IslamicDataRepository
import com.example.data.repository.PrayerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppNavDestination(val titleArabic: String, val shortTitle: String) {
    HOME("الرئيسية", "الرئيسية"),
    PRAYER_TRACKER("تتبع الصلوات", "الصلوات"),
    QIBLA("اتجاه القبلة", "القبلة"),
    DHIKR_TASBIH("الأذكار والسبحة", "الأذكار"),
    STORIES("القصص والعبر", "القصص"),
    QURAN("القرآن الكريم", "القرآن")
}

data class MainUiState(
    val currentDestination: AppNavDestination = AppNavDestination.HOME,
    val selectedDate: String = "",
    val availableDates: List<String> = emptyList(),
    val todayDateFormatted: String = "",
    val prayersForSelectedDate: List<PrayerRecord> = emptyList(),
    val editingPrayerForNotes: PrayerRecord? = null,
    // Dhikr & Salawat
    val salawatCounter: Int = 0,
    val salawatTotal: Long = 0L,
    val selectedSalawatFormulaIndex: Int = 0,
    val selectedTasbihDhikr: String = IslamicDataRepository.tasbihPresets[0],
    val currentTasbihCount: Int = 0,
    val totalTasbihCount: Long = 0L,
    val tasbihTarget: Int = 33, // 33, 100, or 0 (infinity)
    val athkarProgress: Map<String, Int> = emptyMap(),
    val selectedAthkarCategory: AthkarCategory = AthkarCategory.MORNING,
    // Stories
    val storiesCategoryFilter: StoryCategory = StoryCategory.ALL,
    val storiesSearchQuery: String = "",
    val selectedStoryDetail: IslamicStory? = null,
    val bookmarkedStoryIds: Set<String> = emptySet(),
    // Quran
    val selectedSurahDetail: QuranSurahItem? = null,
    // Prayer Schedule & Qibla
    val prayerSchedule: com.example.util.PrayerScheduleInfo = com.example.util.PrayerTimesHelper.getTodaySchedule(),
    val showQiblaCompass: Boolean = false,
    val qiblaHeadingAngle: Float = 138f,
    // Audio Recitation Simulator
    val isPlayingQuranAudio: Boolean = false,
    val audioReciterName: String = "الشيخ مشاري راشد العفاسي",
    // Streak & Milestones
    val showStreakBadgeDialog: Boolean = false,
    // Settings / feedback
    val hapticFeedbackEnabled: Boolean = true
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repo = PrayerRepository(db)

    private val _uiState = MutableStateFlow(
        MainUiState(
            selectedDate = repo.getTodayDateString(),
            availableDates = repo.calculatePastDates(14),
            todayDateFormatted = repo.formatDateForDisplay(repo.getTodayDateString())
        )
    )
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        val today = repo.getTodayDateString()
        viewModelScope.launch {
            repo.ensurePrayersInitializedForDate(today)
            loadTasbihData(_uiState.value.selectedTasbihDhikr)
            loadSalawatData()
        }

        // Observe prayers for selected date
        viewModelScope.launch {
            _uiState.collect { state ->
                repo.getPrayersForDate(state.selectedDate).collect { list ->
                    _uiState.value = _uiState.value.copy(prayersForSelectedDate = list)
                }
            }
        }

        // Observe bookmarks
        viewModelScope.launch {
            repo.getAllBookmarks().collect { bookmarks ->
                val ids = bookmarks.map { it.storyId }.toSet()
                _uiState.value = _uiState.value.copy(bookmarkedStoryIds = ids)
            }
        }
    }

    fun navigateTo(dest: AppNavDestination) {
        _uiState.value = _uiState.value.copy(
            currentDestination = dest,
            selectedStoryDetail = null,
            selectedSurahDetail = null
        )
    }

    fun selectDate(date: String) {
        viewModelScope.launch {
            repo.ensurePrayersInitializedForDate(date)
            _uiState.value = _uiState.value.copy(selectedDate = date)
        }
    }

    fun updatePrayerStatus(prayerType: PrayerType, newStatus: PrayerStatus) {
        triggerHaptic()
        viewModelScope.launch {
            repo.updatePrayerStatus(_uiState.value.selectedDate, prayerType, newStatus)
        }
    }

    fun openNotesDialog(prayer: PrayerRecord) {
        _uiState.value = _uiState.value.copy(editingPrayerForNotes = prayer)
    }

    fun dismissNotesDialog() {
        _uiState.value = _uiState.value.copy(editingPrayerForNotes = null)
    }

    fun savePrayerNotes(notes: String) {
        val current = _uiState.value.editingPrayerForNotes ?: return
        viewModelScope.launch {
            repo.updatePrayerNotes(_uiState.value.selectedDate, current.prayerType, notes)
            dismissNotesDialog()
        }
    }

    // Salawat Counter
    fun incrementSalawat() {
        triggerHaptic()
        val newCount = _uiState.value.salawatCounter + 1
        val newTotal = _uiState.value.salawatTotal + 1L
        _uiState.value = _uiState.value.copy(
            salawatCounter = newCount,
            salawatTotal = newTotal
        )
        viewModelScope.launch {
            repo.incrementTasbih("salawat_prophet")
        }
    }

    fun resetSalawatSession() {
        _uiState.value = _uiState.value.copy(salawatCounter = 0)
    }

    fun setSalawatFormulaIndex(index: Int) {
        _uiState.value = _uiState.value.copy(selectedSalawatFormulaIndex = index)
    }

    // Electronic Tasbih
    fun selectTasbihDhikr(dhikr: String) {
        _uiState.value = _uiState.value.copy(selectedTasbihDhikr = dhikr)
        loadTasbihData(dhikr)
    }

    fun setTasbihTarget(target: Int) {
        _uiState.value = _uiState.value.copy(tasbihTarget = target)
    }

    fun incrementTasbih() {
        triggerHaptic()
        val current = _uiState.value.currentTasbihCount
        val target = _uiState.value.tasbihTarget
        val nextCount = if (target > 0 && current + 1 >= target) {
            triggerTargetCelebrationHaptic()
            0
        } else {
            current + 1
        }

        _uiState.value = _uiState.value.copy(
            currentTasbihCount = nextCount,
            totalTasbihCount = _uiState.value.totalTasbihCount + 1L
        )

        viewModelScope.launch {
            repo.incrementTasbih(_uiState.value.selectedTasbihDhikr)
        }
    }

    fun resetTasbihCurrent() {
        _uiState.value = _uiState.value.copy(currentTasbihCount = 0)
    }

    private fun loadTasbihData(dhikrKey: String) {
        viewModelScope.launch {
            val record = repo.getTasbih(dhikrKey)
            _uiState.value = _uiState.value.copy(
                currentTasbihCount = record?.count ?: 0,
                totalTasbihCount = record?.totalHistoricalCount ?: 0L
            )
        }
    }

    private fun loadSalawatData() {
        viewModelScope.launch {
            val record = repo.getTasbih("salawat_prophet")
            _uiState.value = _uiState.value.copy(
                salawatCounter = record?.count ?: 0,
                salawatTotal = record?.totalHistoricalCount ?: 0L
            )
        }
    }

    // Athkar
    fun selectAthkarCategory(category: AthkarCategory) {
        _uiState.value = _uiState.value.copy(selectedAthkarCategory = category)
    }

    fun incrementAthkarItem(item: AthkarItem) {
        triggerHaptic()
        val current = _uiState.value.athkarProgress[item.id] ?: 0
        val next = (current + 1).coerceAtMost(item.repeatCount)
        val updated = _uiState.value.athkarProgress.toMutableMap()
        updated[item.id] = next
        _uiState.value = _uiState.value.copy(athkarProgress = updated)
    }

    fun resetAthkarProgressForCategory(category: AthkarCategory) {
        val updated = _uiState.value.athkarProgress.toMutableMap()
        IslamicDataRepository.athkarList.filter { it.category == category }.forEach {
            updated.remove(it.id)
        }
        _uiState.value = _uiState.value.copy(athkarProgress = updated)
    }

    // Stories
    fun filterStoriesCategory(category: StoryCategory) {
        _uiState.value = _uiState.value.copy(storiesCategoryFilter = category)
    }

    fun setStoriesSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(storiesSearchQuery = query)
    }

    fun openStoryDetail(story: IslamicStory) {
        _uiState.value = _uiState.value.copy(selectedStoryDetail = story)
    }

    fun closeStoryDetail() {
        _uiState.value = _uiState.value.copy(selectedStoryDetail = null)
    }

    fun toggleStoryBookmark(storyId: String) {
        triggerHaptic()
        val isBookmarked = _uiState.value.bookmarkedStoryIds.contains(storyId)
        viewModelScope.launch {
            repo.toggleBookmark(storyId, isBookmarked)
        }
    }

    // Quran
    fun openSurahDetail(surah: QuranSurahItem) {
        _uiState.value = _uiState.value.copy(selectedSurahDetail = surah)
    }

    fun closeSurahDetail() {
        _uiState.value = _uiState.value.copy(selectedSurahDetail = null)
    }

    // Qibla Compass
    fun openQiblaCompass() {
        triggerHaptic()
        _uiState.value = _uiState.value.copy(showQiblaCompass = true)
    }

    fun closeQiblaCompass() {
        _uiState.value = _uiState.value.copy(showQiblaCompass = false)
    }

    // Audio Simulator
    fun toggleQuranAudio() {
        triggerHaptic()
        _uiState.value = _uiState.value.copy(isPlayingQuranAudio = !_uiState.value.isPlayingQuranAudio)
    }

    // Streak & Badges Dialog
    fun openStreakDialog() {
        triggerHaptic()
        _uiState.value = _uiState.value.copy(showStreakBadgeDialog = true)
    }

    fun closeStreakDialog() {
        _uiState.value = _uiState.value.copy(showStreakBadgeDialog = false)
    }

    // Haptics
    private fun triggerHaptic() {
        if (!_uiState.value.hapticFeedbackEnabled) return
        try {
            val context = getApplication<Application>()
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(20)
            }
        } catch (_: Exception) {}
    }

    private fun triggerTargetCelebrationHaptic() {
        if (!_uiState.value.hapticFeedbackEnabled) return
        try {
            val context = getApplication<Application>()
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(
                    VibrationEffect.createWaveform(
                        longArrayOf(0, 40, 60, 40, 60, 80),
                        -1
                    )
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(150)
            }
        } catch (_: Exception) {}
    }
}
