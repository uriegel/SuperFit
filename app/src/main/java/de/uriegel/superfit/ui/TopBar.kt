package de.uriegel.superfit.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import de.uriegel.superfit.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(onNavigationIconClick: () -> Unit) {

    TopAppBar(
        title = { Text(stringResource(R.string.app_title)) },
        navigationIcon = {
            IconButton(onClick = onNavigationIconClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = null,
                )
            }
        }
    )
}
