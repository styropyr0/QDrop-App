package com.matrix.qdrop.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.matrix.qdrop.ForeignLinksActivity
import com.matrix.qdrop.R
import com.matrix.qdrop.core.Constants

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
            .setSmallIcon(R.drawable.ic_app)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }

    fun extractBuildId(data: Map<String, String>, deepLink: String? = data["deepLink"]): String? {
        data["buildId"]?.takeIf { it.isNotBlank() }?.let { return it }

        if (!deepLink.isNullOrBlank()) {
            val uri = android.net.Uri.parse(deepLink)
            uri.getQueryParameter("id")?.takeIf { it.isNotBlank() }?.let { return it }
        }

        return null
    }
}
