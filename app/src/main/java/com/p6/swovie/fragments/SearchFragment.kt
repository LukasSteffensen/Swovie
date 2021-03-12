package com.p6.swovie.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.p6.swovie.*

class SearchFragment : Fragment() {

    private lateinit var buttonSearch: Button
    private lateinit var popularMovies: RecyclerView
    private lateinit var popularMoviesAdapter: MoviesAdapter
    private lateinit var popularMoviesLayoutMgr: LinearLayoutManager
    private var popularMoviesPage = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_search, container, false)

        //Showing popular movies in RecyclerView (scrollable, vertical)
        popularMovies = root.findViewById(R.id.recyclerView_movies)
        popularMoviesLayoutMgr = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        popularMovies.layoutManager = popularMoviesLayoutMgr
        popularMoviesAdapter = MoviesAdapter(mutableListOf()) { movie -> showMovieDetails(movie) }
        popularMovies.adapter = popularMoviesAdapter

        getPopularMovies()

        return root
    }

    private fun attachPopularMoviesOnScrollListener() { //Basicly updates when scolling, showing movies as you scroll
        popularMovies.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val totalItemCount =
                popularMoviesLayoutMgr.itemCount //the total number of movies inside our popularMoviesAdapter. This will keep increasing the more we call popularMoviesAdapter.appendMovies().
            val visibleItemCount =
                popularMoviesLayoutMgr.childCount //the current number of child views attached to the RecyclerView that are currently being recycled over and over again. The value of this variable for common screen sizes will range roughly around 4-5 which are 3 visible views, +1 left view that’s not seen yet and +1 right view that’s not seen yet also. The value will be higher if you have a bigger screen.
            val firstVisibleItem =
                popularMoviesLayoutMgr.findFirstVisibleItemPosition() // is the position of the leftmost visible item in our list.

            if (firstVisibleItem + visibleItemCount >= totalItemCount / 2) {
                popularMovies.removeOnScrollListener(this)
                popularMoviesPage++
                getPopularMovies()
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
        Log.d("SearchFragment", "Movies: $movies")
        popularMoviesAdapter.appendMovies(movies)
        attachPopularMoviesOnScrollListener()
    }

    private fun onError() { //Used in getPopularMovies
        Toast.makeText(activity, "Error fetching movies", Toast.LENGTH_SHORT).show()
    }

}