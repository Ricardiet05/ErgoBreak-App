package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.Exercise
import com.example.model.Language
import com.example.ui.theme.DangerRed
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardBorder
import com.example.ui.theme.SlateDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.flow.StateFlow

@Composable
fun ExerciseScreen(
    exercise: Exercise,
    exerciseIndex: Int,
    totalExercises: Int,
    secondsRemainingFlow: StateFlow<Int>,
    isPaused: Boolean,
    showExitDialog: Boolean,
    language: Language,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onPromptExit: () -> Unit,
    onDismissExitDialog: () -> Unit,
    onConfirmExit: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onPromptExit() }

    val exerciseNumber = exerciseIndex + 1

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SlateDark)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar: Exit button & Routine overall progress
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Exit button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(SlateCard)
                        .border(1.dp, SlateCardBorder, CircleShape)
                        .clickable { onPromptExit() }
                        .testTag("exit_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Exit",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Progress Indicator (Exercise X of Y)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (language == Language.ES)
                            "Ejercicio $exerciseNumber de $totalExercises"
                        else
                            "Exercise $exerciseNumber of $totalExercises",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EmeraldLight
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { exerciseNumber.toFloat() / totalExercises.toFloat() },
                        modifier = Modifier
                            .width(100.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = EmeraldPrimary,
                        trackColor = Color(0x33334155)
                    )
                }

                // Skip button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(SlateCard)
                        .border(1.dp, SlateCardBorder, CircleShape)
                        .clickable { onSkip() }
                        .testTag("skip_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Skip",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // RECUADRO 1 (ARRIBA): Tarjeta de Video independiente
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Exercise Title inside the card header
                    Text(
                        text = exercise.getName(language),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = exercise.getMovement(language),
                        fontSize = 12.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    // Video Player / Posture Motion Guide
                    ExerciseVideoPlayer(
                        exercise = exercise,
                        language = language,
                        isPlaying = !isPaused,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                }
            }

            // RECUADRO 2 (DEBAJO): Tarjeta de Contador & Controles
            ExerciseTimerCard(
                secondsRemainingFlow = secondsRemainingFlow,
                totalDuration = exercise.durationSeconds,
                isPaused = isPaused,
                language = language,
                onResume = onResume,
                onPause = onPause,
                onSkip = onSkip,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Exit Confirmation Dialog
        if (showExitDialog) {
            AlertDialog(
                onDismissRequest = onDismissExitDialog,
                containerColor = SlateCard,
                title = {
                    Text(
                        text = if (language == Language.ES) "¿Salir de esta pausa?" else "Leave this break?",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                text = {
                    Text(
                        text = if (language == Language.ES)
                            "Si sales ahora, la rutina actual se cancelará."
                        else
                            "If you exit now, your current break session will stop.",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                },
                confirmButton = {
                    Button(
                        onClick = onConfirmExit,
                        colors = ButtonDefaults.buttonColors(containerColor = DangerRed),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("confirm_exit_button")
                    ) {
                        Text(
                            text = if (language == Language.ES) "Salir" else "Exit",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = onDismissExitDialog,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder),
                        modifier = Modifier.testTag("continue_break_button")
                    ) {
                        Text(
                            text = if (language == Language.ES) "Continuar" else "Continue",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun ExerciseTimerCard(
    secondsRemainingFlow: StateFlow<Int>,
    totalDuration: Int,
    isPaused: Boolean,
    language: Language,
    onResume: () -> Unit,
    onPause: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val secondsRemaining by secondsRemainingFlow.collectAsStateWithLifecycle()
    val progressFraction = (totalDuration - secondsRemaining).toFloat() / totalDuration.toFloat()
    val minutes = secondsRemaining / 60
    val secs = secondsRemaining % 60
    val timeFormatted = String.format(java.util.Locale.getDefault(), "%02d:%02d", minutes, secs)

    Card(
        modifier = modifier.padding(vertical = 10.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Digital Time Display & Status
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isPaused) Color(0x33F59E0B) else Color(0x3310B981))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = timeFormatted,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isPaused) Color(0xFFF59E0B) else EmeraldPrimary,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = if (isPaused) {
                            if (language == Language.ES) "EN PAUSA" else "PAUSED"
                        } else {
                            if (language == Language.ES) "EN PROGRESO" else "IN PROGRESS"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isPaused) Color(0xFFF59E0B) else EmeraldLight,
                        letterSpacing = 0.5.sp
                    )
                }

                // Controls: Pause / Resume & Skip
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = { if (isPaused) onResume() else onPause() },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (isPaused) EmeraldPrimary else Color(0xFF1E293B))
                            .border(1.dp, if (isPaused) EmeraldPrimary else SlateCardBorder, CircleShape)
                            .testTag("pause_resume_button")
                    ) {
                        Icon(
                            imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = if (isPaused) "Resume" else "Pause",
                            tint = if (isPaused) Color.Black else EmeraldLight,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    IconButton(
                        onClick = onSkip,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E293B))
                            .border(1.dp, SlateCardBorder, CircleShape)
                            .testTag("skip_button_bottom")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Skip",
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Smooth horizontal progress bar
            LinearProgressIndicator(
                progress = { progressFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (secondsRemaining <= 3) DangerRed else EmeraldPrimary,
                trackColor = Color(0x33334155)
            )
        }
    }
}
