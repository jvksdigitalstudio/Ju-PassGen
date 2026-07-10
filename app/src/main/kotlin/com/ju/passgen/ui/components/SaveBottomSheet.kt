package com.ju.passgen.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ju.passgen.R
import com.ju.passgen.ui.theme.*
import com.ju.passgen.util.CompressionEngine

// ── Formatos de exportación ───────────────────────────────────
enum class SaveFormat {
    TXT,
    PDF,
    ZIP,
    SEVEN_Z,
    TAR_GZ;

    fun toCompressionFormat(): CompressionEngine.Format? = when (this) {
        ZIP     -> CompressionEngine.Format.ZIP
        SEVEN_Z -> CompressionEngine.Format.SEVEN_Z
        TAR_GZ  -> CompressionEngine.Format.TAR_GZ
        else    -> null
    }
}

data class SaveOption(
    val format: SaveFormat,
    val title: String,
    val subtitle: String,
    val iconRes: Int,
    val badge: String? = null,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveBottomSheet(
    onDismiss: () -> Unit,
    onSave: (SaveFormat) -> Unit,
) {
    val juColors = MaterialTheme.juColors
    var selected by remember { mutableStateOf(SaveFormat.TXT) }

    val options = listOf(
        SaveOption(
            format   = SaveFormat.TXT,
            title    = "TXT",
            subtitle = "Archivo de texto · Compatible con todo",
            iconRes  = R.drawable.ic_file_text,
            badge    = "Recomendado",
        ),
        SaveOption(
            format   = SaveFormat.PDF,
            title    = "PDF",
            subtitle = "Documento portable · Listo para imprimir",
            iconRes  = R.drawable.ic_file_pdf,
        ),
        SaveOption(
            format   = SaveFormat.ZIP,
            title    = "ZIP",
            subtitle = "Deflate nivel 9 · Máxima compresión",
            iconRes  = R.drawable.ic_file_zip,
        ),
        SaveOption(
            format   = SaveFormat.SEVEN_Z,
            title    = "7Z",
            subtitle = "LZMA2 · El mejor ratio de compresión",
            iconRes  = R.drawable.ic_file_zip,
            badge    = "Mejor ratio",
        ),
        SaveOption(
            format   = SaveFormat.TAR_GZ,
            title    = "TAR.GZ",
            subtitle = "GZIP nivel 9 · Estándar Unix/Linux",
            iconRes  = R.drawable.ic_file_zip,
        ),
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor   = juColors.cardBg,
        dragHandle = {
            Box(
                Modifier
                    .padding(top = 12.dp, bottom = 8.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(juColors.borderColor)
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 36.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            // ── Título ────────────────────────────────────────
            Column {
                Text(
                    text  = "GUARDAR CONTRASEÑA COMO",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text  = "// ELIGE EL FORMATO DE EXPORTACIÓN",
                    style = MaterialTheme.typography.bodyMedium,
                    color = juColors.mutedText,
                )
            }

            HorizontalDivider(color = juColors.borderColor)

            // ── Opciones ──────────────────────────────────────
            options.forEach { opt ->
                SaveOptionRow(
                    option   = opt,
                    selected = selected == opt.format,
                    onClick  = { selected = opt.format },
                )
            }

            Spacer(Modifier.height(6.dp))

            // ── Botón principal ───────────────────────────────
            Button(
                onClick  = { onSave(selected); onDismiss() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape  = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor   = if (juColors.isDark) DarkBg else LightBg,
                ),
            ) {
                Icon(
                    painter            = painterResource(R.drawable.ic_save),
                    contentDescription = null,
                    modifier           = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text  = "DESCARGAR AHORA",
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

@Composable
private fun SaveOptionRow(
    option: SaveOption,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val juColors = MaterialTheme.juColors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (selected) MaterialTheme.colorScheme.primaryContainer
                else juColors.cardBg
            )
            .border(
                1.dp,
                if (selected) MaterialTheme.colorScheme.primary else juColors.borderColor,
                RoundedCornerShape(14.dp),
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Ícono
        Icon(
            painter            = painterResource(option.iconRes),
            contentDescription = null,
            tint               = if (selected) MaterialTheme.colorScheme.primary else juColors.mutedText,
            modifier           = Modifier.size(22.dp),
        )

        // Texto
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text  = option.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (selected)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurface,
                )
                // Badge opcional
                option.badge?.let { badge ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                    ) {
                        Text(
                            text  = badge,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
            Text(
                text  = option.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = juColors.mutedText,
            )
        }

        // Radio button
        RadioButton(
            selected = selected,
            onClick  = onClick,
            colors   = RadioButtonDefaults.colors(
                selectedColor   = MaterialTheme.colorScheme.primary,
                unselectedColor = juColors.borderColor,
            ),
        )
    }
}
