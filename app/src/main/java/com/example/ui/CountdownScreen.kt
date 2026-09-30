package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.with
import androidx.compose.foundation.background
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Language
import com.example.model.TensionZone
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.SlateDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CountdownScreen(
    count: Int,
    zone: TensionZone,
    language: Language,
    modifier: Modifier = Modifier,
    onCancel: () -> Unit = {}
) {
    BackHandler { onCancel() }
    val displayString = if (count > 0) "$count" else "GO!"

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
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = if (language == Language.ES) "Prepárate" else "Get Ready",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = zone.getTitle(language),
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Animated countdown pulse circle
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                if (count == 0) EmeraldLight else EmeraldPrimary,
                                Color(0xFF047857),
                                Color(0xFF064E3B)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = displayString,
                    transitionSpec = {
                        (fadeIn(animationSpec = tween(180)) + scaleIn(initialScale = 0.6f)) with
                                (fadeOut(animationSpec = tween(180)) + scaleOut(targetScale = 1.3f))
                    },
                    label = "countdown_animation"
                ) { text ->
                    Text(
                        text = text,
                        fontSize = if (text == "GO!") 52.sp else 74.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = if (language == Language.ES) "Acomoda tu postura y respira" else "Sit comfortably and breathe",
                fontSize = 14.sp,
                color = TextSecondary
            )
        }
    }
}
