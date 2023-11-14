package com.zachnology.app

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject
import org.json.JSONTokener

class EditAppointment : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_appointment)

        var toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        val categoryInput = findViewById<AutoCompleteTextView>(R.id.category_input)
        val descriptionInput = findViewById<TextInputEditText>(R.id.description_input)
        val contactMethodInput = findViewById<TextInputEditText>(R.id.contact_method_input)
        val coverImage = findViewById<ImageView>(R.id.cover_image)

        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
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

        var appointmentId = intent.getStringExtra("id").toString()

        AppointmentManager.getPendingAppointment(
            this,
            appointmentId,
            { response ->
                val fullObject = JSONTokener(response).nextValue() as JSONObject
                categoryInput.setText(fullObject.getString("category"))
                val adapter = ArrayAdapter(this, R.layout.list_item, items)
                (categoryInput)?.setAdapter(adapter)
                descriptionInput.setText(fullObject.getString("description"))
                contactMethodInput.setText(fullObject.getString("contactmethod"))
                progressBar.visibility = ProgressBar.GONE
                coverImage.visibility = ImageView.GONE
                submitButton.isEnabled = true
            },
            {

            })

        toolbar.setNavigationOnClickListener() {
            finish()
        }

        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.delete -> {
                    var comfirmDialog = MaterialAlertDialogBuilder(
                        this,
                        R.style.Base_Theme_Zachnology_AlertDialog
                    )
                        .setTitle("Delete Appointment?")
                        .setMessage("Are you sure you want to delete this appointment? This action is irreversible.")
                        .setIcon(R.drawable.outline_delete_24)
                        .setNegativeButton(
                            "Cancel",
                            DialogInterface.OnClickListener { dialog, which ->
                                dialog.dismiss()
                            })
                        .setPositiveButton(
                            "Yes",
                            DialogInterface.OnClickListener { dialog, which ->
                                var deletingDialog = MaterialAlertDialogBuilder(
                                    this,
                                    R.style.Base_Theme_Zachnology_AlertDialog
                                )
                                    .setTitle("Deleting...")
                                    .setMessage("Your appointment is being deleted. Please wait...")
                                    .setIcon(R.drawable.outline_delete_24)
                                    .setCancelable(false)
                                    .show()
                                AppointmentManager.deletePendingAppointment(this, appointmentId, {
                                    deletingDialog.dismiss()
                                    var deletedDialog = MaterialAlertDialogBuilder(
                                        this,
                                        R.style.Base_Theme_Zachnology_AlertDialog
                                    )
                                        .setTitle("Deleted")
                                        .setMessage("Your appointment has been deleted.")
                                        .setIcon(R.drawable.outline_delete_24)
                                        .setCancelable(false)
                                        .setNegativeButton(
                                            "OK",
                                            DialogInterface.OnClickListener { dialog, which ->
                                                val i = Intent(this, MainActivity::class.java)
                                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                                startActivity(i)
                                                finish()
                                            })
                                        .show()
                                }, {})
                            })
                        .show()
                    true
                }

                else -> false
            }
        }

        submitButton.setOnClickListener() {
            var submittingDialog = MaterialAlertDialogBuilder(
                this,
                R.style.Base_Theme_Zachnology_AlertDialog
            )
                .setTitle("Saving")
                .setMessage("Your changes are being saved. Please wait...")
                .setIcon(R.drawable.baseline_cloud_upload_24)
                .setCancelable(false)
                .show()
            AppointmentManager.updatePendingAppointment(
                this,
                appointmentId,
                categoryInput.text.toString(),
                descriptionInput.text.toString(),
                contactMethodInput.text.toString(),
                { response ->
                    submittingDialog.dismiss()
                    MaterialAlertDialogBuilder(
                        this,
                        R.style.Base_Theme_Zachnology_AlertDialog
                    )
                        .setTitle("Saved")
                        .setMessage("Changed saved successfully")
                        .setIcon(R.drawable.baseline_check_24)
                        .setNegativeButton(
                            "Continue Editing",
                            DialogInterface.OnClickListener { dialog, which ->
                                dialog.dismiss()
                            })
                        .setPositiveButton(
                            "Return to Home",
                            DialogInterface.OnClickListener { dialog, which ->
                                val i = Intent(this, MainActivity::class.java)
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(i)
                                finish()
                            })
                        .show()
                },
                {

                })
        }


    }
}