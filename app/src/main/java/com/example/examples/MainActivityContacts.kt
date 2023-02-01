package com.example.examples

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.examples.contacts.ContactManager
import com.example.examples.databinding.ActivityMainContactsBinding

private const val PERMISSION_CONTACTS_CODE = 2

class MainActivityContacts : AppCompatActivity() {


    private lateinit var binding: ActivityMainContactsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainContactsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLoadContacts.setOnClickListener {
            requestPermissions(
                arrayOf(
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_CONTACTS
                ), PERMISSION_CONTACTS_CODE
            )

      /*      if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED
            ) {
                getContacts()
            }*/
            //Log.e(this.localClassName, "${ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR)} ${PackageManager.PERMISSION_GRANTED}")
            // Log.e(this.localClassName, "${ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)} ${PackageManager.PERMISSION_GRANTED}")


            /* requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), PERMISSION_CONTACTS_CODE)
             val launcher =
                 registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
                     if (results[Manifest.permission.READ_CONTACTS] == true) {

                     }
                 }
             launcher.launch(arrayOf(Manifest.permission.READ_CONTACTS))*/
        }

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CONTACTS_CODE) {
            permissions.indexOf(Manifest.permission.READ_CONTACTS)
                .takeIf { it != -1 }
                ?.let { grantResults[it] == PackageManager.PERMISSION_GRANTED }
                ?.let {
                    if (it) {
                        getContacts()
                    }
                    else{
                        Toast.makeText(this,"U must allow Contacts permission in this app Settings", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    private fun getContacts() {
        Log.e(this.localClassName, "permition received")
        try {
            val contactManager = ContactManager(applicationContext)
            val result = contactManager.getContacts()
            if (!result.isEmpty()) {
                binding.tvContacts.setText(result)
            }
        } catch (ex: Exception) {
            Log.e(this.localClassName, "${ex.message}")
        }
    }
}