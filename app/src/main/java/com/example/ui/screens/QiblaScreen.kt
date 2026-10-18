package com.example.ui.screens

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldGlow
import com.example.ui.theme.EmeraldMint
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldPrimaryDark
import com.example.ui.theme.EmeraldPrimaryLight
import com.example.ui.theme.EmeraldSoftBg
import com.example.ui.theme.GoldAura
import com.example.ui.theme.GoldBase
import com.example.ui.theme.GoldChampagne
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.SoftGoldHighlight
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

data class QiblaCity(
    val name: String,
    val country: String,
    val qiblaAngle: Float,
    val distanceKm: String
)

val defaultQiblaCities = listOf(
    QiblaCity("مكة المكرمة", "السعودية", 0f, "في رحاب البيت العتيق"),
    QiblaCity("المدينة المنورة", "السعودية", 170f, "٣٤٠ كم"),
    QiblaCity("الرياض", "السعودية", 245f, "٧٩٠ كم"),
    QiblaCity("جدة", "السعودية", 110f, "٧٥ كم"),
    QiblaCity("القاهرة", "مصر", 136f, "١,٢٨٠ كم"),
    QiblaCity("الإسكندرية", "مصر", 141f, "١,٤٣٠ كم"),
    QiblaCity("القدس الشريف", "فلسطين", 155f, "١,٢٤٠ كم"),
    QiblaCity("دبي", "الإمارات", 260f, "١,٦٥٠ كم"),
    QiblaCity("أبوظبي", "الإمارات", 258f, "١,٥١٠ كم"),
    QiblaCity("الدوحة", "قطر", 254f, "١,٣٢٠ كم"),
    QiblaCity("الكويت", "الكويت", 225f, "١,٢١٠ كم"),
    QiblaCity("المنامة", "البحرين", 248f, "١,٢٣٠ كم"),
    QiblaCity("مسقط", "عُمان", 274f, "١,٩٤٠ كم"),
    QiblaCity("عمّان", "الأردن", 160f, "١,٢٣٠ كم"),
    QiblaCity("بغداد", "العراق", 198f, "١,٣٩٠ كم"),
    QiblaCity("دمشق", "سوريا", 165f, "١,٣٨٠ كم"),
    QiblaCity("بيروت", "لبنان", 162f, "١,٤٧٠ كم"),
    QiblaCity("طرابلس", "ليبيا", 114f, "٢,٨٢٠ كم"),
    QiblaCity("تونس", "تونس", 117f, "٣,٣٨٠ كم"),
    QiblaCity("الجزائر", "الجزائر", 108f, "٣,٩٢٠ كم"),
    QiblaCity("الرباط", "المغرب", 98f, "٤,٧٨٠ كم"),
    QiblaCity("الخرطوم", "السودان", 55f, "١,٠٢٠ كم"),
    QiblaCity("صنعاء", "اليمن", 355f, "٨٣٠ كم"),
    QiblaCity("إسطنبول", "تركيا", 152f, "٢,٤١٠ كم"),
    QiblaCity("لندن", "بريطانيا", 118f, "٤,٧٩٠ كم"),
    QiblaCity("باريس", "فرنسا", 120f, "٤,٤٩٠ كم")
)

@Composable
fun QiblaScreen(
    onBackToHome: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedCity by remember { mutableStateOf(defaultQiblaCities[4]) } // Cairo default
    var deviceAzimuth by remember { mutableFloatStateOf(0f) }
    var hasVibratedOnAlign by remember { mutableStateOf(false) }

    // Sensor listener for real compass rotation
    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val rotationSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
            ?: sensorManager?.getDefaultSensor(Sensor.TYPE_ORIENTATION)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null) return
                if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                    val rotationMatrix = FloatArray(9)
                    SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                    val orientation = FloatArray(3)
                    SensorManager.getOrientation(rotationMatrix, orientation)
                    val azimuthRad = orientation[0]
                    var azimuthDeg = Math.toDegrees(azimuthRad.toDouble()).toFloat()
                    if (azimuthDeg < 0) azimuthDeg += 360f
                    deviceAzimuth = azimuthDeg
                } else if (event.sensor.type == Sensor.TYPE_ORIENTATION) {
                    deviceAzimuth = event.values[0]
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (rotationSensor != null) {
            sensorManager?.registerListener(listener, rotationSensor, SensorManager.SENSOR_DELAY_UI)
        }

        onDispose {
            sensorManager?.unregisterListener(listener)
        }
    }

    // Relative angle toward Kaaba
    val relativeAngle = (selectedCity.qiblaAngle - deviceAzimuth + 360f) % 360f
    val isAligned = abs(relativeAngle) < 6f || abs(relativeAngle - 360f) < 6f

    // Vibrate haptic when alignment is reached!
    LaunchedEffect(isAligned) {
        if (isAligned && !hasVibratedOnAlign) {
            hasVibratedOnAlign = true
            try {
                val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                    vm?.defaultVibrator
                } else {
                    @Suppress("DEPRECATION")
                    context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(100)
                }
            } catch (_: Exception) {}
        } else if (!isAligned) {
            hasVibratedOnAlign = false
        }
    }

    val animatedNeedleRotation by animateFloatAsState(
        targetValue = relativeAngle,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "qiblaNeedle"
    )

    val dialBorderColor by animateColorAsState(
        targetValue = if (isAligned) EmeraldMint else GoldBase,
        label = "dialBorderColor"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("qibla_screen"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Screen Title Header with Back Action
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackToHome,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(EmeraldSoftBg)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "الرجوع للرئيسية",
                        tint = EmeraldPrimary
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "اتجاه القبلة المشرفة",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "﴿فَوَلِّ وَجْهَكَ شَطْرَ الْمَسْجِدِ الْحَرَامِ﴾",
                        style = MaterialTheme.typography.bodySmall,
                        color = EmeraldPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isAligned) EmeraldSoftBg else SoftGoldHighlight,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isAligned) EmeraldMint else GoldBase
                    )
                ) {
                    Text(
                        text = if (isAligned) "متجه للكعبة ✓" else "${selectedCity.qiblaAngle.toInt()}°",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (isAligned) EmeraldPrimary else GoldDark,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // City Selector Chips
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "اختر مدينتك لتحديد زاوية القبلة بدقة:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    defaultQiblaCities.forEach { city ->
                        val isSelected = selectedCity.name == city.name
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCity = city },
                            label = {
                                Text(
                                    text = "${city.name} (${city.qiblaAngle.toInt()}°)",
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EmeraldPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // Live Alignment Banner
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                color = if (isAligned) EmeraldSoftBg else SoftGoldHighlight,
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    if (isAligned) EmeraldMint else GoldBase.copy(alpha = 0.5f)
                ),
                shadowElevation = 3.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isAligned) "✨" else "🧭",
                        fontSize = 26.sp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isAligned)
                                "ما شاء الله! أنت الآن متجه نحو الكعبة المشرفة تماماً"
                            else
                                "وجّه الهاتف حتى تلتقي الإبرة مع علامة الكعبة المشرفة",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (isAligned) EmeraldPrimary else GoldDark
                        )
                        Text(
                            text = "المدينة: ${selectedCity.name} • زاوية القبلة: ${selectedCity.qiblaAngle.toInt()}° • المسافة: ${selectedCity.distanceKm}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // The Grand Luxury Animated Compass
        item {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .size(290.dp),
                contentAlignment = Alignment.Center
            ) {
                // Outer Luminous Glow Aura
                Box(
                    modifier = Modifier
                        .size(285.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    (if (isAligned) EmeraldGlow else GoldAura).copy(alpha = 0.35f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Master Outer Compass Dial
                Surface(
                    modifier = Modifier
                        .size(265.dp)
                        .clip(CircleShape)
                        .shadow(12.dp, CircleShape),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(5.dp, dialBorderColor)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        // Islamic Geometric Dial Canvas Drawing
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val center = Offset(size.width / 2, size.height / 2)
                            val radius = size.minDimension / 2 - 12.dp.toPx()

                            // Draw degree ticks
                            for (angle in 0 until 360 step 15) {
                                val rad = Math.toRadians(angle.toDouble())
                                val isMajor = angle % 45 == 0
                                val tickLength = if (isMajor) 14.dp.toPx() else 7.dp.toPx()
                                val startX = center.x + (radius - tickLength) * cos(rad).toFloat()
                                val startY = center.y + (radius - tickLength) * sin(rad).toFloat()
                                val endX = center.x + radius * cos(rad).toFloat()
                                val endY = center.y + radius * sin(rad).toFloat()

                                drawLine(
                                    color = if (isMajor) GoldDark else Color(0xFFCBD5E1),
                                    start = Offset(startX, startY),
                                    end = Offset(endX, endY),
                                    strokeWidth = if (isMajor) 2.5f else 1.5f
                                )
                            }

                            // Inner decorative Islamic star ring
                            drawCircle(
                                color = GoldBase.copy(alpha = 0.25f),
                                radius = radius * 0.72f,
                                center = center,
                                style = Stroke(width = 1.5.dp.toPx())
                            )
                        }

                        // Cardinal Directions
                        Text(
                            text = "شمال (N)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 16.dp)
                        )
                        Text(
                            text = "جنوب (S)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 16.dp)
                        )
                        Text(
                            text = "شرق (E)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 16.dp)
                        )
                        Text(
                            text = "غرب (W)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = 16.dp)
                        )

                        // Kaaba Target Marker Fixed at Qibla Angle
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .rotate(selectedCity.qiblaAngle),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = EmeraldPrimary,
                                shadowElevation = 6.dp,
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldAura),
                                modifier = Modifier.padding(top = 28.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "🕋", fontSize = 13.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "القبلة",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }

                        // Rotating Pointer Needle toward Qibla
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .rotate(animatedNeedleRotation),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(bottom = 78.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Navigation,
                                    contentDescription = null,
                                    tint = if (isAligned) EmeraldMint else EmeraldPrimary,
                                    modifier = Modifier.size(42.dp)
                                )
                            }
                        }

                        // Central Jewel Hub
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(listOf(GoldLight, GoldBase))
                                )
                                .border(2.5.dp, Color.White, CircleShape)
                                .shadow(4.dp, CircleShape)
                        )
                    }
                }
            }
        }

        // Live Azimuth Readout Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "درجة هاتفك الحالية",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${deviceAzimuth.toInt()}°",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(36.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "اتجاه القبلة المطلوب",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${selectedCity.qiblaAngle.toInt()}°",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(36.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "حالة المطابقة",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isAligned) "مطابقة ✓" else "وجّه الهاتف",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isAligned) EmeraldPrimary else GoldDark
                        )
                    }
                }
            }
        }

        // Spiritual Advice Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SoftGoldHighlight),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldBase.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "💡", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "إرشادات دقة البوصلة:",
                            fontWeight = FontWeight.Bold,
                            color = GoldDark,
                            fontSize = 13.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "• امسك الهاتف أفقياً مستوياً في راحة يدك أو ضعه على طاولة.\n• لتحسين دقة الحساس، قم بتحريك الهاتف برسم الرقم (8) في الهواء.\n• ابتعد عن الأجهزة الكهربائية والمغناطيسية القوية للحصول على أدق نتيجة.",
                        fontSize = 12.sp,
                        color = Color(0xFF382D06),
                        lineHeight = 19.sp
                    )
                }
            }
        }
    }
}
