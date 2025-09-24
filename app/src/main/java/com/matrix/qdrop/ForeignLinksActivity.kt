package com.matrix.qdrop

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.matrix.qdrop.screens.build_detailed.BuildDetailedScreen
import com.matrix.qdrop.ui.theme.QDropTheme

class ForeignLinksActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val buildId = intent.getStringExtra("build_id")
        enableEdgeToEdge()
        setContent {
            QDropTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val insets = PaddingValues(
                        horizontal = innerPadding.calculateLeftPadding(
                            LayoutDirection.Ltr
                        ), vertical = 0.dp
                    )
                    Box(modifier = Modifier.padding(insets)) {
                        BuildDetailedScreen(
                            insets = insets,
                            buildId = buildId.toString(),
                            topInsets = PaddingValues(vertical = innerPadding.calculateTopPadding()),
                            context = this@ForeignLinksActivity
                        )
                    }
                }
            }
        }
    }
}