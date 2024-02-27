package com.zachnology.app

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.onesignal.OneSignal
import com.onesignal.common.OneSignalWrapper
import com.onesignal.core.internal.database.impl.OneSignalDbContract
import com.onesignal.debug.LogLevel
import com.onesignal.notifications.bridges.OneSignalHmsEventBridge
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.json.JSONTokener
import java.util.Objects


class IdentityManager {
    companion object {
        var token: String? = null
        var refreshToken: String? = null
        var email: String? = null
        var password: String? = null
        var name: String? = null
        var hasPassedSplashScreen: Boolean = false


        fun loginWithCredentials(
            email: String,
            password: String,
            context: Context,
            successFunction: (response: String) -> (Unit),
            failureFunction: () -> (Unit)
        ) {
            var accountManager = AccountManager.get(context)
            var queue = Volley.newRequestQueue(context)
            var identityUrl =
                Constants.URL_ROOT + "/.netlify/identity/token?grant_type=password&username=" + email + "&password=" + password;
            var success = false
            val stringRequest = StringRequest(
                Request.Method.POST, identityUrl,
                { response ->
                    val parsedJSON = JSONObject(response)
                    token = parsedJSON["access_token"].toString()
                    refreshToken = parsedJSON["refresh_token"].toString()
                    this.email = email
                    this.password = password
                    Log.e("Token", token.toString())
                    CoroutineScope(Dispatchers.IO).launch {
                        var coQueue = Volley.newRequestQueue(context)
                        var coUrl = Constants.URL_ROOT + "/.netlify/identity/user"
                        var coRequest = object : StringRequest(
                            Method.PUT, coUrl,
                            { response ->
                            },
                            {
                            }
                        ) {
                            override fun getHeaders(): MutableMap<String, String> {
                                val headers = HashMap<String, String>()
                                headers["Authorization"] = "Bearer " + token
                                return headers
                            }

                            override fun getBody(): ByteArray {
                                val params = HashMap<String, HashMap<String, String>>()
                                var data = HashMap<String, String>()
                                data["mobile_subscription_token"] = OneSignal.User.externalId
                                params["data"] = data
                                return JSONObject(params as Map<*, *>).toString().toByteArray()
                            }
                        }
                        coQueue.add(coRequest)

                    }
                    if (accountManager.accounts.isEmpty()) {
                        var currentAccount = Account(email, "com.zachnology")
                        accountManager.addAccountExplicitly(currentAccount, password, null)
                        accountManager.setAuthToken(currentAccount, "com.zachnology", token)
                    } else {
                        var currentAccount = accountManager.accounts[0]
                        accountManager.setPassword(currentAccount, password)
                        accountManager.setAuthToken(currentAccount, "com.zachnology", token)
                    }
                    successFunction(response)
                },
                {
                    failureFunction()
                })
            queue.add(stringRequest)
        }

        fun logout(context: Context) {
            var accountManager = AccountManager.get(context)
            var coQueue = Volley.newRequestQueue(context)
            var coUrl = Constants.URL_ROOT + "/.netlify/identity/user"
            var coRequest = object : StringRequest(
                Method.PUT, coUrl,
                { response ->
                    token = ""
                    refreshToken = ""
                    email = ""
                    password = ""
                    name = ""
                    Log.w("Notifs", "ID was " + OneSignal.User.pushSubscription.id)
                    OneSignal.logout()
                    Log.w("Notifs", "ID is now " + OneSignal.User.pushSubscription.id)
                },
                {
                }
            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = "Bearer " + token
                    return headers
                }

                override fun getBody(): ByteArray {
                    val params = HashMap<String, HashMap<String, String>>()
                    var data = HashMap<String, String>()
                    data["mobile_subscription_token"] = ""
                    params["data"] = data
                    return JSONObject(params as Map<*, *>).toString().toByteArray()
                }
            }
            coQueue.add(coRequest)
            if (accountManager.accounts.isNotEmpty())
                accountManager.removeAccountExplicitly(accountManager.accounts[0])


        }

    }

}