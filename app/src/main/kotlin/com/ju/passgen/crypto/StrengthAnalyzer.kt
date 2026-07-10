package com.ju.passgen.crypto

import androidx.compose.ui.graphics.Color
import com.ju.passgen.ui.components.PasswordOptions
import com.ju.passgen.ui.components.StrengthInfo
import com.ju.passgen.ui.theme.*
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.log2
import kotlin.math.pow

/**
 * Analizador de fortaleza de contraseñas.
 * Puerto exacto de updateStats(pwd) + formatTime() del JS web.
 *
 * Fórmulas:
 *   entropy    = floor(length × log2(poolSize))
 *   logSeconds = length × log10(poolSize) - 12   ← asume 10^12 intentos/seg (GPU moderna)
 *   crackTime  = 10^logSeconds
 */
object StrengthAnalyzer {

    // Etiquetas por nivel (en español como default — se traduce en la UI)
    private val LEVEL_LABELS = mapOf(
        "es" to arrayOf("MUY DÉBIL", "DÉBIL", "REGULAR", "FUERTE", "FORT. MÁXIMA"),
        "en" to arrayOf("VERY WEAK", "WEAK", "FAIR", "STRONG", "MAXIMUM"),
        "pt" to arrayOf("MUITO FRACA", "FRACA", "REGULAR", "FORTE", "MÁXIMA"),
        "fr" to arrayOf("TRÈS FAIBLE", "FAIBLE", "MOYEN", "FORT", "MAXIMUM"),
        "de" to arrayOf("SEHR SCHWACH", "SCHWACH", "MITTEL", "STARK", "MAXIMUM"),
    )

    /**
     * Calcula la fortaleza completa de una contraseña.
     * @param password La contraseña generada
     * @param options  Las opciones activas al momento de generarla
     * @param lang     Idioma para los labels (es/en/pt/fr/de)
     * @param wordCount Para passphrase: número de palabras
     */
    fun analyze(
        password: String,
        options: PasswordOptions,
        lang: String = "es",
        wordCount: Int = 4,
    ): StrengthInfo {
        if (password.isEmpty()) return empty()

        val labels = LEVEL_LABELS[lang] ?: LEVEL_LABELS["es"]!!

        // ── Calcular entropía según modo ─────────────────────
        val (entropy, crackSeconds, pool) = when {
            options.passphrase -> {
                val listSize   = WordList.size.toDouble()
                val sepEntropy = log2(CharsetBuilder.PASSPHRASE_SEPARATORS.size.toDouble())
                val numEntropy = if (options.numbers) log2(1000.0) else 0.0
                val ent        = floor(wordCount * log2(listSize) + sepEntropy + numEntropy)
                val logSecs    = wordCount * log10(listSize) - 12.0
                val secs       = 10.0.pow(logSecs)
                Triple(ent, secs, listSize.toInt())
            }
            options.pronounceable -> {
                val pool = CharsetBuilder.poolSize(options)
                val ent  = floor(password.length * log2(pool.toDouble()))
                val logSecs = password.length * log10(pool.toDouble()) - 12.0
                Triple(ent, 10.0.pow(logSecs), pool)
            }
            else -> {
                val pool = CharsetBuilder.poolSize(options)
                val ent  = floor(password.length * log2(pool.toDouble()))
                val logSecs = password.length * log10(pool.toDouble()) - 12.0
                Triple(ent, 10.0.pow(logSecs), pool)
            }
        }

        // ── Nivel de fortaleza ────────────────────────────────
        // Umbrales exactos basados en entropía en bits
        val (level, color, percent) = when {
            entropy < 28  -> Triple(0, StrengthVeryWeak, 0.10f)
            entropy < 36  -> Triple(1, StrengthWeak,     0.28f)
            entropy < 60  -> Triple(2, StrengthFair,     0.52f)
            entropy < 80  -> Triple(3, StrengthStrong,   0.76f)
            else           -> Triple(4, StrengthMax,     1.00f)
        }

        return StrengthInfo(
            level       = level,
            label       = labels[level],
            color       = color,
            percent     = percent,
            entropyBits = entropy.toInt().toString(),
            crackTime   = formatTime(crackSeconds),
            poolSize    = pool.toString(),
        )
    }

    /**
     * Formatea segundos en texto legible.
     * Porto exacto de formatTime(seconds) del JS.
     */
    fun formatTime(seconds: Double): String = when {
        seconds < 1           -> "< 1s"
        seconds < 60          -> "${seconds.toLong()}s"
        seconds < 3_600       -> "${(seconds / 60).toLong()}m"
        seconds < 86_400      -> "${(seconds / 3_600).toLong()}h"
        seconds < 31_536_000  -> "${(seconds / 86_400).toLong()}d"
        seconds < 3.15e9      -> "${(seconds / 31_536_000).toLong()}a"
        seconds < 3.15e12     -> ">1,000a"
        seconds < 3.15e15     -> ">1,000,000a"
        seconds < 3.15e18     -> ">10⁹a"
        seconds < 3.15e21     -> ">10¹²a"
        else                   -> "∞"
    }

    fun empty() = StrengthInfo(
        level = 0, label = "—", color = Color.Gray,
        percent = 0f, entropyBits = "—", crackTime = "—", poolSize = "—",
    )
}
