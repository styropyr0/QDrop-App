package com.matrix.qdrop

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.matrix.qdrop.core.Constants
import com.matrix.qdrop.core.QStore
import com.matrix.qdrop.core.Router
import com.matrix.qdrop.screens.auth.AuthScreen
import com.matrix.qdrop.screens.home.HomeScreen
import com.matrix.qdrop.screens.update.UpdateScreen
import com.matrix.qdrop.ui.theme.QDropTheme

class MainActivity : ComponentActivity() {
    lateinit var qStore: QStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        qStore = QStore(this)
        setContent {
            QDropTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val insets = PaddingValues(
                        horizontal = innerPadding.calculateLeftPadding(
                            LayoutDirection.Ltr
                        ), vertical = 0.dp
                    )
                    Box(modifier = Modifier.padding(insets)) {
                        if ((qStore.get(Constants.STR_ORG_ID, "") as String).isNotEmpty()) {
                            AppNavHost(
                                navController = navController,
                                insets = insets,
                                PaddingValues(vertical = innerPadding.calculateTopPadding()),
                                startDestination = Router.Home.route
                            )

                            processForeignLink()
                        }
                        else
                            AppNavHost(
                                navController = navController,
                                insets,
                                PaddingValues(vertical = innerPadding.calculateTopPadding())
                            )
                    }
                }
            }
        }
    }

    private fun processForeignLink() {
        intent?.data?.let { uri ->
            when (uri.host) {
                "build" -> {
                    startActivity(Intent(this, ForeignLinksActivity::class.java).apply {
                        putExtra("build_id", uri.getQueryParameter("id"))
                    })
                }

                else -> {}
            }
        }
    }

    @Composable
    fun AppNavHost(
        navController: NavHostController = rememberNavController(),
        insets: PaddingValues,
        topInsets: PaddingValues? = null,
        startDestination: String = Router.Auth.route
    ) {
        NavHost(navController = navController, startDestination = startDestination) {
            composable(Router.Auth.route) {
                AuthScreen(insets = insets, navController = navController)
            }
            composable(Router.Home.route) {
                HomeScreen(navController = navController, insets = insets, topInsets = topInsets)
            }
            composable(Router.Update.route) {
                UpdateScreen(navController = navController, insets = insets, topInsets = topInsets)
            }
        }
    }
}