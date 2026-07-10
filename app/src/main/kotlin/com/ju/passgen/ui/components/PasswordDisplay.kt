package com.ju.passgen.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ju.passgen.R
import com.ju.passgen.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun PasswordDisplay(
    password: String,
    isWriteMode: Boolean,
    onPasswordChange: (String) -> Unit,
    onCopy: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val juColors   = MaterialTheme.juColors
    var revealed   by remember { mutableStateOf(true) }
    var justCopied by remember { mutableStateOf(false) }
    val haptic     = LocalHapticFeedback.current

    LaunchedEffect(justCopied) {
        if (justCopied) { delay(1500); justCopied = false }
    }

    val borderColor by animateColorAsState(
        targetValue   = if (justCopied) MaterialTheme.colorScheme.primary else juColors.borderColor,
        animationSpec = tween(300),
        label         = "border_color",
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(juColors.cardBg)
                .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            // ── Campo de contraseña ───────────────────────────
            if (isWriteMode) {
                BasicTextField(
                    value         = password,
                    onValueChange = onPasswordChange,
                    textStyle     = TextStyle(
                        fontFamily = SpaceMonoFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize   = 16.sp,
                        color      = MaterialTheme.colorScheme.onSurface,
                        lineHeight  = 24.sp,
                    ),
                    cursorBrush   = SolidColor(MaterialTheme.colorScheme.primary),
                    modifier      = Modifier
                        .fillMaxWidth()
                        .padding(end = 72.dp),
                )
            } else {
                // Mostrar contraseña o hint
                val displayText = when {
                    password.isEmpty() -> stringResource(R.string.click_generate)
                    !revealed          -> "•".repeat(password.length)
                    else               -> password
                }
                Text(
                    text      = displayText,
                    style     = if (password.isEmpty())
                        MaterialTheme.typography.bodyMedium
                    else
                        MaterialTheme.typography.bodyLarge,
                    color     = if (password.isEmpty() || !revealed)
                        juColors.mutedText
                    else
                        MaterialTheme.colorScheme.onSurface,
                    modifier  = Modifier
                        .fillMaxWidth()
                        .padding(end = 72.dp),
                )
            }

            // ── Botones revelar + copiar ──────────────────────
            Row(
                modifier              = Modifier.align(Alignment.TopEnd),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                // Revelar/ocultar
                if (password.isNotEmpty() && !isWriteMode) {
                    IconButton(
                        onClick  = { revealed = !revealed },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape),
                    ) {
                        Icon(
                            painter = painterResource(
                                if (revealed) R.drawable.ic_eye_off else R.drawable.ic_eye
                            ),
                            contentDescription = stringResource(R.string.cd_reveal_password),
                            tint     = juColors.mutedText,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }

                // Copiar
                if (password.isNotEmpty()) {
                    AnimatedContent(targetState = justCopied, label = "copy_btn") { copied ->
                        IconButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onCopy()
                                justCopied = true
                            },
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(
                                    if (copied) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                    else juColors.accentSoft
                                ),
                        ) {
                            Icon(
                                painter = painterResource(
                                    if (copied) R.drawable.ic_check else R.drawable.ic_copy
                                ),
                                contentDescription = stringResource(R.string.cd_copy_password),
                                tint     = if (copied) MaterialTheme.colorScheme.primary else juColors.mutedText,
                                modifier = Modifier.size(16.dp),
                            )
                        }
                    }
                }
            }
        }

        // "¡Copiado!" flash
        AnimatedVisibility(
            visible = justCopied,
            enter   = fadeIn() + slideInVertically(),
            exit    = fadeOut(),
        ) {
            Text(
                text      = stringResource(R.string.copied),
                style     = MaterialTheme.typography.bodyMedium,
                color     = MaterialTheme.colorScheme.primary,
                modifier  = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                textAlign = TextAlign.Center,
            )
        }
    }
}
