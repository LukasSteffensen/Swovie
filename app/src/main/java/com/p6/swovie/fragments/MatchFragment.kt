package com.p6.swovie.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
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


class MatchFragment : Fragment(), View.OnClickListener {

    private val TAG = "MatchFragment"

    private lateinit var secondMatchFragment: Fragment

    private lateinit var buttonCreate: Button
    private lateinit var buttonJoin: Button
    private lateinit var buttonViewMembers: Button
    private lateinit var buttonLeave: Button
    private lateinit var editTextCode1: EditText
    private lateinit var editTextCode2: EditText
    private lateinit var editTextCode3: EditText
    private lateinit var editTextCode4: EditText

    private lateinit var uid: String
    private var groupCode: String = ""
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
        buttonJoin.isClickable = false
        editTextCode1 = root.findViewById(R.id.edit)
        editTextCode2 = root.findViewById(R.id.edit1)
        editTextCode3 = root.findViewById(R.id.edit2)
        editTextCode4 = root.findViewById(R.id.edit3)

        secondMatchFragment = SecondMatchFragment()

        //initialize uid
        uid = auth.currentUser.uid

        //Click listeners, makes onClick methods possible
        buttonCreate.setOnClickListener(this)
        buttonJoin.setOnClickListener(this)

        addListenerEdit(editTextCode1, editTextCode2, editTextCode1)
        addListenerEdit(editTextCode2, editTextCode3, editTextCode1)
        addListenerEdit(editTextCode3, editTextCode4, editTextCode2)
        editTextCode4.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == 67) {
                openSoftKeyboard(editTextCode3)
            }
            false
        }
        editTextCode4.addTextChangedListener {
            if (!editTextCode1.text.isNullOrEmpty() && !editTextCode2.text.isNullOrEmpty() && !editTextCode3.text.isNullOrEmpty() && !editTextCode4.text.isNullOrEmpty()) {
                val sb = StringBuilder()
                sb.append(editTextCode1.text.toString())
                    .append(editTextCode2.text.toString())
                    .append(editTextCode3.text.toString())
                    .append(editTextCode4.text.toString())
                Log.i(TAG, groupCode)
                groupCode = sb.toString()
                buttonJoin.isClickable = true
                joinGroup(groupCode)
            } else if (editTextCode4.text.isEmpty()) {
                openSoftKeyboard(editTextCode3)
            } else {
                groupCode = ""
            }
        }
        return root

    }

    private fun addListenerEdit(editText: EditText, editText2: EditText, editTextPrev: EditText) {
        editText.addTextChangedListener {
            if (editText.text.length == 1) {
                openSoftKeyboard(editText2)
            } else if (editText.text.isEmpty()) {
                openSoftKeyboard(editTextPrev)
            }
        }
        editText.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == 67) {
                openSoftKeyboard(editTextPrev)
            }
            false
        }
    }



    override fun onClick(view: View?) { // All OnClick for the buttons in this Fragment
        when (view) {
            buttonCreate -> {
                createGroup()
                replaceFragment(secondMatchFragment)
            }
            buttonJoin -> joinGroup(groupCode)
        }
    }

    private fun openSoftKeyboard(editText: EditText) {
        editText.requestFocus()
        val inputMethodManager =
            context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun joinGroup(code: String) {
        if (code.isEmpty()) {
            //      inputAgain(editTextCode, "Please put in a group code")
        } else {
            val docRef = db.collection("rooms").document(code)
            val updates = hashMapOf<String, Any>(
                "users" to FieldValue.arrayUnion(uid)
            )
            docRef.update(updates).addOnSuccessListener {
                replaceFragment(secondMatchFragment)
            }.addOnFailureListener {
                toast("Group $code does not exist")
                editTextCode1.text.clear()
                editTextCode2.text.clear()
                editTextCode3.text.clear()
                editTextCode4.text.clear()
                openSoftKeyboard(editTextCode1)
            }
        }
    }

    private fun createGroup() {

        val userIdList: ArrayList<String> = ArrayList()

        val groupCode = generateGroupId()

        userIdList.add(uid)

        val group = Group(userIdList)

        db.collection("rooms")
            .document(groupCode).set(group)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot added with ID: $groupCode")
            }

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