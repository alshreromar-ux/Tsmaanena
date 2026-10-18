package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AthkarCategory
import com.example.data.model.AthkarItem
import com.example.data.repository.IslamicDataRepository
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldPrimaryDark
import com.example.ui.theme.EmeraldPrimaryLight
import com.example.ui.theme.GoldBase
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.SoftGoldHighlight
import com.example.ui.theme.StatusCompleted
import com.example.ui.viewmodel.MainUiState

@Composable
fun DhikrTasbihScreen(
    uiState: MainUiState,
    onIncrementSalawat: () -> Unit,
    onResetSalawat: () -> Unit,
    onSetSalawatFormula: (Int) -> Unit,
    onSelectTasbihDhikr: (String) -> Unit,
    onSetTasbihTarget: (Int) -> Unit,
    onIncrementTasbih: () -> Unit,
    onResetTasbih: () -> Unit,
    onSelectAthkarCategory: (AthkarCategory) -> Unit,
    onIncrementAthkarItem: (AthkarItem) -> Unit,
    onResetCategoryAthkar: (AthkarCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTopTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("الصلاة على النبي ﷺ", "السبحة الإلكترونية", "حصن المسلم والأذكار")

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("dhikr_tasbih_screen")
    ) {
        // Top Tab Header
        ScrollableTabRow(
            selectedTabIndex = selectedTopTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = EmeraldPrimary,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTopTab]),
                    color = EmeraldPrimary,
                    height = 3.dp
                )
            }
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTopTab == index,
                    onClick = { selectedTopTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTopTab == index) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    },
                    modifier = Modifier.testTag("dhikr_tab_$index")
                )
            }
        }

        when (selectedTopTab) {
            0 -> SalawatSection(
                uiState = uiState,
                onIncrementSalawat = onIncrementSalawat,
                onResetSalawat = onResetSalawat,
                onSetSalawatFormula = onSetSalawatFormula
            )
            1 -> ElectronicTasbihSection(
                uiState = uiState,
                onSelectDhikr = onSelectTasbihDhikr,
                onSetTarget = onSetTasbihTarget,
                onIncrement = onIncrementTasbih,
                onReset = onResetTasbih
            )
            2 -> HisnMuslimSection(
                uiState = uiState,
                onSelectCategory = onSelectAthkarCategory,
                onIncrementItem = onIncrementAthkarItem,
                onResetCategory = onResetCategoryAthkar
            )
        }
    }
}

// ----------------- 1. Salawat Section -----------------
@Composable
fun SalawatSection(
    uiState: MainUiState,
    onIncrementSalawat: () -> Unit,
    onResetSalawat: () -> Unit,
    onSetSalawatFormula: (Int) -> Unit
) {
    val formulas = IslamicDataRepository.salawatFormulas
    val currentFormula = formulas.getOrElse(uiState.selectedSalawatFormulaIndex) { formulas[0] }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SoftGoldHighlight),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldBase.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "﴿إِنَّ اللَّهَ وَمَلَائِكَتَهُ يُصَلُّونَ عَلَى النَّبِيِّ يَا أَيُّهَا الَّذِينَ آمَنُوا صَلُّوا عَلَيْهِ وَسَلِّمُوا تَسْلِيمًا﴾",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2C240A),
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "سورة الأحزاب - الآية ٥٦",
                        fontSize = 12.sp,
                        color = GoldDark,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Formula Selector Chips
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "اختر صيغة الصلاة على النبي ﷺ:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    formulas.forEachIndexed { index, (name, _) ->
                        FilterChip(
                            selected = uiState.selectedSalawatFormulaIndex == index,
                            onClick = { onSetSalawatFormula(index) },
                            label = { Text(text = name, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EmeraldPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // Active Formula Text Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = currentFormula.first,
                        color = GoldDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = currentFormula.second,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 26.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Big Salawat Counter Button
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(220.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(GoldBase.copy(alpha = 0.25f), Color.Transparent)
                                )
                            )
                    )

                    Surface(
                        modifier = Modifier
                            .size(196.dp)
                            .clip(CircleShape)
                            .clickable { onIncrementSalawat() }
                            .testTag("big_salawat_counter_button"),
                        shape = CircleShape,
                        color = EmeraldPrimary,
                        shadowElevation = 10.dp,
                        border = androidx.compose.foundation.BorderStroke(5.dp, GoldBase)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(EmeraldPrimaryLight, EmeraldPrimary)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Filled.Favorite,
                                    contentDescription = null,
                                    tint = GoldLight,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "${uiState.salawatCounter}",
                                    fontSize = 44.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "صلِّ على الحبيب ﷺ",
                                    fontSize = 13.sp,
                                    color = GoldLight,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "المجموع الكلي: ${uiState.salawatTotal}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedButton(
                        onClick = onResetSalawat,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "تصفير الجلسة", fontSize = 12.sp)
                    }
                }
            }
        }

        // Virtues of Salawat
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "فضائل الصلاة على النبي ﷺ:",
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "• يرفع الله بها المصلي عشر درجات ويحط عنه عشر خطيئات.", fontSize = 12.sp)
                    Text(text = "• سبب لكفاية الهم وتفريج الكرب وغفران الذنوب.", fontSize = 12.sp)
                    Text(text = "• أولى الناس بشفاعة النبي ﷺ أكثرهم صلاة عليه.", fontSize = 12.sp)
                    Text(text = "• الصلاة عليه يوم الجمعة وليلتها معروضة عليه ﷺ.", fontSize = 12.sp)
                }
            }
        }
    }
}

// ----------------- 2. Electronic Tasbih -----------------
@Composable
fun ElectronicTasbihSection(
    uiState: MainUiState,
    onSelectDhikr: (String) -> Unit,
    onSetTarget: (Int) -> Unit,
    onIncrement: () -> Unit,
    onReset: () -> Unit
) {
    val presets = IslamicDataRepository.tasbihPresets
    val target = uiState.tasbihTarget
    val current = uiState.currentTasbihCount
    val progress = if (target > 0) (current.toFloat() / target).coerceIn(0f, 1f) else 0f

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Dhikr Presets Selector
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "اختر الذكر:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presets.forEach { dhikr ->
                        FilterChip(
                            selected = uiState.selectedTasbihDhikr == dhikr,
                            onClick = { onSelectDhikr(dhikr) },
                            label = { Text(text = dhikr, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EmeraldPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // Target Selector: 33, 100, Free
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "الهدف المستهدف:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(Pair(33, "٣٣"), Pair(100, "١٠٠"), Pair(0, "مفتوح")).forEach { (num, label) ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (target == num) GoldBase else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onSetTarget(num) }
                        ) {
                            Text(
                                text = label,
                                color = if (target == num) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // Active Dhikr Display & Progress
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = uiState.selectedTasbihDhikr,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary,
                        textAlign = TextAlign.Center
                    )

                    if (target > 0) {
                        Spacer(modifier = Modifier.height(10.dp))
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = GoldBase,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "$current / $target",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Big Tasbih Counter Button
        item {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(232.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(GoldBase.copy(alpha = 0.22f), Color.Transparent)
                            )
                        )
                )

                Surface(
                    modifier = Modifier
                        .size(204.dp)
                        .clip(CircleShape)
                        .clickable { onIncrement() }
                        .testTag("big_tasbih_button"),
                    shape = CircleShape,
                    color = EmeraldPrimary,
                    shadowElevation = 10.dp,
                    border = androidx.compose.foundation.BorderStroke(5.dp, GoldBase)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(EmeraldPrimaryLight, EmeraldPrimary)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$current",
                                fontSize = 50.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "المس للتسبيح",
                                fontSize = 13.sp,
                                color = GoldLight,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Reset and Total Info
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "الإجمالي: ${uiState.totalTasbihCount}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedButton(
                    onClick = onReset,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "تصفير", fontSize = 12.sp)
                }
            }
        }
    }
}

// ----------------- 3. Hisn Muslim & Athkar -----------------
@Composable
fun HisnMuslimSection(
    uiState: MainUiState,
    onSelectCategory: (AthkarCategory) -> Unit,
    onIncrementItem: (AthkarItem) -> Unit,
    onResetCategory: (AthkarCategory) -> Unit
) {
    val categories = listOf(
        AthkarCategory.MORNING,
        AthkarCategory.EVENING,
        AthkarCategory.POST_PRAYER,
        AthkarCategory.SLEEP,
        AthkarCategory.UNIVERSAL_DUA
    )

    val currentItems = IslamicDataRepository.athkarList.filter { it.category == uiState.selectedAthkarCategory }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 10.dp)
    ) {
        // Category Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { cat ->
                FilterChip(
                    selected = uiState.selectedAthkarCategory == cat,
                    onClick = { onSelectCategory(cat) },
                    label = { Text(text = cat.arabicTitle, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EmeraldPrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Reset Category Button & Count
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${currentItems.size} أذكار في هذا القسم",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "إعادة ضبط القسم ↻",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = EmeraldPrimary,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { onResetCategory(uiState.selectedAthkarCategory) }
                    .padding(4.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(currentItems) { item ->
                val currentCount = uiState.athkarProgress[item.id] ?: 0
                val isCompleted = currentCount >= item.repeatCount

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCompleted) SoftGoldHighlight.copy(alpha = 0.5f)
                        else MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                    border = if (isCompleted) androidx.compose.foundation.BorderStroke(1.dp, GoldBase.copy(alpha = 0.5f)) else null
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = item.arabicText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 26.sp,
                            textAlign = TextAlign.Right
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "✨ الفضل: ${item.virtue}",
                            fontSize = 12.sp,
                            color = GoldDark,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "📚 ${item.reference}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Counter Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "التكرار: ${item.repeatCount} مرات",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Button(
                                onClick = { onIncrementItem(item) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isCompleted) StatusCompleted else EmeraldPrimary
                                ),
                                enabled = !isCompleted
                            ) {
                                if (isCompleted) {
                                    Icon(imageVector = Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("تم بحمد الله")
                                } else {
                                    Text("$currentCount / ${item.repeatCount}")
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
