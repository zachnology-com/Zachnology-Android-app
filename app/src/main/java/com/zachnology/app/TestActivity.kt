package com.zachnology.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        var list = findViewById<RecyclerView>(R.id.recycleThis)
        val customAdapter = PendingAppointmentAdapter(this, AppointmentManager.pendingAppointments)
        list.adapter = customAdapter
//        list.layoutManager = LinearLayoutManager(this.baseContext);

    }
}