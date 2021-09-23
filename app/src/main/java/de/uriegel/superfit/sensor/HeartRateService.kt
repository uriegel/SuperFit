package de.uriegel.superfit.sensor

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService

object HeartRateService : BluetoothLeService() {
    override fun discoverService(bluetoothGatt: BluetoothGatt, service: BluetoothGattService) {
        TODO("Not yet implemented")
    }

    override fun onCharacteristicChanged(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic
    ) {
        TODO("Not yet implemented")
    }

    override fun getTag(): String {
        TODO("Not yet implemented")
    }

    override fun getUuid() = uuid

    private const val uuid = "0000180D-0000-1000-8000-00805f9b34fb"
}