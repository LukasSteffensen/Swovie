package com.p6.swovie

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.p6.swovie.dataClasses.Movie

class CardStackAdapter (private var movies: List<Movie>): RecyclerView.Adapter<CardStackAdapter.ViewHolder>() {





    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var inflater: LayoutInflater = LayoutInflater.from(parent.context);
        var view = inflater.inflate(R.layout.movie_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(movies[position])
    }

    override fun getItemCount(): Int {
        return movies.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var image: ImageView
        lateinit var title: String



        fun setData(movie: Movie) {
            image = itemView.findViewById(R.id.item_image)
            title = movie.title
            Glide.with(itemView)
                .load("https://image.tmdb.org/t/p/w342${movie.posterPath}")
                .transform(CenterCrop())
                .into(image)
        }
    }
}