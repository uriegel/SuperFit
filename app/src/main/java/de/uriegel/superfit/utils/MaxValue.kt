package de.uriegel.superfit.utils

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class MaxValue<T: Comparable<T>>(private val initialValue: T): ReadWriteProperty<Any?, T> {

    override fun getValue(thisRef: Any?, property: KProperty<*>): T = currentValue

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        currentValue = when {
            value > currentValue -> value
            value == Float.NEGATIVE_INFINITY -> this.initialValue
            else -> currentValue
        }
    }

    private var currentValue: T = initialValue
}