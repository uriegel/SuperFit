package de.uriegel.superfit.ui

sealed class NavRoutes(val route: String) {
    object Main: NavRoutes("main")
    object ShowSettings: NavRoutes("showsettings")
    object Controls: NavRoutes("controls")
    object Dialog: NavRoutes("dialog")
}