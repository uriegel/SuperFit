package de.uriegel.superfit.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.uriegel.superfit.R
import de.uriegel.superfit.room.Track
import java.util.*

class TrackListAdapter(private val context: Context, private val clickListener: ((track: Track)->Unit))
    : RecyclerView.Adapter<TrackListAdapter.ViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun setTrackList(tracks: List<Track>) {
        trackList = tracks
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        trackList?.let {
            holder.track = it[position]
            val name = it[position].trackName
            holder.textView.text =
                if (name?.isNotEmpty() == true)
                    name
                else {
                    val timeZone = TimeZone.getDefault().rawOffset + TimeZone.getDefault().dstSavings
                    val date = (Date(it[position].time!! + it[position].timeOffset!! - timeZone))
                    val cal = Calendar.getInstance()
                    cal.time = date
                    val dateFormat = android.text.format.DateFormat.getDateFormat(context)
                    "${dateFormat.format(date)} ${cal.get(Calendar.HOUR_OF_DAY).toString().padStart(2, '0')}:${cal.get(Calendar.MINUTE).toString().padStart(2, '0')}"
                }
            holder.textViewDistance.text = String.format("%.1f km", it[position].distance)
            holder.textViewAverage.text = String.format("%.1f km/h", it[position].averageSpeed)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track, parent, false)
        return ViewHolder(view, clickListener)
    }

    override fun getItemCount(): Int = trackList?.size ?: 0

    class ViewHolder(view: View, clickListener: ((track: Track)->Unit)) : RecyclerView.ViewHolder(view) {
        var track: Track? = null
        val textView: TextView = view.findViewById(R.id.textView)
        val textViewDistance: TextView = view.findViewById(R.id.textViewDistance)
        val textViewAverage: TextView = view.findViewById(R.id.textViewAverage)

        init {
            view.setOnClickListener {clickListener(track!!) }
        }
    }

    private var trackList: List<Track>? = null
}