package com.zachnology.app

import android.content.Context
import android.util.Log
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import org.json.JSONTokener

class AppointmentManager {
    companion object {
        var hasHomeScreenData: Boolean = false
        var numberOfPendingAppointments: Int? = null
        var numberOfConfirmedAppointments: Int? = null
        var pendingAppointments: ArrayList<PendingAppointment> = ArrayList()

        fun addPendingAppointment(pendingAppointment: PendingAppointment) {
            pendingAppointments?.add(pendingAppointment)
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
                    AppointmentManager.numberOfPendingAppointments =
                        fullObject.getInt("numOfPending")
                    AppointmentManager.numberOfConfirmedAppointments =
                        fullObject.getInt("numOfConfirmed")
                    AppointmentManager.hasHomeScreenData = true

                    val pendingAppointments = fullObject.getJSONArray("pendingAppointments")
                    AppointmentManager.pendingAppointments = ArrayList()
                    for (i in 0 until pendingAppointments.length()) {
                        var pendingAppointment = pendingAppointments.getJSONObject(i)
                        var pendingAppointmentObject = PendingAppointment(
                            pendingAppointment.getString("appointmentId"),
                            pendingAppointment.getString("category"),
                            pendingAppointment.getString("description"),
                            pendingAppointment.getString("contactmethod"),
                            pendingAppointment.getLong("datesubmitted")
                        )
                        addPendingAppointment(pendingAppointmentObject)
                    }
                    successFunction(response)

                }, {
                    Log.e("Error", it.toString())
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
            var url = Constants.URL_ROOT + "/api/newappointment"
            val stringRequest = object : StringRequest(Method.POST, url, { response ->
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
            failureFunction: () -> (Unit)
        ) {
            var url = Constants.URL_ROOT + "/api/getpending?id=" + appointmentId
            if (IdentityManager.token != null) {
                val queue = Volley.newRequestQueue(context)
                val stringRequest = object : StringRequest(Method.GET, url, { response ->
                    successFunction(response)

                }, {
                    Log.e("Error", it.toString())
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
            var url = Constants.URL_ROOT + "/api/editappointment"
            val stringRequest = object : StringRequest(Method.POST, url, { response ->
                getAllAppointments(context, { response ->
                    for (i in 0 until pendingAppointments.size) {
                        if (pendingAppointments[i].appointmentId == appointmentId) {
                            pendingAppointments[i].category = category
                            pendingAppointments[i].description = description
                            pendingAppointments[i].contactMethod = contactMethod
                        }
                    }
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
            context: Context, appointmentId: String, successFunction: (response: String) -> (Unit),
            failureFunction: () -> (Unit)
        ) {
            val queue = Volley.newRequestQueue(context)
            var url = Constants.URL_ROOT + "/api/deleteappointment"
            val stringRequest = object : StringRequest(Method.POST, url, { response ->
                getAllAppointments(context, { response ->
                    for (i in 0 until pendingAppointments.size) {
                        if (pendingAppointments[i].appointmentId == appointmentId) {
                            pendingAppointments.removeAt(i)
                        }
                    }
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