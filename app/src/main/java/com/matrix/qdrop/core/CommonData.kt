package com.matrix.qdrop.core

import androidx.annotation.Keep

data class CommonData(
    val state: AppStates,
    val data: Any?,
)

@Keep
data class UpdateData(
    var latestVersion: Int? = null,
    var updateMessage: String? = null,
    var downloadUrl: String? = null,
    var versionName: String? = null
)