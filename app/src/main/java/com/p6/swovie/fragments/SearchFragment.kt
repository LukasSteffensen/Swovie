package com.p6.swovie.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.p6.swovie.*
import com.p6.swovie.dataClasses.Movie

class SearchFragment : Fragment() {

    private val TAG = "SearchFragment"

    private lateinit var buttonSearch: Button
    private lateinit var searchView: SearchView
    private lateinit var moviesRecyclerView: RecyclerView
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var popularMoviesAdapter: MoviesAdapter
    private lateinit var popularMoviesLayoutMgr: LinearLayoutManager
    private var popularMoviesPage = 1
    private var searchedMoviePage = 1
    private lateinit var searchQuery: String
    private var isSearchScroll = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_search, container, false)

        searchView = root.findViewById(R.id.searchView)

        //Showing popular movies in RecyclerView (scrollable, vertical)
        moviesRecyclerView = root.findViewById(R.id.recyclerView_movies)
        gridLayoutManager = GridLayoutManager(context, 3, LinearLayoutManager.VERTICAL, false)
        popularMoviesLayoutMgr = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        moviesRecyclerView.layoutManager = gridLayoutManager

        popularMoviesAdapter = MoviesAdapter(mutableListOf()) { movie -> showMovieDetails(movie) }
        moviesRecyclerView.adapter = popularMoviesAdapter

        getPopularMovies()

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchQuery = query.toString()
                getSearchedMovies(searchQuery, searchedMoviePage)
                hideKeyboard()
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                //getSearchedMovies(query.toString(), searchedMoviePage)
                return true
            }
        })

        return root
    }

    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun getSearchedMovies(query: String, page: Int) {
        MoviesRepository.getSearchedMovies(
            query,
            page,
            onSuccess = ::onSearchedMoviesFetched,
            onError = ::onSearchError
        )
    }

    private fun onSearchedMoviesFetched(movies: List<Movie>) {
        Log.d(TAG, "Movies: $movies")
        Log.i(TAG, "Number of movies ${movies.size}")
        if(movies.isNotEmpty()) {
            if (isSearchScroll){
                popularMoviesAdapter.appendMovies(movies)
            } else {
                popularMoviesAdapter.setList(movies as MutableList<Movie>)
            }
                attachSearchedMoviesOnScrollListener(searchQuery)
                popularMoviesAdapter.notifyDataSetChanged()
        }
    }

    private fun onSearchError() { //Used in getPopularMovies
        Log.i(TAG, "Error fetching searched movies")
    }


    private fun attachPopularMoviesOnScrollListener() { //Basically updates when scrolling, showing movies as you scroll
        moviesRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val totalItemCount =
                gridLayoutManager.itemCount //the total number of movies inside our popularMoviesAdapter. This will keep increasing the more we call popularMoviesAdapter.appendMovies().
            val visibleItemCount =
                gridLayoutManager.childCount //the current number of child views attached to the RecyclerView that are currently being recycled over and over again. The value of this variable for common screen sizes will range roughly around 4-5 which are 3 visible views, +1 left view that’s not seen yet and +1 right view that’s not seen yet also. The value will be higher if you have a bigger screen.
            val firstVisibleItem =
                gridLayoutManager.findFirstVisibleItemPosition() // is the position of the leftmost visible item in our list.

            if (firstVisibleItem + visibleItemCount >= totalItemCount / 2 && !isSearchScroll) {
                moviesRecyclerView.removeOnScrollListener(this)
                popularMoviesPage++
                getPopularMovies()
            }
        }
        })
    }

    private fun attachSearchedMoviesOnScrollListener(query: String) { //Basically updates when scrolling, showing movies as you scroll
        moviesRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val totalItemCount =
                    gridLayoutManager.itemCount //the total number of movies inside our popularMoviesAdapter. This will keep increasing the more we call popularMoviesAdapter.appendMovies().
                val visibleItemCount =
                    gridLayoutManager.childCount //the current number of child views attached to the RecyclerView that are currently being recycled over and over again. The value of this variable for common screen sizes will range roughly around 4-5 which are 3 visible views, +1 left view that’s not seen yet and +1 right view that’s not seen yet also. The value will be higher if you have a bigger screen.
                val firstVisibleItem =
                    gridLayoutManager.findFirstVisibleItemPosition() // is the position of the leftmost visible item in our list.

                if (firstVisibleItem + visibleItemCount >= totalItemCount / 2) {
                    moviesRecyclerView.removeOnScrollListener(this)
                    searchedMoviePage++
                    isSearchScroll = true
                    getSearchedMovies(searchQuery, searchedMoviePage)
                }
            }
        })
    }

    private fun showMovieDetails(movie: Movie) { // Put information from the clicked movie to the Detailed activity
        val intent = Intent(activity, MovieDetailsActivity::class.java)
        intent.putExtra(MOVIE_POSTER, movie.posterPath)
        intent.putExtra(MOVIE_TITLE, movie.title)
        intent.putExtra(MOVIE_OVERVIEW, movie.overview)
        startActivity(intent)
    }

    private fun getPopularMovies() { // Fetching data from JSON
        MoviesRepository.getPopularMovies(
            popularMoviesPage,
            onSuccess = ::onPopularMoviesFetched,
            onError = ::onError
        )
    }

    private fun onPopularMoviesFetched(movies: List<Movie>) { //Used in getPopularMovies. Fetch data if success
        Log.d(TAG, "Popular movies: $movies")
        Log.i(TAG, "Number of movies: ${movies.size}")
        popularMoviesAdapter.appendMovies(movies)
        attachPopularMoviesOnScrollListener()
        popularMoviesAdapter.notifyDataSetChanged()
    }

    private fun onError() { //Used in getPopularMovies
        Toast.makeText(activity, "Error fetching movies", Toast.LENGTH_SHORT).show()
    }

}