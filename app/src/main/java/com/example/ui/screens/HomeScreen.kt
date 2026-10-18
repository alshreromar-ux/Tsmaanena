package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.PrayerStatus
import com.example.data.local.PrayerType
import com.example.data.model.AthkarCategory
import com.example.data.repository.IslamicDataRepository
import com.example.ui.components.IslamicSectionHeader
import com.example.ui.theme.AccentCompanions
import com.example.ui.theme.AccentProphets
import com.example.ui.theme.AccentQuran
import com.example.ui.theme.AccentRighteous
import com.example.ui.theme.EmeraldGlow
import com.example.ui.theme.EmeraldMint
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldPrimaryDark
import com.example.ui.theme.EmeraldPrimaryLight
import com.example.ui.theme.EmeraldRoyal
import com.example.ui.theme.EmeraldSoftBg
import com.example.ui.theme.EmeraldVibrant
import com.example.ui.theme.GoldAura
import com.example.ui.theme.GoldBase
import com.example.ui.theme.GoldChampagne
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldDeep
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldRadiant
import com.example.ui.theme.GoldSoftBg
import com.example.ui.theme.SoftGoldHighlight
import com.example.ui.theme.StatusCompleted
import com.example.ui.theme.StatusCongregation
import com.example.ui.theme.StatusLate
import com.example.ui.theme.StatusMissed
import com.example.ui.viewmodel.AppNavDestination
import com.example.ui.viewmodel.MainUiState

@Composable
fun HomeScreen(
    uiState: MainUiState,
    onNavigate: (AppNavDestination) -> Unit,
    onIncrementSalawat: () -> Unit,
    onSelectAthkarCategory: (AthkarCategory) -> Unit,
    onUpdatePrayerStatus: (PrayerType, PrayerStatus) -> Unit,
    onOpenQibla: () -> Unit = {},
    onOpenStreak: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header with Islamic Background Art and Glowing Scrim
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(235.dp)
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            ) {
                // Background image
                Image(
                    painter = painterResource(id = R.drawable.hero_islamic_bg_1792334465033),
                    contentDescription = "خلفية إسلامية",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Atmospheric Scrim Gradient in light fresh Islamic green
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.22f),
                                    EmeraldPrimary.copy(alpha = 0.65f),
                                    EmeraldRoyal.copy(alpha = 0.88f)
                                )
                            )
                        )
                )

                // Header Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 18.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "طُمأنينة",
                                    color = GoldAura,
                                    fontSize = 25.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldGlow)
                                )
                            }
                            Text(
                                text = "السلام عليكم ورحمة الله وبركاته",
                                color = Color.White.copy(alpha = 0.95f),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = EmeraldPrimaryDark.copy(alpha = 0.7f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldBase.copy(alpha = 0.6f)),
                            shadowElevation = 4.dp
                        ) {
                            Text(
                                text = uiState.todayDateFormatted,
                                color = GoldLight,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }

                    // Spiritual Quote Ribbon
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Black.copy(alpha = 0.35f)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldBase.copy(alpha = 0.35f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AutoAwesome,
                                contentDescription = null,
                                tint = GoldAura,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "﴿أَلَا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ﴾",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Live Prayer Times & Next Prayer Countdown Ribbon
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("prayer_schedule_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.5.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.25f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Next Prayer Banner
                    val nextPrayer = uiState.prayerSchedule.nextPrayer
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
                                    .background(EmeraldSoftBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Schedule,
                                    contentDescription = null,
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "الصلاة القادمة: ${nextPrayer?.nameArabic ?: "الفجر"}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "يحين وقتها: ${nextPrayer?.timeFormatted ?: ""}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = SoftGoldHighlight,
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldBase)
                        ) {
                            Text(
                                text = "متبقي ${uiState.prayerSchedule.nextCountdownFormatted}",
                                color = GoldDark,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Prayer Times Horizontal Flow
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        uiState.prayerSchedule.entries.forEach { entry ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (entry.isNext) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                border = if (entry.isNext) androidx.compose.foundation.BorderStroke(1.dp, GoldBase) else null
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = entry.nameArabic,
                                        fontSize = 12.sp,
                                        fontWeight = if (entry.isNext) FontWeight.Bold else FontWeight.Medium,
                                        color = if (entry.isNext) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = entry.timeFormatted,
                                        fontSize = 11.sp,
                                        color = if (entry.isNext) GoldLight else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Quick Actions Row: Qibla Compass & Streak Badges
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Qibla Compass Button
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onOpenQibla() }
                        .testTag("home_qibla_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(EmeraldSoftBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Explore,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "بوصلة القبلة",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "اتجاه مكة المكرمة",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Spiritual Streak & Badges Button
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onOpenStreak() }
                        .testTag("home_streak_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldBase.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(SoftGoldHighlight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocalFireDepartment,
                                contentDescription = null,
                                tint = GoldDark,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "الهمة والأوسمة",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "سجل التتابع اليومي",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Live Quick Salawat on Prophet ﷺ Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("home_quick_salawat_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Brush.horizontalGradient(listOf(GoldBase.copy(alpha = 0.6f), EmeraldVibrant.copy(alpha = 0.3f)))
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    GoldSoftBg.copy(alpha = 0.45f),
                                    MaterialTheme.colorScheme.surface
                                )
                            )
                        )
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.radialGradient(listOf(GoldLight, GoldAura))
                                        )
                                        .border(1.dp, GoldBase, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Favorite,
                                        contentDescription = null,
                                        tint = GoldChampagne,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "الصلاة على النبي ﷺ",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "تفريج للهم وغفران للذنب",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = EmeraldSoftBg,
                                border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldMint.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = "${uiState.salawatCounter} صلاة اليوم",
                                    color = EmeraldPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "اللَّهُمَّ صَلِّ وَسَلِّمْ وَبَارِكْ عَلَى نَبِيِّنَا مُحَمَّدٍ",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldRoyal,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = onIncrementSalawat,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("home_salawat_button"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EmeraldPrimary,
                                contentColor = Color.White
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AutoAwesome,
                                contentDescription = null,
                                tint = GoldAura,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "صلِّ على النبي ﷺ (+١)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Today's Prayer Tracking Quick Bar
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("home_prayer_tracker_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "صلوات اليوم",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            val completedCount = uiState.prayersForSelectedDate.count {
                                it.prayerType.isObligatory && (it.status == PrayerStatus.CONGREGATION || it.status == PrayerStatus.ON_TIME || it.status == PrayerStatus.LATE)
                            }
                            Text(
                                text = "$completedCount من ٥ صلوات مكتملة",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Text(
                            text = "السجل والتفاصيل ←",
                            color = EmeraldPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onNavigate(AppNavDestination.PRAYER_TRACKER) }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val obligatory = listOf(
                        Pair(PrayerType.FAJR, "الفجر"),
                        Pair(PrayerType.DHUHR, "الظهر"),
                        Pair(PrayerType.ASR, "العصر"),
                        Pair(PrayerType.MAGHRIB, "المغرب"),
                        Pair(PrayerType.ISHA, "العشاء")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        obligatory.forEach { (type, name) ->
                            val record = uiState.prayersForSelectedDate.firstOrNull { it.prayerType == type }
                            val status = record?.status ?: PrayerStatus.PENDING

                            val isDone = status == PrayerStatus.CONGREGATION || status == PrayerStatus.ON_TIME
                            val isLate = status == PrayerStatus.LATE
                            val isMissed = status == PrayerStatus.MISSED

                            val (circleBg, iconColor, borderColor) = when {
                                status == PrayerStatus.CONGREGATION -> Triple(StatusCongregation, Color.White, EmeraldMint)
                                status == PrayerStatus.ON_TIME -> Triple(StatusCompleted, Color.White, EmeraldMint)
                                isLate -> Triple(StatusLate, Color.White, GoldRadiant)
                                isMissed -> Triple(StatusMissed, Color.White, Color.Transparent)
                                else -> Triple(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant, Color.Transparent)
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable {
                                        val nextStatus = when (status) {
                                            PrayerStatus.PENDING -> PrayerStatus.ON_TIME
                                            PrayerStatus.ON_TIME -> PrayerStatus.CONGREGATION
                                            PrayerStatus.CONGREGATION -> PrayerStatus.LATE
                                            PrayerStatus.LATE -> PrayerStatus.MISSED
                                            PrayerStatus.MISSED -> PrayerStatus.PENDING
                                        }
                                        onUpdatePrayerStatus(type, nextStatus)
                                    }
                                    .padding(vertical = 4.dp, horizontal = 6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(circleBg)
                                        .border(if (isDone || isLate) 1.5.dp else 0.dp, borderColor, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isDone || isLate) {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = null,
                                            tint = iconColor,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    } else {
                                        Text(
                                            text = name.first().toString(),
                                            color = iconColor,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = name,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        // Daily Ayah & Reflection
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("home_daily_verse_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = GoldSoftBg
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldBase.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(GoldLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Book,
                                    contentDescription = null,
                                    tint = GoldChampagne,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "آية اليوم وتدبر",
                                fontWeight = FontWeight.Bold,
                                color = GoldDeep,
                                fontSize = 14.sp
                            )
                        }

                        IconButton(
                            onClick = {
                                val textToCopy = "${IslamicDataRepository.dailyVerse.ayahText}\n(${IslamicDataRepository.dailyVerse.surahAndAyah})"
                                clipboardManager.setText(AnnotatedString(textToCopy))
                                Toast.makeText(context, "تم نسخ الآية المباركة", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Share,
                                contentDescription = "مشاركة",
                                tint = GoldChampagne,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "﴿${IslamicDataRepository.dailyVerse.ayahText}﴾",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF261D03),
                        lineHeight = 30.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = IslamicDataRepository.dailyVerse.surahAndAyah,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldChampagne,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.85f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldBase.copy(alpha = 0.25f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "💡 تدبر: ${IslamicDataRepository.dailyVerse.reflection}",
                            fontSize = 13.sp,
                            color = Color(0xFF3B2F0C),
                            modifier = Modifier.padding(12.dp),
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }

        // Quick Access Navigation Grid
        item {
            IslamicSectionHeader(title = "أقسام تطبيق طمأنينة")
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HomeNavTile(
                        title = "أذكار الصباح",
                        subtitle = "حصن المسلم",
                        icon = Icons.Filled.WbSunny,
                        badgeColor = AccentProphets,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            onSelectAthkarCategory(AthkarCategory.MORNING)
                            onNavigate(AppNavDestination.DHIKR_TASBIH)
                        }
                    )
                    HomeNavTile(
                        title = "السبحة الإلكترونية",
                        subtitle = "تسبيح واستغفار",
                        icon = Icons.Filled.AutoAwesome,
                        badgeColor = AccentCompanions,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            onNavigate(AppNavDestination.DHIKR_TASBIH)
                        }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HomeNavTile(
                        title = "القصص والعبر",
                        subtitle = "الأنبياء والصحابة",
                        icon = Icons.Filled.MenuBook,
                        badgeColor = AccentRighteous,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            onNavigate(AppNavDestination.STORIES)
                        }
                    )
                    HomeNavTile(
                        title = "القرآن الكريم",
                        subtitle = "السور الفاضلة",
                        icon = Icons.Filled.Book,
                        badgeColor = AccentQuran,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            onNavigate(AppNavDestination.QURAN)
                        }
                    )
                }
            }
        }

        // Daily Hadith Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(EmeraldPrimary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "حديث نبوي شريف",
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "«${IslamicDataRepository.dailyHadith.matn}»",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 24.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = IslamicDataRepository.dailyHadith.narrator,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun HomeNavTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    badgeColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.5.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, badgeColor.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(badgeColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = badgeColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
