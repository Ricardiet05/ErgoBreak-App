package com.example.ui

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.Language
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldPro
import com.example.ui.theme.GoldProLight
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardBorder
import com.example.ui.theme.SlateDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun PaywallDialog(
    language: Language,
    statusMessage: String?,
    currentPackage: com.revenuecat.purchases.Package? = null,
    onDismiss: () -> Unit,
    onPurchase: (Activity) -> Unit,
    onRestore: () -> Unit,
    onRedeemPromo: (String) -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity

    var showPromoInput by remember { mutableStateOf(false) }
    var promoCodeInput by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xE6060A14))
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33F59E0B)),
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Close button row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.testTag("close_paywall_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = TextSecondary
                            )
                        }
                    }

                    // Gold Crown / Star Icon
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(GoldProLight, GoldPro, Color(0xFFB45309))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "PRO",
                            tint = Color.Black,
                            modifier = Modifier.size(38.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Title
                    Text(
                        text = if (language == Language.ES) "Desbloquear ErgoBreak Pro" else "Unlock ErgoBreak Pro",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Subtitle
                    Text(
                        text = if (language == Language.ES)
                            "Desbloquea todas las rutinas y ejercicios."
                        else
                            "Unlock all routines and exercises.",
                        fontSize = 14.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Benefits List
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF131E35))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        PaywallFeatureRow(
                            text = if (language == Language.ES)
                                "Acceso a las 5 zonas: Espalda, Manos, Cuerpo Completo"
                            else
                                "Full access to Back, Wrists & Hands, and Full Body"
                        )
                        PaywallFeatureRow(
                            text = if (language == Language.ES)
                                "Todos los 13 ejercicios ergonómicos guiados"
                            else
                                "All 13 guided ergonomic stretches"
                        )
                        PaywallFeatureRow(
                            text = if (language == Language.ES)
                                "Pausas ilimitadas para mejorar postura y bienestar"
                            else
                                "Unlimited desk wellness sessions"
                        )
                    }

                    // Status / Promo message
                    if (statusMessage != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        val isSuccess = statusMessage.contains("¡Pro") ||
                            statusMessage.contains("unlocked", ignoreCase = true) ||
                            statusMessage.contains("exitos", ignoreCase = true)
                        val bgColor = if (isSuccess) Color(0x3310B981) else Color(0x33F59E0B)
                        val textColor = if (isSuccess) EmeraldLight else Color(0xFFFCD34D)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(bgColor)
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = statusMessage,
                                color = textColor,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Purchase Button
                    Button(
                        onClick = {
                            if (activity != null) onPurchase(activity)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("purchase_pro_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPro,
                            contentColor = Color.Black
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            val formattedPrice = currentPackage?.product?.price?.formatted
                            Text(
                                text = if (language == Language.ES) {
                                    if (formattedPrice != null) "SUSCRIBIRME • $formattedPrice/MES"
                                    else "OBTENER ERGOBREAK PRO"
                                } else {
                                    if (formattedPrice != null) "SUBSCRIBE • $formattedPrice/MO"
                                    else "GET ERGOBREAK PRO"
                                },
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Restore Purchases
                    TextButton(
                        onClick = onRestore,
                        modifier = Modifier.testTag("restore_purchases_button")
                    ) {
                        Text(
                            text = if (language == Language.ES) "Restaurar Compras" else "Restore Purchases",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Have a promo code section (Judge promo code ERGOBREAK2026)
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (!showPromoInput) {
                            Text(
                                text = if (language == Language.ES) "¿Tienes un código promocional?" else "Have a promo code?",
                                fontSize = 13.sp,
                                color = EmeraldLight,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .clickable { showPromoInput = true }
                                    .padding(vertical = 6.dp)
                                    .testTag("have_promo_code_button")
                            )
                        } else {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFF131E35))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = if (language == Language.ES) "Código para jueces / demo:" else "Judge / Demo promo code:",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = promoCodeInput,
                                        onValueChange = { promoCodeInput = it },
                                        placeholder = {
                                            Text(
                                                text = "ERGOBREAK2026",
                                                fontSize = 13.sp,
                                                color = TextMuted
                                            )
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(50.dp)
                                            .testTag("promo_code_input"),
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(
                                            capitalization = KeyboardCapitalization.Characters,
                                            imeAction = ImeAction.Done
                                        ),
                                        keyboardActions = KeyboardActions(
                                            onDone = { onRedeemPromo(promoCodeInput) }
                                        ),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = GoldPro,
                                            unfocusedBorderColor = SlateCardBorder,
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary
                                        )
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Button(
                                        onClick = { onRedeemPromo(promoCodeInput) },
                                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier
                                            .height(50.dp)
                                            .testTag("unlock_promo_button")
                                    ) {
                                        Text(
                                            text = if (language == Language.ES) "Desbloquear" else "Unlock",
                                            color = Color.Black,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PaywallFeatureRow(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(Color(0x3310B981)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = EmeraldLight,
                modifier = Modifier.size(13.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            fontSize = 13.sp,
            color = TextPrimary,
            lineHeight = 18.sp
        )
    }
}
