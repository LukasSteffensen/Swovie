package com.p6.swovie

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CancellationSignal
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private val TAG = "LoginActivity"


    lateinit var email: String
    lateinit var password: String

    lateinit var editTextEmail: EditText
    lateinit var editTextPassword: EditText

    private var cancellationSignal: CancellationSignal? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val textViewRegister = findViewById<TextView>(R.id.button_register)

        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextTextPassword)

        textViewRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }


        val buttonLogIn = findViewById<Button>(R.id.button_login)
        buttonLogIn?.setOnClickListener()
        {
            if (editTextEmail.text.isEmpty() || editTextPassword.text.isEmpty()) {
                Toast.makeText(this, "Please insert your login credentials", Toast.LENGTH_LONG).show()
            } else {
                email = editTextEmail.text.toString().trim()
                password = editTextPassword.text.toString().trim()

            }
        }
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}
