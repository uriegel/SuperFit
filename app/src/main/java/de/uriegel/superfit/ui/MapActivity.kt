package de.uriegel.superfit.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import de.uriegel.superfit.databinding.ActivityMapBinding

class MapActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    private lateinit var binding: ActivityMapBinding
}
