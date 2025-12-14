package com.omsharma.crikstats.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.omsharma.crikstats.ui.theme.metropolisFamily

@Composable
fun ConfirmationDialog(
    isVisible: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!isVisible) return

    AlertDialog(
        icon = {
            Icon(
                imageVector = Icons.Default.CloudDownload,
                contentDescription = "Download",
                modifier = Modifier.height(48.dp)
            )
        },
        title = {
            Text(
                text = "Download Feature?",
                fontFamily = metropolisFamily
            )
        },
        text = { Text(
            text = "To use this feature, an additional module must be downloaded.",
            fontFamily = metropolisFamily
        )
               },
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = "Download",
                    fontWeight = FontWeight.Bold,
                    fontFamily = metropolisFamily
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancel",
                    fontFamily = metropolisFamily
                )
            }
        }
    )
}