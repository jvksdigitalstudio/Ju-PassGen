package com.ju.passgen.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ju.passgen.R
import com.ju.passgen.data.HistoryEntry
import com.ju.passgen.ui.components.AdBanner
import com.ju.passgen.ui.theme.*
import com.ju.passgen.viewmodel.PasswordViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onNavigateBack: () -> Unit,
    vm: PasswordViewModel = viewModel(),
) {
    val state    by vm.uiState.collectAsStateWithLifecycle()
    val juColors = MaterialTheme.juColors
    val haptic   = LocalHapticFeedback.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text  = stringResource(R.string.history_title),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter            = painterResource(R.drawable.ic_back),
                            contentDescription = "Volver",
                            tint               = MaterialTheme.colorScheme.onBackground,
                            modifier           = Modifier.size(24.dp),
                        )
                    }
                },
                actions = {
                    if (state.history.isNotEmpty()) {
                        TextButton(onClick = vm::clearHistory) {
                            Icon(
                                painter            = painterResource(R.drawable.ic_trash),
                                contentDescription = stringResource(R.string.clear),
                                tint               = MaterialTheme.colorScheme.error,
                                modifier           = Modifier.size(18.dp),
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text  = stringResource(R.string.clear),
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            if (state.history.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Icon(
                            painter            = painterResource(R.drawable.ic_lock),
                            contentDescription = null,
                            tint               = juColors.mutedText,
                            modifier           = Modifier.size(48.dp),
                        )
                        Text(
                            text  = stringResource(R.string.history_empty),
                            style = MaterialTheme.typography.bodyMedium,
                            color = juColors.mutedText,
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier        = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 70.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding  = PaddingValues(top = 8.dp, bottom = 16.dp),
                ) {
                    items(state.history, key = { it.timestamp }) { entry ->
                        HistoryCard(
                            entry  = entry,
                            onCopy = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                vm.copyFromHistory(entry.password)
                            },
                        )
                    }
                }
            }
            AdBanner(modifier = Modifier.align(Alignment.BottomCenter))
        }
    }
}

@Composable
private fun HistoryCard(entry: HistoryEntry, onCopy: () -> Unit) {
    val juColors   = MaterialTheme.juColors
    var justCopied by remember { mutableStateOf(false) }

    LaunchedEffect(justCopied) {
        if (justCopied) { delay(1500); justCopied = false }
    }

    val borderColor by animateColorAsState(
        targetValue   = if (justCopied) MaterialTheme.colorScheme.primary else juColors.borderColor,
        animationSpec = tween(300), label = "border",
    )

    Surface(
        modifier        = Modifier.fillMaxWidth(),
        shape           = MaterialTheme.shapes.small,
        color           = juColors.cardBg,
        border          = BorderStroke(1.dp, borderColor),
        tonalElevation  = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text     = entry.password,
                    style    = MaterialTheme.typography.labelMedium,
                    color    = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text  = entry.timeLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = juColors.mutedText,
                )
            }
            Spacer(Modifier.width(8.dp))
            IconButton(
                onClick  = { onCopy(); justCopied = true },
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (justCopied) MaterialTheme.colorScheme.primaryContainer
                        else juColors.accentSoft
                    ),
            ) {
                AnimatedContent(targetState = justCopied, label = "copy_icon") { copied ->
                    Icon(
                        painter            = painterResource(if (copied) R.drawable.ic_check else R.drawable.ic_copy),
                        contentDescription = stringResource(R.string.cd_copy_password),
                        tint               = if (copied) MaterialTheme.colorScheme.primary else juColors.mutedText,
                        modifier           = Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}
