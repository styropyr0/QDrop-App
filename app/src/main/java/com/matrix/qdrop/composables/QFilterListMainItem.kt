package com.matrix.qdrop.composables

import androidx.compose.foundation.Indication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun QFilterListMainItem(
    text: String,
    textColor: Color = Color.White,
    bgColor: Color = Color.Gray.copy(alpha = 0.3f),
    textSize: Int = 15,
    onClick: () -> Unit = {}
) {
    Row {
        Spacer(Modifier.width(3.dp))
        Text(
            text = text,
            fontSize = textSize.sp,
            fontWeight = FontWeight.Medium,
            color = textColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    onClick()
                }
                .background(bgColor, shape = RoundedCornerShape(10.dp))
                .border(1.dp, Color.White.copy(alpha = 0.05f), shape = RoundedCornerShape(10.dp))
                .padding(horizontal = 15.dp, vertical = 3.dp)
        )
        Spacer(Modifier.width(3.dp))
    }
}