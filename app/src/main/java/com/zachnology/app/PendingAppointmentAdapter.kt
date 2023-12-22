package com.zachnology.app

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat

class PendingAppointmentAdapter(private val myCon: Context, private val dataSet: ArrayList<PendingAppointment>) :
    RecyclerView.Adapter<PendingAppointmentAdapter.ViewHolder>() {
    val dateFormat = SimpleDateFormat("MMMM d, yyyy")
    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val category: TextView
        val description: TextView
        val contactMethod: TextView
        val dateSubmitted: TextView
        val card: MaterialCardView

        init {
            // Define click listener for the ViewHolder's View
            category = view.findViewById(R.id.category)
            description = view.findViewById(R.id.description)
            contactMethod = view.findViewById(R.id.contactMethod)
            dateSubmitted = view.findViewById(R.id.dateSubmitted)
            card = view.findViewById(R.id.buttonCard)

        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.appointment_button_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.category.text = dataSet[position].category
        viewHolder.description.text = dataSet[position].description
        viewHolder.contactMethod.text = dataSet[position].contactMethod
        viewHolder.dateSubmitted.text = dateFormat.format(dataSet[position].dateRequested!!.time)
        viewHolder.card.setOnClickListener() {
                val intent = Intent(myCon, EditAppointment::class.java)
                intent.putExtra("id", dataSet[position].appointmentId)
                startActivity(myCon, intent, null)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}
