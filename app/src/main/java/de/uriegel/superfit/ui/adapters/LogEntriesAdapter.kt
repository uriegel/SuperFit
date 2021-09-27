package de.uriegel.superfit.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.uriegel.superfit.R
import de.uriegel.superfit.room.LogEntry
import java.util.*

class LogEntriesAdapter(private val context: Context, private val clickListener: ((entry: LogEntry)->Unit))
    : RecyclerView.Adapter<LogEntriesAdapter.ViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun setLogList(entries: Array<LogEntry>) {
        entryList = entries
        notifyDataSetChanged()
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        entryList?.let {
            holder.entry = it[position]
            holder.textView.text = it[position].entry

            val cal = Calendar.getInstance()
            cal.time = Date(it[position].time)
            val dateFormat = android.text.format.DateFormat.getDateFormat(context)
            val timeFormat = android.text.format.DateFormat.getTimeFormat(context)
            holder.timeView.text = "${dateFormat.format(cal.time)} ${timeFormat.format(cal.time)}"
            holder.imageView.setImageResource(when (it[position].type) {
                LogEntry.Type.Error -> R.drawable.error
                LogEntry.Type.Info -> R.drawable.info
                LogEntry.Type.Warning -> R.drawable.warning
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.logentry, parent, false)
        return ViewHolder(view, clickListener)
    }

    override fun getItemCount(): Int = entryList?.size ?: 0

    class ViewHolder(view: View, clickListener: ((entry: LogEntry)->Unit)) : RecyclerView.ViewHolder(view) {
        var entry: LogEntry? = null
        val textView: TextView = view.findViewById(R.id.logentry_text)
        val timeView: TextView = view.findViewById(R.id.time_text)
        val imageView: ImageView = view.findViewById(R.id.logentry_image)

        init {
            view.setOnClickListener {clickListener(entry!!) }
        }
    }

    private var entryList: Array<LogEntry>? = null
}
