package com.ju.passgen.util

import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.PersistableBundle

object SecureClipboard {

    fun copy(context: Context, text: String, label: String = "JU Password") {
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        val clip = ClipData.newPlainText(label, text)

        // Android 13+ → marcar como contenido sensible
        if (Build.VERSION.SDK_INT >= 33) {
            try {
                val extras = PersistableBundle()
                extras.putBoolean(ClipDescription.EXTRA_IS_SENSITIVE, true)
                clip.description.extras = extras
            } catch (_: Exception) { }
        }

        cm.setPrimaryClip(clip)
    }

    fun clear(context: Context) {
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        // clearPrimaryClip() es API 28+
        if (Build.VERSION.SDK_INT >= 28) {
            cm.clearPrimaryClip()
        } else {
            cm.setPrimaryClip(ClipData.newPlainText("", ""))
        }
    }
}
