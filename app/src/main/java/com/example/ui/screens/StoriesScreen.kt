package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import com.example.data.model.IslamicStory
import com.example.data.model.StoryCategory
import com.example.data.repository.IslamicDataRepository
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldPrimaryDark
import com.example.ui.theme.GoldBase
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.SoftGoldHighlight
import com.example.ui.viewmodel.MainUiState

@Composable
fun StoriesScreen(
    uiState: MainUiState,
    onFilterCategory: (StoryCategory) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onOpenStory: (IslamicStory) -> Unit,
    onCloseStory: () -> Unit,
    onToggleBookmark: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (uiState.selectedStoryDetail != null) {
        StoryDetailView(
            story = uiState.selectedStoryDetail,
            isBookmarked = uiState.bookmarkedStoryIds.contains(uiState.selectedStoryDetail.id),
            onBack = onCloseStory,
            onToggleBookmark = { onToggleBookmark(uiState.selectedStoryDetail.id) }
        )
    } else {
        StoriesListView(
            uiState = uiState,
            onFilterCategory = onFilterCategory,
            onSearchQueryChange = onSearchQueryChange,
            onOpenStory = onOpenStory,
            onToggleBookmark = onToggleBookmark,
            modifier = modifier
        )
    }
}

@Composable
fun StoriesListView(
    uiState: MainUiState,
    onFilterCategory: (StoryCategory) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onOpenStory: (IslamicStory) -> Unit,
    onToggleBookmark: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val allStories = IslamicDataRepository.storiesList
    val filteredStories = allStories.filter { story ->
        val matchesCategory = uiState.storiesCategoryFilter == StoryCategory.ALL || story.category == uiState.storiesCategoryFilter
        val matchesSearch = uiState.storiesSearchQuery.isBlank() ||
                story.title.contains(uiState.storiesSearchQuery, ignoreCase = true) ||
                story.characterName.contains(uiState.storiesSearchQuery, ignoreCase = true) ||
                story.summary.contains(uiState.storiesSearchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("stories_list_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Screen Title
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                Text(
                    text = "قصص وعبر إسلامية",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "﴿لَقَدْ كَانَ فِي قَصَصِهِمْ عِبْرَةٌ لِّأُولِي الْأَلْبَابِ﴾",
                    style = MaterialTheme.typography.bodyMedium,
                    color = EmeraldPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = uiState.storiesSearchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("stories_search_input"),
                placeholder = { Text("ابحث عن قصة، نبي، صحابي، أو عبرة...", fontSize = 13.sp) },
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                },
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )
        }

        // Category Filter Chips
        item {
            val categories = listOf(
                StoryCategory.ALL,
                StoryCategory.PROPHETS,
                StoryCategory.COMPANIONS,
                StoryCategory.RIGHTEOUS
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    FilterChip(
                        selected = uiState.storiesCategoryFilter == cat,
                        onClick = { onFilterCategory(cat) },
                        label = { Text(text = cat.arabicTitle, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // Stories Count
        item {
            Text(
                text = "${filteredStories.size} قصص متاحة",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // Stories Cards List
        items(filteredStories) { story ->
            val isBookmarked = uiState.bookmarkedStoryIds.contains(story.id)
            StoryCard(
                story = story,
                isBookmarked = isBookmarked,
                onClick = { onOpenStory(story) },
                onToggleBookmark = { onToggleBookmark(story.id) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun StoryCard(
    story: IslamicStory,
    isBookmarked: Boolean,
    onClick: () -> Unit,
    onToggleBookmark: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("story_card_${story.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (story.category) {
                        StoryCategory.PROPHETS -> EmeraldPrimary.copy(alpha = 0.12f)
                        StoryCategory.COMPANIONS -> GoldDark.copy(alpha = 0.12f)
                        else -> Color(0xFF1E88E5).copy(alpha = 0.12f)
                    }
                ) {
                    Text(
                        text = story.category.arabicTitle,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (story.category) {
                            StoryCategory.PROPHETS -> EmeraldPrimary
                            StoryCategory.COMPANIONS -> GoldDark
                            else -> Color(0xFF1E88E5)
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                IconButton(
                    onClick = onToggleBookmark,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                        contentDescription = "حفظ القصة",
                        tint = if (isBookmarked) GoldDark else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = story.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = story.summary,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = SoftGoldHighlight
                ) {
                    Text(
                        text = "💡 ${story.lessons.size} دروس وعبر مستفادة",
                        fontSize = 11.sp,
                        color = GoldDark,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Text(
                    text = "اقرأ القصة والعبرة ←",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldPrimary
                )
            }
        }
    }
}

// ----------------- Story Reader View with Extracted Lessons -----------------
@Composable
fun StoryDetailView(
    story: IslamicStory,
    isBookmarked: Boolean,
    onBack: () -> Unit,
    onToggleBookmark: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("story_detail_view"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top App Bar
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "رجوع")
                    }

                    Text(
                        text = story.characterName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Row {
                        IconButton(
                            onClick = {
                                val lessonsFormatted = story.lessons.joinToString("\n• ")
                                val shareText = "${story.title}\n\n${story.summary}\n\nالدروس المستفادة:\n• $lessonsFormatted\n\nمن تطبيق طُمأنينة"
                                clipboardManager.setText(AnnotatedString(shareText))
                                Toast.makeText(context, "تم نسخ ملخص القصة والدروس المستفادة", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(imageVector = Icons.Filled.Share, contentDescription = "مشاركة")
                        }

                        IconButton(onClick = onToggleBookmark) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                                contentDescription = "حفظ",
                                tint = if (isBookmarked) GoldDark else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Title and Category Header
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EmeraldPrimary.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = story.category.arabicTitle,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = story.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        // Key Quote Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SoftGoldHighlight),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldBase.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = story.keyQuote,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF332906),
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp
                    )
                }
            }
        }

        // Full Narrative Text
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "تفاصيل القصة المباركة",
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary,
                        fontSize = 15.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = story.fullStory,
                        fontSize = 15.sp,
                        lineHeight = 28.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Justify
                    )
                }
            }
        }

        // Extracted Lessons & Morals (الدروس والعبر المستفادة)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("story_lessons_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldBase)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(GoldBase),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "💡", fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "الدروس والعبر المستفادة من القصة:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = GoldDark
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    story.lessons.forEachIndexed { index, lesson ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 6.dp)
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldPrimary)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = lesson,
                                fontSize = 14.sp,
                                lineHeight = 22.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
