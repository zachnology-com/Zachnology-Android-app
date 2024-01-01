package com.zachnology.app

import android.content.Context
import android.content.res.Resources.Theme
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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

        var cover = findViewById<android.widget.ImageView>(R.id.coverImage)
        var loading = findViewById<android.widget.ProgressBar>(R.id.progressBar)
        var storedEmail = sharedPref.getString("email", null)
        var storedPassword = sharedPref.getString("password", null)


        if (storedEmail == null || storedPassword == null) {
            hasPassedSplashScreen = true
            val intent = android.content.Intent(this, SigninActivity::class.java)
            startActivity(intent)
            finish()
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
                hasPassedSplashScreen = true
                val intent = android.content.Intent(this, SigninActivity::class.java)
                intent.putExtra("status", "Login Failed!")
                startActivity(intent)
                finish()

            })
        }

    }
}