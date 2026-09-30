package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Exercise
import com.example.model.Language
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ExerciseTransitionScreen(
    nextExercise: Exercise,
    nextIndex: Int,
    totalExercises: Int,
    language: Language,
    modifier: Modifier = Modifier,
    onCancel: () -> Unit = {}
) {
    BackHandler { onCancel() }
    val nextNumber = nextIndex + 1

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SlateDark)
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color(0x2210B981)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = EmeraldLight,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = if (language == Language.ES)
                    "Siguiente Ejercicio ($nextNumber de $totalExercises)"
                else
                    "Exercise $nextNumber of $totalExercises",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = EmeraldLight
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = nextExercise.getName(language),
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = nextExercise.getMovement(language),
                fontSize = 14.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = if (language == Language.ES) "Comenzando..." else "Starting...",
                fontSize = 13.sp,
                color = EmeraldPrimary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
