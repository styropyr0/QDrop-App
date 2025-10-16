package com.matrix.qdrop.screens.update

import HomeViewModelFactory
import QDropBackground
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.os.Environment
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.matrix.qdrop.R
import com.matrix.qdrop.Repository
import com.matrix.qdrop.core.DownloadStates
import com.matrix.qdrop.core.Utils.getCurrentVersionCode
import com.matrix.qdrop.screens.home.HomeViewModel
import com.matrix.qdrop.ui.theme.BrightYellow
import com.matrix.qdrop.ui.theme.VibrantBlue
import kotlinx.coroutines.delay
import java.io.File

@Composable
fun UpdateScreen(
    insets: PaddingValues,
    topInsets: PaddingValues? = null,
    navController: NavHostController? = null
) {
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(Repository()))

    val context = LocalContext.current
    val fileName = "qdrop_osrel_android_${getCurrentVersionCode() + 1}.apk"

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
        val request =
            DownloadManager.Request(viewModel.updateData.value?.downloadUrl?.toUri()).apply {
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


    Box(
        modifier = Modifier
            .padding(insets)
            .padding(WindowInsets.ime.asPaddingValues())
            .background(Color.Black)
            .fillMaxSize()
    ) {
        QDropBackground()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 70.dp)
        ) {
            Box(
                modifier = Modifier
                    .alpha(1f)
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .padding(
                        PaddingValues(
                            top = (topInsets?.calculateTopPadding() ?: 0.dp) + 5.dp,
                            bottom = 10.dp,
                            start = 10.dp,
                            end = 10.dp
                        )
                    )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                navController?.popBackStack()
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_back),
                                contentDescription = "Go back",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )

                        }
                        Spacer(Modifier.width(10.dp))
                        Text(
                            "QDrop Updates",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Start,
                            color = Color.White
                        )
                    }
                }
            }

            LaunchedEffect(Unit) {
                viewModel.fetchAppUpdateData(false)
            }

            val isLoading by viewModel.isLoading.collectAsState()
            val updateData by viewModel.updateData.collectAsState()

            if (isLoading)
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 3.dp,
                        strokeCap = StrokeCap.Round,
                        modifier = Modifier
                            .size(30.dp)
                            .align(Alignment.Center),
                    )
                }
            else
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 25.dp)
                ) {

                    Box(
                        modifier = Modifier
                            .background(
                                VibrantBlue,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                            Text(
                                text = "Version: ${updateData?.versionName ?: ""}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                    }

                    Text(
                        if (downloadStatus == DownloadStates.DOWNLOADING) "Downloading..." else "A new version of QDrop is available",
                        color = Color.White,
                        fontSize = 26.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                    )

                    Spacer(Modifier.height(10.dp))

                    if (!updateData?.updateMessage.isNullOrEmpty())
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "What's new:",
                            fontSize = 16.sp,
                            color = Color.White,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                        )

                    Spacer(Modifier.height(10.dp))

                    if (!updateData?.updateMessage.isNullOrEmpty())
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = updateData?.updateMessage!!.replace("\\n", "\n"),
                            fontSize = 14.sp,
                            color = Color.LightGray,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                        )

                    Spacer(Modifier.height(20.dp))

                    if (downloadStatus == DownloadStates.DOWNLOADING) {
                        LinearProgressIndicator(
                            progress = progress / 100f,
                            modifier = Modifier.fillMaxWidth(),
                            color = BrightYellow,
                            trackColor = Color.Gray.copy(alpha = 0.3f),
                            strokeCap = StrokeCap.Round
                        )
                        Spacer(Modifier.height(20.dp))
                    }

                    if (downloadStatus == DownloadStates.DELETED)
                        Button(
                            onClick = {
                                downloadApk()
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF216EF3),
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = "Download update"
                            )
                        }
                    else if (downloadStatus == DownloadStates.DOWNLOADED)
                        Button(
                            onClick = {
                                installApk()
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF216EF3),
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = "Install update"
                            )
                        }
                }
        }
    }
}