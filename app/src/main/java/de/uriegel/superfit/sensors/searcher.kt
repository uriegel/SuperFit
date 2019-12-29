package de.uriegel.superfit.sensors

import android.content.Context
import com.dsi.ant.plugins.antplus.pcc.MultiDeviceSearch
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceType
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult
import java.util.*

object Searcher {
    fun start(context: Context) {
        multiDeviceSearch = MultiDeviceSearch(context, EnumSet.of(DeviceType.HEARTRATE, DeviceType.BIKE_SPDCAD), object : MultiDeviceSearch.SearchCallbacks {
            override fun onSearchStopped(p0: RequestAccessResult?) {}
            override fun onSearchStarted(p0: MultiDeviceSearch.RssiSupport?) {}

            override fun onDeviceFound(device: com.dsi.ant.plugins.antplus.pccbase.MultiDeviceSearch.MultiDeviceSearchResult?) {
                when (device?.antDeviceType) {
                    DeviceType.HEARTRATE -> HeartRate.start(context, device)
                    DeviceType.BIKE_SPDCAD -> Bike.start(context, device)
                    else -> {}
                }
                // Alles wird beended!
                // if (Bike.isStarted && HeartRate.isStarted)
                //     stop()
            }
        })
    }

    fun stop() = multiDeviceSearch.close()

    private lateinit var multiDeviceSearch: MultiDeviceSearch
}