package com.zachnology.app

import android.content.DialogInterface
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.onesignal.OneSignal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class SigninActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)
        val sharedPref = getSharedPreferences("loginInformation", MODE_PRIVATE)
        val editor = sharedPref.edit()

        var status = intent.getStringExtra("status")


        var loginButton = findViewById<android.widget.Button>(R.id.loginButton)
        var statusText = findViewById<android.widget.TextView>(R.id.status)
        var email = findViewById<android.widget.EditText>(R.id.emailInput)
        var password = findViewById<android.widget.EditText>(R.id.passwordInput)

        val myUuid = UUID.randomUUID()
        val myUuidAsString = myUuid.toString()

        if(!OneSignal.Notifications.permission) {
            MaterialAlertDialogBuilder(this, R.style.Base_Theme_Zachnology_AlertDialog)
                .setTitle("Notification Permission")
                .setMessage("We use notifications to send you reminders and alerts about your appointments. Please enable notifications to continue.")
                .setPositiveButton("OK") { dialog, which ->
                    CoroutineScope(Dispatchers.IO).launch {
                        if((OneSignal.User.externalId).equals("")) {
                            OneSignal.login(myUuidAsString)
                        }
                        OneSignal.Notifications.requestPermission(true)
                        OneSignal.User.pushSubscription.optIn()
                    }
                }
                .setNegativeButton("Cancel") { dialog, which ->
                    dialog.dismiss()
                }
                .show()
        }

        if (status != null) {
//            statusText.text = status
        }
        loginButton.setOnClickListener() {
            statusText.visibility = View.GONE
            var loggingInDialog = MaterialAlertDialogBuilder(
                this,
                R.style.Base_Theme_Zachnology_AlertDialog
            )
                .setTitle("Logging in...")
                .setMessage(" ")
                .setIcon(R.drawable.avd_anim)
                .setCancelable(false)
                .show()
            IdentityManager.loginWithCredentials(email.text.toString(),
                password.text.toString(),
                this,
                { response ->
                    sharedPref.edit().putString("email", email.text.toString()).commit()
                    sharedPref.edit().putString("password", password.text.toString()).commit()
//                    statusText.text = "Logged in!"
                    loggingInDialog.dismiss()
                    val intent = android.content.Intent(this, AuthenticationActivity::class.java)
                    startActivity(intent)
                    finish()
                },
                {
                    loggingInDialog.dismiss()
                    statusText.text = "Email or password incorrect"
                    statusText.setTextColor(Color.RED)
                    statusText.visibility = View.VISIBLE

                })
        }
    }
}