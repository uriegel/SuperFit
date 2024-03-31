package de.uriegel.superfit.ui.views

import android.view.Window
import android.view.WindowManager
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.LifecycleOwner
import com.jamal.composeprefs3.ui.LocalPrefsDataStore
import de.uriegel.superfit.ui.MainActivity.Companion.prefBikeSupport

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Display(window: Window?, dataStore: DataStore<Preferences>,
            lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current) {

    val prefs by remember { dataStore.data }.collectAsState(initial = null)
    var showDisplay by remember { mutableStateOf(false) }

    LaunchedEffect(lifecycleOwner) {
        window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        prefs?.get(prefBikeSupport)?.also {
            showDisplay = it
        }
    }

    DisposableEffect(lifecycleOwner) {
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    val pagerState = rememberPagerState(pageCount = {2})
    var followLocation by remember { mutableStateOf(true) }

    if (!showDisplay)
        Page2(followLocation) {
            followLocation = !followLocation
        }
    else
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = followLocation
        ){
            when (it) {
                0 -> { Page1()}
                1 -> { Page2(followLocation) {
                    followLocation = !followLocation
                }
                }
            }
        }
}

@Composable
fun Page1() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        SensorDisplay(data = SensorData(78, 24.888, 132))
    }
}
@Composable
fun Page2(followLocation: Boolean, toggleSwipe: ()->Unit) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            TrackingMapView(followLocation)
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
    Display(null, LocalPrefsDataStore.current)
}