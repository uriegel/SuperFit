package de.uriegel.superfit.partnermode

import kotlinx.serialization.*

@Serializable
data class LoginInput(val androidId: String)
@Serializable
data class LoginOutput(val registered: Boolean)