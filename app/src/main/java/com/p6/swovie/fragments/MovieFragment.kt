package com.p6.swovie.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.p6.swovie.*
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.net.URL


class MovieFragment : Fragment() {

    private lateinit var imageViewMovie: ImageView
    private lateinit var textViewTitle: TextView
    private val client = OkHttpClient()


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

        imageViewMovie = root.findViewById(R.id.imageView_movie)
        textViewTitle = root.findViewById(R.id.textView_title)

        MoviesRepository.getPopularMovies(
                onSuccess = ::onPopularMoviesFetched,
                onError = ::onError
        )

        Glide.with(this)
                .load(JSON_URL_IMAGE)
                .into(imageViewMovie)


        return root

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


}
