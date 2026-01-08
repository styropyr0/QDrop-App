package com.matrix.qdrop.screens.home


import HomeViewModelFactory
import QDropBackground
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import com.matrix.qdrop.composables.QFilterWidget
import com.matrix.qdrop.core.AppStates
import com.matrix.qdrop.core.Constants.STR_MAIN_FILTER
import com.matrix.qdrop.core.Constants.STR_SUB_FILTER
import com.matrix.qdrop.core.Utils
import com.matrix.qdrop.screens.auth.AuthViewModel
import com.matrix.qdrop.screens.auth.AuthViewModelFactory

@Composable
fun HomeScreen(
    insets: PaddingValues,
    topInsets: PaddingValues? = null,
    navController: NavHostController? = null
) {
    var orgId by remember { mutableStateOf("") }
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(Repository()))
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(Repository()))
    val qStore: QStore by remember { mutableStateOf(QStore(navController!!.context)) }
    var selectedMainOption by remember { mutableStateOf(qStore.get(STR_MAIN_FILTER, "") as String) }
    var selectedSubOptions by remember {
        mutableStateOf(
            (qStore.get(
                STR_SUB_FILTER,
                ""
            ) as String).split(",").filter { it.isNotBlank() }
        )
    }
    var filterToggle by remember { mutableStateOf(selectedMainOption != "None" && selectedMainOption.isNotEmpty()) }
    var filterApplied by remember { mutableStateOf(filterToggle) }
    var requireRefresh by remember { mutableStateOf(true) }

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
                                with(qStore) {
                                    remove(Constants.STR_ORG_ID)
                                    remove(STR_MAIN_FILTER)
                                    remove(STR_SUB_FILTER)
                                }
                                navController?.popBackStack()
                                navController?.navigate("auth")
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
                        }

                        if (authViewModel.state.value == AppStates.SUCCESS)
                            IconButton(
                                onClick = {
                                    filterToggle = !filterToggle
                                }
                            )
                            {
                                Icon(
                                    painter = painterResource(id = if (filterApplied) R.drawable.ic_filter_applied else R.drawable.ic_filter),
                                    contentDescription = "Filter",
                                    tint = Color.Unspecified,
                                    modifier = Modifier.size(25.dp)
                                )

                            }

                        IconButton(
                            onClick = {
                                if (orgId.isNotEmpty()) {
                                    with(viewModel) {
                                        authViewModel.checkOrgStatus(orgId)
                                        fetchAppUpdateData()

                                        if (filterApplied)
                                            viewModel.fetchBuildsByFilter(
                                                orgId,
                                                selectedMainOption,
                                                selectedSubOptions
                                            )
                                        else
                                            viewModel.fetchBuilds(orgId)
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

            AnimatedVisibility(
                visible = filterToggle && authViewModel.state.value == AppStates.SUCCESS,
                enter = expandVertically(animationSpec = tween(300)),
                exit = shrinkVertically(animationSpec = tween(250))
            ) {
                Column {
                    QFilterWidget(
                        mainOptions = listOf("None") + (authViewModel.orgInfo.value?.appsList().orEmpty()),
                        subOptions = authViewModel.orgInfo.value?.tagsList().orEmpty().sorted().reversed(),
                        selectedMain = selectedMainOption.ifEmpty { "None" },
                        selectedOptions = selectedSubOptions.map { it.removePrefix("tttt_") }
                    ) { a, b ->
                        var f = listOf<String>()
                        with(qStore) {
                            val tagsList = authViewModel.orgInfo.value?.tags.orEmpty()
                            val bTags = b.filter {
                                it in tagsList.values && tagsList.filter { it1 ->
                                    it1.value == it && it1.key.startsWith("tttt_")
                                }.isNotEmpty()
                            }

                            f = (b - bTags) + (bTags.map { "tttt_$it" })

                            save(STR_SUB_FILTER, f.joinToString(","))
                            save(STR_MAIN_FILTER, a)
                        }
                        selectedMainOption = a
                        selectedSubOptions = f
                        requireRefresh = true
                        filterApplied = a != "None" && a.isNotEmpty()
                    }

                    Spacer(Modifier.height(10.dp))
                }
            }

            LaunchedEffect(selectedMainOption, selectedSubOptions) {
                orgId = QStore(context = navController!!.context)
                    .get(Constants.STR_ORG_ID, "") as String
                if (orgId.isNotEmpty()) {
                    if (viewModel.updateData.value == null) {
                        authViewModel.checkOrgStatus(orgId)
                        viewModel.fetchAppUpdateData()
                    }
                    if (requireRefresh) {
                        if (filterApplied)
                            viewModel.fetchBuildsByFilter(
                                orgId,
                                selectedMainOption,
                                selectedSubOptions
                            )
                        else
                            viewModel.fetchBuilds(orgId)
                    }
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
                                if (index > 0)
                                    Spacer(Modifier.height(8.dp))
                                BuildCard(build = build)
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