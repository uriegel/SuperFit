package de.uriegel.superfit.ui.views

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import de.uriegel.superfit.R
import java.math.RoundingMode
import java.text.DecimalFormat

@Composable
fun SensorDisplay(data: SensorData) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        val (cadenceTitle, cadence, cadenceUnit, velocityTitle, velocity, velocityUnit,
            heartBeatTitle, heartBeat, heartBeatUnit) = createRefs()
        Text(modifier = Modifier
                .constrainAs(cadenceTitle) {
                    start.linkTo(parent.start, margin = 8.dp)
                    top.linkTo(parent.top, margin = 8.dp)
                },
            text = stringResource(id = R.string.cadence))
        Text(modifier = Modifier
            .constrainAs(cadence) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(cadenceTitle.bottom)
            },
            fontSize = 128.sp,
            fontWeight = FontWeight.Bold,
            text = data.cadence.toString())
        Text(modifier = Modifier
            .constrainAs(cadenceUnit) {
                start.linkTo(cadence.end)
                end.linkTo(parent.end)
                baseline.linkTo(cadence.baseline)
            },
            text = stringResource(id = R.string._1_minUnit))
        Text(modifier = Modifier
            .constrainAs(velocityTitle) {
                top.linkTo(cadenceUnit.bottom, margin = 32.dp)
                start.linkTo(parent.start, margin = 8.dp)
            },
            text = stringResource(id = R.string.velocity))
        Text(modifier = Modifier
            .constrainAs(velocity) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(velocityTitle.bottom)
            },
            fontSize = 128.sp,
            fontWeight = FontWeight.Bold,
            text = DecimalFormat("#.#")
                .apply { this.roundingMode = RoundingMode.HALF_EVEN }
                .format(data.velocity))
        Text(modifier = Modifier
            .constrainAs(velocityUnit) {
                start.linkTo(velocity.end)
                end.linkTo(parent.end)
                baseline.linkTo(velocity.baseline)
            },
            text = stringResource(id = R.string.velocityUnit))
        Text(modifier = Modifier
            .constrainAs(heartBeatTitle) {
                top.linkTo(velocityUnit.bottom, margin = 32.dp)
                start.linkTo(parent.start, margin = 8.dp)
            },
            text = stringResource(id = R.string.heartBeat))
        Text(modifier = Modifier
            .constrainAs(heartBeat) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(heartBeatTitle.bottom)
            },
            fontSize = 128.sp,
            fontWeight = FontWeight.Bold,
            text = data.heartBeat.toString())
        Text(modifier = Modifier
            .constrainAs(heartBeatUnit) {
                start.linkTo(heartBeat.end)
                end.linkTo(parent.end)
                baseline.linkTo(heartBeat.baseline)
            },
            text = stringResource(id = R.string._1_minUnit))
    }
}

data class SensorData(
    val cadence: Int,
    val velocity: Double,
    val heartBeat: Int
)

@Preview
@Composable
fun SensorPreview() {
    SensorDisplay(SensorData(87, 25.4556, 124))
}
