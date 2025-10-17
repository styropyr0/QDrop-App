package com.matrix.qdrop.composables

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.matrix.qdrop.R
import com.matrix.qdrop.core.DownloadStates
import com.matrix.qdrop.core.Utils
import com.matrix.qdrop.models.BuildMeta
import com.matrix.qdrop.ui.theme.BrightYellow
import com.matrix.qdrop.ui.theme.DeepSea
import kotlinx.coroutines.delay
import java.io.File
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun BuildCard(
    build: BuildMeta
) {
    val context = LocalContext.current
    val fileName = Utils.generateHashedApkFileName(
        build.fileName!!,
        build.uploadedAt,
        build.version
    )

    var isEllipsized by remember { mutableStateOf(false) }

    var seeMore by remember { mutableStateOf(false) }

    val downloadPath = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
        fileName
    )

    var downloadStatus by remember {
        mutableStateOf(
            if (downloadPath.exists()) DownloadStates.DOWNLOADED else DownloadStates.DELETED
        )
    }
    var downloadId by remember { mutableLongStateOf(-1L) }
    var progress by remember { mutableIntStateOf(0) }

    fun downloadApk() {
        if (downloadPath.exists()) downloadPath.delete()
        val request = DownloadManager.Request(build.apkUrl!!.toUri()).apply {
            setTitle("Downloading $fileName")
            setDescription("Please wait…")
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            setMimeType("application/vnd.android.package-archive")
        }
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadId = downloadManager.enqueue(request)
        downloadStatus = DownloadStates.DOWNLOADING
    }

    fun installApk() {
        if (!downloadPath.exists()) return
        val apkUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            downloadPath
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    fun deleteApk() {
        if (downloadPath.exists()) {
            downloadPath.delete()
            downloadStatus = DownloadStates.DELETED
        }
    }

    LaunchedEffect(downloadId) {
        if (downloadId == -1L) return@LaunchedEffect
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        while (downloadStatus != DownloadStates.DOWNLOADED) {
            val query = DownloadManager.Query().setFilterById(downloadId)
            val cursor = downloadManager.query(query)
            if (cursor != null && cursor.moveToFirst()) {
                val status =
                    cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                val totalBytes =
                    cursor.getLong(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                val downloadedBytes =
                    cursor.getLong(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))

                if (totalBytes > 0)
                    progress = ((downloadedBytes * 100L) / totalBytes).toInt()

                if (status == DownloadManager.STATUS_SUCCESSFUL && downloadPath.exists())
                    downloadStatus = DownloadStates.DOWNLOADED
                else if (status == DownloadManager.STATUS_FAILED)
                    downloadStatus = DownloadStates.DELETED
            }
            cursor?.close()
            delay(1000)
        }
    }

    QStyleCard(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White.copy(alpha = 0.05f),
        borderRadius = 20
    ) {
        Column(modifier = Modifier.padding(15.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (build.imageUrl.isNullOrEmpty()) {
                    Icon(
                        painter = painterResource(R.drawable.ic_app),
                        contentDescription = "App Icon",
                        tint = Color.White,
                        modifier = Modifier.size(45.dp)
                    )
                } else {
                    QNetworkImage(
                        build.imageUrl!!,
                        modifier = Modifier.size(45.dp)
                    )
                }

                Spacer(Modifier.width(15.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        build.category ?: build.fileName,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )

                    Spacer(Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ) {
                        QLabel(
                            build.label!!,
                            Utils.resolveColorForLabels(build.label),
                        )

                        Spacer(Modifier.width(5.dp))

                        QLabel(
                            build.version!!,
                            DeepSea,
                            iconRes = R.drawable.ic_version
                        )

                        Spacer(Modifier.width(5.dp))

                        QLabel(
                            "${build.fileSize!! / (1024 * 1024)} MB",
                            DeepSea,
                            iconRes = R.drawable.ic_download_size
                        )
                    }
                }
            }

            Spacer(Modifier.height(15.dp))

            if (build.changelog?.trim().isNullOrEmpty() == false) {
                Text("Changelogs:", fontSize = 13.sp, color = Color.Gray)

                Spacer(Modifier.height(2.dp))

                Text(
                    text = build.changelog.toString(),
                    fontSize = 14.sp,
                    color = Color.LightGray,
                    maxLines = if (seeMore) Int.MAX_VALUE else 4,
                    overflow = TextOverflow.Ellipsis,
                    fontStyle = if (build?.changelog?.trim().isNullOrEmpty() == true)
                        FontStyle.Italic else FontStyle.Normal,
                    onTextLayout = { textLayoutResult ->
                        var lineCount = textLayoutResult.lineCount
                        if (textLayoutResult.lineCount > 0)
                            isEllipsized = textLayoutResult.isLineEllipsized(lineCount - 1)
                    }
                )

                if (isEllipsized || seeMore) {
                    Spacer(Modifier.height(5.dp))
                    Text(
                        text = if (isEllipsized) "Expand" else "Collapse",
                        color = Color(0xFF87CEEB),
                        fontSize = 14.sp,
                        modifier = Modifier.clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }) {
                            seeMore = !seeMore
                        }
                    )
                }

                Spacer(Modifier.height(10.dp))
            }

            val uploadDate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    val displayFormatter =
                        DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a", Locale.US)
                            .withZone(ZoneId.systemDefault())
                    ZonedDateTime.parse(build.uploadedAt).format(displayFormatter)
                } catch (_: Exception) {
                    build.uploadedAt
                }
            } else build.uploadedAt

            Text(
                if (build.IsUpdate == true) "Updated by:" else "Uploaded by:",
                fontSize = 13.sp,
                color = Color.Gray
            )

            Spacer(Modifier.height(1.dp))

            Text(
                build.user!!,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color.LightGray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text("$uploadDate", fontSize = 13.sp, color = Color.Gray)

            Spacer(Modifier.height(15.dp))

            if (downloadStatus != DownloadStates.DOWNLOADING)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (downloadStatus != DownloadStates.DOWNLOADED) {
                        Button(
                            onClick = { downloadApk() },
                            shape = RoundedCornerShape(10.dp),
                            colors = if (build.IsUpdate == true) {
                                ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF216EF3),
                                    contentColor = Color.White
                                )
                            } else {
                                ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF216EF3),
                                    contentColor = Color.White
                                )
                            }
                        ) {
                            Text(
                                text = if (build.IsUpdate == true) "Download Update" else "Download"
                            )
                        }
                    } else {
                        progress = 0
                        Button(
                            onClick = { installApk() }, shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1C9800),
                                contentColor = Color.White
                            )
                        ) {
                            Text("Install")
                        }
                        OutlinedButton(
                            onClick = { deleteApk() },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.Red
                            )
                        ) {
                            Text("Delete")
                        }
                    }
                }
            else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Downloading", fontSize = 12.sp)
                    Spacer(Modifier.width(10.dp))
                    LinearProgressIndicator(
                        progress = progress / 100f,
                        modifier = Modifier.fillMaxWidth(),
                        color = BrightYellow,
                        trackColor = Color.Gray.copy(alpha = 0.3f),
                        strokeCap = StrokeCap.Round
                    )
                }
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}
