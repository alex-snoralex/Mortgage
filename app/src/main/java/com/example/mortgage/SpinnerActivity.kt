package com.example.mortgage

import android.R
import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView


class SpinnerActivity : Activity(), AdapterView.OnItemSelectedListener {

//    override fun onCreate(icicle: Bundle?) {
//        super.onCreate(icicle)
//        setContentView(R.layout.MainActivity)
//        selection = findViewById(R.id.selection) as TextView
//        val spin = findViewById(R.id.spinner) as Spinner
//        spin.onItemSelectedListener = this
//        val aa: ArrayAdapter<String> = ArrayAdapter<String>(
//            this,
//            R.layout.simple_spinner_item,
//            items
//        )
//        aa.setDropDownViewResource(
//            R.layout.simple_spinner_dropdown_item
//        )
//        spin.adapter = aa
//    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
        // An item was selected. You can retrieve the selected item using
         parent.getItemAtPosition(pos)
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Another interface callback
    }
}