package com.ju.passgen.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ju.passgen.crypto.PasswordGenerator
import com.ju.passgen.crypto.StrengthAnalyzer
import com.ju.passgen.data.HistoryEntry
import com.ju.passgen.ui.components.PasswordOptions
import com.ju.passgen.ui.components.SaveFormat
import com.ju.passgen.ui.components.StrengthInfo
import com.ju.passgen.util.AdManager
import com.ju.passgen.util.CompressionEngine
import com.ju.passgen.util.FileSaver
import com.ju.passgen.util.SecureClipboard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// ── Estado completo de la UI ──────────────────────────────────
data class PasswordUiState(
    val password:     String             = "",
    val options:      PasswordOptions    = PasswordOptions(),
    val length:       Int                = 16,
    val wordCount:    Int                = 4,
    val autocopy:     Boolean            = false,
    val isGenerating: Boolean            = false,
    val history:      List<HistoryEntry> = emptyList(),
    val strength:     StrengthInfo       = StrengthAnalyzer.empty(),
    val currentLang:  String             = "es",
    val isDark:       Boolean            = true,
    val toastMessage: String?            = null,
    val shareUri:     android.net.Uri?   = null,
    val pendingShare: Boolean            = false,
)

class PasswordViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(PasswordUiState())
    val uiState: StateFlow<PasswordUiState> = _uiState.asStateFlow()

    private val ctx get() = getApplication<Application>()

    // ── GENERACIÓN ────────────────────────────────────────────
    fun generate() {
        val state = _uiState.value
        _uiState.update { it.copy(isGenerating = true) }

        viewModelScope.launch {
            val pwd = withContext(Dispatchers.Default) {
                PasswordGenerator.generate(
                    length    = state.length,
                    wordCount = state.wordCount,
                    options   = state.options,
                )
            }

            val strength = StrengthAnalyzer.analyze(
                password  = pwd,
                options   = state.options,
                lang      = state.currentLang,
                wordCount = state.wordCount,
            )

            _uiState.update { s ->
                val history = (listOf(HistoryEntry(pwd)) + s.history).take(10)
                s.copy(
                    password     = pwd,
                    strength     = strength,
                    isGenerating = false,
                    history      = history,
                )
            }

            if (_uiState.value.autocopy) {
                SecureClipboard.copy(ctx, pwd)
                scheduleClearClipboard()
            }
        }
    }

    // ── OPCIONES ──────────────────────────────────────────────
    fun updateOptions(opts: PasswordOptions) {
        _uiState.update { it.copy(options = opts) }
        // Regenerar pasando las nuevas opciones directamente — evita race condition con Flow
        val current = _uiState.value
        if (current.password.isNotEmpty()) {
            viewModelScope.launch {
                val pwd = withContext(Dispatchers.Default) {
                    PasswordGenerator.generate(
                        length    = current.length,
                        wordCount = current.wordCount,
                        options   = opts,   // <-- opciones nuevas directamente, no del state
                    )
                }
                val strength = StrengthAnalyzer.analyze(
                    password  = pwd,
                    options   = opts,
                    lang      = current.currentLang,
                    wordCount = current.wordCount,
                )
                _uiState.update { s ->
                    val history = (listOf(HistoryEntry(pwd)) + s.history).take(10)
                    s.copy(password = pwd, strength = strength, history = history)
                }
                if (_uiState.value.autocopy) {
                    SecureClipboard.copy(ctx, pwd)
                    scheduleClearClipboard()
                }
            }
        }
    }

    fun setLength(len: Int) {
        _uiState.update { it.copy(length = len.coerceIn(6, 64)) }
    }

    fun setWordCount(wc: Int) {
        _uiState.update { it.copy(wordCount = wc.coerceIn(3, 8)) }
    }

    fun toggleAutocopy() {
        _uiState.update { it.copy(autocopy = !it.autocopy) }
    }

    fun setPassword(pwd: String) {
        if (_uiState.value.options.writeMode) {
            _uiState.update { it.copy(password = pwd) }
        }
    }

    // ── TEMA / IDIOMA ─────────────────────────────────────────
    fun toggleTheme() {
        _uiState.update { it.copy(isDark = !it.isDark) }
    }

    fun setLanguage(lang: String) {
        _uiState.update { it.copy(currentLang = lang) }
        val state = _uiState.value
        if (state.password.isNotEmpty()) {
            val strength = StrengthAnalyzer.analyze(
                password  = state.password,
                options   = state.options,
                lang      = lang,
                wordCount = state.wordCount,
            )
            _uiState.update { it.copy(strength = strength) }
        }
    }

    // ── CLIPBOARD ─────────────────────────────────────────────
    fun copyPassword() {
        val pwd = _uiState.value.password
        if (pwd.isBlank()) return
        SecureClipboard.copy(ctx, pwd)
        scheduleClearClipboard()
        showToast("✓ Copiado — se limpiará en 60s")
    }

    fun copyFromHistory(pwd: String) {
        SecureClipboard.copy(ctx, pwd)
        scheduleClearClipboard()
        showToast("✓ Copiado al portapapeles")
    }

    private fun scheduleClearClipboard() {
        viewModelScope.launch {
            delay(60_000)
            SecureClipboard.clear(ctx)
        }
    }

    // ── HISTORIAL ─────────────────────────────────────────────
    fun clearHistory() {
        _uiState.update { it.copy(history = emptyList()) }
    }

    // ── GUARDAR / EXPORTAR ────────────────────────────────────
    fun savePassword(format: SaveFormat) {
        val pwd = _uiState.value.password
        if (pwd.isBlank()) return

        viewModelScope.launch {
            try {
                val uri = withContext(Dispatchers.IO) {
                    when (format) {
                        SaveFormat.TXT    -> FileSaver.saveTxt(ctx, pwd)
                        SaveFormat.PDF    -> FileSaver.savePdf(ctx, pwd)
                        SaveFormat.ZIP    -> CompressionEngine.compress(ctx, pwd, CompressionEngine.Format.ZIP).uri
                        SaveFormat.SEVEN_Z -> CompressionEngine.compress(ctx, pwd, CompressionEngine.Format.SEVEN_Z).uri
                        SaveFormat.TAR_GZ -> CompressionEngine.compress(ctx, pwd, CompressionEngine.Format.TAR_GZ).uri
                    }
                }

                _uiState.update { it.copy(shareUri = uri, pendingShare = true) }
                showToast(format.successMsg())

            } catch (e: Exception) {
                showToast("❌ Error: ${e.message?.take(60)}")
            }
        }
    }

    fun clearShareUri() {
        _uiState.update { it.copy(shareUri = null, pendingShare = false) }
    }

    // ── ADMOB: trigger intersticial al guardar ────────────────
    fun onSaveCompleted(activity: android.app.Activity, onDone: () -> Unit) {
        AdManager.maybeShowOnSave(activity, ctx, onDone)
    }

    // ── TOAST ─────────────────────────────────────────────────
    fun showToast(msg: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(toastMessage = msg) }
            delay(2800)
            _uiState.update { it.copy(toastMessage = null) }
        }
    }

    // ── Extensiones SaveFormat ────────────────────────────────
    private fun SaveFormat.mimeType() = when (this) {
        SaveFormat.TXT     -> "text/plain"
        SaveFormat.PDF     -> "application/pdf"
        SaveFormat.ZIP     -> "application/zip"
        SaveFormat.SEVEN_Z -> "application/x-7z-compressed"
        SaveFormat.TAR_GZ  -> "application/gzip"
    }

    private fun SaveFormat.successMsg() = when (this) {
        SaveFormat.TXT     -> "✓ TXT guardado en Descargas"
        SaveFormat.PDF     -> "✓ PDF guardado en Descargas"
        SaveFormat.ZIP     -> "✓ ZIP (Deflate 9) listo"
        SaveFormat.SEVEN_Z -> "✓ 7Z (LZMA2) listo"
        SaveFormat.TAR_GZ  -> "✓ TAR.GZ listo"
    }
}
