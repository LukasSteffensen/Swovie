package com.p6.swovie.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.p6.swovie.*
import com.p6.swovie.dataClasses.Movie
import okhttp3.OkHttpClient


class MovieFragment : Fragment(), View.OnClickListener {

    private val TAG = "MovieFragment"

    private var isInGroup = false
    private lateinit var imageViewMovie: ImageView
    private lateinit var textViewTitle: TextView
    private lateinit var matchFragment: Fragment
    private lateinit var secondMatchFragment: Fragment
    private val client = OkHttpClient()
    private val superLike = 0
    private val like = 1
    private val notToday = 2
    private val never = 3

    private var auth: FirebaseAuth = Firebase.auth
    private val db = Firebase.firestore

    private lateinit var buttonNever: ImageButton
    private lateinit var buttonNotToday: ImageButton
    private lateinit var buttonLike: ImageButton
    private lateinit var buttonSuperLike: ImageButton
    private lateinit var buttonFilter: Button
    private lateinit var buttonMatches: Button
    //val genres = ArrayList<String>()
    private val JSON_URL_IMAGE = "https://image.tmdb.org/t/p/original/z8onk7LV9Mmw6zKz4hT6pzzvmvl.jpg"
    private val JSON_URL = "https://api.themoviedb.org/3/movie/22?api_key=9870f62e69820872d263749cf1055bc1"
    private val JSON_URL_POPULAR = "https://api.themoviedb.org/3/movie/popular?api_key=9870f62e69820872d263749cf1055bc1"




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_movie, container, false)

        if(auth.currentUser != null) {
            db.collection("rooms").whereArrayContains("users", auth.currentUser.uid).get()
                .addOnSuccessListener { document ->
                    isInGroup = !document.isEmpty
                    Log.i(TAG, "isInGroup is True")
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                }
        }

        // Find all components in the Fragment
        imageViewMovie = root.findViewById(R.id.imageView_movie)
        textViewTitle = root.findViewById(R.id.textView_title)
        buttonNever = root.findViewById(R.id.imageView_never)
        buttonNotToday = root.findViewById(R.id.imageView_not_today)
        buttonLike = root.findViewById(R.id.imageView_like)
        buttonSuperLike = root.findViewById(R.id.imageView_super_like)
        buttonFilter = root.findViewById(R.id.button_filter)
        buttonMatches = root.findViewById(R.id.button_matches)

        // Make onClick possible for all of the buttons
        buttonLike.setOnClickListener(this)
        buttonSuperLike.setOnClickListener(this)
        buttonNotToday.setOnClickListener(this)
        buttonNever.setOnClickListener(this)
        buttonFilter.setOnClickListener(this)
        buttonMatches.setOnClickListener(this)

        buttonMatches.isEnabled = true // If user in group, Matches button will be enabled

        matchFragment = MatchFragment()
        secondMatchFragment = SecondMatchFragment()

        MoviesRepository.getPopularMovies(
                onSuccess = ::onPopularMoviesFetched,
                onError = ::onError
        )

        Glide.with(this) // Using Glide to set poster in the app
                .load(JSON_URL_IMAGE)
                .into(imageViewMovie)
        return root

    }

    override fun onClick(view: View?) { // All OnClick for the buttons in this Fragment
        when (view) {
            buttonSuperLike -> swipe(superLike)
            buttonLike -> swipe(like)
            buttonNotToday -> swipe(notToday)
            buttonNever -> swipe(never)
            buttonFilter -> changeToFilters()
            buttonMatches -> if (isInGroup) {
                replaceFragment(secondMatchFragment)
            } else {
                replaceFragment(matchFragment)
            }



        }
    }

    private fun changeToFilters(){
        val intent = Intent (activity, FilterDialogFragment::class.java)
        startActivity(intent)
    }

    private fun onPopularMoviesFetched(movies: List<Movie>) {
        Log.d("MovieFragment", "Movies: $movies")
    }

    private fun onError() {
        Toast.makeText(activity, "Error fetching movies", Toast.LENGTH_SHORT).show()
    }

    private fun populateDetails(extras: Bundle) {
        extras.getString(MOVIE_POSTER)?.let { posterPath ->
            Glide.with(this)
                .load("https://image.tmdb.org/t/p/w342$posterPath")
                .transform(CenterCrop())
                .into(imageViewMovie)
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        activity?.supportFragmentManager?.beginTransaction()?.apply {
            replace(R.id.fl_wrapper, fragment)
            commit()
        }
    }

    private fun toast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    private fun swipe(swipe: Int){
        when(swipe){
            superLike -> toast("Super like")
            like -> toast("Like")
            notToday -> toast("Not today")
            never -> toast("Never")
        }
    }

    private fun showNextMovie() {

    }
}
