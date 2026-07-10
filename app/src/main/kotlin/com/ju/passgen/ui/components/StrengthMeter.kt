package com.ju.passgen.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ju.passgen.R
import com.ju.passgen.ui.theme.*

// ── Modelo de datos de fuerza ─────────────────────────────────
data class StrengthInfo(
    val level: Int,         // 0..4
    val label: String,
    val color: Color,
    val percent: Float,     // 0f..1f
    val entropyBits: String,
    val crackTime: String,
    val poolSize: String,
)

@Composable
fun StrengthMeter(
    strength: StrengthInfo,
    modifier: Modifier = Modifier,
) {
    val juColors = MaterialTheme.juColors

    // Animación de la barra
    val animatedWidth by animateFloatAsState(
        targetValue   = strength.percent,
        animationSpec = tween(durationMillis = 400, easing = EaseOutCubic),
        label         = "strength_bar",
    )
    val animatedColor by animateColorAsState(
        targetValue   = strength.color,
        animationSpec = tween(400),
        label         = "strength_color",
    )

    Column(modifier = modifier.fillMaxWidth()) {
        // ── Label + valor ──────────────────────────────────────
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            Text(
                text  = stringResource(R.string.strength),
                style = MaterialTheme.typography.bodyMedium,
                color = juColors.mutedText,
            )
            Text(
                text  = strength.label,
                style = MaterialTheme.typography.titleSmall,
                color = animatedColor,
            )
        }

        Spacer(Modifier.height(6.dp))

        // ── Barra de progreso ──────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(juColors.borderColor),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedWidth)
                    .clip(RoundedCornerShape(3.dp))
                    .background(animatedColor),
            )
        }

        Spacer(Modifier.height(12.dp))

        // ── 3 stat chips ──────────────────────────────────────
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            StatChip(
                label = stringResource(R.string.bits_entropy),
                value = strength.entropyBits,
                color = animatedColor,
                modifier = Modifier.weight(1f),
            )
            StatChip(
                label = stringResource(R.string.crack_time),
                value = strength.crackTime,
                color = animatedColor,
                modifier = Modifier.weight(1f),
            )
            StatChip(
                label = stringResource(R.string.possible_chars),
                value = strength.poolSize,
                color = animatedColor,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun StatChip(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val juColors = MaterialTheme.juColors
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, juColors.borderColor, RoundedCornerShape(12.dp))
            .background(juColors.cardBg)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text      = value.ifEmpty { "—" },
            style     = MaterialTheme.typography.bodySmall,
            color     = color,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text      = label,
            style     = MaterialTheme.typography.bodyMedium,
            color     = juColors.mutedText,
            textAlign = TextAlign.Center,
        )
    }
}
