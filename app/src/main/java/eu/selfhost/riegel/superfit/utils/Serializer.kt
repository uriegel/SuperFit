package eu.selfhost.riegel.superfit.utils

import com.google.gson.Gson

fun Any.serialize():String {
    val gson = Gson()
    val json = gson.toJson(this)
    return json.toString()
}