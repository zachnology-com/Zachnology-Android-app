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
        val adapter: PendingAppointmentAdapter = PendingAppointmentAdapter(this, AppointmentManager.pendingAppointments)
        list.adapter = adapter

    }
}