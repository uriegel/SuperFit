package de.uriegel.superfit.sensor

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import androidx.lifecycle.MutableLiveData
import java.util.UUID

object HeartRateSensor : BluetoothLeSensor() {

    val heartRate: MutableLiveData<Int> = MutableLiveData(-1)

    override fun getUuid() = uuid
    // override fun getLogId() = "HR"
    
    @SuppressLint("MissingPermission")
    override fun discoverService(bluetoothGatt: BluetoothGatt, service: BluetoothGattService) {
        service.characteristics?.find {
                it.uuid == UUID.fromString(characteristics_id)
        }?.let {
            //logInfo(getLogId() + ": characteristics found")
            bluetoothGatt.setCharacteristicNotification(it, true)
            val descriptor = it.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTICS_ID))
            descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            bluetoothGatt.writeDescriptor(descriptor)
        }
    }

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