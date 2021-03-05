package com.p6.swovie.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.p6.swovie.Movie
import com.p6.swovie.MoviesRepository
import com.p6.swovie.R

class SearchFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_search, container, false)

        MoviesRepository.getPopularMovies(
            onSuccess = ::onPopularMoviesFetched,
            onError = ::onError
        )


        return root
    }

    private fun onPopularMoviesFetched(movies: List<Movie>) {
        Log.d("SearchFragment", "Movies: $movies")
    }

    private fun onError() {
        Toast.makeText(activity, "Error fetching movies", Toast.LENGTH_SHORT).show()
    }

}