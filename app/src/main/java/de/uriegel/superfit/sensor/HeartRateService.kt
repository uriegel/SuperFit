package de.uriegel.superfit.sensor

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import de.uriegel.superfit.ui.PreferenceFragment
import java.util.*

object HeartRateService : BluetoothLeService() {

    override fun discoverService(bluetoothGatt: BluetoothGatt, service: BluetoothGattService) {
        service.characteristics?.find { characteristic -> characteristic.uuid == UUID.fromString(characteristics_id) }?.let {
            bluetoothGatt.setCharacteristicNotification(it, true)
            val descriptor = it.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTICS_ID))
            descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            bluetoothGatt.writeDescriptor(descriptor)
        }
    }

    override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
        val flag = characteristic.properties
        val format = when (flag and 0x01) {
            0x01 -> BluetoothGattCharacteristic.FORMAT_UINT16
            else -> BluetoothGattCharacteristic.FORMAT_UINT8
        }
        setHeartRate?.invoke(characteristic.getIntValue(format, 1))
    }

    var setHeartRate: ((value: Int)->Unit)? = null

    override fun getTag() = "HR"
    override fun getUuid() = uuid
    override fun getPrefAddress() = PreferenceFragment.PREF_HEARTRATE_SENSOR

    private const val uuid = "0000180D-0000-1000-8000-00805f9b34fb"
    private const val characteristics_id = "00002a37-0000-1000-8000-00805f9b34fb"
}