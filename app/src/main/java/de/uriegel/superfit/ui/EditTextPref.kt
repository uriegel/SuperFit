package de.uriegel.superfit.ui

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.DialogProperties
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.jamal.composeprefs3.ui.LocalPrefsDataStore
import com.jamal.composeprefs3.ui.ifNotNullThen
import com.jamal.composeprefs3.ui.prefs.TextPref
import de.uriegel.superfit.R
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun EditTextPref(key: Preferences.Key<String>, title: String, modifier: Modifier = Modifier, summary: String? = null,
                 dialogTitle: String? = null, dialogMessage: String? = null, defaultValue: String = "",
                 onValueSaved: ((String) -> Unit) = {}, onValueChange: ((String) -> Unit) = {},
                 dialogBackgroundColor: Color = MaterialTheme.colorScheme.background,
                 textColor: Color = MaterialTheme.colorScheme.onBackground, enabled: Boolean = true,
                 numbers: Boolean = false) {

    var showDialog by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val datastore = LocalPrefsDataStore.current
    val prefs by remember { datastore.data }.collectAsState(initial = null)

    //value should only change when save button is clicked
    var value by remember { mutableStateOf(defaultValue) }
    //value of the TextField which changes every time the text is modified
    var textVal by remember { mutableStateOf(value) }

    var dialogSize by remember { mutableStateOf(Size.Zero) }

    // Set value initially if it exists in datastore
    LaunchedEffect(Unit) {
        prefs?.get(key)?.also {
            value = it
        }
    }

    LaunchedEffect(datastore.data) {
        datastore.data.collectLatest { pref ->
            pref[key]?.also {
                value = it
            }
        }
    }

    fun edit() = run {
        scope.launch {
            try {
                datastore.edit { preferences ->
                    preferences[key] = textVal
                }
                onValueSaved(textVal)
            } catch (e: Exception) {
                Log.e(
                    "EditTextPref",
                    "Could not write pref $key to database. ${e.printStackTrace()}"
                )
            }
        }
    }

    TextPref(
        title = title,
        modifier = modifier,
        summary = if (value.length > 0) value else { summary},
        textColor = textColor,
        enabled = enabled,
        darkenOnDisable = true,
        onClick = { if (enabled) showDialog = !showDialog },
    )

    if (showDialog) {
        //reset
        LaunchedEffect(null) {
            textVal = value
        }
        AlertDialog(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .onGloballyPositioned {
                    dialogSize = it.size.toSize()
                },
            onDismissRequest = { showDialog = false },
            title = { DialogHeader(dialogTitle, dialogMessage) },
            text = {
                OutlinedTextField(
                    value = textVal,
                    singleLine = true,
                    keyboardOptions =
                        if (numbers)
                            KeyboardOptions(keyboardType = KeyboardType.Number)
                        else
                            KeyboardOptions.Default,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    onValueChange = {
                        textVal = it
                        onValueChange(it)
                    }
                )
            },
            confirmButton = {

                TextButton(
                    modifier = Modifier.padding(end = 16.dp),
                    onClick = {
                        edit()
                        showDialog = false
                    }
                ) {
                    Text(stringResource(id = R.string.ok), style = MaterialTheme.typography.bodyLarge)
                }
            },
            dismissButton = {
                TextButton(
                    modifier = Modifier.padding(end = 16.dp),
                    onClick = { showDialog = false }
                ) {
                    Text(stringResource(id = R.string.cancel), style = MaterialTheme.typography.bodyLarge)
                }
            },
            properties = DialogProperties(usePlatformDefaultWidth = false),
            containerColor = dialogBackgroundColor,
        )
    }
}

@Composable
fun DialogHeader(dialogTitle: String?, dialogMessage: String?) {

    Column(modifier = Modifier.padding(16.dp)) {
        dialogTitle.ifNotNullThen {
            Text(
                text = dialogTitle!!,
                style = MaterialTheme.typography.titleLarge
            )
        }?.invoke()

        dialogMessage.ifNotNullThen {
            Text(
                text = dialogMessage!!,
                style = MaterialTheme.typography.bodyLarge
            )
        }?.invoke()
    }
}