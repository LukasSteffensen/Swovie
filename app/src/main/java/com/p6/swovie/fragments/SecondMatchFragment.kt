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
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.p6.swovie.MatchAdapter
import com.p6.swovie.R
import java.util.*
import kotlin.collections.ArrayList


class SecondMatchFragment : Fragment(), View.OnClickListener {

    private var TAG = "SecondMatchFragment"

    private lateinit var matchFragment: Fragment

    private lateinit var buttonViewMembers: Button
    private lateinit var buttonLeave: Button
    private lateinit var textViewGroup: TextView
    private lateinit var uid: String
    private lateinit var movieId: String
    private lateinit var groupCode: String
    private lateinit var adapter: MatchAdapter
    private lateinit var matchRecyclerView: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    var auth: FirebaseAuth = Firebase.auth
    val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_match2, container, false)
        //Components from fragment_match2 layout
        buttonViewMembers = root.findViewById(R.id.button_view_members)
        buttonLeave = root.findViewById(R.id.button_leave_group)
        textViewGroup = root.findViewById(R.id.textView_current_group_code)
        matchRecyclerView = root.findViewById(R.id.recyclerView_matches)

        //Click listeners, makes onClick methods possible
        buttonViewMembers.setOnClickListener(this)
        buttonLeave.setOnClickListener(this)

        val groupTest = ArrayList<String>()
        groupTest.add("Avengers")
        groupTest.add("Terminator")
        groupTest.add("Silence of the lambs")
        groupTest.add("Toy Story")
        groupTest.add("Glib jocks quiz nymph to vex dwarf")

        //Making the recyclerview adapter thing
        linearLayoutManager = LinearLayoutManager(context)
        matchRecyclerView.layoutManager = linearLayoutManager
        adapter = MatchAdapter(groupTest)
        matchRecyclerView.adapter = adapter

        //initialize uid
        uid = auth.currentUser.uid

        //get group code
        db.collection("rooms").whereArrayContains("users", auth.currentUser.uid).get()
            .addOnSuccessListener { document ->
                groupCode = document.documents[0].id
                Log.i(TAG, "group code: $groupCode")
                textViewGroup.text = "Group code: $groupCode"
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
        return root
    }

    override fun onClick(view: View?) { // All OnClick for the buttons in this Fragment
        when (view) {
            buttonViewMembers -> Toast.makeText(activity, "ViewMembers", Toast.LENGTH_SHORT).show()
            buttonLeave -> {
                matchFragment = MatchFragment()
                val docRef = db.collection("rooms").document(groupCode)
                docRef.get()
                    .addOnSuccessListener { document ->
                        var array: ArrayList<String> = document.get("users") as ArrayList<String>
                        if (array.size == 1) {
                            //Delete group if you are the last group member
                            docRef.delete()
                                .addOnSuccessListener {
                                    replaceFragment(matchFragment)
                                    Log.d(TAG, "DocumentSnapshot successfully deleted!") }
                                .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
                        } else {
                            // remove user from group
                            val updates = hashMapOf<String, Any>(
                                "users" to FieldValue.arrayRemove(uid)
                            )
                            docRef.update(updates).addOnCompleteListener {
                            }
                                .addOnFailureListener { exception ->
                                    Log.d(TAG, "get failed with ", exception)
                                }
                            replaceFragment(matchFragment)
                        }
                    }
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