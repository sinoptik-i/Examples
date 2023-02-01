package com.example.examples

import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.examples.databinding.ActivityMainHandlersBinding
import kotlin.concurrent.thread

class MainActivityHandlers : AppCompatActivity() {

    private lateinit var binding: ActivityMainHandlersBinding

    private val handler = object : Handler(){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            println("HANDLE_MSG $msg")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainHandlersBinding.inflate(layoutInflater)

        setContentView(binding.root)
        binding.button.setOnClickListener {
            loadData()
        }

        handler.sendMessage(Message.obtain(handler,1,8))
    }

    private fun loadData() {
        with(binding) {
            progress.isVisible = true
            button.isEnabled = false
            loadCity {
                tvCity.setText(it)
                val city = tvCity.text.toString()
                loadTemp(city, {
                    tvTemp.setText(it)
                })
                progress.isVisible = false
                button.isEnabled = true
            }

        }
    }

    private fun loadCity(callback: (String) -> Unit) {
        thread {
            Thread.sleep(3000)
            runOnUiThread {
                callback.invoke("LP")
            }
        }
    }

    private fun loadTemp(city: String, callback: (String) -> Unit) {
        thread {
           /* Looper.prepare()
            val handler2=Handler()*/
            val handler2=Handler(Looper.getMainLooper())

            handler2.post {
                Toast.makeText(this, "loading temp for $city", Toast.LENGTH_SHORT).show()
            }
            Thread.sleep(3000)
            Handler(Looper.getMainLooper()).post {
                callback.invoke("23")
            }
        }
    }

}