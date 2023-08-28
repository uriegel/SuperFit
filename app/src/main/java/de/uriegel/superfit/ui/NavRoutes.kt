package de.uriegel.superfit.ui

sealed class NavRoutes(val route: String) {
    object Controls: NavRoutes("controls")
    object Maps: NavRoutes("maps")
    object Dialog: NavRoutes("dialog")
}