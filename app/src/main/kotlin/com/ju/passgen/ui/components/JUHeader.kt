package com.ju.passgen.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ju.passgen.R
import com.ju.passgen.ui.theme.*

// ── Punto de color del logo (logo-dot) ────────────────────────
@Composable
private fun LogoDot() {
    Box(
        Modifier
            .size(7.dp)
            .background(MaterialTheme.colorScheme.primary, CircleShape)
    )
}

// ── Botón cambiar tema ────────────────────────────────────────
@Composable
fun ThemeToggleButton(
    isDark: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val juColors = MaterialTheme.juColors
    IconButton(
        onClick  = onClick,
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .border(1.dp, juColors.borderColor, CircleShape),
    ) {
        AnimatedContent(targetState = isDark, label = "theme_icon") { dark ->
            Icon(
                painter = painterResource(if (dark) R.drawable.ic_sun else R.drawable.ic_moon),
                contentDescription = stringResource(R.string.cd_toggle_theme),
                tint     = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

// ── Selector de idioma ────────────────────────────────────────
data class LangOption(val code: String, val flag: String, val label: String)

val LANG_OPTIONS = listOf(
    LangOption("es", "🇪🇸", "ES"),
    LangOption("en", "🇬🇧", "EN"),
    LangOption("pt", "🇧🇷", "PT"),
    LangOption("fr", "🇫🇷", "FR"),
    LangOption("de", "🇩🇪", "DE"),
)

@Composable
fun LangSelectorButton(
    currentLang: String,
    onLangSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val juColors = MaterialTheme.juColors
    val current  = LANG_OPTIONS.find { it.code == currentLang } ?: LANG_OPTIONS[0]

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, juColors.borderColor, RoundedCornerShape(12.dp))
                .clickable { expanded = true }
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(current.flag, fontSize = 14.sp)
            Text(
                text  = current.code.uppercase(),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        DropdownMenu(
            expanded         = expanded,
            onDismissRequest = { expanded = false },
            modifier         = Modifier.background(juColors.cardBg),
        ) {
            LANG_OPTIONS.forEach { lang ->
                DropdownMenuItem(
                    text = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment     = Alignment.CenterVertically,
                        ) {
                            Text(lang.flag, fontSize = 14.sp)
                            Text(
                                text  = lang.label,
                                style = MaterialTheme.typography.titleSmall,
                                color = if (lang.code == currentLang)
                                    MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    },
                    onClick = { onLangSelected(lang.code); expanded = false },
                )
            }
        }
    }
}

// ── Header completo ───────────────────────────────────────────
@Composable
fun JUHeader(
    isDark: Boolean,
    currentLang: String,
    onThemeToggle: () -> Unit,
    onLangSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val juColors = MaterialTheme.juColors

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
    ) {
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 8.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // Logo badge: dot + texto
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                LogoDot()
                Text(
                    text  = stringResource(R.string.badge),
                    style = MaterialTheme.typography.labelSmall,
                    color = juColors.mutedText,
                )
            }
            // Controles
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ThemeToggleButton(isDark = isDark, onClick = onThemeToggle)
                LangSelectorButton(
                    currentLang    = currentLang,
                    onLangSelected = onLangSelected,
                )
            }
        }

        // Título "JU" con gradiente + "PASSWORD GENERATOR" muted
        Text(
            text      = "JU",
            style     = MaterialTheme.typography.displayLarge.copy(
                brush = Brush.horizontalGradient(
                    listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                )
            ),
            modifier  = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
        Text(
            text      = "PASSWORD GENERATOR",
            style     = MaterialTheme.typography.displayMedium,
            color     = juColors.mutedText,
            modifier  = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(8.dp))
        HorizontalDivider(color = juColors.borderColor, thickness = 1.dp)
        Spacer(Modifier.height(4.dp))
    }
}
