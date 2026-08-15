package com.matrix.qdrop.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.matrix.qdrop.ForeignLinksActivity
import com.matrix.qdrop.R
import com.matrix.qdrop.core.Constants
import androidx.core.net.toUri

object BuildNotificationHelper {

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val manager = context.getSystemService(NotificationManager::class.java) ?: return
        val channel = NotificationChannel(
            Constants.FCM_CHANNEL_ID,
            context.getString(R.string.fcm_channel_name),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = context.getString(R.string.fcm_channel_description)
        }
        manager.createNotificationChannel(channel)
    }

    fun showBuildNotification(
        context: Context,
        title: String,
        body: String,
        buildId: String?,
        notificationId: Int = buildId?.hashCode() ?: System.currentTimeMillis().toInt()
    ) {
        ensureChannel(context)

        val contentIntent = Intent(context, ForeignLinksActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(Constants.EXTRA_BUILD_ID, buildId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, Constants.FCM_CHANNEL_ID)
            .setSmallIcon(R.drawable.qdrop_icon_notif)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || checkNotificationPermission(context)) {
            NotificationManagerCompat.from(context).notify(
                notificationId,
                notification
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkNotificationPermission(context: Context) = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.POST_NOTIFICATIONS
    ) == PackageManager.PERMISSION_GRANTED

    fun extractBuildId(data: Map<String, String>, deepLink: String? = data["deepLink"]): String? {
        data["buildId"]?.takeIf { it.isNotBlank() }?.let { return it }

        if (!deepLink.isNullOrBlank()) {
            val uri = deepLink.toUri()
            uri.getQueryParameter("id")?.takeIf { it.isNotBlank() }?.let { return it }
        }

        return null
    }
}
