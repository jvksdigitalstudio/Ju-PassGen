package com.ju.passgen.crypto

import com.ju.passgen.ui.components.PasswordOptions

/**
 * Construye el charset de caracteres exactamente igual que buildCharset() del JS.
 *
 * CHARS del web:
 *   upper:         'ABCDEFGHIJKLMNOPQRSTUVWXYZ'
 *   lower:         'abcdefghijklmnopqrstuvwxyz'
 *   numbers:       '0123456789'
 *   symbols:       '!@#$%^&*()-_=+[]{}|;:,.<>?'
 *   ambiguousChars:'Il1O0o'
 *   consonants:    'bcdfghjklmnpqrstvwxyz'
 *   vowels:        'aeiou'
 */
object CharsetBuilder {

    // ── Sets de caracteres idénticos al JS ────────────────────
    const val UPPER     = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    const val LOWER     = "abcdefghijklmnopqrstuvwxyz"
    const val NUMBERS   = "0123456789"
    const val SYMBOLS   = "!@#\$%^&*()-_=+[]{}|;:,.<>?"
    const val AMBIGUOUS = "Il1O0o"
    const val CONSONANTS = "bcdfghjklmnpqrstvwxyz"
    const val VOWELS     = "aeiou"

    // Separadores de passphrase (mismos que el JS)
    val PASSPHRASE_SEPARATORS = listOf("-", "_", ".", "#", "@")

    /**
     * Construye el pool de caracteres según las opciones activas.
     * Si el pool queda vacío activa lower+numbers como fallback (igual que JS).
     */
    fun build(options: PasswordOptions): String {
        var pool = buildString {
            if (options.uppercase)  append(UPPER)
            if (options.lowercase)  append(LOWER)
            if (options.numbers)    append(NUMBERS)
            if (options.symbols)    append(SYMBOLS)
        }

        // Fallback: si nada está activo → lower + numbers
        if (pool.isEmpty()) pool = LOWER + NUMBERS

        // Filtrar ambiguos si está activo
        if (options.noAmbiguous) {
            val amb = AMBIGUOUS.toSet()
            pool = pool.filter { it !in amb }
        }

        return pool
    }

    /**
     * Pool de consonantes (para modo pronunciable) con filtro ambiguos.
     */
    fun consonants(noAmbiguous: Boolean): String {
        return if (noAmbiguous) {
            val amb = AMBIGUOUS.toSet()
            CONSONANTS.filter { it !in amb }
        } else CONSONANTS
    }

    /**
     * Pool de vocales (para modo pronunciable) con filtro ambiguos.
     */
    fun vowels(noAmbiguous: Boolean): String {
        return if (noAmbiguous) {
            val amb = AMBIGUOUS.toSet()
            VOWELS.filter { it !in amb }
        } else VOWELS
    }

    /**
     * Calcula el tamaño efectivo del pool para el cálculo de entropía.
     * Porta exactamente la lógica de updateStats() del JS.
     */
    fun poolSize(options: PasswordOptions): Int {
        return when {
            options.passphrase -> 440 // WORD_LIST.size

            options.pronounceable -> {
                val c = consonants(options.noAmbiguous).length
                val v = vowels(options.noAmbiguous).length
                var size = c + v
                if (options.numbers) {
                    size += if (options.noAmbiguous) {
                        val amb = AMBIGUOUS.toSet()
                        NUMBERS.count { it !in amb }
                    } else 10
                }
                if (options.symbols) size += 7 // '!@#$%&*' — 7 chars, ninguno ambiguo
                size.coerceAtLeast(1)
            }

            else -> {
                var size = 0
                if (options.uppercase) size += 26
                if (options.lowercase) size += 26
                if (options.numbers)   size += 10
                if (options.symbols)   size += SYMBOLS.length // exacto: 26

                // Restar ambiguos si está activo
                if (options.noAmbiguous) {
                    val amb = AMBIGUOUS
                    if (options.uppercase) size -= amb.count { it.isUpperCase() }
                    if (options.lowercase) size -= amb.count { it.isLowerCase() }
                    if (options.numbers)   size -= amb.count { it.isDigit() }
                }
                size.coerceAtLeast(1)
            }
        }
    }
}
