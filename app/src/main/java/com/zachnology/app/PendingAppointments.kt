package com.zachnology.app

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Html
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.cardview.widget.CardView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat


class PendingAppointments : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pending_appointments)

        var toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        var appointmentsListView = findViewById<LinearLayout>(R.id.appointmentList)

        for(i in AppointmentManager.pendingAppointments) {
            var appointmentButton = MaterialCardView(this, null, com.google.android.material.R.style.Widget_Material3_CardView_Outlined)
            appointmentButton.elevation = 10F
            appointmentButton.radius = 50F
            appointmentButton.strokeColor = Color.parseColor("#000000")
            appointmentButton.setOnClickListener() {
                val intent = Intent(this, EditAppointment::class.java)
                intent.putExtra("id", i.appointmentId)
                startActivity(intent)
            }
            appointmentButton.isLongClickable = true


            var params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(30, 30, 30, 30)
            appointmentButton.layoutParams = params

            var buttonLayout = LinearLayout(this)
            buttonLayout.orientation = LinearLayout.VERTICAL


            var appointmentCategory = TextView(this)
            appointmentCategory.text = i.category
            var categoryParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            categoryParams.setMargins(60, 50, 30, 15)
            appointmentCategory.layoutParams = categoryParams
            appointmentCategory.textSize = 20F
            appointmentCategory.setTypeface(null, Typeface.BOLD)

            var appointmentDescription = TextView(this)
            var descriptionParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            descriptionParams.setMargins(60, 15, 40, 30)
            appointmentDescription.layoutParams = descriptionParams
            appointmentDescription.text = i.description

            var appointmentContactMethod = TextView(this)
            var contactMethodParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            contactMethodParams.setMargins(60, 15, 30, 30)
            appointmentContactMethod.layoutParams = contactMethodParams
            var contactMethodText = "<b>Contact Method: </b>" + i.contactMethod
            appointmentContactMethod.setText(Html.fromHtml(contactMethodText));


            var appointmentDateSubmitted = TextView(this)
            var dateSubmittedParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            dateSubmittedParams.setMargins(60, 15, 30, 50)
            appointmentDateSubmitted.layoutParams = dateSubmittedParams
            val dateFormat = SimpleDateFormat("MMMM d, yyyy")
            val dateSubmittedText = "<b>Date Submitted: </b>" + dateFormat.format(i.dateRequested!!.time)
            appointmentDateSubmitted.setText(Html.fromHtml(dateSubmittedText));

            appointmentButton.setOnLongClickListener() {v: View ->
                appointmentButton.strokeWidth = 4
                appointmentButton.setStrokeColor(Color.GRAY)
//                appointmentButton.setCardBackgroundColor
                showMenu(appointmentCategory, R.menu.pending_appointment_context_menu, appointmentButton, i.appointmentId!!)
                true
            }



            buttonLayout.addView(appointmentCategory)
            buttonLayout.addView(appointmentDescription)
            buttonLayout.addView(appointmentContactMethod)
            buttonLayout.addView(appointmentDateSubmitted)
            appointmentButton.addView(buttonLayout)
            appointmentsListView.addView(appointmentButton)

        }


        toolbar.setNavigationOnClickListener {
            finish()
        }


    }
    private fun showMenu(v: View, @MenuRes menuRes: Int, buttonView: MaterialCardView, appointmentId: String) {
        val popup = PopupMenu(this!!, v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.edit_appointment -> {
                    val intent = Intent(this, EditAppointment::class.java)
                    intent.putExtra("id", appointmentId)
                    startActivity(intent)
                }
                R.id.delete_appointment -> {
                    var confirmDialog = MaterialAlertDialogBuilder(
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
            }
            true
        }
        popup.setOnDismissListener {
            buttonView.strokeWidth = 0
//            appointmentButton.setCardBackgroundColor(Color.GRAY)
        }
        // Show the popup menu.
        popup.show()
    }
}