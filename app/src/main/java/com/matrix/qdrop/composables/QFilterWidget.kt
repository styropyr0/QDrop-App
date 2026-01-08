package com.matrix.qdrop.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun QFilterWidget(
    mainOptions: List<String>,
    subOptions: List<String>,
    selectedMain: String = "None",
    selectedOptions: List<String> = listOf(),
    onOptionSelected: (String, List<String>) -> Unit
) {
    var selectedMainOption by remember { mutableStateOf(selectedMain) }
    var selectedSubOptions by remember { mutableStateOf(selectedOptions) }

    Column(
        modifier = Modifier
            .padding(PaddingValues(horizontal = 10.dp))
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, Color.White.copy(alpha = 0.05f), shape = RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .padding(PaddingValues(vertical = 8.dp))
    ) {
        Text(
            "Filter",
            color = Color.White,
            modifier = Modifier.padding(horizontal = 15.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 23.sp
        )

        Text(
            "Choose filters from below. You can choose multiple filters.",
            color = Color.LightGray,
            modifier = Modifier.padding(horizontal = 15.dp),
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp
        )

        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .horizontalScroll(rememberScrollState())
        ) {
            for (option in mainOptions) {
                if (option == selectedMainOption)
                    QFilterListMainItem(
                        option,
                        bgColor = Color.Yellow.copy(alpha = 0.3f),
                        textColor = Color.Yellow
                    )
                else
                    QFilterListMainItem(option) {
                        selectedMainOption = option
                        selectedSubOptions = listOf()
                        onOptionSelected(selectedMainOption, selectedSubOptions)
                    }
            }
        }

        if (selectedMainOption == "None")
            Spacer(Modifier.height(10.dp))

        AnimatedVisibility(
            visible = selectedMainOption != "None",
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column {
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .horizontalScroll(rememberScrollState())
                ) {
                    for (option in subOptions) {
                        QFilterListItem(
                            option,
                            isSelected = option in selectedSubOptions
                        ) {
                            selectedSubOptions = if (option in selectedSubOptions)
                                selectedSubOptions - option
                            else
                                selectedSubOptions + option
                            onOptionSelected(selectedMainOption, selectedSubOptions)
                        }
                    }
                }
            }
        }

        if (selectedMainOption == "None") {
            selectedSubOptions = listOf()
            onOptionSelected(selectedMainOption, selectedSubOptions)
        }
    }
}
