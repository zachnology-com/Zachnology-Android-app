package com.zachnology.app

import android.graphics.Typeface
import android.os.Bundle
import android.text.Html
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import java.text.SimpleDateFormat

class PendingAppointments : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pending_appointments)

        var toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        var appointmentsListView = findViewById<LinearLayout>(R.id.appointmentList)

        for(i in AppointmentManager.pendingAppointments) {
            var appointmentButton = CardView(this)
            appointmentButton.elevation = 10F
            appointmentButton.radius = 10F
//            appointmentButton.setContentPadding(30, 30, 30, 30)
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
            categoryParams.setMargins(40, 30, 30, 15)
            appointmentCategory.layoutParams = categoryParams
            appointmentCategory.textSize = 20F
            appointmentCategory.setTypeface(null, Typeface.BOLD)

            var appointmentDescription = TextView(this)
            var descriptionParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            descriptionParams.setMargins(40, 15, 30, 30)
            appointmentDescription.layoutParams = descriptionParams
            appointmentDescription.text = i.description

            var appointmentContactMethod = TextView(this)
            var contactMethodParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            contactMethodParams.setMargins(40, 15, 30, 30)
            appointmentContactMethod.layoutParams = contactMethodParams
            var contactMethodText = "<b>Contact Method: </b>" + i.contactMethod
            appointmentContactMethod.setText(Html.fromHtml(contactMethodText));


            var appointmentDateSubmitted = TextView(this)
            var dateSubmittedParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            dateSubmittedParams.setMargins(40, 15, 30, 30)
            appointmentDateSubmitted.layoutParams = dateSubmittedParams
            val dateFormat = SimpleDateFormat("MMMM d, yyyy")
            val dateSubmittedText = "<b>Date Submitted: </b>" + dateFormat.format(i.dateRequested!!.time)
            appointmentDateSubmitted.setText(Html.fromHtml(dateSubmittedText));



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
}