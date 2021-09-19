package de.uriegel.superfit.ui.adapters

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.uriegel.superfit.R

class SensorDevicesAdapter(private val clickListener: ((track: BluetoothDevice)->Unit))
    : RecyclerView.Adapter<SensorDevicesAdapter.ViewHolder>() {

    class ViewHolder(view: View, val clickListener: ((track: BluetoothDevice)->Unit)) : RecyclerView.ViewHolder(view) {
        var deviceNameView: TextView = view.findViewById(R.id.device_name)
        var device: BluetoothDevice? = null
        var deviceAddressView: TextView = view.findViewById(R.id.device_address)
        init {
            view.setOnClickListener{clickListener(device!!)}
        }
    }

    fun addDevice(device: BluetoothDevice) {
        if (!devices.any { it.address == device.address}) {
            devices.add(device)
            notifyItemInserted(devices.size - 1)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_device, parent, false)
        return ViewHolder(v, clickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.device = devices[position]
        holder.deviceNameView.text = devices[position].name
        holder.deviceAddressView.text = devices[position].address
    }

    override fun getItemCount() = devices.count()

    private val devices: MutableList<BluetoothDevice> = mutableListOf()
}