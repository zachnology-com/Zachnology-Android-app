package com.zachnology.app

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class AuthenticationActivity : AppCompatActivity() {
    companion object {
        var hasPassedSplashScreen: Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
        val sharedPref = getSharedPreferences("loginInformation", MODE_PRIVATE)
        val editor = sharedPref.edit()
        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    return hasPassedSplashScreen
                }
            }
        )

        var loginButton = findViewById<android.widget.Button>(R.id.loginButton)
        var statusText = findViewById<android.widget.TextView>(R.id.status)
        var cover = findViewById<android.widget.ImageView>(R.id.coverImage)
        var loading = findViewById<android.widget.ProgressBar>(R.id.progressBar)
        var email = findViewById<android.widget.EditText>(R.id.emailInput)
        var password = findViewById<android.widget.EditText>(R.id.passwordInput)
        var storedEmail = sharedPref.getString("email", null)
        var storedPassword = sharedPref.getString("password", null)


        if (storedEmail == null || storedPassword == null) {
            cover.visibility = android.view.View.GONE
            loading.visibility = android.view.View.GONE
            hasPassedSplashScreen = true
        } else {
            IdentityManager.loginWithCredentials(storedEmail, storedPassword, this, { response ->
                AppointmentManager.getAllAppointments(this, { response ->

                    val intent = android.content.Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }, {
                    hasPassedSplashScreen = true
                    loading.visibility = android.view.View.GONE
                    MaterialAlertDialogBuilder(this, R.style.Base_Theme_Zachnology_AlertDialog)
                        .setTitle("Unable to fetch appointments")
                        .setMessage("There was an issue getting your appointments. Please try again later or contact us if the issue persists.")
                        .setPositiveButton("OK") { dialog, which ->
                            finish()
                        }
                        .setNegativeButton("Sign out") { dialog, which ->
                            finish()
                        }
                        .show()

                })
            }, {
                statusText.text = "Login failed!"
                hasPassedSplashScreen = true
                cover.visibility = android.view.View.GONE
                loading.visibility = android.view.View.GONE

            })
        }



        loginButton.setOnClickListener() {
            statusText.text = "Logging in..."
            IdentityManager.loginWithCredentials(
                email.text.toString(),
                password.text.toString(),
                this,
                { response ->
                    sharedPref.edit().putString("email", email.text.toString()).commit()
                    sharedPref.edit().putString("password", password.text.toString()).commit()
                    statusText.text = "Logged in!"
                    cover.visibility = android.view.View.VISIBLE
                    loading.visibility = android.view.View.VISIBLE
                    AppointmentManager.getAllAppointments(this, { response ->
                        val intent = android.content.Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }, {
                        statusText.text = "Login failed!"
                        cover.visibility = android.view.View.GONE
                        loading.visibility = android.view.View.GONE

                    })
                },
                { statusText.text = "Login failed!" })
        }
    }
}