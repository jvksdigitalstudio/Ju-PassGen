package com.ju.passgen.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ju.passgen.R
import com.ju.passgen.ui.theme.*

// ── Estado de todas las opciones (refleja toggles del web) ────
data class PasswordOptions(
    val uppercase:    Boolean = true,
    val lowercase:    Boolean = true,
    val numbers:      Boolean = true,
    val symbols:      Boolean = false,
    val noAmbiguous:  Boolean = false,
    val pronounceable:Boolean = false,
    val passphrase:   Boolean = false,
    val writeMode:    Boolean = false,
)

// ── Un toggle individual ──────────────────────────────────────
@Composable
fun OptionToggle(
    label: String,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val juColors = MaterialTheme.juColors

    val bgColor by animateColorAsState(
        targetValue   = if (isActive) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
        animationSpec = tween(200),
        label         = "toggle_bg",
    )
    val borderColor by animateColorAsState(
        targetValue   = if (isActive) MaterialTheme.colorScheme.primary else juColors.borderColor,
        animationSpec = tween(200),
        label         = "toggle_border",
    )
    val textColor by animateColorAsState(
        targetValue   = if (isActive) MaterialTheme.colorScheme.primary else juColors.mutedText,
        animationSpec = tween(200),
        label         = "toggle_text",
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .then(
                if (enabled) Modifier.clickable(role = Role.Checkbox) { onClick() }
                else Modifier
            )
            .padding(horizontal = 8.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text      = label,
            style     = MaterialTheme.typography.titleSmall,
            color     = if (enabled) textColor else juColors.mutedText.copy(alpha = 0.4f),
            textAlign = TextAlign.Center,
        )
    }
}

// ── Grid completo de 8 opciones (4 columnas × 2 filas) ───────
@Composable
fun OptionsGrid(
    options: PasswordOptions,
    onOptionsChange: (PasswordOptions) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Cuando pronunciable o passphrase está activo, deshabilitar some opciones
    val inPronounce   = options.pronounceable
    val inPassphrase  = options.passphrase

    Column(
        modifier           = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Fila 1: Mayúsculas / Minúsculas / Números / Símbolos
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OptionToggle(
                label    = stringResource(R.string.uppercase),
                isActive = options.uppercase && !inPassphrase,
                enabled  = !inPassphrase,
                onClick  = { onOptionsChange(options.copy(uppercase = !options.uppercase)) },
                modifier = Modifier.weight(1f),
            )
            OptionToggle(
                label    = stringResource(R.string.lowercase),
                isActive = options.lowercase && !inPassphrase,
                enabled  = !inPassphrase,
                onClick  = { onOptionsChange(options.copy(lowercase = !options.lowercase)) },
                modifier = Modifier.weight(1f),
            )
            OptionToggle(
                label    = stringResource(R.string.numbers),
                isActive = options.numbers && !inPassphrase,
                enabled  = !inPassphrase,
                onClick  = { onOptionsChange(options.copy(numbers = !options.numbers)) },
                modifier = Modifier.weight(1f),
            )
            OptionToggle(
                label    = stringResource(R.string.symbols),
                isActive = options.symbols && !inPassphrase && !inPronounce,
                enabled  = !inPassphrase && !inPronounce,
                onClick  = { onOptionsChange(options.copy(symbols = !options.symbols)) },
                modifier = Modifier.weight(1f),
            )
        }

        // Fila 2: Sin ambiguos / Pronunciable / Passphrase / Escribir
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OptionToggle(
                label    = stringResource(R.string.no_ambiguous),
                isActive = options.noAmbiguous && !inPassphrase,
                enabled  = !inPassphrase,
                onClick  = { onOptionsChange(options.copy(noAmbiguous = !options.noAmbiguous)) },
                modifier = Modifier.weight(1f),
            )
            OptionToggle(
                label    = stringResource(R.string.pronounceable),
                isActive = options.pronounceable,
                onClick  = {
                    onOptionsChange(
                        options.copy(
                            pronounceable = !options.pronounceable,
                            passphrase    = false,   // mutuamente exclusivo
                        )
                    )
                },
                modifier = Modifier.weight(1f),
            )
            OptionToggle(
                label    = stringResource(R.string.passphrase),
                isActive = options.passphrase,
                onClick  = {
                    onOptionsChange(
                        options.copy(
                            passphrase    = !options.passphrase,
                            pronounceable = false,   // mutuamente exclusivo
                        )
                    )
                },
                modifier = Modifier.weight(1f),
            )
            OptionToggle(
                label    = stringResource(R.string.write_mode),
                isActive = options.writeMode,
                onClick  = { onOptionsChange(options.copy(writeMode = !options.writeMode)) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}
