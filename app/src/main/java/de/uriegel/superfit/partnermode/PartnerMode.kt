package de.uriegel.superfit.partnermode

import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import de.uriegel.superfit.requests.PingInput
import de.uriegel.superfit.requests.postPing
import kotlinx.coroutines.launch

object PartnerMode {
    val enabled = mutableStateOf(false)

    fun start() {
        enabled.value = true

//        scope.launch {
//            postPing("https://uriegel.de/tracker/ping", PingInput("Das ist der Text für das PING"))
//                .fold({
//                    Toast.makeText(context, it.output, Toast.LENGTH_LONG).show()
//                }, {
//                    Toast.makeText(context, it.stackTraceToString(), Toast.LENGTH_LONG).show()
//                })
//        }

    }

    fun stop() {
        enabled.value = false
    }

    fun toggle() {
        if (enabled.value)
            stop()
        else
            start()
    }
}