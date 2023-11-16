package com.zachnology.app

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Html
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.card.MaterialCardView
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
//            appointmentButton.background = getDrawable(R.drawable.shadow_background)
            appointmentButton.radius = 50F
//            appointmentButton.strokeWidth = 2
            appointmentButton.strokeColor = Color.parseColor("#000000")
            appointmentButton.setOnClickListener() {
                val intent = Intent(this, EditAppointment::class.java)
                intent.putExtra("id", i.appointmentId)
                startActivity(intent)
            }

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