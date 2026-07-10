package com.ju.passgen.util

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.ju.passgen.BuildConfig

/**
 * Gestor de anuncios intersticiales AdMob.
 *
 * Política de frecuencia (respeta las políticas de Google Play):
 * – Máximo 1 intersticial cada 5 minutos
 * – Solo se muestra al guardar un archivo (acción voluntaria del usuario)
 * – NUNCA en generación de contraseñas (mala UX y puede violar políticas)
 * – Pre-carga en background para que no bloquee al usuario cuando aparece
 *
 * Test ID en debug, real en release (via BuildConfig).
 */
object AdManager {

    // Tiempo mínimo entre intersticiales (5 minutos)
    private const val MIN_INTERVAL_MS = 5 * 60 * 1000L

    // Contador de guardados para mostrar ad cada N guardados
    private const val SAVES_BETWEEN_ADS = 3

    private var interstitialAd: InterstitialAd?   = null
    private var lastAdShownTime: Long              = 0L
    private var saveCountSinceLastAd: Int          = 0
    private var isLoading: Boolean                 = false

    private fun adUnitId() = if (BuildConfig.IS_DEBUG)
        "ca-app-pub-3940256099942544/1033173712"  // Test Interstitial
    else
        BuildConfig.ADMOB_INTERSTITIAL_ID

    /**
     * Pre-cargar el intersticial en background.
     * Llamar en JUApp.onCreate() o en el ViewModel al arrancar.
     */
    fun preload(context: Context) {
        if (isLoading || interstitialAd != null) return
        isLoading = true

        InterstitialAd.load(
            context,
            adUnitId(),
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isLoading      = false
                }
                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    isLoading      = false
                }
            }
        )
    }

    /**
     * Intentar mostrar el intersticial al guardar un archivo.
     * Respeta el cap de frecuencia y el contador de guardados.
     *
     * @param activity  Activity actual (necesaria para mostrar el ad)
     * @param context   Contexto para precargar el siguiente
     * @param onDone    Callback que se ejecuta SIEMPRE (con o sin ad)
     */
    fun maybeShowOnSave(activity: Activity, context: Context, onDone: () -> Unit) {
        saveCountSinceLastAd++

        val now           = System.currentTimeMillis()
        val cooldownOk    = (now - lastAdShownTime) >= MIN_INTERVAL_MS
        val frequencyOk   = saveCountSinceLastAd >= SAVES_BETWEEN_ADS
        val adReady       = interstitialAd != null

        if (adReady && cooldownOk && frequencyOk) {
            interstitialAd!!.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd        = null
                    lastAdShownTime       = System.currentTimeMillis()
                    saveCountSinceLastAd  = 0
                    onDone()
                    // Pre-cargar el siguiente en background
                    preload(context)
                }
                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    interstitialAd = null
                    onDone()
                }
            }
            interstitialAd!!.show(activity)
        } else {
            // No hay ad disponible o no cumple las condiciones → continuar normalmente
            onDone()
        }
    }
}
