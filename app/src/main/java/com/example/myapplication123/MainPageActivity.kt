package com.example.myapplication123

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainPageActivity : AppCompatActivity() {
    private var messageText: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        val receivedIntent = intent

        messageText = findViewById(R.id.message)

        val firstName = receivedIntent.getStringExtra("firstName")
        val lastName = receivedIntent.getStringExtra("lastName")

        val welcome = String.format("%s %s is logged in!",firstName,lastName)

        messageText!!.text = welcome
    }
}