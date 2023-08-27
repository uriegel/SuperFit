package de.uriegel.superfit.ui

sealed class NavRoutes(val route: String) {
    object Main: NavRoutes("main")
    object Maps: NavRoutes("maps")
    object Dialog: NavRoutes("dialog")
}