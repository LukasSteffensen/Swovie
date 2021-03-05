package com.p6.swovie.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.p6.swovie.Movie
import com.p6.swovie.MoviesAdapter
import com.p6.swovie.MoviesRepository
import com.p6.swovie.R

class SearchFragment : Fragment() {


    private lateinit var popularMovies: RecyclerView
    private lateinit var popularMoviesAdapter: MoviesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_search, container, false)

        popularMovies = root.findViewById(R.id.recyclerView_movies)
        popularMovies.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL,false)
        popularMoviesAdapter = MoviesAdapter(listOf())
        popularMovies.adapter = popularMoviesAdapter

        MoviesRepository.getPopularMovies(
            onSuccess = ::onPopularMoviesFetched,
            onError = ::onError
        )


        return root
    }

    private fun onPopularMoviesFetched(movies: List<Movie>) {
        Log.d("SearchFragment", "Movies: $movies")
        popularMoviesAdapter.updateMovies(movies)
    }

    private fun onError() {
        Toast.makeText(activity, "Error fetching movies", Toast.LENGTH_SHORT).show()
    }

}