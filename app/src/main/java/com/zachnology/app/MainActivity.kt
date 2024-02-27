package com.zachnology.app

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel
import com.onesignal.notifications.INotificationClickEvent
import com.onesignal.notifications.INotificationClickListener
import com.zachnology.app.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import java.util.UUID;
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            // TODO: Inform user that that your app will not show notifications.
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var navController = findNavController(R.id.nav_fragment)
        val sharedPref = getSharedPreferences("loginInformation", MODE_PRIVATE)

        val myUuid = UUID.randomUUID()
        val myUuidAsString = myUuid.toString()

        OneSignal.Debug.logLevel = LogLevel.VERBOSE
        OneSignal.initWithContext(this, AppointmentManager.ONESIGNAL_APP_ID)
        val clickListener = object : INotificationClickListener {
            override fun onClick(event: INotificationClickEvent) {
               try {
                   val data = event.notification.additionalData
                   if(data?.getString("type") == "pending") {
                       val appointmentId = data.getString("appointmentId")
                       val intent = Intent(this@MainActivity, EditAppointment::class.java)
                       intent.putExtra("id", appointmentId)
                       startActivity(intent)
                   }

               }
               catch (e: Exception) {
                   Log.e("Notifs", "Error parsing notification data")
               }
            }
        }

        Log.w("Notifs", "External ID is" + OneSignal.User.externalId)



        if(IdentityManager.token == null) {
            val intent = android.content.Intent(this, AuthenticationActivity::class.java)
            startActivity(intent)
            finish()
            val content: View = findViewById(android.R.id.content)
            //if sdk is greater than or equal to 31
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // Check whether the initial data is ready.
                content.viewTreeObserver.addOnPreDrawListener(
                    object : ViewTreeObserver.OnPreDrawListener {
                        override fun onPreDraw(): Boolean {
                            // Check whether the initial data is ready.
                            return AuthenticationActivity.hasPassedSplashScreen
                        }
                    }
                )
            }

            finish()
        }
        else {
            if(OneSignal.Notifications.permission) {
                OneSignal.User.pushSubscription.optIn()
            }
            if((OneSignal.User.externalId).equals("")) {
                OneSignal.login(myUuidAsString)
            }
            AuthenticationActivity.hasPassedSplashScreen = true
            binding.bottomNavigation.setupWithNavController(navController!!)
            OneSignal.Notifications.addClickListener(clickListener)
            val content: View = findViewById(android.R.id.content)
        }


    }



}