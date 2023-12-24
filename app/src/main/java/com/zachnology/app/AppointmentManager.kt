package com.zachnology.app

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import org.json.JSONTokener

class AppointmentManager {
    companion object {
        var hasHomeScreenData: Boolean = false
        var pendingAppointments: ArrayList<PendingAppointment> = ArrayList()
        var confirmedAppointments: ArrayList<ConfirmedAppointment> = ArrayList()

        val livePendingAppointments : MutableLiveData<ArrayList<PendingAppointment>> =  MutableLiveData<ArrayList<PendingAppointment>>()
        val liveConfirmedAppointments : MutableLiveData<ArrayList<ConfirmedAppointment>> =  MutableLiveData<ArrayList<ConfirmedAppointment>>()

        fun addPendingAppointment(pendingAppointment: PendingAppointment) {
            pendingAppointments?.add(pendingAppointment)
        }

        fun addConfirmedAppointment(confirmedAppointment: ConfirmedAppointment) {
            confirmedAppointments?.add(confirmedAppointment)
        }

        fun getAllAppointments(
            context: Context,
            successFunction: (response: String) -> (Unit),
            failureFunction: () -> (Unit)
        ) {
            var url = Constants.URL_ROOT + "/.netlify/functions/apphomedata"
            if (IdentityManager.token != null) {
                val queue = Volley.newRequestQueue(context)
                val stringRequest = object : StringRequest(Method.GET, url, { response ->
                    val fullObject = JSONTokener(response).nextValue() as JSONObject
                    IdentityManager.name = fullObject.getString("name")

                    try {
                        val pendingAppointments = fullObject.getJSONArray("pendingAppointments")
                        AppointmentManager.pendingAppointments = ArrayList()
                        for (i in 0 until pendingAppointments.length()) {
                            try {
                                var pendingAppointment = pendingAppointments.getJSONObject(i)
                                var pendingAppointmentObject = PendingAppointment(
                                    pendingAppointment.getString("appointmentId"),
                                    pendingAppointment.getString("category"),
                                    pendingAppointment.getString("description"),
                                    pendingAppointment.getString("contactmethod"),
                                    pendingAppointment.getLong("datesubmitted")
                                )
                                addPendingAppointment(pendingAppointmentObject)
                                livePendingAppointments.postValue(this.pendingAppointments)
                            } catch (e: Exception) { }
                        }
                        val confirmedAppointments = fullObject.getJSONArray("confirmedAppointments")
                        AppointmentManager.confirmedAppointments = ArrayList()
                        for (i in 0 until confirmedAppointments.length()) {
                            try {
                                var confirmedAppointment = confirmedAppointments.getJSONObject(i)
                                var confirmedAppointmentObject = ConfirmedAppointment(
                                    confirmedAppointment.getString("appointmentId"),
                                    confirmedAppointment.getString("category"),
                                    confirmedAppointment.getString("description"),
                                    confirmedAppointment.getString("contactmethod"),
                                    confirmedAppointment.getLong("datesubmitted"),
                                    confirmedAppointment.getLong("date"),
                                    confirmedAppointment.getBoolean("include_time"),
                                    confirmedAppointment.getString("representativenotes")

                                )
                                addConfirmedAppointment(confirmedAppointmentObject)
                                liveConfirmedAppointments.postValue(this.confirmedAppointments)
                            } catch (e: Exception) { }
                        }
                        hasHomeScreenData = true
                        successFunction(response)
                    } catch (e: Exception) {
                        hasHomeScreenData = false
                        failureFunction()
                    }


                }, {
                    Log.e("Error", it.toString())
                    hasHomeScreenData = false
                    failureFunction()
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Authorization"] = "Bearer " + IdentityManager.token
                        return headers
                    }
                }
                queue.add(stringRequest)
            }
        }

        fun requestAppointment(
            context: Context,
            category: String,
            description: String,
            contactMethod: String,
            successFunction: (response: String) -> Unit,
            failureFunction: () -> Unit
        ) {
            val queue = Volley.newRequestQueue(context)
            var url = Constants.URL_ROOT + "/api/appointment/pending/new"
            val stringRequest = object : StringRequest(Method.POST, url, { response ->
                var appointment = JSONObject(response).getJSONObject("appointment")
                addPendingAppointment(
                    PendingAppointment(
                        appointment.getString("appointmentId"),
                        appointment.getString("category"),
                        appointment.getString("description"),
                        appointment.getString("contactmethod"),
                        appointment.getLong("datesubmitted")
                    )
                )
                livePendingAppointments.postValue(this.pendingAppointments)
                getAllAppointments(context, { response ->
                    successFunction(response)
                }, {
                    failureFunction()
                })

            }, {
                Log.e("Error", it.toString())
                failureFunction()
            }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = "Bearer " + IdentityManager.token
                    return headers
                }

                override fun getBody(): ByteArray {
                    val params = HashMap<String, String>()
                    params["category"] = category
                    params["description"] = description
                    params["contactmethod"] = contactMethod
                    return JSONObject(params as Map<*, *>).toString().toByteArray()
                }
            }
            queue.add(stringRequest)

        }

        fun getPendingAppointment(
            context: Context,
            appointmentId: String,
            successFunction: (response: String) -> (Unit),
            failureFunction: (it: VolleyError) -> (Unit)
        ) {
            var url = Constants.URL_ROOT + "/api/appointment/pending/get?id=" + appointmentId
            if (IdentityManager.token != null) {
                val queue = Volley.newRequestQueue(context)
                val stringRequest = object : StringRequest(Method.GET, url, { response ->
                    successFunction(response)

                }, {
                    failureFunction(it)
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Authorization"] = "Bearer " + IdentityManager.token
                        return headers
                    }
                }
                queue.add(stringRequest)
            }
        }

        fun updatePendingAppointment(
            context: Context,
            appointmentId: String,
            category: String,
            description: String,
            contactMethod: String,
            successFunction: (response: String) -> (Unit),
            failureFunction: () -> (Unit)
        ) {
            val queue = Volley.newRequestQueue(context)
            var url = Constants.URL_ROOT + "/api/appointment/pending/edit"
            val stringRequest = object : StringRequest(Method.POST, url, { response ->
                getAllAppointments(context, { response ->
                    for (i in 0 until pendingAppointments.size) {
                        if (pendingAppointments[i].appointmentId == appointmentId) {
                            pendingAppointments[i].category = category
                            pendingAppointments[i].description = description
                            pendingAppointments[i].contactMethod = contactMethod
                        }
                    }
                    livePendingAppointments.postValue(this.pendingAppointments)
                    successFunction(response)
                }, {
                    failureFunction()
                })

            }, {
                Log.e("Error", it.toString())
                failureFunction()
            }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = "Bearer " + IdentityManager.token
                    return headers
                }

                override fun getBody(): ByteArray {
                    val params = HashMap<String, String>()
                    params["category"] = category
                    params["description"] = description
                    params["contactmethod"] = contactMethod
                    params["id"] = appointmentId
                    return JSONObject(params as Map<*, *>).toString().toByteArray()
                }
            }
            queue.add(stringRequest)

        }

        fun deletePendingAppointment(
            context: Context,
            appointmentId: String,
            successFunction: (response: String) -> (Unit),
            failureFunction: () -> (Unit)
        ) {
            val queue = Volley.newRequestQueue(context)
            var url = Constants.URL_ROOT + "/api/appointment/pending/delete"
            val stringRequest = object : StringRequest(Method.POST, url, { response ->
                getAllAppointments(context, { response ->
                    for (i in 0 until pendingAppointments.size) {
                        if (pendingAppointments[i].appointmentId == appointmentId) {
                            pendingAppointments.removeAt(i)
                        }
                    }
                    livePendingAppointments.postValue(this.pendingAppointments)
                    successFunction(response)
                }, {
                    failureFunction()
                })

            }, {
                Log.e("Error", it.toString())
                failureFunction()
            }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = "Bearer " + IdentityManager.token
                    return headers
                }

                override fun getBody(): ByteArray {
                    val params = HashMap<String, String>()
                    params["id"] = appointmentId
                    return JSONObject(params as Map<*, *>).toString().toByteArray()
                }
            }
            queue.add(stringRequest)
        }
    }
}