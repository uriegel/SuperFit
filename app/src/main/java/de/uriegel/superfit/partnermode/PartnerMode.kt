package de.uriegel.superfit.partnermode

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import de.uriegel.superfit.requests.post
import de.uriegel.superfit.ui.MainActivity
import de.uriegel.superfit.ui.MainActivity.Companion.dataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

object PartnerMode {
    val enabled = mutableStateOf(false)
    var name = "anonymous"

    fun start(context: Context) {
        enabled.value = true

        CoroutineScope(Dispatchers.IO).launch {
            context.dataStore.data.first()[MainActivity.prefPartnerModeName]?.let {
                name = it
            }


            post<LoginInput, LoginOutput>("https://uriegel.de/superfit/login", LoginInput(name))
                .fold({
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(context, it.registered.toString(), Toast.LENGTH_LONG).show()
                    }
                }, {
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(context, it.stackTraceToString(), Toast.LENGTH_LONG).show()
                    }
                })
        }
    }

    fun stop() {
        enabled.value = false
    }

    fun toggle(context: Context) {
        if (enabled.value)
            stop()
        else
            start(context)
    }
}