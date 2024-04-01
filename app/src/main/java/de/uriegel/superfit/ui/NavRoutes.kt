package de.uriegel.superfit.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.ui.graphics.vector.ImageVector
import de.uriegel.superfit.R

sealed class NavRoutes(val route: String, val id: Int = -1, val icon: ImageVector? = null) {
    object CheckPermission: NavRoutes("checkPermission")
    object CheckBackgroundPermission: NavRoutes("checkBackgroundPermission")
    object Main: NavRoutes("main")
    object MainControls: NavRoutes("mainControls", R.string.display, Icons.Outlined.LocationOn)
    object TracksList: NavRoutes("tracksList", R.string.tracks, Icons.Outlined.List)
    object ShowSettings: NavRoutes("showSettings")
    object Controls: NavRoutes("controls")
    object TrackMapView: NavRoutes("trackMapView")
    object DevicesView: NavRoutes("devicesView")
}