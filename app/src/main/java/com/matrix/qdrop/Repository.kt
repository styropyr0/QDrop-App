package com.matrix.qdrop

import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.matrix.qdrop.core.AppStates
import com.matrix.qdrop.core.CommonData
import com.matrix.qdrop.models.BuildMeta
import com.matrix.qdrop.models.OrgInfo
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class Repository {
    private val database = Firebase.database

    suspend fun validateOrganization(orgId: String): CommonData {
        return suspendCoroutine { continuation ->
            val ref = database.getReference("organizations/$orgId")
            ref.get().addOnSuccessListener { snapshot ->
                val data = snapshot.getValue(OrgInfo::class.java)
                if (data?.active != null)
                    continuation.resume(CommonData(AppStates.SUCCESS, data))
                else
                    continuation.resume(CommonData(AppStates.FAILED, OrgInfo(active = false)))
            }.addOnFailureListener {
                continuation.resume(CommonData(AppStates.FAILED, null))
            }
        }
    }

    suspend fun fetchBuilds(orgId: String): List<BuildMeta> {
        return suspendCoroutine { continuation ->
            val ref = database.getReference("qa_builds/$orgId")
                .orderByChild("uploadedAt")
                .limitToLast(10)

            ref.get().addOnSuccessListener { snapshot ->
                val builds = mutableListOf<BuildMeta>()
                for (child in snapshot.children) {
                    val build = child.getValue(BuildMeta::class.java)
                    if (build != null) {
                        builds.add(build)
                    }
                }
                continuation.resume(builds.reversed())
            }.addOnFailureListener {
                continuation.resume(emptyList())
            }
        }
    }

}