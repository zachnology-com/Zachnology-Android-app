package com.zachnology.app

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.text.Html
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.MenuRes
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
        viewHolder.contactMethod.text = Html.fromHtml("<b>Contact Method: </b>" + dataSet[position].contactMethod)
        viewHolder.dateSubmitted.text = Html.fromHtml("<b>Submitted On: </b>" + dateFormat.format(dataSet[position].dateRequested!!.time))
        viewHolder.card.setOnClickListener() {
                val intent = Intent(myCon, EditAppointment::class.java)
                intent.putExtra("id", dataSet[position].appointmentId)
                startActivity(myCon, intent, null)
        }
        viewHolder.card.setOnLongClickListener() {
            dataSet[position].appointmentId?.let { it1 ->
                showMenu(viewHolder.category, R.menu.pending_appointment_context_menu, viewHolder.card,
                    it1
                )
            }
            true
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

    private fun showMenu(v: View, @MenuRes menuRes: Int, buttonView: MaterialCardView, appointmentId: String) {
        val popup = PopupMenu(myCon, v)

        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.edit_appointment -> {
                    val intent = Intent(myCon, EditAppointment::class.java)
                    intent.putExtra("id", appointmentId)
                    startActivity(myCon, intent, null)
                }
                R.id.delete_appointment -> {
                    var confirmDialog = MaterialAlertDialogBuilder(
                        myCon,
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
                                    myCon,
                                    R.style.Base_Theme_Zachnology_AlertDialog
                                )
                                    .setView(R.layout.loading)
                                    .setCancelable(false)
                                    .show()
                                AppointmentManager.deletePendingAppointment(myCon, appointmentId, {
                                    deletingDialog.dismiss()
                                }, {})
                            })
                        .show()
                    true
                }
            }
            true
        }
        popup.setOnDismissListener {
        }
        // Show the popup menu.
        popup.show()
    }

}
