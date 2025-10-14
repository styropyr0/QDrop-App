package com.matrix.qdrop.models

import androidx.annotation.Keep

@Keep
data class OrgInfo(
    val active: Boolean? = null,
    val name: String? = null,
    val created: String? = null,
    val filters: Map<String, String>? = null,
    val tags: Map<String, String>? = null
) {
    fun appsList(): List<String> = filters?.keys?.toList().orEmpty()
    fun tagsList(): List<String> = tags?.values?.toList().orEmpty()
}
