package com.ju.passgen

import android.app.Application
import com.ju.passgen.BuildConfig
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.ju.passgen.util.AdManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Application class.
 * – Inicializa AdMob en background (no bloquea arranque)
 * – En debug activa Test Device Mode automáticamente
 * – Pre-carga el intersticial desde el arranque
 */
class JUApp : Application() {

    override fun onCreate() {
        super.onCreate()

        CoroutineScope(Dispatchers.IO).launch {
            // Configurar dispositivos de prueba en debug
            if (BuildConfig.IS_DEBUG) {
                val config = RequestConfiguration.Builder()
                    .setTestDeviceIds(listOf(
                        RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_UNSPECIFIED.toString(),
                        "EMULATOR",
                    ))
                    .build()
                MobileAds.setRequestConfiguration(config)
            }

            // Inicializar AdMob
            MobileAds.initialize(this@JUApp)

            // Pre-cargar intersticial en background
            AdManager.preload(this@JUApp)
        }
    }
}
