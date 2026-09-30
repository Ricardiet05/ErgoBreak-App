package com.example.ui

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundManager
import com.example.billing.BillingManager
import com.example.model.Exercise
import com.example.model.ExerciseRepository
import com.example.model.Language
import com.example.model.TensionZone
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

sealed class ErgoScreen {
    object Home : ErgoScreen()
    data class RoutinePreview(val zone: TensionZone, val exercises: List<Exercise>) : ErgoScreen()
    data class Countdown(val zone: TensionZone, val count: Int) : ErgoScreen()
    data class ExerciseSession(
        val zone: TensionZone,
        val exerciseIndex: Int,
        val exercise: Exercise,
        val totalExercises: Int,
        val secondsRemaining: Int = 30,
        val isPaused: Boolean = false,
        val showExitDialog: Boolean = false
    ) : ErgoScreen()
    data class ExerciseTransition(
        val zone: TensionZone,
        val nextIndex: Int,
        val nextExercise: Exercise,
        val totalExercises: Int,
        val secondsRemaining: Int = 2
    ) : ErgoScreen()
    data class Completed(
        val zone: TensionZone,
        val totalExercises: Int,
        val totalSeconds: Int
    ) : ErgoScreen()
}

class ErgoBreakViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("ergobreak_prefs", Context.MODE_PRIVATE)
    private val soundManager = SoundManager()
    val billingManager = BillingManager(application)

    private val _language = MutableStateFlow(loadInitialLanguage())
    val language: StateFlow<Language> = _language.asStateFlow()

    private val _screen = MutableStateFlow<ErgoScreen>(ErgoScreen.Home)
    val screen: StateFlow<ErgoScreen> = _screen.asStateFlow()

    private val _exerciseSecondsRemaining = MutableStateFlow(30)
    val exerciseSecondsRemaining: StateFlow<Int> = _exerciseSecondsRemaining.asStateFlow()

    private val _showPaywall = MutableStateFlow(false)
    val showPaywall: StateFlow<Boolean> = _showPaywall.asStateFlow()

    private val _promoStatusMessage = MutableStateFlow<String?>(null)
    val promoStatusMessage: StateFlow<String?> = _promoStatusMessage.asStateFlow()

    val isPro = billingManager.isPro
    val currentOfferingPackage = billingManager.currentOfferingPackage

    private var timerJob: Job? = null

    private fun loadInitialLanguage(): Language {
        val saved = prefs.getString("selected_language", null)
        if (saved != null) {
            return if (saved == "es") Language.ES else Language.EN
        }
        val defaultLocale = Locale.getDefault().language
        return if (defaultLocale.equals("es", ignoreCase = true)) Language.ES else Language.EN
    }

    fun toggleLanguage() {
        val newLang = if (_language.value == Language.EN) Language.ES else Language.EN
        _language.value = newLang
        prefs.edit().putString("selected_language", newLang.code).apply()
    }

    fun selectZone(zone: TensionZone) {
        if (zone.isPro && !isPro.value) {
            _showPaywall.value = true
        } else {
            val exercises = ExerciseRepository.getExercisesForZone(zone)
            _screen.value = ErgoScreen.RoutinePreview(zone, exercises)
        }
    }

    fun startRoutine(zone: TensionZone) {
        timerJob?.cancel()
        _screen.value = ErgoScreen.Countdown(zone, 3)
        soundManager.playCountdownTick()

        timerJob = viewModelScope.launch {
            // Countdown 3
            delay(1000)
            soundManager.playCountdownTick()
            _screen.value = ErgoScreen.Countdown(zone, 2)

            // Countdown 2
            delay(1000)
            soundManager.playCountdownTick()
            _screen.value = ErgoScreen.Countdown(zone, 1)

            // Countdown 1 -> GO!
            delay(1000)
            soundManager.playCountdownGo()
            _screen.value = ErgoScreen.Countdown(zone, 0) // GO!

            delay(600)
            startExercise(zone, 0)
        }
    }

    private fun startExercise(zone: TensionZone, exerciseIndex: Int) {
        timerJob?.cancel()
        val exercises = ExerciseRepository.getExercisesForZone(zone)
        if (exerciseIndex >= exercises.size) {
            finishRoutine(zone, exercises.size)
            return
        }

        val exercise = exercises[exerciseIndex]
        _exerciseSecondsRemaining.value = exercise.durationSeconds
        _screen.value = ErgoScreen.ExerciseSession(
            zone = zone,
            exerciseIndex = exerciseIndex,
            exercise = exercise,
            totalExercises = exercises.size,
            secondsRemaining = exercise.durationSeconds,
            isPaused = false,
            showExitDialog = false
        )

        runExerciseTimer(zone, exerciseIndex, exercises)
    }

    private fun runExerciseTimer(
        zone: TensionZone,
        exerciseIndex: Int,
        exercises: List<Exercise>
    ) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                val current = _screen.value
                if (current is ErgoScreen.ExerciseSession) {
                    if (!current.isPaused) {
                        val nextRemaining = _exerciseSecondsRemaining.value - 1

                        // Audio beeps on 3, 2, 1
                        if (nextRemaining in 1..3) {
                            soundManager.playTimerTick()
                        }

                        if (nextRemaining <= 0) {
                            _exerciseSecondsRemaining.value = 0
                            soundManager.playExerciseComplete()
                            if (exerciseIndex + 1 < exercises.size) {
                                // Transition screen before next exercise
                                showTransition(zone, exerciseIndex + 1, exercises)
                            } else {
                                // Routine complete!
                                finishRoutine(zone, exercises.size)
                            }
                            break
                        } else {
                            _exerciseSecondsRemaining.value = nextRemaining
                        }
                    }
                } else {
                    break
                }
            }
        }
    }

    private fun showTransition(
        zone: TensionZone,
        nextIndex: Int,
        exercises: List<Exercise>
    ) {
        timerJob?.cancel()
        val nextEx = exercises[nextIndex]
        _screen.value = ErgoScreen.ExerciseTransition(
            zone = zone,
            nextIndex = nextIndex,
            nextExercise = nextEx,
            totalExercises = exercises.size,
            secondsRemaining = 2
        )

        timerJob = viewModelScope.launch {
            delay(2000)
            startExercise(zone, nextIndex)
        }
    }

    private fun finishRoutine(zone: TensionZone, totalCount: Int) {
        timerJob?.cancel()
        soundManager.playBreakDone()
        _screen.value = ErgoScreen.Completed(
            zone = zone,
            totalExercises = totalCount,
            totalSeconds = totalCount * 30
        )
    }

    fun pauseExercise() {
        val current = _screen.value
        if (current is ErgoScreen.ExerciseSession) {
            _screen.value = current.copy(isPaused = true)
        }
    }

    fun resumeExercise() {
        val current = _screen.value
        if (current is ErgoScreen.ExerciseSession) {
            _screen.value = current.copy(isPaused = false, showExitDialog = false)
        }
    }

    fun promptExit() {
        val current = _screen.value
        if (current is ErgoScreen.ExerciseSession) {
            _screen.value = current.copy(isPaused = true, showExitDialog = true)
        }
    }

    fun dismissExitDialog() {
        val current = _screen.value
        if (current is ErgoScreen.ExerciseSession) {
            _screen.value = current.copy(isPaused = false, showExitDialog = false)
        }
    }

    fun confirmExit() {
        timerJob?.cancel()
        _screen.value = ErgoScreen.Home
    }

    fun skipCurrentExercise() {
        val current = _screen.value
        if (current is ErgoScreen.ExerciseSession) {
            val exercises = ExerciseRepository.getExercisesForZone(current.zone)
            if (current.exerciseIndex + 1 < exercises.size) {
                showTransition(current.zone, current.exerciseIndex + 1, exercises)
            } else {
                finishRoutine(current.zone, exercises.size)
            }
        }
    }

    fun returnToHome() {
        timerJob?.cancel()
        _screen.value = ErgoScreen.Home
    }

    fun openPaywall() {
        _promoStatusMessage.value = null
        _showPaywall.value = true
    }

    fun dismissPaywall() {
        _showPaywall.value = false
        _promoStatusMessage.value = null
    }

    fun redeemPromoCode(code: String) {
        val success = billingManager.redeemPromoCode(code)
        if (success) {
            _promoStatusMessage.value = if (_language.value == Language.ES) "¡Pro desbloqueado!" else "Pro unlocked!"
            viewModelScope.launch {
                delay(1200)
                _showPaywall.value = false
                _promoStatusMessage.value = null
            }
        } else {
            _promoStatusMessage.value = if (_language.value == Language.ES) "Código no válido" else "Invalid promo code"
        }
    }

    fun purchasePro(activity: Activity) {
        billingManager.purchasePro(activity) { success, msg ->
            if (success) {
                _promoStatusMessage.value = if (_language.value == Language.ES) "¡Pro desbloqueado!" else "Pro unlocked!"
                viewModelScope.launch {
                    delay(1200)
                    _showPaywall.value = false
                    _promoStatusMessage.value = null
                }
            } else if (msg != null) {
                _promoStatusMessage.value = msg
            }
        }
    }

    fun restorePurchases() {
        billingManager.restorePurchases { success, msg ->
            _promoStatusMessage.value = msg
            if (success) {
                viewModelScope.launch {
                    delay(1200)
                    _showPaywall.value = false
                    _promoStatusMessage.value = null
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        soundManager.release()
    }
}
