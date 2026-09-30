package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.AirlineSeatReclineNormal
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ExerciseRepository
import com.example.model.Language
import com.example.model.TensionZone
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldPro
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardBorder
import com.example.ui.theme.SlateDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun HomeScreen(
    language: Language,
    isPro: Boolean,
    onToggleLanguage: () -> Unit,
    onSelectZone: (TensionZone) -> Unit,
    onOpenPaywall: () -> Unit,
    modifier: Modifier = Modifier
) {
    val zones = listOf(
        TensionZone.NECK,
        TensionZone.SHOULDERS,
        TensionZone.BACK,
        TensionZone.WRISTS_AND_HANDS,
        TensionZone.FULL_BODY
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlateDark)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Top Bar: Brand, Language Switcher & Pro Badge
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(EmeraldPrimary, Color(0xFF047857))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SelfImprovement,
                        contentDescription = "ErgoBreak Logo",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "ErgoBreak",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Language Toggle Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(SlateCard)
                        .border(1.dp, SlateCardBorder, RoundedCornerShape(20.dp))
                        .clickable { onToggleLanguage() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("language_toggle")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "EN",
                            fontSize = 12.sp,
                            fontWeight = if (language == Language.EN) FontWeight.Bold else FontWeight.Normal,
                            color = if (language == Language.EN) EmeraldLight else TextMuted
                        )
                        Text(
                            text = " / ",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                        Text(
                            text = "ES",
                            fontSize = 12.sp,
                            fontWeight = if (language == Language.ES) FontWeight.Bold else FontWeight.Normal,
                            color = if (language == Language.ES) EmeraldLight else TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Pro Status Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isPro) Color(0x33F59E0B) else Color(0x22334155))
                        .border(
                            1.dp,
                            if (isPro) GoldPro else SlateCardBorder,
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { onOpenPaywall() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("pro_badge_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isPro) Icons.Default.Star else Icons.Default.Lock,
                            contentDescription = if (isPro) "PRO Active" else "PRO Locked",
                            tint = if (isPro) GoldPro else TextSecondary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isPro) "PRO" else "PRO",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isPro) GoldPro else TextSecondary
                        )
                    }
                }
            }
        }

        // Section Title: Where do you feel tension?
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Text(
                text = if (language == Language.ES) "¿Dónde sientes tensión?" else "Where do you feel tension?",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (language == Language.ES)
                    "Elige una zona para comenzar tu pausa ergonómica"
                else
                    "Select an area to start your quick movement break",
                fontSize = 13.sp,
                color = TextSecondary
            )
        }

        // Zone Cards List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 12.dp)
        ) {
            items(zones) { zone ->
                val exercises = ExerciseRepository.getExercisesForZone(zone)
                val durationMin = String.format(Locale.US, "%.1f", (exercises.size * 30) / 60.0)
                val isLocked = zone.isPro && !isPro

                ZoneCard(
                    zone = zone,
                    language = language,
                    isLocked = isLocked,
                    exerciseCount = exercises.size,
                    durationMin = durationMin,
                    onClick = { onSelectZone(zone) }
                )
            }

            // Wellness Advice Footer
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131E35)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Spa,
                            contentDescription = "Wellness Advice",
                            tint = EmeraldPrimary,
                            modifier = Modifier
                                .size(20.dp)
                                .padding(top = 1.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (language == Language.ES)
                                "ErgoBreak ofrece pausas de movimiento de bienestar general y no sustituye el consejo médico profesional. Detén el ejercicio si sientes dolor o mareo."
                            else
                                "ErgoBreak offers general wellness movement breaks and does not replace professional medical advice. Stop if you feel pain or dizziness.",
                            fontSize = 11.5.sp,
                            lineHeight = 16.sp,
                            color = TextSecondary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun ZoneCard(
    zone: TensionZone,
    language: Language,
    isLocked: Boolean,
    exerciseCount: Int,
    durationMin: String,
    onClick: () -> Unit
) {
    val icon: ImageVector = when (zone) {
        TensionZone.NECK -> Icons.Default.SelfImprovement
        TensionZone.SHOULDERS -> Icons.Default.AccessibilityNew
        TensionZone.BACK -> Icons.Default.AirlineSeatReclineNormal
        TensionZone.WRISTS_AND_HANDS -> Icons.Default.PanTool
        TensionZone.FULL_BODY -> Icons.Default.FitnessCenter
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isLocked) Color(0x33F59E0B) else SlateCardBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("zone_${zone.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Zone Icon Container
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (isLocked) Color(0x22F59E0B) else Color(0x2210B981)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = zone.getTitle(language),
                        tint = if (isLocked) GoldPro else EmeraldLight,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = zone.getTitle(language),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        if (isLocked) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0x33F59E0B))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "PRO",
                                        tint = GoldPro,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "PRO 🔒",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = GoldPro
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = if (language == Language.ES)
                            "$exerciseCount ejercicios • ~${durationMin} min"
                        else
                            "$exerciseCount exercises • ~${durationMin} min",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Open",
                tint = if (isLocked) GoldPro else TextMuted,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
