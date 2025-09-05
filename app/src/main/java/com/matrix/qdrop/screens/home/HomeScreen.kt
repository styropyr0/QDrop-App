package com.matrix.qdrop.screens.home


import HomeViewModelFactory
import QDropBackground
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.matrix.qdrop.Repository
import com.matrix.qdrop.core.Constants
import com.matrix.qdrop.core.QStore
import com.matrix.qdrop.R
import com.matrix.qdrop.composables.BuildCard
import com.matrix.qdrop.core.Utils

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    insets: PaddingValues,
    topInsets: PaddingValues? = null,
    navController: NavHostController? = null
) {
    var orgId by remember { mutableStateOf("") }
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(Repository()))

    Box(
        modifier = Modifier
            .padding(insets)
            .padding(WindowInsets.ime.asPaddingValues())
            .background(Color.Black)
            .fillMaxSize()
    ) {
        QDropBackground()
        Column(modifier = Modifier.fillMaxWidth()) {
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
                    Row {
                        IconButton(
                            onClick = {
                                QStore(navController!!.context).save(Constants.STR_ORG_ID, "")
                                navController.popBackStack()
                                navController.navigate("auth")
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_back),
                                contentDescription = "Log Out",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )

                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                "Builds",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Start,
                                color = Color.White
                            )
                            Text(
                                QStore(context = navController!!.context).get(
                                    Constants.STR_ORG_NAME,
                                    ""
                                ) as String,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Start,
                                color = Color.White
                            )
                        }
                    }

                    Row {
                        viewModel.updateData.collectAsState().value?.latestVersion?.let {
                            val updateAvailability = it > Utils.getCurrentVersionCode()
                            IconButton(
                                onClick = {
                                    if (updateAvailability) {
                                        navController?.navigate("update")
                                    } else
                                        Toast.makeText(
                                            navController?.context,
                                            "You are using the latest version of this app.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                }
                            )
                            {
                                Icon(
                                    painter = painterResource(
                                        id = if (!updateAvailability) R.drawable.ic_no_update
                                        else R.drawable.ic_update_available
                                    ),
                                    contentDescription = "QDrop Updates",
                                    tint = Color.Unspecified,
                                    modifier = Modifier.size(25.dp)
                                )
                            }
                            Spacer(Modifier.width(10.dp))
                        }

                        IconButton(
                            onClick = {
                                if (orgId.isNotEmpty()) {
                                    with(viewModel) {
                                        fetchAppUpdateData()
                                        fetchBuilds(orgId)
                                    }
                                }
                            }
                        )
                        {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_refresh),
                                contentDescription = "Refresh",
                                tint = Color.White,
                                modifier = Modifier.size(25.dp)
                            )

                        }
                    }
                }
            }

            LaunchedEffect(Unit) {
                orgId = QStore(context = navController!!.context)
                    .get(Constants.STR_ORG_ID, "") as String
                if (orgId.isNotEmpty()) {
                    viewModel.fetchAppUpdateData()
                    viewModel.fetchBuilds(orgId)
                }
            }

            val builds by viewModel.builds.collectAsState()
            val isLoading by viewModel.isLoading.collectAsState()

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 3.dp,
                        strokeCap = StrokeCap.Round,
                        modifier = Modifier.size(30.dp)
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = if (builds.isEmpty()) Arrangement.Center else Arrangement.Top
                    ) {
                        if (builds.isEmpty()) {
                            Text(
                                "No builds found.",
                                color = Color.White,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .padding(10.dp)
                                    .fillMaxWidth()
                            )
                        } else {
                            builds.forEachIndexed { index, build ->
                                Spacer(Modifier.height(8.dp))
                                BuildCard(
                                    build = build,
                                )
                                if (index == builds.size - 1)
                                    Spacer(
                                        Modifier.height(
                                            WindowInsets.navigationBars
                                                .asPaddingValues()
                                                .calculateBottomPadding() - 10.dp
                                        )
                                    )
                            }
                        }

                        Spacer(Modifier.height(20.dp))
                    }
                }
            }

        }
    }
}