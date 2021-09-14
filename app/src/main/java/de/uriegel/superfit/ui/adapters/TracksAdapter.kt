package de.uriegel.superfit.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.uriegel.superfit.R
import de.uriegel.superfit.database.Track
import java.util.*

class TracksAdapter(private val context: Context, private var data: Array<Track>, private val clickListener: ((track: Track)->Unit))
    : RecyclerView.Adapter<TracksAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return data.count()
    }

    @Suppress("DEPRECATION")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.track = data[position]
        val name = data[position].name
        holder.textView.text =
                if (name.isNotEmpty())
                    name
                else {
                    val timeZone = TimeZone.getDefault().rawOffset + TimeZone.getDefault().dstSavings
                    val date = (Date(data[position].time+ data[position].timeOffset - timeZone))
                    val dateFormat = android.text.format.DateFormat.getDateFormat(context)
                    "${dateFormat.format(date)} ${date.hours.toString().padStart(2, '0')}:${date.minutes.toString().padStart(2, '0')}"
                }
        holder.textViewDistance.text = String.format("%.1f km", data[position].distance)
        holder.textViewAverage.text = String.format("%.1f km/h", data[position].averageSpeed)
    }

    fun delete(track: Track) {
        val index = data.indexOf(track)
        data = data.filter { it.trackNr != track.trackNr }.toTypedArray()
        notifyItemRemoved(index)
    }

    class ViewHolder(view: View, clickListener: ((track: Track)->Unit)) : RecyclerView.ViewHolder(view) {
        init {
            view.setOnClickListener {clickListener(track) }
        }

        var track: Track = Track(0, "", 0f, 0, 0f, 0, 0)
        val textView: TextView = view.findViewById(R.id.textView)
        val textViewDistance: TextView = view.findViewById(R.id.textViewDistance)
        val textViewAverage: TextView = view.findViewById(R.id.textViewAverage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.track, parent, false)
        return ViewHolder(v, clickListener)
    }
}