package com.p6.swovie.fragments

import android.content.Context
import android.content.SharedPreferences
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.p6.swovie.MatchAdapter
import com.p6.swovie.MoviesRepository
import com.p6.swovie.R
import com.p6.swovie.dataClasses.Match
import com.p6.swovie.dataClasses.Movie
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class SecondMatchFragment : Fragment(), View.OnClickListener {

    private var TAG = "SecondMatchFragment"

    private lateinit var matchFragment: Fragment

    private lateinit var buttonViewMembers: Button
    private lateinit var buttonLeave: Button
    private lateinit var textViewGroup: TextView
    private lateinit var textViewNoMatches: TextView

    private lateinit var uid: String
    private lateinit var movieId: String
    private lateinit var groupCode: String
    private var groupSize: Int = 0

    private var matchArrayList: ArrayList<Match> = arrayListOf()
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
        textViewNoMatches = root.findViewById(R.id.textView_no_matches)
        matchRecyclerView = root.findViewById(R.id.recyclerView_matches)

        //Click listeners, makes onClick methods possible
        buttonViewMembers.setOnClickListener(this)
        buttonLeave.setOnClickListener(this)

        getGroupCode()

        return root
    }

    private fun getSwipes() {

        val colRef = db.collection("rooms")
            .document(groupCode)
            .collection("swipes")

        //get swipes collection
        colRef
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    textViewNoMatches.text = getString(R.string.nomatches)
                } else {
                    var superLikes: ArrayList<String>
                    var likes: ArrayList<String>
                    var notTodays: ArrayList<String>
                    var nevers: ArrayList<String>
                    for (document in result) {
                        movieId = document.id

                        superLikes = if (document.get("Super like") != null) {
                            document.get("Super like") as ArrayList<String>
                        } else {
                            arrayListOf()
                        }
                        likes = if (document.get("Like") != null) {
                            document.get("Like") as ArrayList<String>
                        } else {
                            arrayListOf()
                        }
                        notTodays = if (document.get("Not today") != null) {
                            document.get("Not today") as ArrayList<String>
                        } else {
                            arrayListOf()
                        }
                        nevers = if (document.get("Never") != null) {
                            document.get("Never") as ArrayList<String>
                        } else {
                            arrayListOf()
                        }

                        var superLikesDouble = superLikes.size.toDouble()
                        var likesDouble = likes.size.toDouble()
                        var notTodayDouble = notTodays.size
                        var neverDouble = nevers.size.toDouble()

                        var tempGroupSize = groupSize + superLikesDouble.toInt() + neverDouble.toInt()
                        var matchPercentage = (2*superLikesDouble+likesDouble)*100/tempGroupSize

                        //Sort of bad previous solution to calculating match percentage (could go under 0 and over 100)
//                        var matchPercentage = (superLikesDouble+superLikesDouble/groupSize+likesDouble-neverDouble)*100/groupSize
//                        if (matchPercentage < 0) {
//                            matchPercentage = 0.0
//                        } else if (matchPercentage > 100) {
//                            matchPercentage = 100.0
//                        }

                        //doesn't work yet
                        getMovieFromId()

                        // right now title is also movieId but should be title when that works
                        var match = Match(movieId,movieId,matchPercentage.toString())
                        matchArrayList.add(match)

                        Log.i(TAG, "MovieID: $movieId")
                        Log.i(TAG, "Super: $superLikesDouble")
                        Log.i(TAG, "Like: $likesDouble")
                        Log.i(TAG, "Not: $notTodayDouble")
                        Log.i(TAG, "Never: $neverDouble")
                        Log.i(TAG, "match percentage: $matchPercentage")
                    }


                    var sortedList = matchArrayList.sortedWith(compareBy { it.matchPercentage }).reversed()

                    //Making the recyclerview adapter thing
                    linearLayoutManager = LinearLayoutManager(context)
                    matchRecyclerView.layoutManager = linearLayoutManager
                    adapter = MatchAdapter(sortedList as MutableList<Match>)
                    matchRecyclerView.adapter = adapter
                }
            }.addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

    private fun leaveGroup() {

        matchFragment = MatchFragment()
        val docRef = db.collection("rooms").document(groupCode)
        docRef.get()
            .addOnSuccessListener { document ->
                var array: ArrayList<String> = document.get("users") as ArrayList<String>
                if (array.size == 1) {
                    //Delete group if you are the last group member
                    docRef.delete()
                        .addOnSuccessListener {
                            deleteSharedPreferencesList(requireContext())
                            replaceFragment(matchFragment)
                            Log.d(TAG, "DocumentSnapshot successfully deleted!")
                        }
                        .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
                } else {
                    deleteSharedPreferencesList(requireContext())
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
            }.addOnFailureListener { e ->
                Log.i(TAG, e.toString())
            }
        //TODO Delete user's swipes from the group in firestore
    }

    private fun deleteSharedPreferencesList(context: Context) {
        val mPrefs: SharedPreferences =
            context.getSharedPreferences("savedMovieList", Context.MODE_PRIVATE)
        val prefsEditor = mPrefs.edit()
        prefsEditor.clear()
        prefsEditor.commit()
    }

    private fun getGroupCode() {
        uid = auth.currentUser.uid
        db.collection("rooms").whereArrayContains("users", uid).get()
            .addOnSuccessListener { document ->
                groupCode = document.documents[0].id
                var groupArrayList: ArrayList<String> = arrayListOf<String>()
                groupArrayList = document.documents[0].get("users") as ArrayList<String>
                groupSize = groupArrayList.size
                getSwipes()
                Log.i(TAG, "group code: $groupCode")
                textViewGroup.text = "Group code: $groupCode"
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
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

    override fun onClick(v: View?) {
        when (v) {
            buttonViewMembers -> Toast.makeText(activity, "ViewMembers", Toast.LENGTH_SHORT).show()
            buttonLeave -> {
                leaveGroup()
            }
        }
    }

    private fun getMovieFromId() {
    }
}