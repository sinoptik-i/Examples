package com.example.examples

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.examples.contacts.ContactManager
import com.example.examples.databinding.ActivityMainCoroutinesBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch



class MainActivityCoroutines : AppCompatActivity() {

    private lateinit var binding: ActivityMainCoroutinesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainCoroutinesBinding.inflate(layoutInflater)


        setContentView(binding.root)
        binding.button.setOnClickListener {

            /* lifecycleScope.launch {
                 loadData()
             }*/
        }


    }




    private suspend fun loadData() {
        with(binding) {
            progress.isVisible = true
            button.isEnabled = false
            val city = loadCity()
            tvCity.setText(city)
            tvTemp.setText(loadTemp(city))
            progress.isVisible = false
            button.isEnabled = true
        }
    }

    private suspend fun loadCity(): String {
        delay(3000)
        return "LP"
    }

    private suspend fun loadTemp(city: String): String {
        Toast.makeText(this, "loading temp for $city", Toast.LENGTH_SHORT).show()
        delay(3000)
        return "23"
    }
}