package de.uriegel.superfit.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun Main(navController: NavHostController) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope =  rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,

        drawerContent = {
            ModalDrawerSheet {
                Text("Drawer title", modifier = Modifier.padding(16.dp))
                Divider()
                NavigationDrawerItem(
                    label = { Text(text = "Drawer Item") },
                    selected = false,
                    onClick = { /*TODO*/ }
                )
            }
        }) {
        Scaffold(
            topBar = {
                TopBar(onNavigationIconClick = {
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
                ConstraintLayout(
                    modifier =
                    Modifier
                        .padding(it)
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    val (startBtn, stopBtn) = createRefs()
                    Button(
                        modifier = Modifier.constrainAs(startBtn) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            top.linkTo(parent.top)
                            bottom.linkTo(stopBtn.top)
                            width = Dimension.fillToConstraints
                        },
                        onClick = { navController.navigate(NavRoutes.Controls.route) }) {
                        Text(text = "Start")
                    }
                    Button(
                        modifier = Modifier.constrainAs(stopBtn) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            top.linkTo(startBtn.bottom)
                            bottom.linkTo(parent.bottom)
                            width = Dimension.fillToConstraints
                        },
                        onClick = {  }) {
                        Text(text = "Stop")
                    }
                }
            }
        )
    }
}


@Preview()
@Composable
fun Preview() {
    Main(rememberNavController())
}