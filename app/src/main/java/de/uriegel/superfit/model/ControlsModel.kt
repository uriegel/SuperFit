package de.uriegel.superfit.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.uriegel.superfit.android.Service

class ControlsModel : ViewModel() {
    val serviceRunning: MutableLiveData<Boolean> = Service.running
}