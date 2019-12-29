package de.uriegel.superfit.utils

import java.util.*
import kotlin.concurrent.timerTask

object StopWatch {

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
    private const val period = 500L
    private lateinit var timer: Timer
}