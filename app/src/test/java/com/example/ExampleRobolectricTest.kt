package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.billing.BillingManager
import com.example.model.ExerciseRepository
import com.example.model.TensionZone
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read app_name from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("ErgoBreak", appName)
    }

    @Test
    fun `verify routine exercises mapped correctly`() {
        val neckExercises = ExerciseRepository.getExercisesForZone(TensionZone.NECK)
        assertEquals(3, neckExercises.size)
        assertEquals("EX01", neckExercises[0].code)
        assertEquals("EX02", neckExercises[1].code)
        assertEquals("EX03", neckExercises[2].code)

        val shoulderExercises = ExerciseRepository.getExercisesForZone(TensionZone.SHOULDERS)
        assertEquals(4, shoulderExercises.size)

        val backExercises = ExerciseRepository.getExercisesForZone(TensionZone.BACK)
        assertEquals(3, backExercises.size)

        val wristExercises = ExerciseRepository.getExercisesForZone(TensionZone.WRISTS_AND_HANDS)
        assertEquals(2, wristExercises.size)

        val fullBodyExercises = ExerciseRepository.getExercisesForZone(TensionZone.FULL_BODY)
        assertEquals(7, fullBodyExercises.size)
    }

    @Test
    fun `judge promo code ERGOBREAK2026 unlocks Pro entitlement`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val billingManager = BillingManager(context)

        // Invalid code should fail
        assertFalse(billingManager.redeemPromoCode("WRONG_CODE"))

        // Correct code should unlock immediately
        assertTrue(billingManager.redeemPromoCode("ERGOBREAK2026"))
        assertTrue(billingManager.isPro.value)

        // Case insensitivity & trimming test
        assertTrue(billingManager.redeemPromoCode("  ergobreak2026  "))
        assertTrue(billingManager.isPro.value)
    }
}
