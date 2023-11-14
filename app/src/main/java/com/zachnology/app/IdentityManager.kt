package com.zachnology.app

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.zachnology.app.databinding.ActivityMainBinding
import org.json.JSONArray
import org.json.JSONTokener


class IdentityManager {
    companion object {
        var token: String? = null
        var refreshToken: String? = null
        var email: String? = null
        var password: String? = null
        var name: String? = null


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