package com.zachnology.app

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText


class NewAppointment : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_appointment)

        val categoryInput = findViewById<AutoCompleteTextView>(R.id.category_input)
        val descriptionInput = findViewById<TextInputEditText>(R.id.description_input)
        val contactMethodInput = findViewById<TextInputEditText>(R.id.contact_method_input)
        val submitButton = findViewById<Button>(R.id.request_button)
        val items = listOf(
            "Home entertainment systems",
            "Personal computers",
            "Printers",
            "Smart home devices",
            "Smartphones",
            "Vehicle Bluetooth systems",
            "Wireless networking",
            "Other"
        )
        val adapter = ArrayAdapter(this, R.layout.list_item, items)
        (categoryInput)?.setAdapter(adapter)

        submitButton.setOnClickListener() {
            var submittingDialog = MaterialAlertDialogBuilder(
                this,
                R.style.Base_Theme_Zachnology_AlertDialog
            )
                .setTitle("Submitting")
                .setMessage("Your appointment request is being submitted. Please wait...")
                .setIcon(R.drawable.baseline_cloud_upload_24)
                .setCancelable(false)
                .show()
            AppointmentManager.requestAppointment(
                this,
                categoryInput.text.toString(),
                descriptionInput.text.toString(), contactMethodInput.text.toString(),
                { response ->
                    submittingDialog.dismiss()
                    MaterialAlertDialogBuilder(
                        this,
                        R.style.Base_Theme_Zachnology_AlertDialog
                    )
                        .setTitle("Submitted")
                        .setMessage("Appointment requested successfully. Please wait to hear from a Zachnology representative to finalize your appointment")
                        .setIcon(R.drawable.baseline_check_24)
                        .setCancelable(false)
                        .setNeutralButton("OK", DialogInterface.OnClickListener { dialog, which ->
                            val i = Intent(this, MainActivity::class.java)
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(i)
                            finish()
                        })
                        .show()
                },
                {
                    Log.e("Error", "Error")
                })
        }


    }
}
