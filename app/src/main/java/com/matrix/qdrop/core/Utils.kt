package com.matrix.qdrop.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.matrix.qdrop.ui.theme.ElectricPink
import com.matrix.qdrop.ui.theme.NeonPurple
import com.matrix.qdrop.ui.theme.SuperGreen
import java.security.MessageDigest

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
        return if (label?.lowercase()?.contains("prod") == true) SuperGreen
        else if (label?.lowercase()?.contains("beta") == true || label?.lowercase()
                ?.contains("stag") == true
        ) NeonPurple
        else if (label == "Unspecified") Color.Gray
        else ElectricPink
    }

    @Composable
    fun getCurrentVersionCode() = LocalContext.current.packageManager.getPackageInfo(
        LocalContext.current.packageName,
        0
    ).versionCode
}