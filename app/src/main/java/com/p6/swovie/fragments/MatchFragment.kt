package com.p6.swovie.fragments

import android.content.Context
import android.os.Bundle
import android.text.TextUtils.replace
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.p6.swovie.R
import com.p6.swovie.dataClasses.Group
import com.p6.swovie.dataClasses.generateGroupId
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class MatchFragment : Fragment(), View.OnClickListener {

    private val TAG = "MatchFragment"

    private val secondMatchFragment = SecondMatchFragment()

    private lateinit var buttonCreate: Button
    private lateinit var buttonJoin: Button
    private lateinit var buttonViewMembers: Button
    private lateinit var buttonLeave: Button
    private lateinit var editTextCode: EditText
    private lateinit var uid: String
    private lateinit var groupCode: String
    private var inGroup = false
    private var isInGroup = false
    var auth: FirebaseAuth = Firebase.auth
    val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_match, container, false)

        //Components from fragment_match layout
        buttonCreate = root.findViewById(R.id.button_create_group)
        buttonJoin = root.findViewById(R.id.button_join_group)
        editTextCode = root.findViewById(R.id.editText_groupcode)


        //initialize uid
        uid = auth.currentUser.uid

        //Click listeners, makes onClick methods possible
        buttonCreate.setOnClickListener(this)
        buttonJoin.setOnClickListener(this)

        editTextCode.addTextChangedListener {
            if (editTextCode.text.length==4) {
                joinGroup(editTextCode.text.toString().toUpperCase())
            }
        }
        return root
    }

    override fun onClick(view: View?) { // All OnClick for the buttons in this Fragment
        when (view) {
            buttonCreate -> {
                createGroup()
                replaceFragment(secondMatchFragment)
            }
            buttonJoin -> joinGroup(editTextCode.text.toString().toUpperCase())
        }
    }

    private fun joinGroup(text: String) {
        if (text.isEmpty()) {
            inputAgain(editTextCode, "Please put in a group code")
        } else {
            groupCode = text
            val docRef = db.collection("rooms").document(groupCode)
            val updates = hashMapOf<String, Any>(
                "users" to FieldValue.arrayUnion(uid)
            )
            docRef.update(updates).addOnSuccessListener {
                replaceFragment(secondMatchFragment)
            }.addOnFailureListener {
                    inputAgain(editTextCode,"Incorrect group code")
                }
        }
    }

    private fun createGroup(){

        val userIdList: ArrayList<String> = ArrayList()

        val groupCode = generateGroupId()

        userIdList.add(uid)

        val group = Group(userIdList)

        db.collection("rooms")
            .document(groupCode).set(group)
            .addOnSuccessListener {
                Log.d("RegisterActivity: ", "DocumentSnapshot added with ID: $groupCode")
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
    // making hashmap of movie ID containing arrays of user IDs for each type of swipe
    //        val swipedMovieId = hashMapOf<String, List<String>>(
//            "userSuperLike" to emptyList<String>(),
//            "userLike" to emptyList<String>(),
//            "userNotToday" to emptyList<String>(),
//            "userNever" to emptyList<String>(),
//        )


}