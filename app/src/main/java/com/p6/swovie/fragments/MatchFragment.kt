package com.p6.swovie.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.p6.swovie.R
import java.util.HashMap

class MatchFragment : Fragment(), View.OnClickListener {

    private lateinit var buttonCreate: Button
    private lateinit var buttonJoin: Button
    private lateinit var editTextCode: EditText
    private lateinit var uid: String
    private var isInGroup: Boolean = false
    var auth: FirebaseAuth = Firebase.auth
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_match, container, false)
        val root2 = inflater.inflate(R.layout.fragment_match2, container, false)

        buttonCreate = root.findViewById(R.id.button_create_group)
        buttonJoin = root.findViewById(R.id.button_join_group)
        editTextCode = root.findViewById(R.id.editText_groupcode)

        isInGroup() // Code in here for when user is in a group
        if (isInGroup) {

            //Make adapter n shit sometime



            return root2
        }




        return root
    }

    override fun onClick(view: View?) { // All OnClick for the buttons in this Fragment
        when (view) {
            buttonCreate -> createGroup()
            buttonJoin -> joinGroup(editTextCode.text)
        }
    }

    private fun joinGroup(text: Editable) {
        if (text.isEmpty()) {
            toast("Please put in a group code")
            //inputAgain(editTextCode, "Please put in a group code")
        } else {
            toast("Group joined")
        }
    }

    private fun createGroup(){

        val group = hashMapOf(
                "name" to "Los Angeles",
                "state" to "CA",
                "country" to "USA"
        )

        db.collection("rooms")
            .add(group)
            .addOnSuccessListener { documentReference ->
                Log.d("MatchFragment", "DocumentSnapshot written with ID: ${documentReference.id}")
                toast("works")
            }
            .addOnFailureListener { e ->
                Log.w("MatchFragment", "Error adding document", e)
                toast("not works")
            }
    }

    /*private fun inputAgain(editText: EditText, toast: String) {
        editText.requestFocus()
        val imm: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
        toast(toast)
    }*/

    private fun toast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    private fun isInGroup() {
        val user = Firebase.auth.currentUser
        db.collection("rooms")
            .whereEqualTo("user", user)
            .get()
            .addOnSuccessListener { documents ->
                isInGroup = true
            }
            .addOnFailureListener { exception ->
                isInGroup = false
            }
    }

}