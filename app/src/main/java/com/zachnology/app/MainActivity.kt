package com.zachnology.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.zachnology.app.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var navController = findNavController(R.id.nav_fragment)
        val sharedPref = getSharedPreferences("loginInformation", MODE_PRIVATE)

        if(IdentityManager.token == null) {
            val intent = android.content.Intent(this, AuthenticationActivity::class.java)
            startActivity(intent)
            finish()
        }
        else {
            binding.bottomNavigation.setupWithNavController(navController!!)
        }

    }



}