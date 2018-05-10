package eu.selfhost.riegel.superfit.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import kotlin.coroutines.experimental.Continuation
import kotlin.coroutines.experimental.suspendCoroutine

suspend fun Activity.createDocument(name: String): Uri? {
    return suspendCoroutine { continuation ->
        currentContinuation = continuation
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "text/xml"
        intent.putExtra(Intent.EXTRA_TITLE, name)
        startActivityForResult(intent, REQUEST_CODE)
    }
}

fun Activity.onCreateDocument(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
    return when (requestCode) {
        REQUEST_CODE -> {
            when (resultCode) {
                Activity.RESULT_OK -> currentContinuation.resume(data?.data)
                else -> currentContinuation.resume(null)
            }
            return true
        }
        else -> false
    }
}

private lateinit var currentContinuation: Continuation<Uri?>
private const val REQUEST_CODE = 1