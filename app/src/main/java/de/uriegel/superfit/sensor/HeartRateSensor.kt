package de.uriegel.superfit.sensor

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import androidx.lifecycle.MutableLiveData

object HeartRateSensor : BluetoothLeSensor() {

    val heartRate: MutableLiveData<Int> = MutableLiveData(-1)

    override fun getUuid() = uuid
    override fun getCharacteristicsId() = characteristics_id
    // override fun getLogId() = "HR"

    override fun onCharacteristicChanged(gatt: BluetoothGatt,
                                         characteristic: BluetoothGattCharacteristic) {
        val format = when (characteristic.properties and 0x01) {
            0x01 -> BluetoothGattCharacteristic.FORMAT_UINT16
            else -> BluetoothGattCharacteristic.FORMAT_UINT8
        }
        heartRate.postValue(characteristic.getIntValue(format, 1))
    }

    private const val uuid = "0000180D-0000-1000-8000-00805f9b34fb"
    private const val characteristics_id = "00002a37-0000-1000-8000-00805f9b34fb"
}