package com.ju.passgen.ui.screens

import android.app.Activity
import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ju.passgen.R
import com.ju.passgen.ui.components.*
import com.ju.passgen.ui.theme.*
import com.ju.passgen.viewmodel.PasswordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneratorScreen(
    onNavigateToHistory: () -> Unit,
    vm: PasswordViewModel = viewModel(),
) {
    val state             by vm.uiState.collectAsStateWithLifecycle()
    val juColors          = MaterialTheme.juColors
    val scrollState       = rememberScrollState()
    var showSave          by remember { mutableStateOf(false) }
    val context           = LocalContext.current
    val activity          = context as? Activity
    val snackbarHostState = remember { SnackbarHostState() }

    // Toast desde ViewModel
    LaunchedEffect(state.toastMessage) {
        state.toastMessage?.let { snackbarHostState.showSnackbar(it) }
    }

    // Compartir archivo cuando el ViewModel lo produce
    LaunchedEffect(state.pendingShare) {
        if (state.pendingShare && state.shareUri != null) {
            // Mostrar intersticial AdMob si corresponde, luego compartir
            activity?.let { act ->
                vm.onSaveCompleted(act) {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "application/octet-stream"
                        putExtra(Intent.EXTRA_STREAM, state.shareUri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(intent, "Exportar contraseña"))
                    vm.clearShareUri()
                }
            } ?: vm.clearShareUri()
        }
    }

    Scaffold(
        snackbarHost   = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData     = data,
                    containerColor   = juColors.cardBg,
                    contentColor     = MaterialTheme.colorScheme.onSurface,
                    actionColor      = MaterialTheme.colorScheme.primary,
                    shape            = MaterialTheme.shapes.small,
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // ── Header ────────────────────────────────────
                JUHeader(
                    isDark         = juColors.isDark,
                    currentLang    = state.currentLang,
                    onThemeToggle  = vm::toggleTheme,
                    onLangSelected = vm::setLanguage,
                )

                // ── Contenido scrollable ──────────────────────
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Spacer(Modifier.height(8.dp))

                    // Contraseña
                    PasswordDisplay(
                        password         = state.password,
                        isWriteMode      = state.options.writeMode,
                        onPasswordChange = vm::setPassword,
                        onCopy           = vm::copyPassword,
                    )

                    // Fuerza
                    AnimatedVisibility(
                        visible = state.password.isNotEmpty(),
                        enter   = fadeIn() + expandVertically(),
                        exit    = fadeOut() + shrinkVertically(),
                    ) {
                        StrengthMeter(strength = state.strength)
                    }

                    // Opciones
                    SectionCard {
                        OptionsGrid(
                            options         = state.options,
                            onOptionsChange = vm::updateOptions,
                        )
                    }

                    // Slider longitud (oculto en passphrase)
                    AnimatedVisibility(
                        visible = !state.options.passphrase,
                        enter   = fadeIn() + expandVertically(),
                        exit    = fadeOut() + shrinkVertically(),
                    ) {
                        SectionCard {
                            LengthSlider(
                                length         = state.length,
                                onLengthChange = vm::setLength,
                            )
                        }
                    }

                    // Slider palabras (solo en passphrase)
                    AnimatedVisibility(
                        visible = state.options.passphrase,
                        enter   = fadeIn() + expandVertically(),
                        exit    = fadeOut() + shrinkVertically(),
                    ) {
                        SectionCard {
                            WordCountSlider(
                                wordCount         = state.wordCount,
                                onWordCountChange = vm::setWordCount,
                            )
                        }
                    }

                    // Autocopy + botón guardar
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        AutocopyToggle(
                            isEnabled = state.autocopy,
                            onToggle  = vm::toggleAutocopy,
                        )
                        AnimatedVisibility(visible = state.password.isNotEmpty()) {
                            IconButton(onClick = { showSave = true }) {
                                Icon(
                                    painter            = painterResource(R.drawable.ic_save),
                                    contentDescription = stringResource(R.string.save),
                                    tint               = MaterialTheme.colorScheme.primary,
                                    modifier           = Modifier.size(22.dp),
                                )
                            }
                        }
                    }

                    // Botón GENERAR
                    GenerateButton(
                        onClick      = vm::generate,
                        isGenerating = state.isGenerating,
                    )

                    // Historial mini
                    AnimatedVisibility(
                        visible = state.history.isNotEmpty(),
                        enter   = fadeIn() + expandVertically(),
                        exit    = fadeOut() + shrinkVertically(),
                    ) {
                        SectionCard {
                            HistoryMini(
                                history  = state.history,
                                onCopy   = vm::copyFromHistory,
                                onClear  = vm::clearHistory,
                                onSeeAll = onNavigateToHistory,
                            )
                        }
                    }

                    // Footer
                    FooterNote()
                }
            }

            // Banner AdMob — fijo en la parte inferior
            AdBanner(modifier = Modifier.align(Alignment.BottomCenter))
        }
    }

    // Bottom sheet guardar
    if (showSave) {
        SaveBottomSheet(
            onDismiss = { showSave = false },
            onSave    = { format ->
                vm.savePassword(format)
                showSave = false
            },
        )
    }
}

// ── Card de sección con borde ─────────────────────────────────
@Composable
fun SectionCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val juColors = MaterialTheme.juColors
    Surface(
        modifier        = modifier.fillMaxWidth(),
        shape           = MaterialTheme.shapes.medium,
        color           = juColors.cardBg,
        border          = androidx.compose.foundation.BorderStroke(1.dp, juColors.borderColor),
        tonalElevation  = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

// ── Historial compacto ────────────────────────────────────────
@Composable
private fun HistoryMini(
    history: List<com.ju.passgen.data.HistoryEntry>,
    onCopy: (String) -> Unit,
    onClear: () -> Unit,
    onSeeAll: () -> Unit,
) {
    val juColors = MaterialTheme.juColors
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        Text(
            text  = stringResource(R.string.history_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Row {
            TextButton(onClick = onSeeAll) {
                Text(
                    "Ver todo",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            TextButton(onClick = onClear) {
                Text(
                    stringResource(R.string.clear),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
    Spacer(Modifier.height(6.dp))
    history.take(3).forEach { entry ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.surfaceVariant,
                    MaterialTheme.shapes.extraSmall,
                )
                .padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text     = entry.password,
                style    = MaterialTheme.typography.labelMedium,
                color    = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text  = entry.timeLabel,
                style = MaterialTheme.typography.bodyMedium,
                color = juColors.mutedText,
            )
            IconButton(
                onClick  = { onCopy(entry.password) },
                modifier = Modifier.size(32.dp),
            ) {
                Icon(
                    painter            = painterResource(R.drawable.ic_copy),
                    contentDescription = null,
                    tint               = juColors.mutedText,
                    modifier           = Modifier.size(14.dp),
                )
            }
        }
        Spacer(Modifier.height(4.dp))
    }
}

// ── Footer ────────────────────────────────────────────────────
@Composable
private fun FooterNote() {
    val juColors = MaterialTheme.juColors
    Column(
        modifier            = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text  = stringResource(R.string.security_note),
            style = MaterialTheme.typography.bodyMedium,
            color = juColors.mutedText,
        )
        Text(
            text  = "JU Password Generator © 2026",
            style = MaterialTheme.typography.bodyMedium,
            color = juColors.mutedText.copy(alpha = 0.4f),
        )
    }
}
