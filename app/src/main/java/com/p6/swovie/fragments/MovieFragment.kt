package com.p6.swovie.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.p6.swovie.Movie
import com.p6.swovie.MoviesRepository
import com.p6.swovie.R


class MovieFragment : Fragment() {

    private lateinit var imageViewMovie: ImageView
    private lateinit var textViewTitle: TextView

    private val JSON_URL_FIGHTCLUB = "https://api.themoviedb.org/3/movie/550?api_key=9870f62e69820872d263749cf1055bc1"
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


        //val title = JSON_URL_FIGHTCLUB.getJSONObject().getString("original_title")
        textViewTitle.text = "Title of Movie"
        Glide.with(root)
                .load(JSON_URL_IMAGE)
                .into(imageViewMovie)

        MoviesRepository.getPopularMovies()

        return root
    }


}