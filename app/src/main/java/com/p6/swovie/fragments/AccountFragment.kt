package com.p6.swovie.fragments

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.p6.swovie.R

class AccountFragment : Fragment(), View.OnClickListener {

    private val JSON_URL_LOGO = "https://www.themoviedb.org/assets/2/v4/logos/v2/blue_square_2-d537fb228cf3ded904ef09b136fe3fec72548ebc1fea3fbbd1ad9e36364db38b.svg"
    private lateinit var imageViewLogo: ImageView
    private lateinit var buttonResetSwipes: Button
    private lateinit var buttonResetPassword: Button
    private lateinit var buttonLogout: Button

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

        return root
    }

    fun signOut() {
        //Firebase.auth.signOut()
    }

    override fun onClick(view: View?) { // All OnClick buttons, with strings depending on button
        when (view) {
            buttonResetSwipes -> alert(getString(R.string.resetswipes), getString(R.string.alertswipes))
            buttonResetPassword -> alert(getString(R.string.resetpassword), getString(R.string.alertpassword))
            buttonLogout -> alert(getString(R.string.logout), getString(R.string.alertlogout))
        }
    }

    private fun alert(title: String, message: String){ //Making an alert dialog
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(R.string.alertyes) { dialogInterface, which ->
            Toast.makeText(activity, "clicked yes", Toast.LENGTH_LONG).show()
        }
        builder.setNeutralButton(R.string.alertcancel){dialogInterface , which ->
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }


}