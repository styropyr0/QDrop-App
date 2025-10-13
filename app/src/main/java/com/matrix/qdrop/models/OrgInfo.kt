package com.matrix.qdrop.models

import androidx.annotation.Keep

@Keep
data class OrgInfo(
    val active: Boolean? = null,
    val name: String? = null,
    val created: String? = null,
    val apps: Map<String, String>? = null
) {
    fun appsList(): List<String> = apps?.values?.toList().orEmpty()
}
