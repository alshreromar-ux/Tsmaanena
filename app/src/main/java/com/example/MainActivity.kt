package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.data.local.PrayerStatus
import com.example.ui.components.AppBottomNavBar
import com.example.ui.components.QiblaCompassDialog
import com.example.ui.components.StreakBadgesDialog
import com.example.ui.screens.DhikrTasbihScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PrayerTrackerScreen
import com.example.ui.screens.QuranScreen
import com.example.ui.screens.StoriesScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppNavDestination
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                // Ensure natural Arabic RTL reading and layout flow across the entire app
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    MainAppContent(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    // Handle system back navigation gracefully
    BackHandler(enabled = uiState.selectedStoryDetail != null || uiState.selectedSurahDetail != null || uiState.currentDestination != AppNavDestination.HOME) {
        when {
            uiState.selectedStoryDetail != null -> viewModel.closeStoryDetail()
            uiState.selectedSurahDetail != null -> viewModel.closeSurahDetail()
            uiState.currentDestination != AppNavDestination.HOME -> viewModel.navigateTo(AppNavDestination.HOME)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            AppBottomNavBar(
                currentDestination = uiState.currentDestination,
                onNavigate = { dest -> viewModel.navigateTo(dest) }
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            color = MaterialTheme.colorScheme.background
        ) {
            when (uiState.currentDestination) {
                AppNavDestination.HOME -> {
                    HomeScreen(
                        uiState = uiState,
                        onNavigate = { dest -> viewModel.navigateTo(dest) },
                        onIncrementSalawat = { viewModel.incrementSalawat() },
                        onSelectAthkarCategory = { cat -> viewModel.selectAthkarCategory(cat) },
                        onUpdatePrayerStatus = { type, status -> viewModel.updatePrayerStatus(type, status) },
                        onOpenQibla = { viewModel.navigateTo(AppNavDestination.QIBLA) },
                        onOpenStreak = { viewModel.openStreakDialog() }
                    )
                }
                AppNavDestination.QIBLA -> {
                    com.example.ui.screens.QiblaScreen(
                        onBackToHome = { viewModel.navigateTo(AppNavDestination.HOME) }
                    )
                }
                AppNavDestination.PRAYER_TRACKER -> {
                    PrayerTrackerScreen(
                        uiState = uiState,
                        onSelectDate = { date -> viewModel.selectDate(date) },
                        onUpdatePrayerStatus = { type, status -> viewModel.updatePrayerStatus(type, status) },
                        onOpenNotesDialog = { prayer -> viewModel.openNotesDialog(prayer) },
                        onDismissNotesDialog = { viewModel.dismissNotesDialog() },
                        onSaveNotes = { notes -> viewModel.savePrayerNotes(notes) },
                        onNavigateToQibla = { viewModel.navigateTo(AppNavDestination.QIBLA) }
                    )
                }
                AppNavDestination.DHIKR_TASBIH -> {
                    DhikrTasbihScreen(
                        uiState = uiState,
                        onIncrementSalawat = { viewModel.incrementSalawat() },
                        onResetSalawat = { viewModel.resetSalawatSession() },
                        onSetSalawatFormula = { idx -> viewModel.setSalawatFormulaIndex(idx) },
                        onSelectTasbihDhikr = { dhikr -> viewModel.selectTasbihDhikr(dhikr) },
                        onSetTasbihTarget = { target -> viewModel.setTasbihTarget(target) },
                        onIncrementTasbih = { viewModel.incrementTasbih() },
                        onResetTasbih = { viewModel.resetTasbihCurrent() },
                        onSelectAthkarCategory = { cat -> viewModel.selectAthkarCategory(cat) },
                        onIncrementAthkarItem = { item -> viewModel.incrementAthkarItem(item) },
                        onResetCategoryAthkar = { cat -> viewModel.resetAthkarProgressForCategory(cat) }
                    )
                }
                AppNavDestination.STORIES -> {
                    StoriesScreen(
                        uiState = uiState,
                        onFilterCategory = { cat -> viewModel.filterStoriesCategory(cat) },
                        onSearchQueryChange = { q -> viewModel.setStoriesSearchQuery(q) },
                        onOpenStory = { story -> viewModel.openStoryDetail(story) },
                        onCloseStory = { viewModel.closeStoryDetail() },
                        onToggleBookmark = { id -> viewModel.toggleStoryBookmark(id) }
                    )
                }
                AppNavDestination.QURAN -> {
                    QuranScreen(
                        uiState = uiState,
                        onOpenSurah = { surah -> viewModel.openSurahDetail(surah) },
                        onCloseSurah = { viewModel.closeSurahDetail() }
                    )
                }
            }
        }
    }

    // Interactive Qibla Compass Dialog
    if (uiState.showQiblaCompass) {
        QiblaCompassDialog(
            headingAngle = uiState.qiblaHeadingAngle,
            onDismiss = { viewModel.closeQiblaCompass() }
        )
    }

    // Spiritual Streak & Badges Dialog
    if (uiState.showStreakBadgeDialog) {
        val completedPrayers = uiState.prayersForSelectedDate.count {
            it.status == PrayerStatus.CONGREGATION || it.status == PrayerStatus.ON_TIME
        }
        StreakBadgesDialog(
            salawatCount = uiState.salawatCounter,
            prayersLogged = completedPrayers,
            onDismiss = { viewModel.closeStreakDialog() }
        )
    }
}
