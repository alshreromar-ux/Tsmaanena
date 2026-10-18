package com.example.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.IslamicStory
import com.example.data.model.QuranSurahItem
import com.example.data.model.StoryCategory
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.viewmodel.MainUiState

@Composable
fun QuranStoriesScreen(
    uiState: MainUiState,
    onOpenSurah: (QuranSurahItem) -> Unit,
    onCloseSurah: () -> Unit,
    onFilterStoriesCategory: (StoryCategory) -> Unit,
    onSearchStoriesQueryChange: (String) -> Unit,
    onOpenStory: (IslamicStory) -> Unit,
    onCloseStory: () -> Unit,
    onToggleBookmark: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // If a detail view is open (Surah or Story), show it directly
    if (uiState.selectedSurahDetail != null) {
        SurahReaderView(
            surah = uiState.selectedSurahDetail,
            onBack = onCloseSurah
        )
        return
    }

    if (uiState.selectedStoryDetail != null) {
        StoryDetailView(
            story = uiState.selectedStoryDetail,
            isBookmarked = uiState.bookmarkedStoryIds.contains(uiState.selectedStoryDetail.id),
            onBack = onCloseStory,
            onToggleBookmark = { onToggleBookmark(uiState.selectedStoryDetail.id) }
        )
        return
    }

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("القرآن الكريم والسور", "قصص الأنبياء والصحابة")

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("quran_stories_screen")
    ) {
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = EmeraldPrimary,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = EmeraldPrimary,
                    height = 3.dp
                )
            }
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    },
                    modifier = Modifier.testTag("quran_stories_tab_$index")
                )
            }
        }

        when (selectedTab) {
            0 -> QuranSurahsListView(
                onOpenSurah = onOpenSurah
            )
            1 -> StoriesListView(
                uiState = uiState,
                onFilterCategory = onFilterStoriesCategory,
                onSearchQueryChange = onSearchStoriesQueryChange,
                onOpenStory = onOpenStory,
                onToggleBookmark = onToggleBookmark
            )
        }
    }
}
