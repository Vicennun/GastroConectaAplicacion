package com.example.gastroconectaaplicacion.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RatingBar(
    currentRating: Int,
    onRatingChanged: (Int) -> Unit,
    canVote: Boolean = true
) {
    Row {
        for (i in 1..5) {
            Icon(
                imageVector = if (i <= currentRating) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = null,
                tint = if (i <= currentRating) Color(0xFFFFC107) else MaterialTheme.colorScheme.onSurfaceVariant, // Color Ambar
                modifier = Modifier
                    .size(32.dp)
                    .padding(2.dp)
                    .clickable(enabled = canVote) { onRatingChanged(i) }
            )
        }
    }
}