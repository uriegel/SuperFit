package de.uriegel.superfit.ui.views

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import de.uriegel.superfit.R

@Composable
fun ServiceAlertDialog(onDismiss: () -> Unit, onOk: ()->Unit) {
    AlertDialog(
        onDismissRequest = { },
        confirmButton = {
            TextButton(onClick = {
                onDismiss()
                onOk()
            }) { Text(text = stringResource(id = R.string.ok)) }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(text = stringResource(id = R.string.cancel))
            }
        },
        title = { Text(text = stringResource(id = R.string.alert_title_stop_service)) },
        text = { Text(text = stringResource(id = R.string.alert_stop_service)) }
    )
}

@Preview
@Composable
fun ServiceAlertDialogPreview() {
    ServiceAlertDialog({}, {})
}

