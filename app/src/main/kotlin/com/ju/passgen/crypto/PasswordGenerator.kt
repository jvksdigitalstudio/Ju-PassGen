package com.ju.passgen.crypto

import com.ju.passgen.ui.components.PasswordOptions

/**
 * Motor de generación de contraseñas premium.
 *
 * Porta los 3 algoritmos exactos del proyecto web:
 *   1. generateRandom()    → pool + required chars + Fisher-Yates shuffle
 *   2. generatePronounce() → patrón consonante/vocal con 15% extra chars
 *   3. generatePassphrase()→ palabras del WORD_LIST + separador + número
 *
 * Toda la aleatoriedad usa CryptoRandom (SecureRandom sin sesgo de módulo).
 */
object PasswordGenerator {

    // ─────────────────────────────────────────────────────────
    // 1. GENERACIÓN ALEATORIA (modo estándar)
    // ─────────────────────────────────────────────────────────
    /**
     * Genera una contraseña aleatoria garantizando al menos 1 carácter
     * de cada categoría activa (igual que el JS: required[]).
     * Luego aplica Fisher-Yates criptográfico.
     */
    fun generateRandom(length: Int, options: PasswordOptions): String {
        val pool = CharsetBuilder.build(options)

        // Caracteres requeridos: al menos uno de cada categoría activa
        val required = mutableListOf<Char>()
        if (options.uppercase && !options.pronounceable && !options.passphrase) {
            val upper = filterAmbiguous(CharsetBuilder.UPPER, options.noAmbiguous)
            if (upper.isNotEmpty()) required.add(CryptoRandom.pick(upper))
        }
        if (options.lowercase && !options.pronounceable && !options.passphrase) {
            val lower = filterAmbiguous(CharsetBuilder.LOWER, options.noAmbiguous)
            if (lower.isNotEmpty()) required.add(CryptoRandom.pick(lower))
        }
        if (options.numbers && !options.pronounceable && !options.passphrase) {
            val nums = filterAmbiguous(CharsetBuilder.NUMBERS, options.noAmbiguous)
            if (nums.isNotEmpty()) required.add(CryptoRandom.pick(nums))
        }
        if (options.symbols && !options.pronounceable && !options.passphrase) {
            val syms = filterAmbiguous(CharsetBuilder.SYMBOLS, options.noAmbiguous)
            if (syms.isNotEmpty()) required.add(CryptoRandom.pick(syms))
        }

        // Rellenar hasta longitud deseada con chars aleatorios del pool
        val remaining = (required.size until length).map { CryptoRandom.pick(pool) }

        // Combinar y aplicar Fisher-Yates criptográfico
        val combined = (required + remaining).toMutableList()
        return CryptoRandom.shuffle(combined).joinToString("")
    }

    // ─────────────────────────────────────────────────────────
    // 2. MODO PRONUNCIABLE
    // ─────────────────────────────────────────────────────────
    /**
     * Genera una contraseña pronunciable alternando consonantes y vocales.
     * 15% de probabilidad de insertar un número o símbolo (después del 1er char).
     * Puerto exacto de generatePronounce(len) del JS.
     */
    fun generatePronounce(length: Int, options: PasswordOptions): String {
        val consonants = CharsetBuilder.consonants(options.noAmbiguous)
        val vowels     = CharsetBuilder.vowels(options.noAmbiguous)
        val nums       = if (options.numbers) filterAmbiguous(CharsetBuilder.NUMBERS, options.noAmbiguous) else ""
        val syms       = if (options.symbols) "!@#\$%&*" else ""
        val extra      = nums + syms

        // Fallback si todos los pools están vacíos
        if (consonants.isEmpty() && vowels.isEmpty() && extra.isEmpty()) {
            return generateRandom(length, options.copy(uppercase = false, symbols = false, pronounceable = false))
        }

        val lettersAvailable = consonants.isNotEmpty() || vowels.isNotEmpty()
        val result = StringBuilder(length)
        var useVowel     = CryptoRandom.nextInt(2) == 0
        var safetyCount  = 0

        var i = 0
        while (i < length) {
            // 15% de probabilidad de insertar extra char (igual que JS)
            if (extra.isNotEmpty() && (!lettersAvailable || (CryptoRandom.nextInt(100) < 15 && i > 0))) {
                result.append(CryptoRandom.pick(extra))
                i++
                continue
            }

            val src = if (useVowel) vowels else consonants
            if (src.isEmpty()) {
                useVowel = !useVowel
                if (++safetyCount > length * 3) break
                continue
            }

            safetyCount = 0
            var c = CryptoRandom.pick(src)

            // 20% de probabilidad de capitalizar si uppercase está activo
            if (options.uppercase && CryptoRandom.nextInt(10) < 2) {
                c = c.uppercaseChar()
            }

            result.append(c)
            useVowel = !useVowel
            i++
        }

        return result.toString()
    }

    // ─────────────────────────────────────────────────────────
    // 3. MODO PASSPHRASE
    // ─────────────────────────────────────────────────────────
    /**
     * Genera una passphrase de [wordCount] palabras.
     * - Separador aleatorio del set [-, _, ., #, @]
     * - 33% de probabilidad de capitalizar cada palabra (si uppercase activo)
     * - Añade número de 3 dígitos al final si numbers activo
     * - Añade separador extra si symbols activo
     * Puerto exacto de generatePassphrase() del JS.
     */
    fun generatePassphrase(wordCount: Int, options: PasswordOptions): String {
        val separator = CryptoRandom.pick(CharsetBuilder.PASSPHRASE_SEPARATORS)
        val words     = mutableListOf<String>()

        repeat(wordCount) {
            var word = WordList.random()
            // 33% probabilidad de capitalizar primera letra
            if (options.uppercase && CryptoRandom.nextInt(3) == 0) {
                word = word[0].uppercaseChar() + word.substring(1)
            }
            words.add(word)
        }

        val phrase = StringBuilder(words.joinToString(separator))

        // Número de 3 dígitos al final (0–999 con padding)
        if (options.numbers) {
            phrase.append(CryptoRandom.nextInt(1000).toString().padStart(3, '0'))
        }

        // Separador extra final si symbols activo
        if (options.symbols) {
            phrase.append(CryptoRandom.pick(CharsetBuilder.PASSPHRASE_SEPARATORS))
        }

        return phrase.toString()
    }

    // ─────────────────────────────────────────────────────────
    // DISPATCH: elige el algoritmo correcto según opciones
    // ─────────────────────────────────────────────────────────
    fun generate(length: Int, wordCount: Int, options: PasswordOptions): String {
        return when {
            options.passphrase    -> generatePassphrase(wordCount, options)
            options.pronounceable -> generatePronounce(length, options)
            else                  -> generateRandom(length, options)
        }
    }

    // ─────────────────────────────────────────────────────────
    // UTILS
    // ─────────────────────────────────────────────────────────
    private fun filterAmbiguous(pool: String, noAmbiguous: Boolean): String {
        if (!noAmbiguous) return pool
        val amb = CharsetBuilder.AMBIGUOUS.toSet()
        return pool.filter { it !in amb }
    }
}
