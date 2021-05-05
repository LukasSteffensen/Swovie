package com.p6.swovie.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.p6.swovie.MainActivity
import com.p6.swovie.MatchAdapter
import com.p6.swovie.MoviesRepository
import com.p6.swovie.R
import com.p6.swovie.dataClasses.Match
import com.p6.swovie.dataClasses.Movie
import kotlin.collections.ArrayList


class MatchFragment : Fragment(), View.OnClickListener, MatchAdapter.OnClickListener {

    private var TAG = "MatchFragment"

    private lateinit var buttonViewMembers: Button
    private lateinit var buttonLeave: Button
    private lateinit var textViewGroup: TextView
    private lateinit var textViewNoMatches: TextView

    private lateinit var uid: String
    private lateinit var movieId: String
    private lateinit var movie: Movie
    private var colSize = 0
    private var matchPercentage: Double = 0.0
    private lateinit var groupCode: String
    private var groupSize: Int = 0

    private var matchArrayList: ArrayList<Match> = arrayListOf()
    private lateinit var adapter: MatchAdapter
    private lateinit var matchRecyclerView: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var progressBar: ProgressBar

    var auth: FirebaseAuth = Firebase.auth
    val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_match, container, false)

        //Initialize uid
        uid = auth.currentUser.uid

        progressBar = root.findViewById(R.id.progress_bar)

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

    override fun onResume() {
        matchArrayList = arrayListOf()
        super.onResume()
    }

    override fun onViewSwipesClick(match: Match) {
        viewSwipes(match)
    }

    private fun getSwipes() {

        val colRef = db.collection("groups")
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
                    colSize = result.size()
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

                        val superLikesDouble = superLikes.size.toDouble()
                        val likesDouble = likes.size.toDouble()
                        val notTodayDouble = notTodays.size
                        val neverDouble = nevers.size.toDouble()

                        var tempGroupSize = groupSize + superLikesDouble.toInt() + neverDouble.toInt()
                        matchPercentage = (2*superLikesDouble+likesDouble)*100/tempGroupSize

                        val match = Match(movieId,"",matchPercentage, "")
                        matchArrayList.add(match)

                        //Sort of bad previous solution to calculating match percentage (could go under 0 and over 100)
//                        var matchPercentage = (superLikesDouble+superLikesDouble/groupSize+likesDouble-neverDouble)*100/groupSize
//                        if (matchPercentage < 0) {
//                            matchPercentage = 0.0
//                        } else if (matchPercentage > 100) {
//                            matchPercentage = 100.0
//                        }

                        Log.i(TAG, "MovieID: $movieId")
                        Log.i(TAG, "Super: $superLikesDouble")
                        Log.i(TAG, "Like: $likesDouble")
                        Log.i(TAG, "Not: $notTodayDouble")
                        Log.i(TAG, "Never: $neverDouble")
                        Log.i(TAG, "match percentage: $matchPercentage")
                    }
                    setMovieTitles()
                }
            }.addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

    private fun setMovieTitles() {
        for (match in matchArrayList){
            getMovieFromId(match.movieId!!.toInt())
        }
    }

    private fun viewMembers() {

        val docRef = db.collection("groups").document(groupCode)
        docRef.get()
            .addOnCompleteListener { task ->

                val document = task.result
                val userIds: ArrayList<String> = document?.get("users") as ArrayList<String>
                val users = Array(userIds.size){""}
                Log.i(TAG, userIds.toString())

                var n = 0
                for (userId in userIds) {
                    db.collection("users").document(userId)
                        .get()
                        .addOnCompleteListener { task2 ->
                            val document2 = task2.result
                            users[n] = document2?.data!!["name"].toString()
                            n++
                            if (n == userIds.size) {
                                alertDialog(users)
                            }
                        }
                }
            }
    }

    private fun viewSwipes(match: Match) {

        movieId = match.movieId.toString()

        val docRef = db.collection("groups").document(groupCode).collection("swipes").document(movieId)
        docRef.get()
            .addOnCompleteListener { task ->

                val document = task.result

                var superlikes: ArrayList<String>
                var likes: ArrayList<String>
                var nottodays: ArrayList<String>
                var nevers: ArrayList<String>

                superlikes = if (document?.get("Super like") != null) {
                    document.get("Super like") as ArrayList<String>
                } else {
                    arrayListOf()
                }
                likes = if (document?.get("Like") != null) {
                    document.get("Like") as ArrayList<String>
                } else {
                    arrayListOf()
                }
                nottodays = if (document?.get("Not today") != null) {
                    document.get("Not today") as ArrayList<String>
                } else {
                    arrayListOf()
                }
                nevers = if (document?.get("Never") != null) {
                    document.get("Never") as ArrayList<String>
                } else {
                    arrayListOf()
                }


                //likes = document?.get("Like") as ArrayList<String>
                //superlikes = document.get("Super Like") as ArrayList<String>
                //nottodays = document.get("Not Today") as ArrayList<String>
                //nevers = document.get("Never") as ArrayList<String>

            val users = Array(likes.size) { "" }
            var n = 0
                for (user in likes) {
                    db.collection("users").document(user)
                        .get()
                        .addOnCompleteListener { task2 ->
                            val document2 = task2.result
                            users[n] = document2?.data!!["name"].toString()
                            n++
                            if (n == likes.size) {
                                swipesDialog(users)
                            }
                        }
                }
        }
    }

    private fun alertDialog(array: Array<String>){
        context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle("Group members")
                .setItems(array) { _, _ ->
                }
                .setNeutralButton("Close") { _, _ ->
                }
                .show()
        }
    }

    private fun swipesDialog(array: Array<String>){
        context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle(resources.getString(R.string.viewswipes))
                .setItems(array) { _, _ ->
                }
                .setNeutralButton("Close") { _, _ ->
                }
                .show()
        }
    }

    private fun leaveGroup() {

        buttonLeave.isClickable = false

        val docRef = db.collection("groups").document(groupCode)
        docRef.get()
            .addOnSuccessListener { document ->
                var array: ArrayList<String> = document.get("users") as ArrayList<String>
                if (array.size == 1) {
                    deleteSwipesAndSharedPref()
                    //Delete group if you are the last group member
                    docRef.delete()
                        .addOnSuccessListener {
                            replaceFragment(CreateGroupFragment())
                            Log.d(TAG, "DocumentSnapshot successfully deleted!")
                        }
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
                    deleteSwipesAndSharedPref()
                    replaceFragment(CreateGroupFragment())
                }
            }.addOnFailureListener { e ->
                Log.i(TAG, e.toString())
            }
    }

    private fun deleteSwipesAndSharedPref() {
        MainActivity.isInGroup = false
        deleteSharedPreferencesList(requireContext())
        deleteSwipesFromGroup()
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

    private fun deleteSharedPreferencesList(context: Context) {
        val mPrefs: SharedPreferences =
            context.getSharedPreferences("savedMovieList", Context.MODE_PRIVATE)
        val prefsEditor = mPrefs.edit()
        prefsEditor.clear()
        prefsEditor.commit()
    }

    private fun getGroupCode() {
        db.collection("groups").whereArrayContains("users", uid).get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    groupCode = document.documents[0].id
                    var groupArrayList: ArrayList<String> = arrayListOf<String>()
                    groupArrayList = document.documents[0].get("users") as ArrayList<String>
                    groupSize = groupArrayList.size
                    getSwipes()
                    Log.i(TAG, "group code: $groupCode")
                    textViewGroup.text = "Group code: $groupCode"
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
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



    override fun onClick(v: View?) {
        when (v) {
            buttonViewMembers -> viewMembers()
            buttonLeave -> leaveGroup()
        }
    }

    private fun getMovieFromId(movieId: Int) {
        MoviesRepository.getMovieDetails(movieId,
            onSuccess = ::onMovieFetched,
            onError = ::onError
        )
    }

    private fun onMovieFetched(movie: Movie) { //Used in getPopularMovies. Fetch data if success
        Log.d(TAG, "Movie: $movie")
        Log.i(TAG, "Movie: ${movie.id} and ${movie.title}")
        val index = matchArrayList.indexOfFirst{
            it.movieId == movie.id.toString()
        }
        matchArrayList[index].title = movie.title
        matchArrayList[index].posterPath = movie.posterPath
        colSize--
        if (colSize == 0) {
            progressBar.visibility = View.GONE
            setAdapter(matchArrayList)
        }
    }

    private fun setAdapter(matchArrayList: ArrayList<Match>) {
        var sortedList = matchArrayList.sortedWith(compareBy { it.matchPercentage }).reversed()

        //Making the recyclerview adapter thing
        linearLayoutManager = LinearLayoutManager(context)
        matchRecyclerView.layoutManager = linearLayoutManager
        adapter = MatchAdapter(sortedList as MutableList<Match>, this)
        matchRecyclerView.adapter = adapter
    }

    private fun onError() { //Used in getPopularMovies
        toast("Error fetching movie")
    }
}