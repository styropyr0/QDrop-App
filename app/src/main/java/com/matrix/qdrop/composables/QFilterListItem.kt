package com.matrix.qdrop.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.matrix.qdrop.R

@Composable
fun QFilterListItem(
    text: String,
    isSelected: Boolean,
    textColor: Color = Color.White,
    bgColor: Color = Color.Gray.copy(alpha = 0.3f),
    textSize: Int = 15,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(0.dp, 4.dp, 6.dp, 4.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onClick()
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(
                    if (isSelected) Color.Green.copy(alpha = 0.3f) else bgColor,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                painter = if (isSelected) painterResource(R.drawable.ic_no_update)
                else painterResource(R.drawable.ic_add),
                contentDescription = if (isSelected) "Selected" else "Unselected",
                tint = if (isSelected) Color.Green else Color.LightGray,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = text,
                fontSize = textSize.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) Color.Green else textColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}