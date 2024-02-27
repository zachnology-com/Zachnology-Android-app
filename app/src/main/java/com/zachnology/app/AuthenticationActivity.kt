package com.zachnology.app

import android.accounts.AccountManager
import android.content.Context
import android.content.Intent
import android.content.res.Resources.Theme
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.onesignal.OneSignal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AuthenticationActivity : AppCompatActivity() {
    companion object {
        var hasPassedSplashScreen: Boolean = false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        var accountManager = AccountManager.get(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
        val sharedPref = getSharedPreferences("loginInformation", MODE_PRIVATE)
        val editor = sharedPref.edit()
        val content: View = findViewById(android.R.id.content)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            content.viewTreeObserver.addOnPreDrawListener(
                object : ViewTreeObserver.OnPreDrawListener {
                    override fun onPreDraw(): Boolean {
                        return hasPassedSplashScreen
                    }
                }
            )
        }

        var cover = findViewById<android.widget.ImageView>(R.id.coverImage)
        var loading = findViewById<android.widget.ProgressBar>(R.id.progressBar)


        if (accountManager.accounts.isEmpty()) {
            hasPassedSplashScreen = true
            val intent = Intent(this, SigninActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            Log.e("Auth", "No accounts found")

            finish()
        } else {
            var thisAccount = accountManager.accounts[0];
            IdentityManager.loginWithCredentials(thisAccount.name, accountManager.getPassword(thisAccount), this, { response ->
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