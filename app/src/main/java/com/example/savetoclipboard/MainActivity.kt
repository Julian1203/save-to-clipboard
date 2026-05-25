package com.example.architecture.templates

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast

class MainActivity : Activity() {

    private var hasProcessedIntent = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Let the transparent window load without finishing immediately
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        
        // Android 10+ requires the app window to have focus before it 
        // is allowed to write to or read from the system clipboard.
        if (hasFocus && !hasProcessedIntent) {
            hasProcessedIntent = true
            handleIncomingIntent(intent)
            finish() // Safe to close the activity after copying
        }
    }

    private fun handleIncomingIntent(intent: Intent?) {
        if (intent == null) return
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        when (intent.action) {
            Intent.ACTION_VIEW -> {
                val uri: Uri? = intent.data
                if (uri != null) copyUriToClipboard(clipboard, uri)
                else showToast("Nothing to copy")
            }
            Intent.ACTION_SEND -> {
                val uri: Uri? = intent.getParcelableExtra(Intent.EXTRA_STREAM)
                val text: String? = intent.getStringExtra(Intent.EXTRA_TEXT)

                if (uri != null) {
                    copyUriToClipboard(clipboard, uri)
                } else if (!text.isNullOrEmpty()) {
                    copyTextToClipboard(clipboard, text)
                } else {
                    showToast("Nothing to copy")
                }
            }
        }
    }

    private fun copyUriToClipboard(clipboard: ClipboardManager, uri: Uri) {
        try {
            val clip = ClipData.newUri(contentResolver, "Copied File", uri)
            clipboard.setPrimaryClip(clip)
            showToast("File copied to clipboard")
        } catch (e: Exception) {
            showToast("Failed to copy file")
        }
    }

    private fun copyTextToClipboard(clipboard: ClipboardManager, text: String) {
        try {
            val clip = ClipData.newPlainText("Copied Text", text)
            clipboard.setPrimaryClip(clip)
            showToast("Text copied to clipboard")
        } catch (e: Exception) {
            showToast("Failed to copy text")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
