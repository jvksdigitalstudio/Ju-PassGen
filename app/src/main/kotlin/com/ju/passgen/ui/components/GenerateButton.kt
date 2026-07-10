package com.ju.passgen.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ju.passgen.R
import com.ju.passgen.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun GenerateButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isGenerating: Boolean = false,
) {
    val haptic      = LocalHapticFeedback.current
    val scope       = rememberCoroutineScope()
    val juColors    = MaterialTheme.juColors

    // Animación shake al generar
    val shakeOffset = remember { Animatable(0f) }

    val gradient = if (juColors.isDark) {
        Brush.horizontalGradient(listOf(DarkAccent, DarkAccent2))
    } else {
        Brush.horizontalGradient(listOf(LightAccent, LightAccent2))
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .offset(x = shakeOffset.value.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(gradient)
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                scope.launch {
                    // Animación shake: izq → der → izq → centro
                    shakeOffset.animateTo(
                        targetValue   = 0f,
                        animationSpec = keyframes {
                            durationMillis = 400
                            -6f  at 60
                            6f   at 120
                            -4f  at 180
                            4f   at 240
                            -2f  at 300
                            0f   at 400
                        }
                    )
                }
                onClick()
            },
        contentAlignment = Alignment.Center,
    ) {
        if (isGenerating) {
            CircularProgressIndicator(
                modifier  = Modifier.size(24.dp),
                color     = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp,
            )
        } else {
            Text(
                text  = stringResource(R.string.generate),
                style = MaterialTheme.typography.labelLarge,
                color = if (juColors.isDark) DarkBg else LightBg,
            )
        }
    }
}

// ── Autocopy toggle ───────────────────────────────────────────
@Composable
fun AutocopyToggle(
    isEnabled: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val juColors = MaterialTheme.juColors
    Row(
        modifier              = modifier
            .clickable { onToggle() }
            .padding(vertical = 4.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Switch(
            checked  = isEnabled,
            onCheckedChange = { onToggle() },
            colors   = SwitchDefaults.colors(
                checkedThumbColor      = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor      = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor    = juColors.mutedText,
                uncheckedTrackColor    = juColors.borderColor,
            ),
            modifier = Modifier.height(24.dp),
        )
        Text(
            text  = stringResource(R.string.autocopy),
            style = MaterialTheme.typography.bodyMedium,
            color = juColors.mutedText,
        )
    }
}
