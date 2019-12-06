package com.example.ble
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView

class ListAdapter(
    context: Context,
    arrayListDetails: ArrayList<deviceClass>
) : BaseAdapter(){

    private val layoutInflater: LayoutInflater
    private val bluetooh_device_array_list:ArrayList<deviceClass>
    private val set = null

    init {
        this.layoutInflater = LayoutInflater.from(context)
        this.bluetooh_device_array_list= arrayListDetails
    }

    override fun getCount(): Int {
        return bluetooh_device_array_list.size
    }

    override fun getItem(position: Int): Any {
        return bluetooh_device_array_list.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val view: View?
        val listRowHolder: ListRowHolder
        if (convertView == null) {
            view = this.layoutInflater.inflate(R.layout.list_layout, parent, false)
            listRowHolder = ListRowHolder(view)
            view.tag = listRowHolder
        } else {
            view = convertView
            listRowHolder = view.tag as ListRowHolder
        }

        listRowHolder.name.text = bluetooh_device_array_list.get(position).device.name
        listRowHolder.mac.text = bluetooh_device_array_list.get(position).device.address
        return view
    }
}

private class ListRowHolder(row: View?) {
    val name: TextView
    val mac: TextView

    public val linearLayout: LinearLayout

    init {
        this.name = row?.findViewById(R.id.name) as TextView
        this.mac = row?.findViewById(R.id.mac) as TextView

        this.linearLayout = row?.findViewById(R.id.linearLayout) as LinearLayout
    }
}