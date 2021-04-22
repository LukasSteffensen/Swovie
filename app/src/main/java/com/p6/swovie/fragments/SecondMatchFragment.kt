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


class SecondMatchFragment : Fragment(), View.OnClickListener {

    private var TAG = "SecondMatchFragment"

    private lateinit var buttonCreate: Button
    private lateinit var matchFragment: Fragment

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
        val root = inflater.inflate(R.layout.fragment_match2, container, false)
        //Components from fragment_match2 layout
        buttonViewMembers = root.findViewById(R.id.button_view_members)
        buttonLeave = root.findViewById(R.id.button_leave_group)

        //Click listeners, makes onClick methods possible
        buttonViewMembers.setOnClickListener(this)
        buttonLeave.setOnClickListener(this)

        //get group code
//        db.collection("rooms").whereArrayContains("users", auth.currentUser.uid).get()
//            .addOnSuccessListener { document ->
//                document.toString()
//            }
//            .addOnFailureListener { exception ->
//                Log.d(TAG, "get failed with ", exception)
//            }

        return root
    }

    override fun onClick(view: View?) { // All OnClick for the buttons in this Fragment
        when (view) {
            buttonViewMembers -> Toast.makeText(activity, "ViewMembers", Toast.LENGTH_SHORT).show()
            buttonLeave -> {

                //trying to delete user from group
//                val docRef = db.collection("rooms").document("BJ")
//                val updates = hashMapOf<String, Any>(
//                    "capital" to FieldValue.delete()
//                )
//
//                docRef.update(updates).addOnCompleteListener {
                    matchFragment = MatchFragment()
                    replaceFragment(matchFragment)
//                }
                //TODO Delete user's swipes from the group in firestore
            }
        }
    }

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

    private fun replaceFragment(fragment: Fragment) {
        activity?.supportFragmentManager?.beginTransaction()?.apply {
            replace(R.id.fl_wrapper, fragment)
            commit()
        }
    }
}