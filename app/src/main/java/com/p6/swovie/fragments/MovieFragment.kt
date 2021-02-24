package com.p6.swovie.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.p6.swovie.MoviesRepository
import com.p6.swovie.R


class MovieFragment : Fragment() {

    private lateinit var imageViewMovie: ImageView
    private lateinit var textViewTitle: TextView
    private val JSON_URL = "https://api.themoviedb.org/3/movie/550?api_key=9870f62e69820872d263749cf1055bc1"

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

        imageViewMovie.setImageResource(R.drawable.ic_movie)
        textViewTitle.text = "Title of Movie"

        MoviesRepository.getPopularMovies()

        return root
    }


}