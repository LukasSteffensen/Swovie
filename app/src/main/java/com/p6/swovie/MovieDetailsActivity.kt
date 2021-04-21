package com.p6.swovie
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop

const val MOVIE_TITLE = "extra_movie_title"
const val MOVIE_POSTER = "extra_movie_poster"
const val MOVIE_OVERVIEW = "extra_movie_overview"


class MovieDetailsActivity : AppCompatActivity() {

    private lateinit var imageViewMovieDetails: ImageView
    private lateinit var textViewTitleDetails: TextView
    private lateinit var textViewDescriptionDetails: TextView

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_movie_details)

    imageViewMovieDetails = findViewById(R.id.imageView_movie_details)
    textViewTitleDetails = findViewById(R.id.textView_title_details)
    textViewDescriptionDetails = findViewById(R.id.textView_description_details)

    textViewDescriptionDetails.movementMethod = ScrollingMovementMethod()

    val extras = intent.extras

    if (extras != null) {
        populateDetails(extras)
    } else {
        finish()
    }

}

    private fun populateDetails(extras: Bundle){
        extras.getString(MOVIE_POSTER)?.let { posterPath ->
            Glide.with(this)
                .load("https://image.tmdb.org/t/p/w342$posterPath")
                .transform(CenterCrop())
                .into(imageViewMovieDetails)
        }

        textViewTitleDetails.text = extras.getString(MOVIE_TITLE, "")
        textViewDescriptionDetails.text = extras.getString(MOVIE_OVERVIEW, "")
    }

}
