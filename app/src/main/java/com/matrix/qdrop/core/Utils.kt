package com.matrix.qdrop.core

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.matrix.qdrop.ui.theme.BrightOrange
import com.matrix.qdrop.ui.theme.ElectricPink
import com.matrix.qdrop.ui.theme.MildGreen
import com.matrix.qdrop.ui.theme.SuperGreen
import java.security.MessageDigest
import androidx.core.net.toUri

object Utils {
    fun generateHashedApkFileName(fileName: String, uploadedAt: String?, version: String?): String {
        val input = "$fileName|$uploadedAt|$version"
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(input.toByteArray())
            .joinToString("") { "%02x".format(it) }
            .take(16)

        return "$hash.apk"
    }

    fun resolveColorForLabels(label: String?): Color {
        return if (label?.lowercase()?.startsWith("prod") == true) SuperGreen
        else if (label?.lowercase()?.contains("prod") == true) MildGreen
        else if (label?.lowercase()?.startsWith("beta") == true ||
            label?.lowercase()?.startsWith("stag") == true)
            BrightOrange
        else if (label?.lowercase()?.contains("beta") == true ||
            label?.lowercase()?.contains("stag") == true)
            Color.Gray
        else if (label == "Unspecified") Color.Gray
        else ElectricPink
    }

    fun openUrl(url: String, context: Context) {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri()).apply {
            setPackage("com.android.chrome")
        }

        try {
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            context.startActivity(
                Intent(Intent.ACTION_VIEW, url.toUri())
            )
        }
    }

    @Composable
    fun getCurrentVersionCode() = LocalContext.current.packageManager.getPackageInfo(
        LocalContext.current.packageName,
        0
    ).versionCode
}