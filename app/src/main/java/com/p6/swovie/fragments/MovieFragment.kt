package com.p6.swovie.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.p6.swovie.Movie
import com.p6.swovie.MoviesRepository
import com.p6.swovie.R
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException


class MovieFragment : Fragment() {

    private lateinit var imageViewMovie: ImageView
    private lateinit var textViewTitle: TextView


    private val JSON_URL_IMAGE = "https://image.tmdb.org/t/p/original/8kNruSfhk5IoE4eZOc4UpvDn6tq.jpg"


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

        textViewTitle.text = "Title of Movie"
        Glide.with(root)
                .load(JSON_URL_IMAGE)
                .into(imageViewMovie)

        MoviesRepository.getPopularMovies(
            onSuccess = ::onPopularMoviesFetched,
            onError = ::onError
        )

        return root
    }

    private fun onPopularMoviesFetched(movies: List<Movie>) {
        Log.d("MovieFragment", "Movies: $movies")
    }

    private fun onError() {
        Toast.makeText(activity, "Error fetching movies", Toast.LENGTH_SHORT).show()
    }

/*
    fun fetchJson() {
        val JSON_URL_FIGHTCLUB = "https://api.themoviedb.org/3/movie/550?api_key=9870f62e69820872d263749cf1055bc1"
        val client = OkHttpClient()

        val request = Request.Builder().url(JSON_URL_FIGHTCLUB).build()

        client.newCall(request).enqueue(object: Callback {
            override fun onResponse(call: Call, response: Response) {
                val body = response?.body()?.string()
                Log.d("OKHttpRequest", "Works")
            }
            override fun onFailure(call: Call, e: IOException) {
                Log.e("OkHttpRequest", "onFailure", e)
            }
                })
    }*/


}
