package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.PrayerRecord
import com.example.data.local.PrayerStatus
import com.example.data.local.PrayerType
import com.example.ui.components.PrayerStatusBadge
import com.example.ui.theme.EmeraldMint
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldPrimaryLight
import com.example.ui.theme.EmeraldSoftBg
import com.example.ui.theme.GoldBase
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.SoftGoldHighlight
import com.example.ui.theme.StatusCompleted
import com.example.ui.theme.StatusCongregation
import com.example.ui.theme.StatusLate
import com.example.ui.theme.StatusMissed
import com.example.ui.theme.StatusPending
import com.example.ui.viewmodel.MainUiState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PrayerTrackerScreen(
    uiState: MainUiState,
    onSelectDate: (String) -> Unit,
    onUpdatePrayerStatus: (PrayerType, PrayerStatus) -> Unit,
    onOpenNotesDialog: (PrayerRecord) -> Unit,
    onDismissNotesDialog: () -> Unit,
    onSaveNotes: (String) -> Unit,
    onNavigateToQibla: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val obligatoryPrayers = uiState.prayersForSelectedDate
        .filter { it.prayerType.isObligatory }
        .sortedBy { it.prayerType.orderIndex }

    val voluntaryPrayers = uiState.prayersForSelectedDate
        .filter { !it.prayerType.isObligatory }
        .sortedBy { it.prayerType.orderIndex }

    val completedCount = obligatoryPrayers.count {
        it.status == PrayerStatus.CONGREGATION || it.status == PrayerStatus.ON_TIME || it.status == PrayerStatus.LATE
    }
    val missedCount = obligatoryPrayers.count { it.status == PrayerStatus.MISSED }
    val progressFraction = if (obligatoryPrayers.isNotEmpty()) completedCount.toFloat() / obligatoryPrayers.size else 0f

    val isSelectedToday = uiState.selectedDate == (uiState.availableDates.firstOrNull() ?: "")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("prayer_tracker_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Screen Header with Qibla Button
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "سجل وتتبع الصلوات",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "﴿إِنَّ الصَّلَاةَ كَانَتْ عَلَى الْمُؤْمِنِينَ كِتَابًا مَّوْقُوتًا﴾",
                        style = MaterialTheme.typography.bodyMedium,
                        color = EmeraldPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = EmeraldSoftBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.35f)),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onNavigateToQibla() }
                        .testTag("tracker_qibla_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Explore,
                            contentDescription = "القبلة",
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "القبلة",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary
                        )
                    }
                }
            }
        }

        // Date Selector Bar
        item {
            val dateScrollState = rememberScrollState()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(dateScrollState)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                uiState.availableDates.forEachIndexed { index, dateStr ->
                    val isSelected = dateStr == uiState.selectedDate
                    val label = when (index) {
                        0 -> "اليوم"
                        1 -> "أمس"
                        else -> {
                            val parts = dateStr.split("-")
                            if (parts.size == 3) "${parts[2]}/${parts[1]}" else dateStr
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onSelectDate(dateStr) }
                            .testTag("date_chip_$dateStr"),
                        color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, GoldBase) else null
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            )
                            if (index > 1) {
                                Text(
                                    text = dateStr,
                                    color = if (isSelected) GoldLight else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Daily Progress Summary Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("prayer_daily_progress_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            androidx.compose.ui.graphics.Brush.linearGradient(
                                colors = listOf(
                                    com.example.ui.theme.EmeraldSoftBg.copy(alpha = 0.5f),
                                    MaterialTheme.colorScheme.surface
                                )
                            )
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isSelectedToday) "إنجاز صلوات اليوم" else "إنجاز يوم ${uiState.selectedDate}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = when {
                                    completedCount == 5 -> "ما شاء الله! أتممت صلواتك الخمس كاملة 🌟"
                                    completedCount > 0 -> "أدّيت $completedCount صلوات، واصل الحفاظ عليها"
                                    else -> "سجّل صلواتك أولاً بأول لنيل عظيم الأجر"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = StatusCompleted.copy(alpha = 0.15f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, StatusCompleted.copy(alpha = 0.3f))
                                ) {
                                    Text(
                                        text = "مؤداة: $completedCount",
                                        color = StatusCompleted,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                                if (missedCount > 0) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = StatusMissed.copy(alpha = 0.15f),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, StatusMissed.copy(alpha = 0.3f))
                                    ) {
                                        Text(
                                            text = "فاتت: $missedCount",
                                            color = StatusMissed,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(76.dp)
                        ) {
                            CircularProgressIndicator(
                                progress = { progressFraction },
                                modifier = Modifier.fillMaxSize(),
                                color = EmeraldPrimary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                strokeWidth = 8.dp,
                                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${(progressFraction * 100).toInt()}%",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = EmeraldPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section Title: Obligatory Prayers
        item {
            Text(
                text = "الصلوات المكتوبة (الفرائض الخمس)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
            )
        }

        // Obligatory Prayers List
        items(obligatoryPrayers) { prayer ->
            PrayerRecordCard(
                prayer = prayer,
                onUpdateStatus = { status -> onUpdatePrayerStatus(prayer.prayerType, status) },
                onOpenNotes = { onOpenNotesDialog(prayer) }
            )
        }

        // Section Title: Sunnah & Voluntary Prayers
        if (voluntaryPrayers.isNotEmpty()) {
            item {
                Text(
                    text = "السنن والنوافل المباركة",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            items(voluntaryPrayers) { prayer ->
                PrayerRecordCard(
                    prayer = prayer,
                    onUpdateStatus = { status -> onUpdatePrayerStatus(prayer.prayerType, status) },
                    onOpenNotes = { onOpenNotesDialog(prayer) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Notes Dialog
    if (uiState.editingPrayerForNotes != null) {
        val prayer = uiState.editingPrayerForNotes
        var notesText by remember(prayer) { mutableStateOf(prayer.notes) }

        val suggestions = listOf(
            "جماعة في المسجد",
            "مع تكبيرة الإحرام",
            "صليت السنن الرواتب",
            "صلاة خاشعة بتدبر",
            "قراءة سورة الملك بعدها",
            "أداء الأذكار كاملة"
        )

        AlertDialog(
            onDismissRequest = onDismissNotesDialog,
            title = {
                Text(
                    text = "ملاحظات حول ${prayer.prayerType.arabicName}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = "أضف ملاحظة حول حضور القلب، السنن، أو أي تفاصيل خاصة بأداء الصلاة:",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick Chips Suggestions
                    Text(
                        text = "اقتراحات سريعة:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        suggestions.forEach { chipText ->
                            FilterChip(
                                selected = notesText.contains(chipText),
                                onClick = {
                                    notesText = if (notesText.isBlank()) {
                                        chipText
                                    } else if (!notesText.contains(chipText)) {
                                        "$notesText ، $chipText"
                                    } else {
                                        notesText
                                    }
                                },
                                label = { Text(text = chipText, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SoftGoldHighlight,
                                    selectedLabelColor = GoldDark
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = notesText,
                        onValueChange = { notesText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .testTag("prayer_notes_input"),
                        placeholder = { Text("اكتب ملاحظاتك هنا...", fontSize = 13.sp) },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { onSaveNotes(notesText) },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    modifier = Modifier.testTag("save_notes_button")
                ) {
                    Text("حفظ الملاحظة")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissNotesDialog) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@Composable
fun PrayerRecordCard(
    prayer: PrayerRecord,
    onUpdateStatus: (PrayerStatus) -> Unit,
    onOpenNotes: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .testTag("prayer_card_${prayer.prayerType.name.lowercase()}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Name & Current Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                if (prayer.prayerType.isObligatory) EmeraldPrimary.copy(alpha = 0.12f)
                                else GoldDark.copy(alpha = 0.12f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (prayer.prayerType.isObligatory) Icons.Filled.Mosque else Icons.Filled.Star,
                            contentDescription = null,
                            tint = if (prayer.prayerType.isObligatory) EmeraldPrimary else GoldDark,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = prayer.prayerType.arabicName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                PrayerStatusBadge(status = prayer.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons Row: جماعة، في وقتها، قضاء، فاتتني
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                StatusActionButton(
                    label = "جماعة",
                    isSelected = prayer.status == PrayerStatus.CONGREGATION,
                    activeColor = StatusCongregation,
                    modifier = Modifier.weight(1f),
                    onClick = { onUpdateStatus(PrayerStatus.CONGREGATION) }
                )
                StatusActionButton(
                    label = "في وقتها",
                    isSelected = prayer.status == PrayerStatus.ON_TIME,
                    activeColor = StatusCompleted,
                    modifier = Modifier.weight(1f),
                    onClick = { onUpdateStatus(PrayerStatus.ON_TIME) }
                )
                StatusActionButton(
                    label = "قضاء",
                    isSelected = prayer.status == PrayerStatus.LATE,
                    activeColor = StatusLate,
                    modifier = Modifier.weight(1f),
                    onClick = { onUpdateStatus(PrayerStatus.LATE) }
                )
                StatusActionButton(
                    label = "فاتتني",
                    isSelected = prayer.status == PrayerStatus.MISSED,
                    activeColor = StatusMissed,
                    modifier = Modifier.weight(1f),
                    onClick = { onUpdateStatus(PrayerStatus.MISSED) }
                )
            }

            // Notes Display & Edit Row
            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onOpenNotes() },
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (prayer.notes.isNotBlank()) Icons.Filled.EditNote else Icons.Filled.AddComment,
                            contentDescription = null,
                            tint = if (prayer.notes.isNotBlank()) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (prayer.notes.isNotBlank()) prayer.notes else "إضافة ملاحظة حول أدائك...",
                            fontSize = 12.sp,
                            color = if (prayer.notes.isNotBlank()) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }

                    Text(
                        text = if (prayer.notes.isNotBlank()) "تعديل" else "+ إضافة",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun StatusActionButton(
    label: String,
    isSelected: Boolean,
    activeColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        color = if (isSelected) activeColor else MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            modifier = Modifier.padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
