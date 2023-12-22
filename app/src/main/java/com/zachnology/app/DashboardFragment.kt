package com.zachnology.app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.android.volley.toolbox.Volley
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DashboardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DashboardFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val infl = inflater.inflate(R.layout.fragment_dashboard, container, false)
        val queue = Volley.newRequestQueue(activity)
        var welcometext = infl.findViewById<TextView>(R.id.welcometext)
        val numOfPending = infl.findViewById<TextView>(R.id.num_of_pending)
        val numOfConfirmed = infl.findViewById<TextView>(R.id.num_of_confirmed)
        val pendingButton = infl.findViewById<MaterialCardView>(R.id.pendingcard)
        val confirmedButton = infl.findViewById<MaterialCardView>(R.id.confirmedcard)
        val refreshFab = infl.findViewById<FloatingActionButton>(R.id.refresh_fab)
        val newAppointmentButton = infl.findViewById<Button>(R.id.newappointment)

        pendingButton.setOnClickListener() {
            val intent = Intent(context, PendingAppointments::class.java)
            startActivity(intent)
        }
        confirmedButton.setOnClickListener() {
            val intent = Intent(context, ConfirmedAppointments::class.java)
            startActivity(intent)
        }
        newAppointmentButton.setOnClickListener() {
            val intent = Intent(context, NewAppointment::class.java)
            startActivity(intent)
        }
        refreshFab.setOnClickListener() {
            val intent = android.content.Intent(activity?.baseContext, AuthenticationActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }


        if(AppointmentManager.hasHomeScreenData) {
            welcometext.text = "Hello, " + IdentityManager.name + "!"
            numOfPending.text = AppointmentManager.livePendingAppointments.value?.size.toString()
            numOfConfirmed.text = AppointmentManager.liveConfirmedAppointments.value?.size.toString()
        }
        else {
            activity?.let {
                AppointmentManager.getAllAppointments(it.baseContext, { response ->
                    welcometext.text = "Hello, " + IdentityManager.name + "!"
                    numOfPending.text = AppointmentManager.livePendingAppointments.value?.size.toString()
                    numOfConfirmed.text = AppointmentManager.liveConfirmedAppointments.value?.size.toString()
                }, {
                    val intent = android.content.Intent(activity?.baseContext, AuthenticationActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                })
            }
        }

        AppointmentManager.livePendingAppointments.observe(viewLifecycleOwner, {
            numOfPending.text = AppointmentManager.livePendingAppointments.value?.size.toString()
        })




        return infl
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DashboardFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DashboardFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}