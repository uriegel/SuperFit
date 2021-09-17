package de.uriegel.superfit.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import de.uriegel.superfit.databinding.ActivityMapBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapActivity: AppCompatActivity(), CoroutineScope {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val trackNr = intent.getIntExtra(TracksFragment.TRACK_NR, -1)
        launch {
            val points = viewModel.findTrackPointsAsync(trackNr).await()
        }
    }

    override val coroutineContext = Dispatchers.Main

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMapBinding
}
