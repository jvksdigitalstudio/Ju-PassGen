package com.ju.passgen.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.*
import com.ju.passgen.BuildConfig

/**
 * Banner AdMob profesional.
 * – Invisible hasta que carga (no muestra espacio vacío)
 * – Fade-in suave al aparecer
 * – Test ID en debug, real en release vía BuildConfig
 * – Silencioso si no hay conexión
 */
@Composable
fun AdBanner(modifier: Modifier = Modifier) {
    var adLoaded by remember { mutableStateOf(false) }

    AnimatedVisibility(
        visible = adLoaded,
        enter   = fadeIn(animationSpec = tween(400)),
        exit    = fadeOut(animationSpec = tween(200)),
        modifier = modifier.fillMaxWidth(),
    ) {
        Box(
            modifier         = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 50.dp),
            contentAlignment = Alignment.Center,
        ) {
            AndroidView(
                factory = { ctx ->
                    AdView(ctx).apply {
                        adUnitId = if (BuildConfig.IS_DEBUG)
                            "ca-app-pub-3940256099942544/6300978111"
                        else
                            BuildConfig.ADMOB_BANNER_ID

                        setAdSize(AdSize.BANNER)

                        adListener = object : AdListener() {
                            override fun onAdLoaded() {
                                adLoaded = true
                            }
                            override fun onAdFailedToLoad(error: LoadAdError) {
                                adLoaded = false
                            }
                        }
                        loadAd(AdRequest.Builder().build())
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
