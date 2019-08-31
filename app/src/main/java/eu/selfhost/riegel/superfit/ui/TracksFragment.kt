package eu.selfhost.riegel.superfit.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import eu.selfhost.riegel.superfit.R
import eu.selfhost.riegel.superfit.database.DataBase
import eu.selfhost.riegel.superfit.database.Track
import eu.selfhost.riegel.superfit.ui.adapters.TracksAdapter
import kotlinx.android.synthetic.main.fragment_tracks.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class TracksFragment : Fragment(), CoroutineScope {
    override val coroutineContext = Dispatchers.Main

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tracks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        tracksView.layoutManager = linearLayoutManager
        tracksView.setHasFixedSize(true)
        val itemDecoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        tracksView.addItemDecoration(itemDecoration)

        fun onItemClick(track: Track) {
            launch {
                val intent = Intent(activity, MapActivity::class.java)
                intent.putExtra(TRACK_NR, track.trackNr.toLong())
                val result = (activity as ActivityEx).activityRequest(intent)
                if (result?.resultCode == Activity.RESULT_OK)
                    if (result.data?.getStringExtra(MapActivity.RESULT_TYPE) == MapActivity.RESULT_TYPE_DELETE)
                        (tracksView.adapter as TracksAdapter).delete(track)
            }
        }

        launch {
            val tracks = DataBase.getTracks()
            tracksView.adapter = TracksAdapter(activity!!, tracks, ::onItemClick)
        }
    }

    companion object {
        const val TRACK_NR = "TRACK_NR"
    }
}
