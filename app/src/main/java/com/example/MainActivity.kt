package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Modifier
import com.example.ui.CompletionScreen
import com.example.ui.CountdownScreen
import com.example.ui.ErgoBreakViewModel
import com.example.ui.ErgoScreen
import com.example.ui.ExerciseScreen
import com.example.ui.ExerciseTransitionScreen
import com.example.ui.HomeScreen
import com.example.ui.PaywallDialog
import com.example.ui.RoutinePreviewScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SlateDark

class MainActivity : ComponentActivity() {
    private val viewModel: ErgoBreakViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = SlateDark
                ) {
                    ErgoBreakApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun ErgoBreakApp(viewModel: ErgoBreakViewModel) {
    val currentScreen by viewModel.screen.collectAsState()
    val language by viewModel.language.collectAsState()
    val isPro by viewModel.isPro.collectAsState()
    val showPaywall by viewModel.showPaywall.collectAsState()
    val promoStatusMessage by viewModel.promoStatusMessage.collectAsState()

    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        },
        label = "screen_transition"
    ) { screen ->
        when (screen) {
            is ErgoScreen.Home -> {
                HomeScreen(
                    language = language,
                    isPro = isPro,
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onSelectZone = { zone -> viewModel.selectZone(zone) },
                    onOpenPaywall = { viewModel.openPaywall() }
                )
            }

            is ErgoScreen.RoutinePreview -> {
                RoutinePreviewScreen(
                    zone = screen.zone,
                    exercises = screen.exercises,
                    language = language,
                    onStart = { viewModel.startRoutine(screen.zone) },
                    onBack = { viewModel.returnToHome() }
                )
            }

            is ErgoScreen.Countdown -> {
                CountdownScreen(
                    count = screen.count,
                    zone = screen.zone,
                    language = language,
                    onCancel = { viewModel.returnToHome() }
                )
            }

            is ErgoScreen.ExerciseSession -> {
                ExerciseScreen(
                    exercise = screen.exercise,
                    exerciseIndex = screen.exerciseIndex,
                    totalExercises = screen.totalExercises,
                    secondsRemainingFlow = viewModel.exerciseSecondsRemaining,
                    isPaused = screen.isPaused,
                    showExitDialog = screen.showExitDialog,
                    language = language,
                    onPause = { viewModel.pauseExercise() },
                    onResume = { viewModel.resumeExercise() },
                    onPromptExit = { viewModel.promptExit() },
                    onDismissExitDialog = { viewModel.dismissExitDialog() },
                    onConfirmExit = { viewModel.confirmExit() },
                    onSkip = { viewModel.skipCurrentExercise() }
                )
            }

            is ErgoScreen.ExerciseTransition -> {
                ExerciseTransitionScreen(
                    nextExercise = screen.nextExercise,
                    nextIndex = screen.nextIndex,
                    totalExercises = screen.totalExercises,
                    language = language,
                    onCancel = { viewModel.returnToHome() }
                )
            }

            is ErgoScreen.Completed -> {
                CompletionScreen(
                    zone = screen.zone,
                    totalExercises = screen.totalExercises,
                    totalSeconds = screen.totalSeconds,
                    language = language,
                    onDone = { viewModel.returnToHome() }
                )
            }
        }
    }

    // Paywall Dialog
    val currentPackage by viewModel.currentOfferingPackage.collectAsStateWithLifecycle()
    if (showPaywall) {
        PaywallDialog(
            language = language,
            statusMessage = promoStatusMessage,
            currentPackage = currentPackage,
            onDismiss = { viewModel.dismissPaywall() },
            onPurchase = { activity -> viewModel.purchasePro(activity) },
            onRestore = { viewModel.restorePurchases() },
            onRedeemPromo = { code -> viewModel.redeemPromoCode(code) }
        )
    }
}
