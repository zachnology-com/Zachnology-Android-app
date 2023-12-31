package com.zachnology.app

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel
import org.json.JSONObject
import org.json.JSONTokener


class IdentityManager {
    companion object {
        var token: String? = null
        var refreshToken: String? = null
        var email: String? = null
        var password: String? = null
        var name: String? = null
        var hasPassedSplashScreen: Boolean = false
        private val ONESIGNAL_APP_ID = "d8e33eb7-d25e-4b01-bfe3-95d01ac2a928"


        fun loginWithCredentials(email:String, password:String, context: Context, successFunction: (response: String) -> (Unit), failureFunction: () -> (Unit)) {
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
                    OneSignal.Debug.logLevel = LogLevel.VERBOSE
                    OneSignal.initWithContext(context, ONESIGNAL_APP_ID)
                    OneSignal.User.addEmail(email)
                    OneSignal.User.addAlias("email", email)
                    OneSignal.User.addTag("email", email)
                    OneSignal.login(email)
                    Log.e("Token", token.toString())
                    successFunction(response)
                },
                {
                    failureFunction()
                })
            queue.add(stringRequest)
        }

    }

}