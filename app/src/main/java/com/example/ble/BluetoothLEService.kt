package com.example.ble

import android.app.Service
import android.bluetooth.*
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.IBinder
import android.util.Log
import kotlinx.android.synthetic.main.list_layout.view.*
import java.lang.reflect.Array.get
import java.util.*


const val ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED"
const val ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED"
const val ACTION_GATT_SERVICES_DISCOVERED =
    "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED"
const val ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE"
const val EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA"





private const val STATE_DISCONNECTED = 0
private const val STATE_CONNECTING = 1
private const val STATE_CONNECTED = 2
const val BLUETOOTH_CHARACTERISTIC_MUSE_DEVICE_INFORMATION_MANUFACTURER_UUID = "00002a29-0000-1000-8000-00805f9b34fb"

 class BluetoothLEservice (private var bluetoothGatt: BluetoothGatt?) : Service(){
     override fun onBind(intent: Intent?): IBinder? {
         TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
     }

     private var connectionState = STATE_DISCONNECTED

     var gattCallback =object : BluetoothGattCallback() {

        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)

            val intentAction:String

            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    intentAction = ACTION_GATT_CONNECTED
                    connectionState = STATE_CONNECTED
                    broadcastUpdate(intentAction)

                    Log.i(TAG, "Connected to GATT server.")
                    Log.i(TAG, "Attempting to start service discovery: " +
                            bluetoothGatt?.discoverServices())
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    intentAction = ACTION_GATT_DISCONNECTED
                    connectionState = STATE_DISCONNECTED
                    Log.i(TAG, "Disconnected from GATT server.")
                    broadcastUpdate(intentAction)
                }
            }
        }

        //New Services
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            when (status) {

                BluetoothGatt.GATT_SUCCESS -> {
                    broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED)
                    var services = gatt.services

                }
                else -> Log.w(TAG, "onServicesDiscovered received: $status")
            }
        }

        //Result
        override fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int
        ) {
            when (status) {
                BluetoothGatt.GATT_SUCCESS -> {
                    broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic)
                }
            }
        }
    }
    private fun broadcastUpdate(action: String) {
        val intent = Intent(action)
        sendBroadcast(intent)

    }



     private fun broadcastUpdate(action: String, characteristic: BluetoothGattCharacteristic) {

        val intent = Intent(action)

        when (characteristic.uuid){

            UUID.fromString(BLUETOOTH_CHARACTERISTIC_MUSE_DEVICE_INFORMATION_MANUFACTURER_UUID)  -> {



            }


        }
    }

     fun findCharacteristic(macAddress: String, characteristicUUID: UUID): BluetoothGattCharacteristic? {
          bluetoothGatt = get(macAddress,1) as BluetoothGatt? ?: return null

         for (service in bluetoothGatt!!.getServices()) {

             val characteristic = service.getCharacteristic(characteristicUUID)
             if (characteristic != null) {
                 return characteristic
             }
         }

         return null
     }


 }