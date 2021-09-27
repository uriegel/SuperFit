package de.uriegel.superfit.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import de.uriegel.superfit.databinding.ActivityEventLogBinding
import de.uriegel.superfit.model.MainViewModel
import de.uriegel.superfit.room.LogEntry
import de.uriegel.superfit.ui.adapters.LogEntriesAdapter

class EventLogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        recyclerSetup{ }
        observerSetup()
    }

    private fun recyclerSetup(onItemClick: (entry: LogEntry)->Unit) {
        adapter = LogEntriesAdapter(this, onItemClick)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.evenlogView.layoutManager = linearLayoutManager
        binding.evenlogView.setHasFixedSize(true)
        val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        binding.evenlogView.addItemDecoration(itemDecoration)
        binding.evenlogView.adapter = adapter
    }

    private fun observerSetup() {
        viewModel.getEventLog()?.observe(this, { tracks ->
            tracks?.let {
                adapter?.setLogList(it)
            }
        })
    }

    private val binding by lazy {
        ActivityEventLogBinding.inflate(layoutInflater)
    }
    private val viewModel: MainViewModel by viewModels()
    private var adapter: LogEntriesAdapter? = null
}