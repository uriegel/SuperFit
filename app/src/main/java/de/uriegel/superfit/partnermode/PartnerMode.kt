package de.uriegel.superfit.partnermode

import android.content.Context
import android.provider.Settings.Secure
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import de.uriegel.superfit.requests.post
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object PartnerMode {
    val enabled = mutableStateOf(false)

    fun start(context: Context) {
        enabled.value = true

        CoroutineScope(Dispatchers.IO).launch {
            post<LoginInput, LoginOutput>("https://uriegel.de/superfit/login", LoginInput(Secure.getString(context.contentResolver, Secure.ANDROID_ID)))
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