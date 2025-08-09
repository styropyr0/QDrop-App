package com.matrix.qdrop.models

data class BuildMeta(
    val apkUrl: String? = null,
    val changelog: String? = null,
    val fileName: String? = null,
    val label: String? = "Unspecified",
    val fileSize: Long? = null,
    val organizationId: String? = null,
    val uploadedAt: String? = null,
    val version: String? = null
)
