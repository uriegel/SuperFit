package de.uriegel.superfit.ui

import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.Composable

@Composable
fun <I, O> rememberLauncherWithState(
    contract: ActivityResultContract<I, O>): StateLauncher<I, O> {
    val result = StateLauncher<I, O>()

    result.launcher = rememberLauncherForActivityResult(contract, result.onActivityResult)
    return result
}


class StateLauncher<I, O> {

    val onActivityResult = { o: O ->
        onResult!!(o)
    }

    var onResult: ((O)->Unit)? = null

    var launcher: ManagedActivityResultLauncher<I, O>? = null

    fun launch(input: I, onResult: (O)->Unit) {
        this.onResult = onResult
        return launcher!!.launch(input)
    }
}