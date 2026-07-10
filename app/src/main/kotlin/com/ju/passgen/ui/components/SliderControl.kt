package com.ju.passgen.ui.components

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ju.passgen.R
import com.ju.passgen.ui.theme.*

// ── Slider de longitud (6–64, default 16) ─────────────────────
@Composable
fun LengthSlider(
    length: Int,
    onLengthChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    SliderControl(
        label       = stringResource(R.string.length),
        value       = length,
        valueRange  = 6..64,
        onValueChange = onLengthChange,
        modifier    = modifier,
    )
}

// ── Slider de palabras (3–8, default 4) ───────────────────────
@Composable
fun WordCountSlider(
    wordCount: Int,
    onWordCountChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    SliderControl(
        label       = stringResource(R.string.word_count),
        value       = wordCount,
        valueRange  = 3..8,
        onValueChange = onWordCountChange,
        modifier    = modifier,
    )
}

// ── Componente slider base ─────────────────────────────────────
@Composable
private fun SliderControl(
    label: String,
    value: Int,
    valueRange: IntRange,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val juColors = MaterialTheme.juColors

    // Animación del badge al cambiar valor
    val scale by animateFloatAsState(
        targetValue   = 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label         = "badge_scale",
    )

    Column(modifier = modifier.fillMaxWidth()) {
        // Label + badge con valor actual
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            Text(
                text  = label,
                style = MaterialTheme.typography.bodyMedium,
                color = juColors.mutedText,
            )

            // Badge animado con el valor
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(juColors.accentSoft)
                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 3.dp),
            ) {
                Text(
                    text  = value.toString(),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Slider con track personalizado
        Slider(
            value         = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange    = valueRange.first.toFloat()..valueRange.last.toFloat(),
            steps         = valueRange.last - valueRange.first - 1,
            modifier      = Modifier.fillMaxWidth(),
            colors        = SliderDefaults.colors(
                thumbColor            = MaterialTheme.colorScheme.primary,
                activeTrackColor      = MaterialTheme.colorScheme.primary,
                inactiveTrackColor    = juColors.borderColor,
                activeTickColor       = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                inactiveTickColor     = juColors.borderColor,
            ),
        )

        // Ticks mín / máx
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text  = valueRange.first.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = juColors.mutedText,
            )
            Text(
                text  = valueRange.last.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = juColors.mutedText,
            )
        }
    }
}
