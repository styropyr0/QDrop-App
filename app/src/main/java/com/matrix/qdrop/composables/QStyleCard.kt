package com.matrix.qdrop.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun QStyleCard(
    modifier: Modifier = Modifier,
    color: Color = Color.White.copy(alpha = 0.02f),
    borderRadius: Int = 25,
    content: @Composable () -> Unit
) {
    Surface(
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.06f)),
        color = color,
        modifier = modifier,
        tonalElevation = 5.dp,
        shape = RoundedCornerShape(borderRadius.dp)
    ) {
        content()
    }
}