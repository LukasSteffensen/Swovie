package com.p6.swovie.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.p6.swovie.*
import com.p6.swovie.R
import com.p6.swovie.dataClasses.Movie
import com.yuyakaido.android.cardstackview.*
import okhttp3.OkHttpClient


class MovieFragment : Fragment(), View.OnClickListener, CardStackListener {

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

    private lateinit var manager: CardStackLayoutManager
    private lateinit var adapter: CardStackAdapter

    private var auth: FirebaseAuth = Firebase.auth
    private val db = Firebase.firestore
    private lateinit var uid: String
    private lateinit var groupCode: String
    private lateinit var cardStackView: CardStackView

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


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_movie, container, false)

        //set isInGroup boolean
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

        //initialize uid
        uid = auth.currentUser.uid

        //get group code
        db.collection("rooms").whereArrayContains("users", auth.currentUser.uid).get()
            .addOnSuccessListener { document ->
                groupCode = document.documents[0].id
                Log.i(TAG, "group code: $groupCode")
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }

        cardStackView= root.findViewById(R.id.card_stack_view)
        manager = CardStackLayoutManager(context, this)
        manager.setStackFrom(StackFrom.None)
        manager.setVisibleCount(2)
        manager.setTranslationInterval(8.0f)
        manager.setScaleInterval(0.95f)
        manager.setSwipeThreshold(0.3f)
        manager.setMaxDegree(20.0f)
        manager.setDirections(Direction.FREEDOM)
        manager.setCanScrollHorizontal(true)
        manager.setSwipeableMethod(SwipeableMethod.Manual)
        val interpol = LinearInterpolator()
        manager.setOverlayInterpolator(interpol)

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
        adapter = CardStackAdapter(movies)
        cardStackView.layoutManager = manager
        cardStackView.adapter = adapter
        cardStackView.itemAnimator = DefaultItemAnimator()
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

    override fun onCardDragging(direction: Direction?, ratio: Float) {

    }

    override fun onCardSwiped(direction: Direction?) {
        if(direction == Direction.Right){
            swipe(like)
        } else if (direction == Direction.Left){
            swipe(notToday)
        } else if (direction == Direction.Top){
            swipe(superLike)
        } else if (direction == Direction.Bottom){
            swipe(never)
        }
    }

    override fun onCardRewound() {

    }

    override fun onCardCanceled() {

    }

    override fun onCardAppeared(view: View?, position: Int) {

    }

    override fun onCardDisappeared(view: View?, position: Int) {

    }

    private fun swipe(swipe: Int){
        when(swipe){
            superLike -> saveSwipeToDatabase(swipe)
            like -> saveSwipeToDatabase(swipe)
            notToday -> saveSwipeToDatabase(swipe)
            never -> saveSwipeToDatabase(swipe)
        }
    }

    private fun saveSwipeToDatabase(swipe: Int) {

        // making hashmap of movie ID containing arrays of user IDs for each type of swipe

        val swipedMovieId = hashMapOf<String, Any> (
            when(swipe) {
                superLike -> uid to "Super like"
                like -> uid to "Like"
                notToday -> uid to "Not today"
                never -> uid to "Never"
                else -> "" to ""
            }
        )

        db.collection("rooms")
            .document(groupCode).update(swipedMovieId)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot added with ID: $groupCode")
            }

    }
}
