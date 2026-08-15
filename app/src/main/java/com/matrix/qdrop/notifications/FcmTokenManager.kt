package com.matrix.qdrop.notifications

import android.content.Context
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.messaging.FirebaseMessaging
import com.matrix.qdrop.core.Constants
import com.matrix.qdrop.core.QStore

object FcmTokenManager {
    private const val TAG = "FcmTokenManager"

    fun registerForOrganization(context: Context, orgId: String? = null) {
        val store = QStore(context)
        val organizationId = orgId?.takeIf { it.isNotBlank() }
            ?: (store.get(Constants.STR_ORG_ID, "") as String)

        if (organizationId.isBlank()) return

        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                if (token.isNullOrBlank()) return@addOnSuccessListener
                saveToken(context, organizationId, token)
            }
            .addOnFailureListener { error ->
                Log.w(TAG, "Failed to fetch FCM token", error)
            }
    }

    fun saveToken(context: Context, organizationId: String, token: String) {
        if (organizationId.isBlank() || token.isBlank()) return

        val store = QStore(context)
        val previousToken = store.get(Constants.STR_FCM_TOKEN, "") as String
        if (previousToken.isNotBlank() && previousToken != token) {
            removeTokenFromFirebase(organizationId, previousToken)
        }

        store.save(Constants.STR_FCM_TOKEN, token)

        val payload = mapOf(
            "token" to token,
            "platform" to "android",
            "updatedAt" to ServerValue.TIMESTAMP
        )

        FirebaseDatabase.getInstance()
            .getReference("fcm_tokens")
            .child(organizationId)
            .child(tokenKey(token))
            .setValue(payload)
            .addOnFailureListener { error ->
                Log.w(TAG, "Failed to save FCM token for org $organizationId", error)
            }
    }

    fun unregister(context: Context) {
        val store = QStore(context)
        val organizationId = store.get(Constants.STR_ORG_ID, "") as String
        val token = store.get(Constants.STR_FCM_TOKEN, "") as String

        if (organizationId.isNotBlank() && token.isNotBlank()) {
            removeTokenFromFirebase(organizationId, token)
        }

        store.remove(Constants.STR_FCM_TOKEN)
    }

    private fun removeTokenFromFirebase(organizationId: String, token: String) {
        FirebaseDatabase.getInstance()
            .getReference("fcm_tokens")
            .child(organizationId)
            .child(tokenKey(token))
            .removeValue()
            .addOnFailureListener { error ->
                Log.w(TAG, "Failed to remove FCM token for org $organizationId", error)
            }
    }

    private fun tokenKey(token: String): String {
        return token.replace(Regex("[.#$\\[\\]/]"), "_")
    }
}
