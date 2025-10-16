package com.matrix.qdrop.screens.build_detailed

import HomeViewModelFactory
import QDropBackground
import androidx.activity.ComponentActivity
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
import com.matrix.qdrop.Repository
import com.matrix.qdrop.core.Constants
import com.matrix.qdrop.core.QStore
import com.matrix.qdrop.R
import com.matrix.qdrop.composables.BuildCard
import com.matrix.qdrop.screens.home.HomeViewModel

@Composable
fun BuildDetailedScreen(
    insets: PaddingValues,
    topInsets: PaddingValues? = null,
    buildId: String,
    context: ComponentActivity
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
                                context.finish()
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
                                QStore(context).get(
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
                }
            }

            LaunchedEffect(Unit) {
                orgId = QStore(context).get(Constants.STR_ORG_ID, "") as String
                if (orgId.isNotEmpty()) {
                    viewModel.fetchBuild(orgId, buildId)
                }
            }

            val build by viewModel.foreignBuild.collectAsState()
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
                        verticalArrangement = if (build == null) Arrangement.Center else Arrangement.Top
                    ) {
                        if (build == null) {
                            Icon(
                                painter = painterResource(R.drawable.ic_not_found),
                                contentDescription = "Build Not Found",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(40.dp).align(Alignment.CenterHorizontally)
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(
                                "Build not found!",
                                color = Color.White,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .padding(10.dp)
                                    .fillMaxWidth()
                            )
                        } else {
                            BuildCard(build!!)
                        }

                        Spacer(Modifier.height(20.dp))
                    }
                }
            }

        }
    }
}