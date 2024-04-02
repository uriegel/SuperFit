package de.uriegel.superfit.extensions

import java.math.RoundingMode
import java.text.DecimalFormat

fun Float?.displayFormat(): String =
    this?.let {
        if (it != Float.NEGATIVE_INFINITY)
            DecimalFormat("#.#")
                .apply { this.roundingMode = RoundingMode.HALF_EVEN }
                .format(it)
        else
            "-"
    } ?: ""

