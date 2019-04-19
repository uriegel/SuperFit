package eu.selfhost.riegel.superfit.utils

import android.content.Context
import java.io.File

fun Context?.getSdCard() =
    if (this == null)
        ""
    else {
        try {
            this.getExternalFilesDirs(null)
                    .map { getRootOfExternalStorage(it) }
                    .first { !it.contains("emulated") }
        } catch (e: Exception) {
            this.getExternalFilesDirs(null)
                    .map { getRootOfExternalStorage(it) }
                    .first { it.contains("emulated") }
        }
    }

private fun Context.getRootOfExternalStorage(file: File) =
        file.absolutePath.replace("/Android/data/${this.packageName}/files".toRegex(), "")
