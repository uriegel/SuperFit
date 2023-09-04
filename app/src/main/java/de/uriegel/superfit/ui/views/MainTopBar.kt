package de.uriegel.superfit.ui.views

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import de.uriegel.superfit.R
import de.uriegel.superfit.ui.NavRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavHostController, onNavigationIconClick: () -> Unit) {

    var displayMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text(stringResource(R.string.app_title)) },
        navigationIcon = {
            IconButton(onClick = onNavigationIconClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = null,
                )
            }
        },
        actions = {
            IconButton(onClick = {  displayMenu = !displayMenu }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    stringResource(id = R.string.settings),
                )
            }
            DropdownMenu(
                expanded = displayMenu,
                onDismissRequest = { displayMenu = false }
            ) {
                DropdownMenuItem(
                    onClick = {
                        navController.navigate(NavRoutes.ShowSettings.route)
                        displayMenu = false
                    },
                    text = { Text(text = stringResource(id = R.string.settings)) }
                )
            }
        }
    )
}
