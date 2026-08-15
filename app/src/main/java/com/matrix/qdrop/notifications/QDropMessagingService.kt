package com.matrix.qdrop.notifications

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.matrix.qdrop.core.Constants
import com.matrix.qdrop.core.QStore

class QDropMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val orgId = QStore(this).get(Constants.STR_ORG_ID, "") as String
        if (orgId.isNotBlank()) {
            FcmTokenManager.saveToken(this, orgId, token)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val data = message.data
        val notification = message.notification
        val title = notification?.title
            ?: when (data["type"]) {
                "build_updated" -> "Build updated"
                else -> "New build available"
            }
        val body = notification?.body
            ?: listOfNotNull(
                data["category"]?.takeIf { it.isNotBlank() },
                data["version"]?.takeIf { it.isNotBlank() },
                data["label"]?.takeIf { it.isNotBlank() }?.let { "($it)" }
            ).joinToString(" ").ifBlank { "Tap to open build" }

        val buildId = BuildNotificationHelper.extractBuildId(data)
        if (buildId.isNullOrBlank()) {
            Log.w(TAG, "Received FCM message without buildId")
        }

        BuildNotificationHelper.showBuildNotification(
            context = this,
            title = title,
            body = body,
            buildId = buildId
        )
    }

    companion object {
        private const val TAG = "QDropMessagingService"
    }
}
