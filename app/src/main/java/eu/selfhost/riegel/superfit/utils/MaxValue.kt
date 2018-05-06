package eu.selfhost.riegel.superfit.utils

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class MaxValue<T: Comparable<T>>(initialValue: T): ReadWriteProperty<Any?, T> {

    override fun getValue(thisRef: Any?, property: KProperty<*>): T = currentValue

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        if (value > currentValue)
            currentValue = value
    }

    private var currentValue: T = initialValue
}