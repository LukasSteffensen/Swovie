package com.p6.swovie

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import java.util.HashMap
import java.util.regex.Matcher
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {

    private val TAG = "RegisterActivity"

    private lateinit var uid: String

    private lateinit var user: HashMap<String, String>

    //var auth: FirebaseAuth = Firebase.auth

    // Access a Cloud Firestore instance from your Activity
    //val db = Firebase.firestore

    lateinit var editTextFirstName: EditText
    lateinit var editTextEmail: EditText
    lateinit var editTextPassword: EditText

    lateinit var email: String
    lateinit var password: String
    lateinit var firstName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val textViewSignIn = findViewById<TextView>(R.id.button_login_from_register)

        textViewSignIn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        editTextFirstName = findViewById(R.id.editTextNameRegister)
        editTextEmail = findViewById<EditText>(R.id.editTextEmailRegister)
        editTextPassword = findViewById<EditText>(R.id.editTextPasswordRegister)

        val buttonRegister = findViewById<Button>(R.id.button_register_account)

        buttonRegister.setOnClickListener {
            firstName = editTextFirstName.text.toString().trim()
            email = editTextEmail.text.toString().trim()
            password = editTextPassword.text.toString().trim()

            //Checks if all the editTexts are empty and if some requirements are not met
            if (firstName.isEmpty()) {
                inputAgain(editTextFirstName, "Please put in your first name")
            } else if (email.isEmpty()) {
                inputAgain(editTextEmail, "Please put in your email address")
            } else if (!email.isEmailValid()) {
                inputAgain(editTextEmail, "Please enter a valid email address")
            } else if (password.isEmpty()) {
                inputAgain(editTextPassword, "Please put in your password")
            } else if (!password.isPasswordValid()) {
                inputAgain(editTextPassword, "Password must be at least 8 characters and contain at least a number, uppercase letter and lowercase letter")
            } else {
                user = hashMapOf<String, String>(
                    "firstName" to firstName,
                    "email" to email
                )

                //createUserAndSendEmail()

                Log.i("RegisterActivity: ", "we hit the else!")


            }
        }
    }

    private fun inputAgain(editText: EditText, toast: String) {
        editText.requestFocus()
        val imm: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
        toast(toast)
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    //Checks if password meets the requirements
    private fun String.isPasswordValid(): Boolean {
        val pattern: Pattern
        val matcher: Matcher

        val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$"

        pattern = Pattern.compile(PASSWORD_PATTERN)
        matcher = pattern.matcher(this)

        return matcher.matches()
    }

    //Checks if email is valid
    private fun String.isEmailValid(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }

   /* private fun addUserToDatabase() {
        uid = auth.currentUser!!.uid
        db.collection("users")
            .document(uid).set(user)
            .addOnSuccessListener {
                Log.d("RegisterActivity: ", "DocumentSnapshot added with ID: $uid")
                auth.signOut()
            }
            .addOnFailureListener { e ->
                Log.w("RegisterActivity: ", "Error adding document", e)
            }

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun createUserAndSendEmail() {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    auth.currentUser?.sendEmailVerification()
                        ?.addOnCompleteListener(this) {
                            if (task.isSuccessful) {
                                toast("A verification email has been sent")
                                addUserToDatabase()
                            }
                        }
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    } */

}