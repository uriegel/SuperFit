package de.uriegel.superfit.ui.views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Controls() {
    val pagerState = rememberPagerState(pageCount = {2})

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ){
        when (it) {
            0 -> { Page1()}
            1 -> { Page2()}
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
fun Page2() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Red)
    ) {
        MapsView()
    }
}


@Preview()
@Composable
fun Preview() {
    Controls()
}