package com.example.billing

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import com.revenuecat.purchases.PurchasesError
import com.revenuecat.purchases.interfaces.PurchaseCallback
import com.revenuecat.purchases.interfaces.ReceiveCustomerInfoCallback
import com.revenuecat.purchases.interfaces.ReceiveOfferingsCallback
import com.revenuecat.purchases.models.StoreTransaction
import com.revenuecat.purchases.Offerings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BillingManager(private val context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("ergobreak_billing_prefs", Context.MODE_PRIVATE)

    companion object {
        const val PROMO_CODE_JUDGES = "ERGOBREAK2026"
        const val ENTITLEMENT_PRO = "pro"
        const val OFFERING_DEFAULT = "default"
        const val PRODUCT_SKU = "ergobreak_monthly"
        const val PACKAGE_MONTHLY = "ergobreak_monthly"
        const val GALAXY_API_KEY = "galx_TAkWfIUUxEqmBpYNfnTmBZHjfgo"
        private const val KEY_PROMO_UNLOCKED = "key_promo_unlocked"
        private const val KEY_REVENUECAT_PRO_CACHED = "key_rc_pro_cached"
        private const val TAG = "BillingManager"
    }

    private val _isPro = MutableStateFlow(checkInitialProState())
    val isPro: StateFlow<Boolean> = _isPro.asStateFlow()

    private val _currentOfferingPackage = MutableStateFlow<Package?>(null)
    val currentOfferingPackage: StateFlow<Package?> = _currentOfferingPackage.asStateFlow()

    private val _billingStatusMessage = MutableStateFlow<String?>(null)
    val billingStatusMessage: StateFlow<String?> = _billingStatusMessage.asStateFlow()

    init {
        // Reset any cached fake-pro from earlier emulator tests
        if (!prefs.getBoolean(KEY_PROMO_UNLOCKED, false)) {
            prefs.edit().remove(KEY_REVENUECAT_PRO_CACHED).apply()
            _isPro.value = false
        }
        setupRevenueCat()
    }

    private fun checkInitialProState(): Boolean {
        val promoUnlocked = prefs.getBoolean(KEY_PROMO_UNLOCKED, false)
        val rcCached = prefs.getBoolean(KEY_REVENUECAT_PRO_CACHED, false)
        return promoUnlocked || (Purchases.isConfigured && rcCached)
    }

    private fun setupRevenueCat() {
        try {
            if (!Purchases.isConfigured) {
                val apiKey = try {
                    val field = com.example.BuildConfig::class.java.getField("REVENUECAT_API_KEY")
                    val value = field.get(null) as? String
                    if (!value.isNullOrBlank() &&
                        !value.contains("placeholder", ignoreCase = true) &&
                        !value.contains("demo", ignoreCase = true) &&
                        !value.contains("shipathon", ignoreCase = true) &&
                        value.length >= 20
                    ) {
                        value
                    } else GALAXY_API_KEY
                } catch (e: Exception) {
                    GALAXY_API_KEY
                }

                // RevenueCat native Android SDK strictly requires public keys starting with 'goog_' or 'amzn_'
                val isRecognizedKey = apiKey != null && (apiKey.startsWith("goog_") || apiKey.startsWith("amzn_"))

                if (isRecognizedKey) {
                    Purchases.configure(
                        PurchasesConfiguration.Builder(context, apiKey!!)
                            .build()
                    )
                    Log.d(TAG, "RevenueCat configured successfully")
                    refreshCustomerInfo()
                    fetchOfferings()
                } else {
                    Log.i(TAG, "RevenueCat waiting for valid Android public key (goog_... or amzn_...). In-app promo code active.")
                }
            } else {
                refreshCustomerInfo()
                fetchOfferings()
            }
        } catch (e: Exception) {
            Log.w(TAG, "RevenueCat initialization safely handled: ${e.message}")
        }
    }

    fun refreshCustomerInfo() {
        try {
            if (Purchases.isConfigured) {
                Purchases.sharedInstance.getCustomerInfo(object : ReceiveCustomerInfoCallback {
                    override fun onReceived(customerInfo: CustomerInfo) {
                        val hasPro = customerInfo.entitlements[ENTITLEMENT_PRO]?.isActive == true
                        prefs.edit().putBoolean(KEY_REVENUECAT_PRO_CACHED, hasPro).apply()
                        if (hasPro || prefs.getBoolean(KEY_PROMO_UNLOCKED, false)) {
                            _isPro.value = true
                        }
                    }

                    override fun onError(error: PurchasesError) {
                        Log.w(TAG, "Error fetching customer info: ${error.message}")
                    }
                })
            }
        } catch (e: Exception) {
            Log.w(TAG, "refreshCustomerInfo exception: ${e.message}")
        }
    }

    private fun fetchOfferings() {
        try {
            if (Purchases.isConfigured) {
                Purchases.sharedInstance.getOfferings(object : ReceiveOfferingsCallback {
                    override fun onReceived(offerings: Offerings) {
                        val defaultOffering = offerings[OFFERING_DEFAULT] ?: offerings.current
                        val pkg = defaultOffering?.availablePackages?.firstOrNull {
                            it.identifier.equals(PACKAGE_MONTHLY, ignoreCase = true) ||
                            it.product.id.equals(PRODUCT_SKU, ignoreCase = true) ||
                            it.identifier == "\$rc_monthly"
                        } ?: defaultOffering?.availablePackages?.firstOrNull()
                        _currentOfferingPackage.value = pkg
                        Log.d(TAG, "Selected RevenueCat package: ${pkg?.identifier}, SKU: ${pkg?.product?.id}, price: ${pkg?.product?.price?.formatted}")
                    }

                    override fun onError(error: PurchasesError) {
                        Log.w(TAG, "Error fetching offerings: ${error.message}")
                    }
                })
            }
        } catch (e: Exception) {
            Log.w(TAG, "fetchOfferings exception: ${e.message}")
        }
    }

    fun purchasePro(activity: Activity, onComplete: (Boolean, String?) -> Unit) {
        val targetPackage = _currentOfferingPackage.value
        if (targetPackage != null && Purchases.isConfigured) {
            Purchases.sharedInstance.purchase(
                com.revenuecat.purchases.PurchaseParams.Builder(activity, targetPackage).build(),
                object : PurchaseCallback {
                    override fun onCompleted(
                        storeTransaction: StoreTransaction,
                        customerInfo: CustomerInfo
                    ) {
                        val hasPro = customerInfo.entitlements[ENTITLEMENT_PRO]?.isActive == true
                        prefs.edit().putBoolean(KEY_REVENUECAT_PRO_CACHED, hasPro).apply()
                        if (hasPro) {
                            _isPro.value = true
                            onComplete(true, null)
                        } else {
                            onComplete(false, "Compra completada. Verificando suscripción...")
                        }
                    }

                    override fun onError(error: PurchasesError, userCancelled: Boolean) {
                        if (userCancelled) {
                            onComplete(false, null)
                        } else {
                            onComplete(false, error.message)
                        }
                    }
                }
            )
        } else {
            onComplete(
                false,
                "La facturación de la tienda no está disponible en este emulador. Puedes probar las funciones Pro con el código de acceso."
            )
        }
    }

    fun restorePurchases(onComplete: (Boolean, String?) -> Unit) {
        try {
            if (Purchases.isConfigured) {
                Purchases.sharedInstance.restorePurchases(object : ReceiveCustomerInfoCallback {
                    override fun onReceived(customerInfo: CustomerInfo) {
                        val hasPro = customerInfo.entitlements[ENTITLEMENT_PRO]?.isActive == true
                        prefs.edit().putBoolean(KEY_REVENUECAT_PRO_CACHED, hasPro).apply()
                        if (hasPro) {
                            _isPro.value = true
                            onComplete(true, "¡Compras restauradas exitosamente!")
                        } else {
                            onComplete(false, "No se encontró suscripción Pro activa para restaurar.")
                        }
                    }

                    override fun onError(error: PurchasesError) {
                        onComplete(false, error.message)
                    }
                })
            } else {
                val hasPromo = prefs.getBoolean(KEY_PROMO_UNLOCKED, false)
                if (hasPromo) {
                    _isPro.value = true
                    onComplete(true, "¡Acceso Pro restaurado!")
                } else {
                    onComplete(false, "No hay compras registradas en este dispositivo.")
                }
            }
        } catch (e: Exception) {
            onComplete(false, e.message ?: "Error al restaurar compras")
        }
    }

    /**
     * Validates Promo Code for Judges (ERGOBREAK2026).
     * If valid, instantly unlocks PRO and saves preference.
     */
    fun redeemPromoCode(code: String): Boolean {
        val trimmed = code.trim()
        if (trimmed.equals(PROMO_CODE_JUDGES, ignoreCase = true)) {
            prefs.edit().putBoolean(KEY_PROMO_UNLOCKED, true).apply()
            _isPro.value = true
            return true
        }
        return false
    }

    fun clearStatusMessage() {
        _billingStatusMessage.value = null
    }
}
