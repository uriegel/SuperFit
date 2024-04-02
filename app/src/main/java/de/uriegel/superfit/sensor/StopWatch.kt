package de.uriegel.superfit.sensor

import java.util.Timer
import kotlin.concurrent.timerTask

class StopWatch {
    var tick: ((time: Int)->Unit)? = null

    fun start() {
        timer = Timer()
        startTime = System.currentTimeMillis()
        timer.schedule(timerTask {
            val now = System.currentTimeMillis()
            tick?.invoke(((now - startTime + previousTimeSpan) / 1000).toInt())
        }, period, period)
    }

    fun pause()
    {
        val now = System.currentTimeMillis()
        timer.cancel()
        previousTimeSpan += now - startTime
    }

    private var startTime = 0L
    private var previousTimeSpan = 0L
    private val period = 500L
    private lateinit var timer: Timer
}