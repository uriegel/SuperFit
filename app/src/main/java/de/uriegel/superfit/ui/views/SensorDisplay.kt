package de.uriegel.superfit.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import de.uriegel.superfit.R
import de.uriegel.superfit.extensions.displayDuration
import de.uriegel.superfit.extensions.displayFormat

@Composable
fun SensorDisplay(data: SensorData) {
    val scrollState = rememberScrollState()

    ConstraintLayout(
        modifier = Modifier
            .verticalScroll(scrollState)
            .fillMaxWidth()
            .fillMaxHeight()
            .background(color = Color.Black)
    ) {
        val (cadenceTitle, cadence, cadenceUnit,
            velocityTitle, velocity, velocityUnit,
            heartBeatTitle, heartBeat, heartBeatUnit,
            distanceTitle, distance, distanceUnit,
            durationTitle, duration, durationUnit) = createRefs()
        val (averageVelocityTitle, averageVelocity, averageVelocityUnit,
            maxVelocityTitle, maxVelocity, maxVelocityUnit) = createRefs()
        ProvideTextStyle(TextStyle(color = Color.White)) {
            Text(
                modifier = Modifier
                    .constrainAs(cadenceTitle) {
                        start.linkTo(parent.start, margin = 8.dp)
                        top.linkTo(parent.top, margin = 32.dp)
                    },
                text = stringResource(id = R.string.cadence)
            )
            Text(
                modifier = Modifier
                    .constrainAs(cadence) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(cadenceTitle.bottom)
                    },
                fontSize = 128.sp,
                fontWeight = FontWeight.Bold,
                text = data.cadence.displayFormat()
            )
            Text(
                modifier = Modifier
                    .constrainAs(cadenceUnit) {
                        start.linkTo(cadence.end)
                        end.linkTo(parent.end)
                        baseline.linkTo(cadence.baseline)
                    },
                text = stringResource(id = R.string._1_minUnit)
            )
            Text(
                modifier = Modifier
                    .constrainAs(velocityTitle) {
                        top.linkTo(cadenceUnit.bottom, margin = 32.dp)
                        start.linkTo(parent.start, margin = 8.dp)
                    },
                text = stringResource(id = R.string.velocity)
            )
            Text(modifier = Modifier
                .constrainAs(velocity) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(velocityTitle.bottom)
                },
                fontSize = 128.sp,
                fontWeight = FontWeight.Bold,
                text = data.velocity.displayFormat()
            )
            Text(
                modifier = Modifier
                    .constrainAs(velocityUnit) {
                        start.linkTo(velocity.end)
                        end.linkTo(parent.end)
                        baseline.linkTo(velocity.baseline)
                    },
                text = stringResource(id = R.string.velocityUnit)
            )
            Text(
                modifier = Modifier
                    .constrainAs(heartBeatTitle) {
                        top.linkTo(velocityUnit.bottom, margin = 32.dp)
                        start.linkTo(parent.start, margin = 8.dp)
                    },
                text = stringResource(id = R.string.heartBeat)
            )
            Text(
                modifier = Modifier
                    .constrainAs(heartBeat) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(heartBeatTitle.bottom)
                    },
                fontSize = 128.sp,
                fontWeight = FontWeight.Bold,
                text = data.heartBeat.displayFormat()
            )
            Text(
                modifier = Modifier
                    .constrainAs(heartBeatUnit) {
                        start.linkTo(heartBeat.end)
                        end.linkTo(parent.end)
                        baseline.linkTo(heartBeat.baseline)
                    },
                text = stringResource(id = R.string._1_minUnit)
            )
            Text(
                modifier = Modifier
                    .constrainAs(distanceTitle) {
                        top.linkTo(heartBeatUnit.bottom, margin = 32.dp)
                        start.linkTo(parent.start, margin = 8.dp)
                    },
                text = stringResource(id = R.string.distance)
            )
            Text(
                modifier = Modifier
                    .constrainAs(distance) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(distanceTitle.bottom)
                    },
                fontSize = 96.sp,
                fontWeight = FontWeight.Bold,
                text = data.distance.displayFormat()
            )
            Text(
                modifier = Modifier
                    .constrainAs(distanceUnit) {
                        start.linkTo(distance.end)
                        end.linkTo(parent.end)
                        baseline.linkTo(distance.baseline)
                    },
                text = stringResource(id = R.string.km)
            )
            Text(
                modifier = Modifier
                    .constrainAs(durationTitle) {
                        top.linkTo(distanceUnit.bottom, margin = 32.dp)
                        start.linkTo(parent.start, margin = 8.dp)
                    },
                text = stringResource(id = R.string.duration)
            )
            Text(
                modifier = Modifier
                    .constrainAs(duration) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(durationTitle.bottom)
                    },
                fontSize = 96.sp,
                fontWeight = FontWeight.Bold,
                text = data.duration.displayDuration()
            )
            Text(
                modifier = Modifier
                    .constrainAs(durationUnit) {
                        start.linkTo(duration.end)
                        end.linkTo(parent.end)
                        baseline.linkTo(duration.baseline)
                    },
                text = ""
            )
            Text(
                modifier = Modifier
                   .constrainAs(averageVelocityTitle) {
                       top.linkTo(durationUnit.bottom, margin = 32.dp)
                       start.linkTo(parent.start, margin = 8.dp)
                   },
               text = stringResource(id = R.string.averageVelocity)
            )
            Text(
                modifier = Modifier
                    .constrainAs(averageVelocity) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(averageVelocityTitle.bottom)
                    },
                fontSize = 96.sp,
                fontWeight = FontWeight.Bold,
                text = data.averageVelocity.displayFormat()
            )
            Text(
                modifier = Modifier
                    .constrainAs(averageVelocityUnit) {
                        start.linkTo(averageVelocity.end)
                        end.linkTo(parent.end)
                        baseline.linkTo(averageVelocity.baseline)
                    },
                text = stringResource(id = R.string.velocityUnit)
            )
            Text(
                modifier = Modifier
                    .constrainAs(maxVelocityTitle) {
                        top.linkTo(averageVelocityUnit.bottom, margin = 32.dp)
                        start.linkTo(parent.start, margin = 8.dp)
                    },
                text = stringResource(id = R.string.maxVelocity)
            )
            Text(
                modifier = Modifier
                    .constrainAs(maxVelocity) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(maxVelocityTitle.bottom)
                        bottom.linkTo(parent.bottom, margin = 24.dp)
                    },
                fontSize = 96.sp,
                fontWeight = FontWeight.Bold,
                text = data.maxVelocity.displayFormat()
            )
            Text(
                modifier = Modifier
                    .constrainAs(maxVelocityUnit) {
                        start.linkTo(maxVelocity.end)
                        end.linkTo(parent.end)
                        baseline.linkTo(maxVelocity.baseline)
                    },
                text = stringResource(id = R.string.velocityUnit)
            )
        }
    }
}

data class SensorData(
    val cadence: Int?,
    val velocity: Float?,
    val heartBeat: Int?,
    val distance: Float?,
    val duration: Int?,
    val averageVelocity: Float?,
    val maxVelocity: Float?
)

@Preview
@Composable
fun SensorPreview() {
    SensorDisplay(SensorData(87, 25.4556f, 124, 34.7f, 7000,
        23.7f, 47.8f))
}
