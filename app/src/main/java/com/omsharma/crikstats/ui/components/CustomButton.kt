package com.omsharma.crikstats.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.omsharma.crikstats.ui.theme.metropolisFamily

@Composable
fun CustomButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
) {

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = text,
            fontFamily = metropolisFamily,
            style = TextStyle(color = Color.White, fontSize = 16.sp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDynamicModuleDownloadButton() {
    CustomButton(
        text = "Player Stats"
    ) {}
}