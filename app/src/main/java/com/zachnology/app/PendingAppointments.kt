package com.zachnology.app

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.size
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class PendingAppointments : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pending_appointments)

        var toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        var recyclerLayout = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerLayout)
        var swipeLayout = findViewById<SwipeRefreshLayout>(R.id.swiperefresh)

        val customAdapter = PendingAppointmentAdapter(this, AppointmentManager.livePendingAppointments.value!!)
        recyclerLayout.adapter = customAdapter

        swipeLayout.setColorSchemeResources(R.color.md_theme_light_primary)


        swipeLayout.setOnRefreshListener {
            swipeLayout.performHapticFeedback(1)
            AppointmentManager.getAllAppointments(this, {
                customAdapter.notifyDataSetChanged()
                recyclerLayout.setAdapter(PendingAppointmentAdapter(this, AppointmentManager.livePendingAppointments.value!!));
                recyclerLayout.invalidate();
                swipeLayout.isRefreshing = false
                println("refreshed")
//                swipeLayout.performHapticFeedback(1 )
            }, {
                val intent = android.content.Intent(this, SigninActivity::class.java)
                intent.putExtra("status", "Login Failed!")
                startActivity(intent)
                finish()
            })
        }

        AppointmentManager.livePendingAppointments.observe(this, {
            customAdapter.notifyDataSetChanged()
            recyclerLayout.setAdapter(PendingAppointmentAdapter(this, AppointmentManager.livePendingAppointments.value!!));
            recyclerLayout.invalidate();
        })



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
            buttonView.strokeWidth = 3
            buttonView.strokeColor = Color.parseColor("#e8e8e8")
//            appointmentButton.setCardBackgroundColor(Color.GRAY)
        }
        // Show the popup menu.
        popup.show()
    }
}