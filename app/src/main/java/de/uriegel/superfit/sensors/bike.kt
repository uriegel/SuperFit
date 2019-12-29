package de.uriegel.superfit.sensors

import android.content.Context
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeCadencePcc
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeSpeedDistancePcc
import com.dsi.ant.plugins.antplus.pcc.defines.EventFlag
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult
import com.dsi.ant.plugins.antplus.pccbase.MultiDeviceSearch
import com.dsi.ant.plugins.antplus.pccbase.PccReleaseHandle
import de.uriegel.superfit.ui.PreferenceActivity
import de.uriegel.superfit.utils.MaxValue
import de.uriegel.superfit.utils.StopWatch
import org.jetbrains.anko.defaultSharedPreferences
import java.math.BigDecimal
import java.util.*

object Bike {
    val isStarted
        get() = deviceHandle != null

    var speed = 0F
    var maxSpeed by MaxValue(0F)
    var averageSpeed = 0F
    var distance = 0F
    var cadence = 0
    var duration = 0L

    fun start(context: Context, device: MultiDeviceSearch.MultiDeviceSearchResult) {
        deviceHandle = AntPlusBikeSpeedDistancePcc.requestAccess(context, device.antDeviceNumber, 0, true, { bikeController, resultCode, _ ->
            if (resultCode == RequestAccessResult.SUCCESS)
                subScribeToBikeSpeed(context, bikeController)
        }, {
        })

        StopWatch.tick = {
            duration = it
            averageSpeed = (if (it > 0) distance / it else 0f) * 3600f
        }
    }

    fun stop() {
        deviceHandle?.close()
        deviceHandle = null
        cadenceDeviceHandle?.close()
        cadenceDeviceHandle = null

        speed = 0F
        maxSpeed = Float.NEGATIVE_INFINITY
        averageSpeed = 0F
        distance = 0F
        cadence = 0
        duration = 0L
    }

    private fun subScribeToBikeSpeed(context: Context, bikeController: AntPlusBikeSpeedDistancePcc) {
        var speedIsNull = true

        val preferences = context.defaultSharedPreferences
        val value = BigDecimal(preferences.getString(PreferenceActivity.PREF_WHEEL, "2096"))
        val wheelCircumference = value.divide(BigDecimal(1000.0))


        bikeController.subscribeCalculatedSpeedEvent(object : AntPlusBikeSpeedDistancePcc.CalculatedSpeedReceiver(wheelCircumference) {
            override fun onNewCalculatedSpeed(estTimestamp: Long, flags: EnumSet<EventFlag>?, calculatedSpeedInMs: BigDecimal?) {
                if (calculatedSpeedInMs != null) {
                    speed = calculatedSpeedInMs.toFloat() * 3.6f
                    maxSpeed = speed

                    if (speed > 0.0f && speedIsNull) {
                        speedIsNull = false
                        StopWatch.start()
                    } else if (speed == 0.0f && !speedIsNull) {
                        speedIsNull = true
                        StopWatch.pause()
                    }
                }
            }
        })

        val mToKm = BigDecimal(1000)
        bikeController.subscribeCalculatedAccumulatedDistanceEvent(object : AntPlusBikeSpeedDistancePcc.CalculatedAccumulatedDistanceReceiver(wheelCircumference) {
            override fun onNewCalculatedAccumulatedDistance(estTimestamp: Long, flags: EnumSet<EventFlag>?, currentDistance: BigDecimal?) {
                if (currentDistance != null)
                    distance = currentDistance.divide(mToKm, 3, BigDecimal.ROUND_HALF_UP).toFloat()
            }
        })

        if (bikeController.isSpeedAndCadenceCombinedSensor) {
            cadenceDeviceHandle = AntPlusBikeCadencePcc.requestAccess(context, bikeController.antDeviceNumber, 0, true, { bikeCadenceController, resultCode, _ ->
                if (resultCode == RequestAccessResult.SUCCESS)
                    bikeCadenceController.subscribeCalculatedCadenceEvent { _, _, calculatedCadence -> cadence = calculatedCadence.toInt() }
            }, {
            })
        }
    }

    private var deviceHandle: PccReleaseHandle<AntPlusBikeSpeedDistancePcc>? = null
    private var cadenceDeviceHandle: PccReleaseHandle<AntPlusBikeCadencePcc>? = null
}




