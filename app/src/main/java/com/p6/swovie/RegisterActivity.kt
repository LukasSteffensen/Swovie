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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.p6.swovie.dataClasses.User
import java.util.regex.Matcher
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {

    private val TAG = "RegisterActivity"

    private lateinit var auth: FirebaseAuth
    val db = FirebaseFirestore.getInstance()
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

        // Initialize Firebase Auth
        auth = Firebase.auth

        editTextFirstName = findViewById(R.id.editTextNameRegister)
        editTextEmail = findViewById(R.id.editTextEmailRegister)
        editTextPassword = findViewById(R.id.editTextPasswordRegister)

        val buttonRegister = findViewById<Button>(R.id.button_register_account)

        buttonRegister.setOnClickListener {
            firstName = editTextFirstName.text.toString().trim()
            email = editTextEmail.text.toString().trim()
            password = editTextPassword.text.toString().trim()

            if (isInputValid(firstName, email, password)) {
                val user = User(auth.currentUser.uid, firstName, email, emptyList())
                createUserAndSendEmail(user)
            }
        }
    }

    private fun isInputValid(name: String, email: String, password: String): Boolean {
        if (name.isEmpty()) {
            inputAgain(editTextFirstName, "Please put in your first name")
        } else if (email.isEmpty()) {
            inputAgain(editTextEmail, "Please put in your email address")
        } else if (!email.isEmailValid()) {
            inputAgain(editTextEmail, "Please enter a valid email address")
        } else if (password.isEmpty()) {
            inputAgain(editTextPassword, "Please put in your password")
        } else if (!password.isPasswordValid()) {
            inputAgain(
                editTextPassword,
                "Password must be at least 8 characters and contain at least a number, uppercase letter and lowercase letter"
            )
        } else {
            return true
        }
        return false
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            //add code to go to somewhere
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
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this)
            .matches()
    }

    private fun addUserToDatabase(user: User) {
         db.collection("users")
             .document(user.userID!!).set(user)
             .addOnSuccessListener {
                 Log.d("RegisterActivity: ", "DocumentSnapshot added with ID: ${user.userID}")
                 auth.signOut()
             }
             .addOnFailureListener { e ->
                 Log.w("RegisterActivity: ", "Error adding document", e)
             }

         val intent = Intent(this, LoginActivity::class.java)
         startActivity(intent)
     }

     private fun createUserAndSendEmail(user: User) {
         auth.createUserWithEmailAndPassword(email, password)
             .addOnCompleteListener(this) { task ->
                 if (task.isSuccessful) {
                     auth.currentUser?.sendEmailVerification()
                         ?.addOnSuccessListener (this) {
                             if (task.isSuccessful) {
                                 toast("A verification email has been sent")
                                 addUserToDatabase(user)
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
     }

}