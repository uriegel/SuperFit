package de.uriegel.superfit.ui.views

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import de.uriegel.superfit.ui.NavRoutes

@Composable
fun MainNavigationBar(navController: NavHostController, modifier: Modifier) {

    val screens = listOf(NavRoutes.MainControls, NavRoutes.TracksList)

    NavigationBar(modifier = modifier) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        screens.forEach {
            NavigationBarItem(
                label = {
                    Text(text = stringResource(it.id))
                },
                icon = {
                    Icon(imageVector = it.icon!!, contentDescription = null)
                },
                selected = currentRoute == it.route,
                onClick = {
                    navController.navigate(it.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}