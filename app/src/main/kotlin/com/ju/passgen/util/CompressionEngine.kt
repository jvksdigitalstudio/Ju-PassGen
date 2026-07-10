package com.ju.passgen.util

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry
import org.apache.commons.compress.archivers.sevenz.SevenZMethod
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile
import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream
import org.apache.commons.compress.archivers.zip.Zip64Mode
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream
import org.apache.commons.compress.compressors.gzip.GzipParameters
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.Deflater

/**
 * Motor de compresión profesional — Apache Commons Compress.
 * ZIP Deflate-9 / 7Z LZMA2 / TAR.GZ GZIP-9
 */
object CompressionEngine {

    enum class Format(val extension: String, val label: String, val mimeType: String) {
        ZIP   ("zip",    "ZIP (Deflate 9)",  "application/zip"),
        SEVEN_Z("7z",   "7Z (LZMA2)",        "application/x-7z-compressed"),
        TAR_GZ("tar.gz","TAR.GZ (GZIP 9)",   "application/gzip"),
    }

    data class Result(
        val uri: Uri,
        val fileName: String,
        val format: Format,
        val originalBytes: Int,
        val compressedBytes: Long,
        val ratioPercent: Int,
    )

    fun compress(context: Context, password: String, format: Format): Result {
        val date      = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val baseName  = "JU_Password_$date"
        val content   = buildContent(password)
        val bytes     = content.toByteArray(Charsets.UTF_8)
        val outFile   = File(context.cacheDir, "$baseName.${format.extension}")

        when (format) {
            Format.ZIP     -> compressZip(bytes, outFile, baseName)
            Format.SEVEN_Z -> compressSevenZ(bytes, outFile, baseName)
            Format.TAR_GZ  -> compressTarGz(bytes, outFile, baseName)
        }

        // Mover a Descargas (API 29+) o usar FileProvider
        val finalUri = exportToDownloads(context, outFile, format)

        return Result(
            uri             = finalUri,
            fileName        = "$baseName.${format.extension}",
            format          = format,
            originalBytes   = bytes.size,
            compressedBytes = outFile.length(),
            ratioPercent    = if (bytes.isNotEmpty())
                ((1.0 - outFile.length().toDouble() / bytes.size) * 100)
                    .toInt().coerceIn(0, 100)
            else 0,
        )
    }

    // ── ZIP Deflate nivel 9 ───────────────────────────────────
    private fun compressZip(data: ByteArray, output: File, entryName: String) {
        ZipArchiveOutputStream(FileOutputStream(output)).use { zos ->
            zos.setLevel(Deflater.BEST_COMPRESSION)       // nivel 9
            zos.setMethod(ZipArchiveOutputStream.DEFLATED)
            zos.setUseZip64(Zip64Mode.AsNeeded)
            zos.setEncoding("UTF-8")

            val entry = ZipArchiveEntry("$entryName.txt")
            entry.size = data.size.toLong()
            zos.putArchiveEntry(entry)
            zos.write(data)
            zos.closeArchiveEntry()
        }
    }

    // ── 7Z LZMA2 — mejor ratio existente en Android ──────────
    private fun compressSevenZ(data: ByteArray, output: File, entryName: String) {
        // SevenZOutputFile trabaja con File, no con OutputStream
        // Usamos el archivo de salida directamente
        SevenZOutputFile(output).use { sz ->
            sz.setContentCompression(SevenZMethod.LZMA2)

            // Crear entry manualmente (sin createArchiveEntry que requiere File)
            val entry = SevenZArchiveEntry()
            entry.name          = "$entryName.txt"
            entry.size          = data.size.toLong()
            entry.isDirectory   = false
            sz.putArchiveEntry(entry)
            sz.write(data)
            sz.closeArchiveEntry()
        }
    }

    // ── TAR.GZ GZIP nivel 9 ──────────────────────────────────
    private fun compressTarGz(data: ByteArray, output: File, entryName: String) {
        val params = GzipParameters().apply {
            compressionLevel = Deflater.BEST_COMPRESSION   // nivel 9
        }
        GzipCompressorOutputStream(FileOutputStream(output), params).use { gz ->
            TarArchiveOutputStream(gz).use { tar ->
                tar.setBigNumberMode(TarArchiveOutputStream.BIGNUMBER_POSIX)
                tar.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU)

                val entry = TarArchiveEntry("$entryName.txt")
                entry.size = data.size.toLong()
                tar.putArchiveEntry(entry)
                tar.write(data)
                tar.closeArchiveEntry()
            }
        }
    }

    // ── Exportar a Descargas (MediaStore API 29+) ─────────────
    private fun exportToDownloads(context: Context, file: File, format: Format): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, file.name)
                put(MediaStore.Downloads.MIME_TYPE, format.mimeType)
                put(MediaStore.Downloads.IS_PENDING, 1)
            }
            val uri = context.contentResolver
                .insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)!!
            context.contentResolver.openOutputStream(uri)!!.use { out ->
                file.inputStream().use { it.copyTo(out) }
            }
            values.clear()
            values.put(MediaStore.Downloads.IS_PENDING, 0)
            context.contentResolver.update(uri, values, null, null)
            uri
        } else {
            // Android 8-9 → FileProvider
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file,
            )
        }
    }

    // ── Contenido del archivo ─────────────────────────────────
    private fun buildContent(password: String): String {
        val date = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())
        return buildString {
            appendLine("══════════════════════════════════════")
            appendLine("   JU PASSWORD GENERATOR")
            appendLine("   100% Local · Sin servidores · Seguro")
            appendLine("══════════════════════════════════════")
            appendLine()
            appendLine("CONTRASEÑA:")
            appendLine(password)
            appendLine()
            appendLine("──────────────────────────────────────")
            appendLine("Longitud : ${password.length} caracteres")
            appendLine("Generado : $date")
            appendLine("──────────────────────────────────────")
            appendLine()
            appendLine("⚠  AVISO DE SEGURIDAD")
            appendLine("Esta contraseña fue generada localmente.")
            appendLine("Guárdala en un gestor de contraseñas")
            appendLine("seguro. No la compartas por email ni")
            appendLine("mensajería sin cifrar.")
            appendLine()
            appendLine("JU Password Generator © 2026")
        }
    }
}
