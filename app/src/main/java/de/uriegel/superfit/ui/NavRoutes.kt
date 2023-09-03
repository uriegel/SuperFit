package de.uriegel.superfit.ui

sealed class NavRoutes(val route: String) {
    object CheckPermission: NavRoutes("checkPermission")
    object CheckBackgroundPermission: NavRoutes("checkBackgroundPermission")
    object Main: NavRoutes("main")
    object ShowSettings: NavRoutes("showSettings")
    object Controls: NavRoutes("controls")
}