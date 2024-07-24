package de.uriegel.superfit.models

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import de.uriegel.superfit.android.Service
import de.uriegel.superfit.partnermode.PartnerMode

class ServiceModel : ViewModel() {
    val servicePending: MutableState<Boolean> = Service.pending
    val serviceRunning: MutableState<Boolean> = Service.running
    val partnerMode: MutableState<Boolean> = PartnerMode.enabled
}