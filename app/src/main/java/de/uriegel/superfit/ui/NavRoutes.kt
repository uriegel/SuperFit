package de.uriegel.superfit.ui

sealed class NavRoutes(val route: String) {
    object CheckPermission: NavRoutes("checkPermission")
    object Main: NavRoutes("main")
    object ShowSettings: NavRoutes("showSettings")
    object Controls: NavRoutes("controls")
}