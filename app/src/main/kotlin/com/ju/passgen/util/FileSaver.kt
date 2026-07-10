package com.ju.passgen.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * Guarda contraseñas como TXT y PDF.
 * PDF usa fuentes del proyecto si están disponibles, con fallback al sistema.
 */
object FileSaver {

    // ── TXT ───────────────────────────────────────────────────
    fun saveTxt(context: Context, password: String): Uri {
        val date     = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "JU_Password_$date.txt"
        val content  = buildTxtContent(password)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, "text/plain")
                put(MediaStore.Downloads.IS_PENDING, 1)
            }
            val uri = context.contentResolver
                .insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)!!
            context.contentResolver.openOutputStream(uri)!!.use {
                it.write(content.toByteArray(Charsets.UTF_8))
            }
            values.clear()
            values.put(MediaStore.Downloads.IS_PENDING, 0)
            context.contentResolver.update(uri, values, null, null)
            uri
        } else {
            val dir  = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(dir, fileName)
            FileOutputStream(file).use { it.write(content.toByteArray(Charsets.UTF_8)) }
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        }
    }

    // ── PDF ───────────────────────────────────────────────────
    fun savePdf(context: Context, password: String): Uri {
        val date     = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "JU_Password_$date.pdf"
        val pdfFile  = File(context.cacheDir, fileName)

        buildPdf(context, password, pdfFile)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                put(MediaStore.Downloads.IS_PENDING, 1)
            }
            val uri = context.contentResolver
                .insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)!!
            context.contentResolver.openOutputStream(uri)!!.use { out ->
                pdfFile.inputStream().use { it.copyTo(out) }
            }
            values.clear()
            values.put(MediaStore.Downloads.IS_PENDING, 0)
            context.contentResolver.update(uri, values, null, null)
            uri
        } else {
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", pdfFile)
        }
    }

    // ── PDF builder ───────────────────────────────────────────
    private fun buildPdf(context: Context, password: String, output: File) {
        // Usar fuentes del sistema como base (Space Mono → MONOSPACE, Orbitron → DEFAULT_BOLD)
        val monoTypeface = Typeface.MONOSPACE
        val boldTypeface = Typeface.DEFAULT_BOLD

        val doc  = PdfDocument()
        val info = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = doc.startPage(info)
        val c: Canvas = page.canvas

        // Fondo
        c.drawColor(Color.parseColor("#080C14"))

        fun paint(
            color: String,
            size: Float,
            tf: Typeface = Typeface.DEFAULT,
            bold: Boolean = false,
        ) = Paint().apply {
            this.color     = Color.parseColor(color)
            textSize       = size
            typeface       = if (bold) Typeface.create(tf, Typeface.BOLD) else tf
            isAntiAlias    = true
        }

        val pAccent  = paint("#00F5D4", 26f, boldTypeface, bold = true)
        val pTitle   = paint("#E8F4F8", 13f, boldTypeface, bold = true)
        val pMuted   = paint("#5A7A90", 11f)
        val pPwd     = paint("#00F5D4", 19f, monoTypeface)
        val pLine    = Paint().apply { color = Color.parseColor("#1E2D45"); strokeWidth = 1f }
        val pBody    = paint("#A0B4C0", 10f)

        var y = 58f
        val L = 40f; val R = 555f

        c.drawText("JU PASSWORD GENERATOR", L, y, pAccent);   y += 18f
        c.drawText("100% Local · Sin servidores · Generado localmente", L, y, pMuted)
        y += 22f; c.drawLine(L, y, R, y, pLine); y += 26f

        c.drawText("CONTRASEÑA GENERADA", L, y, pTitle); y += 22f

        // Contraseña en líneas de 28 chars para que entre bien
        password.chunked(28).forEach { chunk ->
            c.drawText(chunk, L, y, pPwd); y += 26f
        }

        y += 8f; c.drawLine(L, y, R, y, pLine); y += 20f

        val date = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())
        c.drawText("Longitud  :  ${password.length} caracteres", L, y, pMuted); y += 16f
        c.drawText("Generado  :  $date",                         L, y, pMuted); y += 16f

        y += 16f; c.drawLine(L, y, R, y, pLine); y += 22f

        c.drawText("AVISO DE SEGURIDAD", L, y, pTitle); y += 18f
        listOf(
            "Esta contraseña fue generada completamente de forma local.",
            "Ningún dato sale de tu dispositivo.",
            "Guárdala en un gestor de contraseñas seguro (Bitwarden,",
            "1Password, KeePass). No la compartas por canales inseguros.",
        ).forEach { line -> c.drawText(line, L, y, pBody); y += 15f }

        c.drawText("JU Password Generator © 2026", L, 818f, pMuted)

        doc.finishPage(page)
        FileOutputStream(output).use { doc.writeTo(it) }
        doc.close()
    }

    // ── TXT content ───────────────────────────────────────────
    private fun buildTxtContent(password: String): String {
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
            appendLine("Contraseña generada localmente.")
            appendLine("Guárdala en un gestor seguro.")
            appendLine("No la compartas por canales inseguros.")
            appendLine()
            appendLine("JU Password Generator © 2026")
        }
    }
}
