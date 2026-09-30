package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.model.Language
import com.example.model.TensionZone
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
fun CompletionScreen(
    zone: TensionZone,
    totalExercises: Int,
    totalSeconds: Int,
    language: Language,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onDone() }

    val durationMin = String.format(Locale.US, "%.1f", totalSeconds / 60.0)

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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Success Glowing Icon
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                EmeraldLight,
                                EmeraldPrimary,
                                Color(0xFF047857)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Success",
                    tint = Color.Black,
                    modifier = Modifier.size(56.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Great job!
            Text(
                text = if (language == Language.ES) "¡Gran trabajo!" else "Great job!",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Your break is complete.
            Text(
                text = if (language == Language.ES)
                    "Tu pausa de movimiento ha terminado."
                else
                    "Your break is complete.",
                fontSize = 17.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Summary Card
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder),
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 18.dp, horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$totalExercises",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldLight
                        )
                        Text(
                            text = if (language == Language.ES) "Ejercicios" else "Exercises",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }

                    Box(
                        modifier = Modifier
                            .height(36.dp)
                            .width(1.dp)
                            .background(SlateCardBorder)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${durationMin}m",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldLight
                        )
                        Text(
                            text = if (language == Language.ES) "Tiempo" else "Duration",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }

                    Box(
                        modifier = Modifier
                            .height(36.dp)
                            .width(1.dp)
                            .background(SlateCardBorder)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = zone.getTitle(language),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = if (language == Language.ES) "Zona" else "Target",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Large DONE Button
            Button(
                onClick = onDone,
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(56.dp)
                    .testTag("done_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmeraldPrimary,
                    contentColor = Color.Black
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
            ) {
                Text(
                    text = if (language == Language.ES) "LISTO" else "DONE",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp,
                    color = Color.Black
                )
            }
        }
    }
}
