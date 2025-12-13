package com.omsharma.crikstats.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun DownloadModuleConfirmationDialog(
    state: MutableState<Boolean>,
    onClickConfirm: () -> Unit
) {

    if (!state.value) return

    AlertDialog(
        icon = {
            Image(
                imageVector = Icons.Default.Warehouse,
                contentDescription = "Google play store",
                modifier = Modifier.height(70.dp),
            )
        },
        title = { Text("Download PLayer Stats Module") },
        text = { Text("Your requested player stats module need to be downloaded from Google Play Store. Download size is about 18 MB.") },
        onDismissRequest = { state.value = false },
        confirmButton = {
            TextButton(
                onClick = {
                    onClickConfirm.invoke()
                    state.value = false
                }
            ) {
                Text("Download")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { state.value = false }
            ) {
                Text("Cancel")
            }
        }
    )

}

var dialogState = mutableStateOf(true)

@Preview(showBackground = true)
@Composable
fun PreviewDownloadModuleConfirmationDialog() {
    DownloadModuleConfirmationDialog(dialogState) {}
}