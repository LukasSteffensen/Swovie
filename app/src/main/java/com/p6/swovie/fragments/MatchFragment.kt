package com.p6.swovie.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.p6.swovie.R
import java.util.*


class MatchFragment : Fragment(), View.OnClickListener {

    private val TAG = "MatchFragment"

    private lateinit var buttonCreate: Button
    private lateinit var buttonJoin: Button
    private lateinit var buttonViewMembers: Button
    private lateinit var buttonLeave: Button
    private lateinit var editTextCode: EditText
    private lateinit var uid: String
    private var inGroup = false
    private var isInGroup = false
    var auth: FirebaseAuth = Firebase.auth
    val db = Firebase.firestore
    private lateinit var group: HashMap<String, String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_match, container, false)

        //Components from fragment_match layout
        buttonCreate = root.findViewById(R.id.button_create_group)
        buttonJoin = root.findViewById(R.id.button_join_group)
        editTextCode = root.findViewById(R.id.editText_groupcode)


        //Click listeners, makes onClick methods possible
        buttonCreate.setOnClickListener(this)
        buttonJoin.setOnClickListener(this)

        return root
    }

    override fun onClick(view: View?) { // All OnClick for the buttons in this Fragment
        when (view) {
            buttonCreate -> {
                Toast.makeText(activity, "Create", Toast.LENGTH_SHORT).show()
                refreshFragment()
            }
            buttonJoin -> joinGroup(editTextCode.text.toString())
        }
    }

    private fun refreshFragment() {
        //TODO
    }

    private fun joinGroup(text: String) {
        Log.i(TAG, "hi")
        if (text.isEmpty()) {
            inputAgain(editTextCode, "Please put in a group code")
        } else {
            toast("Group joined")
        }
    }

    /*private fun createGroup(){

        group = hashMapOf<String, String>(
            "users" to user
        )

        uid = auth.currentUser!!.uid
        db.collection("users")
            .document(uid).set(user)
            .addOnSuccessListener {
                Log.d("RegisterActivity: ", "DocumentSnapshot added with ID: $uid")
                auth.signOut()
            }

    }*/

    private fun inputAgain(editText: EditText, toast: String) {
        editText.requestFocus()
        val imm: InputMethodManager =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
        toast(toast)
    }

    private fun toast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }
}