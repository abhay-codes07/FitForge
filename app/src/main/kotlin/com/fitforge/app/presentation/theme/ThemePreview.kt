package com.fitforge.app.presentation.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(showBackground = true)
@Composable
fun ThemePreview() {
    FitForgeTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            LazyColumn(
                modifier = Modifier.padding(Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                item {
                    Text("FitForge Theme Preview", style = MaterialTheme.typography.headlineMedium)
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                        PreviewSwatch("Primary", Purple600)
                        PreviewSwatch("Mint", Mint500)
                        PreviewSwatch("Amber", Amber500)
                    }
                }
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                        Text("Display", style = MaterialTheme.typography.displaySmall)
                        Text("Headline", style = MaterialTheme.typography.headlineSmall)
                        Text("Body", style = MaterialTheme.typography.bodyLarge)
                        Text("Label", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}

@Composable
private fun PreviewSwatch(name: String, color: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
        Surface(
            modifier = Modifier
                .width(96.dp)
                .height(72.dp),
            shape = RoundedCornerShape(Radius.md),
            color = color,
        ) {}
        Text(text = name, style = MaterialTheme.typography.labelMedium)
    }
}
