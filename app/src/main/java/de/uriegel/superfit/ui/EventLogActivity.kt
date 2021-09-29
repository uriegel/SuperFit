package de.uriegel.superfit.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import de.uriegel.superfit.R
import de.uriegel.superfit.databinding.ActivityEventLogBinding
import de.uriegel.superfit.model.MainViewModel
import de.uriegel.superfit.room.LogEntry
import de.uriegel.superfit.room.TracksRepository
import de.uriegel.superfit.ui.adapters.LogEntriesAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EventLogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        recyclerSetup{ }
        observerSetup()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.eventlog, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setPositiveButton(R.string.ok) { _, _ ->
                CoroutineScope(Dispatchers.IO).launch {
                    TracksRepository.clearEventlogAsync().await()
                }
                finish()
            }
            setNegativeButton(R.string.cancel) { _, _ -> }
        }
        val dialog = builder
            .setMessage(getString(R.string.alert_clear_eventlog))
            .setTitle(getString(R.string.alert_title_clear_eventlog))
            .create()
        dialog.show()

        return true
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
        viewModel.logEntries.observe(this, { tracks ->
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