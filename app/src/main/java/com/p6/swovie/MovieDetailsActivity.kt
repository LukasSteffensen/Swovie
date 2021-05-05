package com.p6.swovie

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.google.common.reflect.TypeToken
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.p6.swovie.dataClasses.Movie
import java.lang.reflect.Type
import java.util.ArrayList

const val MOVIE_TITLE = "extra_movie_title"
const val MOVIE_ID = "extra_movie_id"
const val MOVIE_POSTER = "extra_movie_poster"
const val MOVIE_OVERVIEW = "extra_movie_overview"


class MovieDetailsActivity : AppCompatActivity() {

    private val TAG = "MovieDetailsActivity"

    private val db = Firebase.firestore
    private var auth: FirebaseAuth = Firebase.auth
    private lateinit var uid: String
    private lateinit var groupCode: String

    private val superLike = 0
    private val like = 1
    private val notToday = 2
    private val never = 3

    private lateinit var imageViewMovieDetails: ImageView
    private lateinit var textViewTitleDetails: TextView
    private lateinit var textViewDescriptionDetails: TextView

    private lateinit var swipedMoviesList: MutableList<Movie>
    private lateinit var movie: Movie

    private lateinit var buttonNever: ImageButton
    private lateinit var buttonNotToday: ImageButton
    private lateinit var buttonLike: ImageButton
    private lateinit var buttonSuperLike: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)

        imageViewMovieDetails = findViewById(R.id.imageView_movie_details)
        textViewTitleDetails = findViewById(R.id.textView_title_details)
        textViewDescriptionDetails = findViewById(R.id.textView_description_details)

        buttonNever = findViewById(R.id.imageView_never)
        buttonNotToday = findViewById(R.id.imageView_not_today)
        buttonLike = findViewById(R.id.imageView_like)
        buttonSuperLike = findViewById(R.id.imageView_super_like)

        val extras = intent.extras

        groupCode = MainActivity.groupCode

        buttonLike.setOnClickListener {
            saveSwipeToDatabase(like)
        }
        buttonSuperLike.setOnClickListener {
            saveSwipeToDatabase(superLike)
        }
        buttonNotToday.setOnClickListener {
            saveSwipeToDatabase(notToday)
        }
        buttonNever.setOnClickListener {
            saveSwipeToDatabase(never)
        }

        textViewDescriptionDetails.movementMethod = ScrollingMovementMethod()

        //initialize uid
        uid = auth.currentUser.uid

        swipedMoviesList = loadSharedPreferencesList(this)


        if (extras != null) {
            populateDetails(extras)
        } else {
            finish()
        }
    }

    private fun populateDetails(extras: Bundle) {
        extras.getString(MOVIE_POSTER)?.let { posterPath ->
            Glide.with(this)
                .load("https://image.tmdb.org/t/p/w342$posterPath")
                .transform(CenterCrop())
                .into(imageViewMovieDetails)
        }

        textViewTitleDetails.text = extras.getString(MOVIE_TITLE, "")
        textViewDescriptionDetails.text = extras.getString(MOVIE_OVERVIEW, "")

        movie = Movie(
            extras.getLong(MOVIE_ID),
            extras.getString(MOVIE_TITLE).toString(),
            extras.getString(MOVIE_OVERVIEW).toString(),
            extras.getString(MOVIE_POSTER).toString()
        )
    }

    private fun loadSharedPreferencesList(context: Context): MutableList<Movie> {
        val savedMovies: MutableList<Movie>
        val mPrefs = context.getSharedPreferences("savedMovieList", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = mPrefs.getString("myJson", "")
        savedMovies = if (json!!.isEmpty()) {
            ArrayList()
        } else {
            val type: Type = object : TypeToken<MutableList<Movie>?>() {}.type
            gson.fromJson(json, type)
        }
        return savedMovies
    }

    private fun saveSharedPreferencesList(context: Context, list: MutableList<Movie>) {
        val mPrefs: SharedPreferences =
            context.getSharedPreferences("savedMovieList", Context.MODE_PRIVATE)
        val prefsEditor = mPrefs.edit()
        val gson = Gson()
        val json = gson.toJson(list)
        Log.i(TAG, json.toString())
        prefsEditor.putString("myJson", json)
        prefsEditor.apply()
    }

    private fun saveSwipeToDatabase(swipe: Int) {

        swipedMoviesList.add(movie)
        saveSharedPreferencesList(this, swipedMoviesList)

        val updates = hashMapOf<String, Any>(
            when (swipe) {
                superLike -> "Super like" to FieldValue.arrayUnion(uid)
                like -> "Like" to FieldValue.arrayUnion(uid)
                notToday -> "Not today" to FieldValue.arrayUnion(uid)
                never -> "Never" to FieldValue.arrayUnion(uid)
                else -> "" to ""
            }
        )

        val docRef = db.collection("groups")
            .document(groupCode)
            .collection("swipes")
            .document(movie.id.toString())

        //get movie document
        docRef
            .get()
            .addOnSuccessListener { document ->
                // if document exists it will update the correct swipe array with user uid
                if (document.exists()) {
                    docRef
                        .update(updates)
                        .addOnSuccessListener {
                            Log.d(TAG, "DocumentSnapshot added with ID: $groupCode")
                        }
                } else {
                    // if document doesn't exist it will set document and the given array
                    // it will also create the subcollection if it is not there
                    docRef
                        .set(updates)
                        .addOnSuccessListener {
                            Log.d(TAG, "DocumentSnapshot added with ID: $groupCode")
                        }
                }
                Log.i(TAG, "group code: $groupCode")
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }
}
