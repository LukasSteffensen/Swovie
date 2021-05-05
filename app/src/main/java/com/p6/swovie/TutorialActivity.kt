package com.p6.swovie

import android.app.Activity
import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class TutorialActivity : AppCompatActivity() {

    private lateinit var buttonOk: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial)

        buttonOk = findViewById(R.id.buttonOk)

        buttonOk.setOnClickListener {
            val intent = Intent(this, Tutorial2Activity::class.java)
            startActivity(intent)
        }
    }
}