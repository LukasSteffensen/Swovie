package com.p6.swovie.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.google.common.reflect.TypeToken
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.p6.swovie.*
import com.p6.swovie.R
import com.p6.swovie.dataClasses.Movie
import com.yuyakaido.android.cardstackview.*
import okhttp3.OkHttpClient
import java.lang.reflect.Type
import java.util.*


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
    private var movieList: MutableList<Movie> = arrayListOf()
    private lateinit var swipedMoviesList: MutableList<Movie>

    private lateinit var buttonNever: ImageButton
    private lateinit var buttonNotToday: ImageButton
    private lateinit var buttonLike: ImageButton
    private lateinit var buttonSuperLike: ImageButton
    private lateinit var buttonFilter: Button
    private lateinit var buttonMatches: Button
    private var popularMoviesPage: Int = 1
    private var hasSwipedBefore = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_movie, container, false)

        //initialize uid
        uid = auth.currentUser.uid

        //set isInGroup boolean
        if (auth.currentUser != null) {
            db.collection("groups").whereArrayContains("users", uid).get()
                .addOnSuccessListener { document ->
                    isInGroup = !document.isEmpty
                    Log.i(TAG, "isInGroup is True")
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                }
        }


        //get group code
        db.collection("groups").whereArrayContains("users", uid).get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    groupCode = document.documents[0].id
                    Log.i(TAG, "group code: $groupCode")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }

        cardStackView = root.findViewById(R.id.card_stack_view)
        manager = CardStackLayoutManager(context, this)
        manager.setStackFrom(StackFrom.None)
        manager.setVisibleCount(5)
        manager.setTranslationInterval(8.0f)
        manager.setScaleInterval(0.95f)
        manager.setSwipeThreshold(0.3f)
        manager.setMaxDegree(20.0f)
        manager.setDirections(Direction.FREEDOM)
        manager.setCanScrollHorizontal(true)
        manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
        manager.setOverlayInterpolator(LinearInterpolator())

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

        matchFragment = CreateGroupFragment()
        secondMatchFragment = MatchFragment()

        adapter = CardStackAdapter(movieList)
        cardStackView.layoutManager = manager
        cardStackView.adapter = adapter
        cardStackView.itemAnimator = DefaultItemAnimator()

        swipedMoviesList = loadSharedPreferencesList(requireContext())
        Log.i(TAG, "Movies that have been swiped before $swipedMoviesList")
        hasSwipedBefore = !swipedMoviesList.isNullOrEmpty()

        return root
    }

    override fun onStart() {
        loadMoreMovies()
        super.onStart()
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

    override fun onClick(view: View?) { // All OnClick for the buttons in this Fragment
        when (view) {
            buttonSuperLike -> {
                val setting = SwipeAnimationSetting.Builder()
                    .setDirection(Direction.Top)
                    .setDuration(Duration.Normal.duration)
                    .setInterpolator(AccelerateInterpolator())
                    .build()
                manager.setSwipeAnimationSetting(setting)
                cardStackView.swipe()
            }
            buttonLike -> {
                val setting = SwipeAnimationSetting.Builder()
                    .setDirection(Direction.Right)
                    .setDuration(Duration.Normal.duration)
                    .setInterpolator(AccelerateInterpolator())
                    .build()
                manager.setSwipeAnimationSetting(setting)
                cardStackView.swipe()
            }
            buttonNotToday -> {
                val setting = SwipeAnimationSetting.Builder()
                    .setDirection(Direction.Left)
                    .setDuration(Duration.Normal.duration)
                    .setInterpolator(AccelerateInterpolator())
                    .build()
                manager.setSwipeAnimationSetting(setting)
                cardStackView.swipe()
            }
            buttonNever -> {
                val setting = SwipeAnimationSetting.Builder()
                    .setDirection(Direction.Bottom)
                    .setDuration(Duration.Normal.duration)
                    .setInterpolator(AccelerateInterpolator())
                    .build()
                manager.setSwipeAnimationSetting(setting)
                cardStackView.swipe()
            }
            buttonFilter -> changeToFilters()
            buttonMatches -> if (isInGroup) {
                replaceFragment(secondMatchFragment)
            } else {
                replaceFragment(matchFragment)
            }
        }
    }

    private fun changeToFilters() {
        val intent = Intent(activity, FilterDialogFragment::class.java)
        startActivity(intent)
    }

    private fun onPopularMoviesFetched(movies: List<Movie>) {
        Log.d("MovieFragment", "Movies: $movies")
        if (!hasSwipedBefore) {
            movieList = movies as MutableList<Movie>
            movieList.addAll(movies)
            adapter = CardStackAdapter(movies)
            cardStackView.layoutManager = manager
            cardStackView.adapter = adapter
            cardStackView.itemAnimator = DefaultItemAnimator()
            Log.i(TAG, "should only be called once")
        } else {
            movieList.addAll(movies)
            updateAdapter(movieList)
        }
        if (swipedMoviesList.size > movieList.size) {
            loadMoreMovies()
        } else {
            movieList.removeAll(swipedMoviesList)
            Log.i(TAG, "Removed elements")
            Log.i(TAG, movieList.toString())
            adapter.setList(movieList)
            adapter.notifyDataSetChanged()
        }

        popularMoviesPage++
    }

    private fun updateAdapter(list: MutableList<Movie>){
        adapter.setList(list)
        val previousPosition = manager.topPosition
        adapter.notifyDataSetChanged()
        manager.topPosition = previousPosition
    }


    private fun loadMoreMovies() {
        MoviesRepository.getPopularMovies(
            popularMoviesPage,
            onSuccess = ::onPopularMoviesFetched,
            onError = ::onError
        )
    }

    private fun onError() {
        toast("Error fetching movies")
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

    override fun onPause() {
        popularMoviesPage = 1
        super.onPause()
    }

    private fun toast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {

    }

    override fun onCardSwiped(direction: Direction?) {
        when (direction) {
            Direction.Right -> {
                saveSwipeToDatabase(like)
            }
            Direction.Left -> {
                saveSwipeToDatabase(notToday)
            }
            Direction.Top -> {
                saveSwipeToDatabase(superLike)
            }
            Direction.Bottom -> {
                saveSwipeToDatabase(never)
            }
        }
        Log.i(
            TAG,
            "\nMovies in adapter: ${adapter.itemCount}\n Movies left in manager: ${manager.topPosition}"
        )
        if (manager.topPosition == adapter.itemCount - 5) {
            loadMoreMovies()
            Log.i(TAG, "Loading more movies")
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

    private fun saveSwipeToDatabase(swipe: Int) {

        var movieId: Long = 0

        swipedMoviesList.add(movieList[manager.topPosition-1])
        saveSharedPreferencesList(requireContext(), swipedMoviesList)

        movieId = movieList[manager.topPosition - 1].id
        Log.i(
            TAG, "movie id: ${movieList[manager.topPosition - 1].id}" +
                    " movie title: ${movieList[manager.topPosition - 1].title}"
        )


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
            .document(movieId.toString())

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
