package com.example.ui

import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.example.model.Exercise
import com.example.model.Language
import com.example.model.VisualPostureType
import kotlin.math.cos
import kotlin.math.sin

@OptIn(UnstableApi::class)
@Composable
fun ExerciseVideoPlayer(
    exercise: Exercise,
    language: Language,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Flexible resolution: checks raw resources and assets folder
    val videoUri = remember(exercise.id) {
        val exerciseNum = exercise.code.removePrefix("EX").toIntOrNull() ?: 1
        val numStr = exerciseNum.toString()
        val padStr = String.format("%02d", exerciseNum)

        // Raw candidates (must start with letter in Android res/raw)
        val rawCandidates = listOf(
            exercise.videoFileName,
            "ex$padStr",
            "ex$numStr",
            "exercise_$padStr",
            "exercise_$numStr",
            "exercise$padStr",
            "exercise$numStr",
            "video_$padStr",
            "video_$numStr",
            "video$padStr",
            "video$numStr"
        )

        var resId = 0
        for (candidate in rawCandidates) {
            val id = context.resources.getIdentifier(candidate.lowercase(), "raw", context.packageName)
            if (id != 0) {
                resId = id
                break
            }
        }

        if (resId != 0) {
            Uri.parse("android.resource://${context.packageName}/$resId")
        } else {
            // Assets candidates (can start with number or letters)
            val assetCandidates = listOf(
                "$numStr.mp4",
                "$padStr.mp4",
                "ex$numStr.mp4",
                "ex$padStr.mp4",
                "${exercise.videoFileName}.mp4",
                "exercise$numStr.mp4",
                "exercise$padStr.mp4",
                "video$numStr.mp4",
                "video$padStr.mp4",
                "videos/$numStr.mp4",
                "videos/$padStr.mp4",
                "videos/ex$padStr.mp4"
            )
            val matchingAsset = assetCandidates.firstOrNull { assetName ->
                try {
                    context.assets.open(assetName).close()
                    true
                } catch (e: Exception) {
                    false
                }
            }
            if (matchingAsset != null) {
                Uri.parse("file:///android_asset/$matchingAsset")
            } else {
                null
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0F172A)),
        contentAlignment = Alignment.Center
    ) {
        if (videoUri != null) {
            val exoPlayer = remember(videoUri) {
                ExoPlayer.Builder(context).build().apply {
                    setMediaItem(MediaItem.fromUri(videoUri))
                    // Simple continuous loop: restarts as soon as it ends
                    repeatMode = Player.REPEAT_MODE_ONE
                    prepare()
                    playWhenReady = isPlaying
                }
            }

            LaunchedEffect(isPlaying) {
                if (isPlaying) {
                    exoPlayer.play()
                } else {
                    exoPlayer.pause()
                }
            }

            DisposableEffect(exoPlayer) {
                onDispose {
                    exoPlayer.stop()
                    exoPlayer.release()
                }
            }

            AndroidView(
                factory = { ctx ->
                    val playerView = android.view.LayoutInflater.from(ctx)
                        .inflate(com.example.R.layout.view_exercise_player, null) as PlayerView
                    playerView.apply {
                        player = exoPlayer
                        setShutterBackgroundColor(android.graphics.Color.TRANSPARENT)
                        setKeepContentOnPlayerReset(true)
                        setShowBuffering(PlayerView.SHOW_BUFFERING_NEVER)
                        setEnableComposeSurfaceSyncWorkaround(true)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Elegant ergonomic visual motion guide
            PostureMotionGuide(
                visualType = exercise.visualType,
                language = language,
                isPlaying = isPlaying,
                exerciseCode = exercise.code,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun PostureMotionGuide(
    visualType: VisualPostureType,
    language: Language,
    isPlaying: Boolean,
    exerciseCode: String,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "posture_motion")

    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f, // 2*PI
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1750, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val breathText = when {
        sin(phase) > 0.3f -> if (language == Language.ES) "Inhala profundamente" else "Deep breath in"
        sin(phase) < -0.3f -> if (language == Language.ES) "Exhala suavemente" else "Gentle breath out"
        else -> if (language == Language.ES) "Mantén la postura" else "Hold & steady"
    }

    Box(
        modifier = modifier
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFF1E293B), Color(0xFF090D16))
                )
            )
            .padding(12.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val cx = w / 2f
            val cy = h / 2f

            // Background subtle grid lines
            drawLine(
                color = Color(0x1A10B981),
                start = Offset(cx, 20f),
                end = Offset(cx, h - 20f),
                strokeWidth = 2f
            )
            drawLine(
                color = Color(0x1A10B981),
                start = Offset(40f, cy),
                end = Offset(w - 40f, cy),
                strokeWidth = 2f
            )

            // Draw motion guide based on posture type
            val activeColor = Color(0xFF10B981)
            val accentColor = Color(0xFF38BDF8)
            val bodyColor = Color(0xFFE2E8F0)

            when (visualType) {
                VisualPostureType.NECK_TILT -> {
                    // Head tilting left and right
                    val tiltAngle = sin(phase) * 0.45f
                    val headCenterX = cx + sin(tiltAngle) * 35f
                    val headCenterY = cy - 45f - (1f - cos(tiltAngle)) * 20f

                    // Torso
                    drawLine(bodyColor, Offset(cx, cy - 20f), Offset(cx, cy + 50f), 12f, StrokeCap.Round)
                    // Shoulders (relaxed)
                    drawLine(bodyColor, Offset(cx - 50f, cy - 10f), Offset(cx + 50f, cy - 10f), 10f, StrokeCap.Round)
                    // Neck
                    drawLine(accentColor, Offset(cx, cy - 15f), Offset(headCenterX, headCenterY), 8f, StrokeCap.Round)
                    // Head
                    drawCircle(activeColor, 28f * pulse, Offset(headCenterX, headCenterY))
                    // Stretch dynamic arc
                    drawArc(
                        color = Color(0x4D10B981),
                        startAngle = 180f,
                        sweepAngle = 180f * sin(phase),
                        useCenter = false,
                        topLeft = Offset(cx - 55f, cy - 85f),
                        size = androidx.compose.ui.geometry.Size(110f, 60f),
                        style = Stroke(4f)
                    )
                }

                VisualPostureType.NECK_ROTATION -> {
                    val turnOffset = sin(phase) * 32f
                    // Torso & shoulders
                    drawLine(bodyColor, Offset(cx, cy - 15f), Offset(cx, cy + 55f), 12f, StrokeCap.Round)
                    drawLine(bodyColor, Offset(cx - 50f, cy - 5f), Offset(cx + 50f, cy - 5f), 10f, StrokeCap.Round)
                    // Head turning indicator
                    drawCircle(activeColor, 28f, Offset(cx + turnOffset * 0.5f, cy - 50f))
                    // Rotation arrow indicator
                    drawArc(
                        color = accentColor,
                        startAngle = 200f,
                        sweepAngle = 140f,
                        useCenter = false,
                        topLeft = Offset(cx - 45f, cy - 80f),
                        size = androidx.compose.ui.geometry.Size(90f, 60f),
                        style = Stroke(5f, cap = StrokeCap.Round)
                    )
                }

                VisualPostureType.CHIN_TUCK -> {
                    val tuckBack = (sin(phase) * 0.5f + 0.5f) * -22f
                    // Spine
                    drawLine(bodyColor, Offset(cx, cy - 15f), Offset(cx, cy + 60f), 12f, StrokeCap.Round)
                    drawLine(bodyColor, Offset(cx - 40f, cy - 5f), Offset(cx + 40f, cy - 5f), 10f, StrokeCap.Round)
                    // Head retracting back
                    drawCircle(activeColor, 28f, Offset(cx + tuckBack, cy - 45f))
                    // Retraction alignment dashed line
                    drawLine(Color(0x8038BDF8), Offset(cx + 15f, cy - 45f), Offset(cx - 30f, cy - 45f), 4f)
                }

                VisualPostureType.SHOULDER_LIFT -> {
                    val lift = (sin(phase) * 0.5f + 0.5f) * -28f
                    // Spine
                    drawLine(bodyColor, Offset(cx, cy - 10f), Offset(cx, cy + 60f), 12f, StrokeCap.Round)
                    // Head
                    drawCircle(bodyColor, 24f, Offset(cx, cy - 45f))
                    // Shoulders lifting up to ears
                    drawLine(activeColor, Offset(cx - 55f, cy - 5f + lift), Offset(cx + 55f, cy - 5f + lift), 14f, StrokeCap.Round)
                }

                VisualPostureType.SHOULDER_ROLLS -> {
                    val rollX = cos(phase) * 20f
                    val rollY = sin(phase) * 16f
                    drawLine(bodyColor, Offset(cx, cy - 10f), Offset(cx, cy + 60f), 12f, StrokeCap.Round)
                    drawCircle(bodyColor, 24f, Offset(cx, cy - 45f))
                    // Shoulders in circular motion
                    drawCircle(activeColor, 12f, Offset(cx - 45f + rollX, cy - 5f + rollY))
                    drawCircle(activeColor, 12f, Offset(cx + 45f - rollX, cy - 5f + rollY))
                }

                VisualPostureType.CHEST_OPENER -> {
                    val expand = (sin(phase) * 0.5f + 0.5f) * 35f
                    // Torso & head
                    drawLine(bodyColor, Offset(cx, cy - 15f), Offset(cx, cy + 60f), 12f, StrokeCap.Round)
                    drawCircle(bodyColor, 24f, Offset(cx, cy - 50f))
                    // Arms opening wide backward
                    drawLine(activeColor, Offset(cx, cy - 5f), Offset(cx - 60f - expand, cy + 5f), 10f, StrokeCap.Round)
                    drawLine(activeColor, Offset(cx, cy - 5f), Offset(cx + 60f + expand, cy + 5f), 10f, StrokeCap.Round)
                }

                VisualPostureType.ARM_ACROSS -> {
                    val cross = sin(phase) * 50f
                    drawLine(bodyColor, Offset(cx, cy - 15f), Offset(cx, cy + 60f), 12f, StrokeCap.Round)
                    drawCircle(bodyColor, 24f, Offset(cx, cy - 50f))
                    // Cross arm
                    drawLine(activeColor, Offset(cx - 30f, cy), Offset(cx + 40f + cross, cy + 5f), 10f, StrokeCap.Round)
                }

                VisualPostureType.TORSO_ROTATION -> {
                    val twist = sin(phase) * 40f
                    drawCircle(bodyColor, 24f, Offset(cx, cy - 50f))
                    // Seated base
                    drawLine(Color(0xFF64748B), Offset(cx - 40f, cy + 65f), Offset(cx + 40f, cy + 65f), 8f, StrokeCap.Round)
                    // Rotating torso line
                    drawLine(activeColor, Offset(cx - twist, cy - 10f), Offset(cx + twist, cy - 10f), 12f, StrokeCap.Round)
                    drawLine(bodyColor, Offset(cx, cy - 10f), Offset(cx, cy + 60f), 12f, StrokeCap.Round)
                }

                VisualPostureType.BACK_EXTENSION -> {
                    val arch = (sin(phase) * 0.5f + 0.5f) * -20f
                    drawCircle(bodyColor, 24f, Offset(cx + arch * 0.8f, cy - 52f))
                    // Arched spine
                    drawLine(activeColor, Offset(cx + arch, cy), Offset(cx, cy + 60f), 12f, StrokeCap.Round)
                    drawLine(accentColor, Offset(cx - 40f, cy - 5f), Offset(cx + 40f, cy - 5f), 10f, StrokeCap.Round)
                }

                VisualPostureType.WRIST_STRETCH -> {
                    val flex = sin(phase) * 35f
                    // Forearm
                    drawLine(bodyColor, Offset(cx - 70f, cy), Offset(cx + 20f, cy), 12f, StrokeCap.Round)
                    // Hand stretching upward/downward
                    drawLine(activeColor, Offset(cx + 20f, cy), Offset(cx + 50f, cy + flex), 10f, StrokeCap.Round)
                }

                VisualPostureType.WRIST_CIRCLES -> {
                    val rotX = cos(phase) * 22f
                    val rotY = sin(phase) * 22f
                    drawLine(bodyColor, Offset(cx - 60f, cy), Offset(cx + 10f, cy), 12f, StrokeCap.Round)
                    drawCircle(accentColor, 24f, Offset(cx + 35f, cy), style = Stroke(3f))
                    drawCircle(activeColor, 8f, Offset(cx + 35f + rotX, cy + rotY))
                }

                VisualPostureType.KNEE_LIFTS -> {
                    val liftLeft = (sin(phase) * 0.5f + 0.5f) * -35f
                    // Torso seated
                    drawLine(bodyColor, Offset(cx, cy - 35f), Offset(cx, cy + 25f), 12f, StrokeCap.Round)
                    drawCircle(bodyColor, 22f, Offset(cx, cy - 65f))
                    // Knees
                    drawLine(activeColor, Offset(cx - 20f, cy + 25f), Offset(cx - 40f, cy + 50f + liftLeft), 10f, StrokeCap.Round)
                    drawLine(accentColor, Offset(cx + 20f, cy + 25f), Offset(cx + 40f, cy + 50f), 10f, StrokeCap.Round)
                }

                VisualPostureType.LEG_EXTENSIONS -> {
                    val extend = (sin(phase) * 0.5f + 0.5f) * 45f
                    drawLine(bodyColor, Offset(cx, cy - 35f), Offset(cx, cy + 25f), 12f, StrokeCap.Round)
                    drawCircle(bodyColor, 22f, Offset(cx, cy - 65f))
                    // Extended leg
                    drawLine(activeColor, Offset(cx, cy + 25f), Offset(cx + 65f + extend * 0.5f, cy + 25f - extend * 0.3f), 10f, StrokeCap.Round)
                }
            }
        }

        // Subtitle badge & Breath guidance
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xCC0F172A))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "$exerciseCode • $breathText",
                    color = Color(0xFF34D399),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
