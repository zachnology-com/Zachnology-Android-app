package com.zachnology.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        var list = findViewById<ListView>(R.id.thisList)
        var myArray = arrayOf("hey", "what")
        val adapter: ArrayAdapter<*> = ArrayAdapter<String>(this, R.layout.list_item, myArray)
        list.adapter = adapter

    }
}