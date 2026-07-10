package com.ju.passgen.crypto

import java.nio.ByteBuffer
import java.security.SecureRandom

/**
 * Motor de aleatoriedad criptográfica sin sesgo.
 *
 * Porta exactamente el algoritmo JS del proyecto web:
 *   const limit = floor(0x100000000 / max) * max
 *   do { getRandomValues(buf) } while (buf[0] >= limit)
 *   return buf[0] % max
 *
 * Esto elimina el sesgo de módulo (modulo bias) que tienen
 * nextInt(max) y Random.nextInt() normales.
 *
 * SecureRandom en Android usa /dev/urandom — la misma fuente
 * de entropía que usa el navegador para crypto.getRandomValues().
 */
object CryptoRandom {

    private val secureRandom = SecureRandom()

    /**
     * Genera un entero aleatorio en [0, max) sin sesgo de módulo.
     * Equivalente exacto de cryptoRandom(max) del JS.
     */
    fun nextInt(max: Int): Int {
        require(max > 0) { "max debe ser > 0" }
        if (max == 1) return 0

        val maxLong = max.toLong()
        val limit   = (0x100000000L / maxLong) * maxLong
        val buf     = ByteArray(4)

        var value: Long
        do {
            secureRandom.nextBytes(buf)
            // Interpretar los 4 bytes como unsigned int 32
            value = ByteBuffer.wrap(buf).int.toLong() and 0xFFFFFFFFL
        } while (value >= limit)

        return (value % maxLong).toInt()
    }

    /**
     * Genera un índice aleatorio para seleccionar de una lista.
     * Versión genérica: nextIndex(list.size)
     */
    fun nextIndex(size: Int) = nextInt(size)

    /**
     * Selecciona un elemento aleatorio de una CharArray sin sesgo.
     */
    fun pick(chars: CharArray): Char = chars[nextInt(chars.size)]

    /**
     * Selecciona un elemento aleatorio de una String sin sesgo.
     */
    fun pick(str: String): Char = str[nextInt(str.length)]

    /**
     * Selecciona un elemento aleatorio de una List sin sesgo.
     */
    fun <T> pick(list: List<T>): T = list[nextInt(list.size)]

    /**
     * Fisher-Yates shuffle criptográfico in-place.
     * Usa CryptoRandom.nextInt() en cada iteración.
     */
    fun <T> shuffle(array: MutableList<T>): MutableList<T> {
        for (i in array.size - 1 downTo 1) {
            val j = nextInt(i + 1)
            val tmp    = array[i]
            array[i]   = array[j]
            array[j]   = tmp
        }
        return array
    }

    fun shuffle(chars: CharArray): CharArray {
        for (i in chars.size - 1 downTo 1) {
            val j      = nextInt(i + 1)
            val tmp    = chars[i]
            chars[i]   = chars[j]
            chars[j]   = tmp
        }
        return chars
    }
}
