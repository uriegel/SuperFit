package eu.selfhost.riegel.superfit.sensors

import android.content.Context
import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult
import com.dsi.ant.plugins.antplus.pccbase.MultiDeviceSearch
import com.dsi.ant.plugins.antplus.pccbase.PccReleaseHandle

object HeartRate   {
    var isStarted = false
        get() = deviceHandle != null

    var listener: ((heartRate: Int)->Unit)? = null

    fun start(context: Context, device: MultiDeviceSearch.MultiDeviceSearchResult) {
        deviceHandle = AntPlusHeartRatePcc.requestAccess(context, device.antDeviceNumber, 0, { heartRateController, resultCode, _ ->
            if (resultCode == RequestAccessResult.SUCCESS) {
                var lastHeartRate = 0
                heartRateController.subscribeHeartRateDataEvent({ _ /*estTimeStamp*/, _, computedHeartRate, _, _, _ ->
                    if (lastHeartRate != computedHeartRate) {
                        listener?.invoke(computedHeartRate)
                        lastHeartRate = computedHeartRate
                    }
                })
            }
        }, {})
    }

    fun stop() {
        deviceHandle?.close()
        deviceHandle = null
    }

    private var deviceHandle: PccReleaseHandle<AntPlusHeartRatePcc>? = null
}

