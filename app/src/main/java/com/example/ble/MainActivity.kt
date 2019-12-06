package com.example.ble

import android.app.AlertDialog
import android.app.ListActivity
import android.bluetooth.*
import android.bluetooth.le.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build

import android.os.Bundle
import android.os.Handler
import android.os.ParcelUuid
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import android.content.ContentValues.TAG
import android.view.View
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    val REQUEST_ENABLE_BT = 1
    var bluetooth_adapter:BluetoothAdapter? = null
    val BluetoothAdapter.isDisabled: Boolean get() = !isEnabled

    var arrayList: ArrayList<deviceClass> = ArrayList();

    var bluetoothGatt:BluetoothGatt ? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        request_permission()
        check_bluetooth_hardware()
        bluetooth_adapter  = get_bluetooth_adapter()
        check_bluetooth_enabled()


        // Click to Scan The BLE Devices
        scan.setOnClickListener {
            DeviceScanActivity(bluetooth_adapter, Handler()).scanLeDevice(true)

        }

        }




    fun request_permission()
    {
    val permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION)
    ActivityCompat.requestPermissions(this, permissions,0)
    }
    fun check_bluetooth_hardware() {
        fun PackageManager.missingSystemFeature(name: String): Boolean = !hasSystemFeature(name)
        packageManager.takeIf { it.missingSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE) }
            ?.also {
                Toast.makeText(this, "Bluetooth Not Supported", Toast.LENGTH_SHORT).show()

            }
    }
    fun get_bluetooth_adapter(): BluetoothAdapter?
    {
        val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
            val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            bluetoothManager.adapter
        }
        return bluetoothAdapter
    }
    fun check_bluetooth_enabled()
    {
        bluetooth_adapter?.takeIf { it.isDisabled }?.apply {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }
    }
    inner class DeviceScanActivity(var bluetoothAdapter: BluetoothAdapter?, private val handler: Handler) :
        ListActivity()
    {
        val BLUETOOTH_SERVICE_MUSE_DEVICE_INFORMATION_UUID = "0000180a-0000-1000-8000-00805f9b34fb"
        private val SCAN_PERIOD: Long = 10000
        var  obj = bluetoothAdapter?.bluetoothLeScanner


       fun scanLeDevice(enable: Boolean) {
            when (enable) {
                true -> {
                    // Stops scanning after a pre-defined scan period.
                    handler.postDelayed(
                        {
                            obj?.stopScan(mLeScanCallback)
                }, SCAN_PERIOD)



                    val uuid = ParcelUuid(UUID.fromString(BLUETOOTH_SERVICE_MUSE_DEVICE_INFORMATION_UUID))

                    // Adding Filters
                    val filters = ArrayList<ScanFilter>()
                    filters.add(ScanFilter.Builder().setServiceUuid(uuid).build())


                    // Scanning settings
                    val settings = ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                        // Refresh the devices list every second
                        .setReportDelay(0)
                        .build()

                    // Check for the Version
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        obj?.startScan( filters, settings, mLeScanCallback)
                    } else {
                        bluetoothAdapter?.startLeScan(leScanCallback)
                    }

                }

                else -> {
                    obj?.stopScan(mLeScanCallback)
                }
            }
        }
    }
    private val leScanCallback = BluetoothAdapter.LeScanCallback { device, rssi, scanRecord ->
           runOnUiThread {
               Log.d("device",device.address)
           }
       }

    private val mLeScanCallback = object : ScanCallback()
    {

        override fun onScanResult(callbackType: Int, result: ScanResult)
        {
            super.onScanResult(callbackType, result)

            result.run {
                    // Save into Bluetooth Device Data Class

                arrayList.add(deviceClass(device))
                    val adapter=ListAdapter(applicationContext,arrayList)
                    listView.adapter = adapter

                listView.setOnItemClickListener { parent, view, position, id ->
                    val device =  parent.getItemAtPosition(position) as deviceClass// The item that was clicked
                    connect_device(device) 
                    //bluetoothGatt = device.device.connectGatt(applicationContext,false,BluetoothLEservice(bluetoothGatt).gattCallback)



                    }

                }
            Log.d("name",result.device.name)
            Log.d("device",result.device.address)
            }




        }

    private fun connect_device(device: deviceClass) {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Connect to Device")

         builder.setPositiveButton(android.R.string.yes){ dialog, which ->
            Toast.makeText(applicationContext,android.R.string.yes, Toast.LENGTH_SHORT).show()
             bluetoothGatt = device.device.connectGatt(applicationContext,false,BluetoothLEservice(bluetoothGatt).gattCallback)
        }


        builder.setNegativeButton(android.R.string.no){dialog, which ->
            Toast.makeText(applicationContext,android.R.string.no, Toast.LENGTH_SHORT).show()
        }

        builder.show()



    }

}













