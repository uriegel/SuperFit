package de.uriegel.superfit.ui.views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Display() {
    val pagerState = rememberPagerState(pageCount = {1})
    var followLocation by remember { mutableStateOf(true) }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
        userScrollEnabled = followLocation
    ){
        when (it) {
            //0 -> { Page1()}
            0 -> { Page2(followLocation) { followLocation = !followLocation }
            }
        }
    }
}

@Composable
fun Page1() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Yellow)
    ) {
        Text(text = "Das ist die erste Seite")
    }
}
@Composable
fun Page2(followLocation: Boolean, toggleSwipe: ()->Unit) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            TrackingMapsView(followLocation)
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .alpha(0f),
                shape = RectangleShape,
                onClick = { toggleSwipe() }) {}
        }
    }
}


@Preview()
@Composable
fun PreviewControls() {
    Display()
}