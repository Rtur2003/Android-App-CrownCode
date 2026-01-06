package com.crowncode.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.crowncode.presentation.theme.Border
import com.crowncode.presentation.theme.TextMuted

/**
 * A horizontal divider with centered text.
 * Commonly used to separate sections in auth screens.
 *
 * @param text The text to display in the center of the divider
 * @param modifier Optional modifier for the row
 * @param useGradient If true, uses a gradient effect on the divider lines
 */
@Composable
fun DividerWithText(
    text: String,
    modifier: Modifier = Modifier,
    useGradient: Boolean = false
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(
                    brush = if (useGradient) {
                        Brush.horizontalGradient(
                            colors = listOf(Color.Transparent, Border)
                        )
                    } else {
                        SolidColor(Border)
                    }
                )
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(
                    brush = if (useGradient) {
                        Brush.horizontalGradient(
                            colors = listOf(Border, Color.Transparent)
                        )
                    } else {
                        SolidColor(Border)
                    }
                )
        )
    }
}
