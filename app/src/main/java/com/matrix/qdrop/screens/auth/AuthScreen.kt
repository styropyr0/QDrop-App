package com.matrix.qdrop.screens.auth

import QDropBackground
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.matrix.qdrop.R
import com.matrix.qdrop.Repository
import com.matrix.qdrop.composables.QStyleCard
import com.matrix.qdrop.core.AppStates
import com.matrix.qdrop.core.Constants
import com.matrix.qdrop.core.QStore
import com.matrix.qdrop.ui.theme.NeonPurple

@Composable
fun AuthScreen(
    insets: PaddingValues,
    navController: NavHostController? = null
) {
    var orgId by remember { mutableStateOf("") }
    val viewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(Repository()))

    Box(
        modifier = Modifier
            .padding(insets)
            .padding(WindowInsets.ime.asPaddingValues())
            .background(Color.Black)
            .fillMaxSize()
    ) {
        QDropBackground()

        Column(
            Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                painter = painterResource(id = R.drawable.qdrop_icon),
                contentDescription = "Log Out",
                modifier = Modifier.size(60.dp),
                tint = Color.Unspecified
            )

            Text(
                "QDrop",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = NeonPurple
            )

            Text(
                "Share QA builds with ease",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = Color.LightGray
            )

            Spacer(Modifier.height(0.dp))

            QStyleCard(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(25.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Enter Organization",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(2.dp))

                    Text(
                        "Access your team's builds",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.White.copy(alpha = 0.55f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(20.dp))

                    OutlinedTextField(
                        value = orgId,
                        onValueChange = { orgId = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = Color.White.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(13.dp)
                            ),
                        placeholder = { Text("Organization ID") },
                        singleLine = true,
                        shape = RoundedCornerShape(13.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            cursorColor = Color.White.copy(alpha = 0.3f)
                        )
                    )

                    when (viewModel.orgInfo.value?.active) {
                        true -> {
                            Spacer(Modifier.height(20.dp))
                            QStore(navController!!.context).apply {
                                save(Constants.STR_ORG_ID, orgId)
                                save(
                                    Constants.STR_ORG_NAME,
                                    viewModel.orgInfo.value?.name ?: "Unknown organization"
                                )
                            }
                            navController.popBackStack()
                            navController.navigate("home")
                        }

                        false -> {
                            Spacer(Modifier.height(10.dp))
                            Text(
                                "This organization does not exist",
                                color = Color.Red,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Start,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(20.dp))
                        }

                        null -> Spacer(Modifier.height(20.dp))
                    }

                    Button(
                        onClick = {
                            if (viewModel.state.value != AppStates.SUCCESS && orgId.isNotEmpty())
                                viewModel.checkOrgStatus(orgId)
                        },
                        modifier = Modifier
                            .background(
                                brush = if (viewModel.state.value == AppStates.SUCCESS) Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0x00000000),
                                        Color(0x00000000)
                                    )
                                )
                                else Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(
                                            0xFF1478F9
                                        ), Color(0xFFA649F1)
                                    )
                                ), shape = RoundedCornerShape(12.dp)
                            ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 15.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            when (viewModel.state.value) {
                                AppStates.LOADING ->
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(21.dp),
                                        color = Color.White,
                                        strokeWidth = 3.dp,
                                        strokeCap = StrokeCap.Round
                                    )

                                AppStates.SUCCESS ->
                                    Text(
                                        text = "Success",
                                        color = Color.Green,
                                        fontWeight = FontWeight.Bold
                                    )

                                else -> Text(
                                    text = "Submit",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                }
            }
        }
    }
}
