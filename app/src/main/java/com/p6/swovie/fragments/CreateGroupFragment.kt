package com.p6.swovie.fragments

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
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
import com.p6.swovie.MainActivity
import com.p6.swovie.R
import com.p6.swovie.dataClasses.Group
import com.p6.swovie.dataClasses.generateGroupId
import kotlin.collections.ArrayList


class CreateGroupFragment : Fragment(), View.OnClickListener {

    private val TAG = "CreateGroupFragment"

    private lateinit var secondMatchFragment: Fragment

    private lateinit var buttonCreate: Button
    private lateinit var editTextCode1: EditText
    private lateinit var editTextCode2: EditText
    private lateinit var editTextCode3: EditText
    private lateinit var editTextCode4: EditText

    private lateinit var uid: String
    private var groupCode: String = ""
    var auth: FirebaseAuth = Firebase.auth
    val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_group, container, false)

        //Components from fragment_match layout
        buttonCreate = root.findViewById(R.id.button_create_group)
        editTextCode1 = root.findViewById(R.id.edit)
        editTextCode2 = root.findViewById(R.id.edit1)
        editTextCode3 = root.findViewById(R.id.edit2)
        editTextCode4 = root.findViewById(R.id.edit3)

        secondMatchFragment = MatchFragment()

        //initialize uid
        uid = auth.currentUser.uid

        //Click listeners, makes onClick methods possible
        buttonCreate.setOnClickListener(this)

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
                groupCode = sb.toString().toUpperCase()
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

    private fun deleteSharedPreferencesList() {
        val mPrefs: SharedPreferences =
            activity?.getSharedPreferences("savedMovieList", Context.MODE_PRIVATE)!!
        val prefsEditor = mPrefs.edit()
        prefsEditor.clear()
        prefsEditor.commit()
    }

    override fun onClick(view: View?) { // All OnClick for the buttons in this Fragment
        when (view) {
            buttonCreate -> {
                createGroup()
                replaceFragment(secondMatchFragment)
            }
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
            val docRef = db.collection("groups").document(code)
            val updates = hashMapOf<String, Any>(
                "users" to FieldValue.arrayUnion(uid)
            )
            docRef.update(updates).addOnSuccessListener {
                MainActivity.isInGroup = true
                MainActivity.groupCode = groupCode
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

        val generatedGroupCode = generateGroupId()

        MainActivity.groupCode = generatedGroupCode

        userIdList.add(uid)

        val group = Group(userIdList)

        db.collection("groups")
            .document(generatedGroupCode).set(group)
            .addOnSuccessListener {
                MainActivity.isInGroup = true
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