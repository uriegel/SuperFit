package de.uriegel.superfit.ui.views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Preview()
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun Controls() {
    val pagerState = rememberPagerState(pageCount = {3})

    HorizontalPager(state = pagerState,
        Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ){
        Text(text = "Das ist die Seite ${pagerState.currentPage}")
    }
}