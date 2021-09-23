package de.uriegel.superfit.sensor

import android.content.Context
import android.content.Intent
import de.uriegel.activityextensions.ActivityRequest
import de.uriegel.superfit.ui.SensorDevicesActivity

suspend fun ActivityRequest.scan(context: Context, uuid: String): String? {
    val intent = Intent(context, SensorDevicesActivity::class.java)
    intent.putExtra(SensorDevicesActivity.SERVICE_UUID, uuid)
    val result = this.launch(intent)
    // TODO also return name
    // TODO make Dialog with progress circle and OK Cancel
    return result.data?.getStringExtra(SensorDevicesActivity.RESULT_DEVICE)
}