package de.uriegel.superfit.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ActivityRequest(activity: ComponentActivity) {

    suspend fun launch(intent: Intent): ActivityResult {
        return suspendCoroutine { continuation ->
            this@ActivityRequest.continuation = continuation
            starter.launch(intent)
        }
    }

    suspend fun checkAndAccessPermissions(permissions: Array<String>): Map<String, Boolean> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return suspendCoroutine { continuation ->
                if (permissions.any { context.checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED }) {
                    permissionsContinuation = continuation
                    requestPermissionStarter.launch(permissions)
                } else
                    continuation.resume(permissions.map { it to true }.toMap())
            }
        } else
            throw Exception("SDK 23 is required")
    }

    private val starter: ActivityResultLauncher<Intent>
    private val requestPermissionStarter: ActivityResultLauncher<Array<String>>
    private val context: Context
    private lateinit var continuation: Continuation<ActivityResult>
    private lateinit var permissionsContinuation: Continuation<Map<String, Boolean>>

    init {
        starter = activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            continuation.resume(it)
        }
        requestPermissionStarter = activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            permissionsContinuation.resume(it)
        }

        context = activity
    }
}