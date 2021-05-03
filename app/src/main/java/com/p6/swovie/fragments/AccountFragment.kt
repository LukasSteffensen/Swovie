package com.p6.swovie.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.p6.swovie.LoginActivity
import com.p6.swovie.R

class AccountFragment : Fragment(), View.OnClickListener {

    private val TAG = "AccountFragment"

    private val JSON_URL_LOGO = "https://www.themoviedb.org/assets/2/v4/logos/v2/blue_square_2-d537fb228cf3ded904ef09b136fe3fec72548ebc1fea3fbbd1ad9e36364db38b.svg"
    private lateinit var imageViewLogo: ImageView
    private lateinit var buttonResetSwipes: Button
    private lateinit var buttonResetPassword: Button
    private lateinit var buttonLogout: Button

    private val db = Firebase.firestore
    private lateinit var uid: String
    private val groupCode = "DV3L"

    var auth: FirebaseAuth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_account, container, false)

        imageViewLogo = root.findViewById(R.id.imageView_logo)
        buttonResetSwipes = root.findViewById(R.id.button_reset_swipes)
        buttonResetPassword = root.findViewById(R.id.button_reset_password)
        buttonLogout = root.findViewById(R.id.button_logout)

        Glide.with(root)
                .load(JSON_URL_LOGO)
                .into(imageViewLogo)

        buttonResetSwipes.setOnClickListener(this)
        buttonResetPassword.setOnClickListener(this)
        buttonLogout.setOnClickListener(this)

        uid = auth.currentUser.uid

        return root
    }

    override fun onClick(view: View?) { // All OnClick buttons, with strings depending on button
        when (view) {
            buttonResetSwipes -> //working on leave group stuff here
                deleteSwipesFromGroup()
            buttonResetPassword -> alert(getString(R.string.resetpassword), getString(R.string.alertpassword), buttonResetPassword)
            buttonLogout -> alert(getString(R.string.logout), getString(R.string.alertlogout), buttonLogout)
        }
    }

    private fun deleteSwipesFromGroup() {
        val swipesRef = db.collection("groups")
            .document(groupCode)
            .collection("swipes")
        swipesRef.get().addOnSuccessListener { result ->
            for (document in result) {
                // Atomically remove a region from the "regions" array field.
                document.reference.update("Super like", FieldValue.arrayRemove(uid))
                document.reference.update("Like", FieldValue.arrayRemove(uid))
                document.reference.update("Not today", FieldValue.arrayRemove(uid))
                document.reference.update("Never", FieldValue.arrayRemove(uid))
            }
        }
    }

    private fun alert(title: String, message: String, button: Button){ //Making an alert dialog
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(R.string.alertyes) { _, _ ->
            when (button) { // based on buttons, when pressed yes do the action
                buttonLogout -> { logOut() }
                buttonResetPassword -> { resetPassword() }
                buttonResetSwipes -> { toast("Reset all swipes clicked yes") }
                else -> { toast("Error occurred") }
            }
        }
        builder.setNeutralButton(R.string.alertcancel){dialogInterface , which ->
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun logOut() {
        auth.signOut()
        val intent = Intent(activity, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun resetPassword(){
        val user = Firebase.auth.currentUser
        user?.let {
            for (profile in it.providerData) {
                val email = profile.email
                Firebase.auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            toast("An email has been sent with a link to reset your password")
                            Log.d(TAG, "Email sent.")
                        }
                    }
            }
        }
    }

    private fun toast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }
}