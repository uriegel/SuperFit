package de.uriegel.superfit.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.lifecycle.ViewModelProvider
import de.uriegel.superfit.BR
import de.uriegel.superfit.databinding.ActivityDisplayBinding
import de.uriegel.superfit.model.DisplayModel

class DisplayActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDisplayBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        setContentView(binding.root)
        binding.setVariable(BR.displayModel, viewModel)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private lateinit var binding: ActivityDisplayBinding

    private val viewModel by lazy {
        ViewModelProvider(this).get(DisplayModel::class.java)
    }
}