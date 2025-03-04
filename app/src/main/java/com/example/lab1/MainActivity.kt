package com.example.lab1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import android.view.View

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun navigateToIntents(view: View) {
        findNavController(R.id.nav_host_fragment).navigate(R.id.intentsFragment)
    }

    fun navigateToMusic(view: View) {
        findNavController(R.id.nav_host_fragment).navigate(R.id.musicFragment)
    }

    fun navigateToBroadcast(view: View) {
        findNavController(R.id.nav_host_fragment).navigate(R.id.broadcastFragment)
    }

    fun navigateToCalendar(view: View) {
        findNavController(R.id.nav_host_fragment).navigate(R.id.calendarFragment)
    }
}
