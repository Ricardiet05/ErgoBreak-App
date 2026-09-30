package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Exercise
import com.example.model.Language
import com.example.model.TensionZone
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardBorder
import com.example.ui.theme.SlateDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun RoutinePreviewScreen(
    zone: TensionZone,
    exercises: List<Exercise>,
    language: Language,
    onStart: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val breakTitle = if (language == Language.ES) {
        "Pausa de ${zone.titleEs}"
    } else {
        "${zone.titleEn} Break"
    }

    val totalSeconds = exercises.size * 30
    val durationText = String.format(Locale.US, "%.1f", totalSeconds / 60.0)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlateDark)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Top Navigation Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("back_button")
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = if (language == Language.ES) "Tu Pausa" else "Your Break",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary
            )
        }

        // Title Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            Text(
                text = breakTitle,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = null,
                    tint = EmeraldLight,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (language == Language.ES)
                        "${exercises.size} ejercicios • ~$durationText min"
                    else
                        "${exercises.size} exercises • ~$durationText min",
                    fontSize = 14.sp,
                    color = EmeraldLight,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Exercises List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 16.dp)
        ) {
            itemsIndexed(exercises) { index, exercise ->
                RoutineExerciseItem(
                    index = index + 1,
                    exercise = exercise,
                    language = language
                )
            }
        }

        // Large START Button at Bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Button(
                onClick = onStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .testTag("start_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmeraldPrimary,
                    contentColor = Color.Black
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == Language.ES) "COMENZAR" else "START",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun RoutineExerciseItem(
    index: Int,
    exercise: Exercise,
    language: Language
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Exercise Step Number Pill
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color(0x2210B981))
                    .border(1.dp, Color(0x4410B981), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$index",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldLight
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (language == Language.ES) "Ejercicio $index" else "Exercise $index",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = exercise.getName(language),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            // Duration Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF131E35))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "30s",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                )
            }
        }
    }
}
