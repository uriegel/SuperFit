package de.uriegel.superfit.ui.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.uriegel.superfit.ui.NavRoutes
import kotlinx.coroutines.launch

@Composable
fun Main(navController: NavHostController) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope =  rememberCoroutineScope()
    val navContentController = rememberNavController()

    ModalNavigationDrawer(
        drawerState = drawerState,

        drawerContent = {
            ModalDrawerSheet {
                Text("Drawer title", modifier = Modifier.padding(16.dp))
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text(text = "Drawer Item") },
                    selected = false,
                    onClick = { /*TODO*/ }
                )
            }
        }) {
        Scaffold(
            topBar = {
                MainTopBar(navController, onNavigationIconClick = {
                    scope.launch {
                        drawerState.apply {
                            if (isClosed)
                                open()
                            else
                                close()
                        }
                    }
                })
            },
            content = {
                Box(
                    modifier = Modifier.padding(it)
                ) {
                    NavigationGraph(
                        navController = navController,
                        navContentController = navContentController)
                }
            },
            bottomBar = {
                MainNavigationBar(
                    navController = navContentController,
                    modifier = Modifier
                )
            }
        )
    }
}

@Composable
fun NavigationGraph(navContentController: NavHostController, navController: NavHostController) {
    NavHost(navContentController, startDestination = NavRoutes.MainControls.route) {
        composable(NavRoutes.MainControls.route) {
            MainControls(navController = navController)
        }
        composable(NavRoutes.TracksList.route) {
            TracksList(navController)
        }
    }
}

@Preview
@Composable
fun Preview() {
    Main(rememberNavController())
}


// TODO Card layout for track list with map svg icon
