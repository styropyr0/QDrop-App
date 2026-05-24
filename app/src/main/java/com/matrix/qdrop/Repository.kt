package com.matrix.qdrop

import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.matrix.qdrop.core.AppStates
import com.matrix.qdrop.core.CommonData
import com.matrix.qdrop.core.UpdateData
import com.matrix.qdrop.models.BuildMeta
import com.matrix.qdrop.models.OrgInfo
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class Repository {
    private val database = Firebase.database

    suspend fun validateOrganization(orgId: String): CommonData {
        return suspendCancellableCoroutine { continuation ->
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
        return suspendCancellableCoroutine { continuation ->
            val ref = database.getReference("qa_builds/$orgId")
                .orderByChild("uploadedAt")
                .limitToLast(50)

            ref.get().addOnSuccessListener { snapshot ->
                val builds = mutableListOf<BuildMeta>()
                for (child in snapshot.children) {
                    val build = child.getValue(BuildMeta::class.java)
                    if (build != null) builds.add(build.apply { this.id = child.key })
                }
                continuation.resume(builds.reversed())
            }.addOnFailureListener {
                continuation.resume(emptyList())
            }
        }
    }

    suspend fun fetchBuildWithId(orgId: String, buildId: String): BuildMeta? {
        return suspendCancellableCoroutine { continuation ->
            val ref = database.getReference("qa_builds/$orgId/$buildId")
            ref.get().addOnSuccessListener { snapshot ->
                val build = snapshot.getValue(BuildMeta::class.java)
                continuation.resume(build.apply { this?.id = buildId })
            }.addOnFailureListener {
                continuation.resume(null)
            }
        }
    }

    suspend fun getAppUpdateData(): UpdateData? {
        return suspendCancellableCoroutine { continuation ->
            val ref = database.getReference("app_update")
            ref.get().addOnSuccessListener { snapshot ->
                val data = snapshot.getValue(UpdateData::class.java)
                if (data != null)
                    continuation.resume(data)
                else
                    continuation.resume(null)
            }.addOnFailureListener {
                continuation.resume(null)
            }
        }
    }

    suspend fun fetchBuildsByCategory(orgId: String, category: String): List<BuildMeta> {
        return suspendCancellableCoroutine { continuation ->
            val ref = database.getReference("qa_builds/$orgId")
                .orderByChild("category")
                .equalTo(category)
                .limitToLast(50)

            ref.get().addOnSuccessListener { snapshot ->
                val builds = mutableListOf<BuildMeta>()
                for (child in snapshot.children) {
                    val build = child.getValue(BuildMeta::class.java)
                    if (build != null) builds.add(build.apply { this.id = child.key })
                }
                continuation.resume(builds.reversed())
            }.addOnFailureListener {
                continuation.resume(emptyList())
            }
        }
    }

}