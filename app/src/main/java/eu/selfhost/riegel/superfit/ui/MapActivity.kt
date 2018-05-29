package eu.selfhost.riegel.superfit.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import eu.selfhost.riegel.superfit.R
import eu.selfhost.riegel.superfit.database.DataBase
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async

class MapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val bundle = intent.extras
        val trackNr = bundle.getLong(MainActivity.TRACK_NR)
        async(UI) {
            val trackPoints = DataBase.getTrackPointsAsync(trackNr).await()
            val fragment = supportFragmentManager.findFragmentById(R.id.fragment) as MapFragment
            fragment.loadGpxTrack(trackPoints)
        }
    }
}
