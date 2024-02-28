package com.zachnology.app

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText


class NewAppointment : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_appointment)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
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


        toolbar.setNavigationOnClickListener {
            finish()
        }

        submitButton.setOnClickListener() {
            var submittingDialog = MaterialAlertDialogBuilder(
                this,
                R.style.Base_Theme_Zachnology_AlertDialog
            )
                .setView(R.layout.loading)
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
                        .setCancelable(true)
                        .setNeutralButton("OK", DialogInterface.OnClickListener { dialog, which ->
                            finish()
                        })
                        .setOnDismissListener(DialogInterface.OnDismissListener {
                            finish()
                        })
                        .setOnCancelListener(DialogInterface.OnCancelListener {
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
