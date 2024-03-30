package de.uriegel.superfit.ui.views

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import de.uriegel.superfit.R
import de.uriegel.superfit.extensions.startService
import de.uriegel.superfit.extensions.stopService
import de.uriegel.superfit.models.ServiceModel
import de.uriegel.superfit.ui.NavRoutes

@Composable
fun MainControls(navController: NavHostController, viewModel: ServiceModel = viewModel()) {

    val context = LocalContext.current

    var showDialog by remember { mutableStateOf(false) }
    if (showDialog)
        ResourceAlertDialog(
            R.string.alert_title_stop_service, R.string.alert_stop_service,
            { showDialog = false },
            {
                viewModel.servicePending.value = true
                context.stopService()
            }
        )

    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        val (startBtn, displayBtn, stopBtn) = createRefs()
        Button(
            modifier = Modifier
                .height(100.dp)
                .constrainAs(startBtn) {
                    start.linkTo(parent.start, margin = 30.dp)
                    end.linkTo(parent.end, margin = 30.dp)
                    top.linkTo(parent.top)
                    bottom.linkTo(displayBtn.top)
                    width = Dimension.fillToConstraints
                },
            enabled = !viewModel.servicePending.value && !viewModel.serviceRunning.value,
            onClick = {
                // Permission already granted, update the location
                viewModel.servicePending.value = true
                context.startService()
                navController.navigate(NavRoutes.Controls.route)
            }) {
            Text(text = stringResource(R.string.start))
        }
        Button(
            modifier = Modifier
                .height(100.dp)
                .constrainAs(displayBtn) {
                    start.linkTo(parent.start, margin = 30.dp)
                    end.linkTo(parent.end, margin = 30.dp)
                    top.linkTo(startBtn.bottom)
                    bottom.linkTo(stopBtn.top)
                    width = Dimension.fillToConstraints
                },
            enabled = !viewModel.servicePending.value && viewModel.serviceRunning.value,
            onClick = { navController.navigate(NavRoutes.Controls.route) }) {
            Text(text = stringResource(R.string.display))
        }
        Button(
            modifier = Modifier
                .height(100.dp)
                .constrainAs(stopBtn) {
                    start.linkTo(parent.start, margin = 30.dp)
                    end.linkTo(parent.end, margin = 30.dp)
                    top.linkTo(displayBtn.bottom)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.fillToConstraints
                },
            enabled = !viewModel.servicePending.value && viewModel.serviceRunning.value,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            onClick = { showDialog = true }) {
            Text(text = stringResource(R.string.stop))
        }
    }
}

@Preview
@Composable
fun PreviewMainControls() {
    MainControls(rememberNavController())
}
