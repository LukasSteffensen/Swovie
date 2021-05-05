package com.p6.swovie

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.p6.swovie.dataClasses.Match

class MatchAdapter(private val matches: MutableList<Match>, private val itemClickListener: OnClickListener) : RecyclerView.Adapter<MatchAdapter.MatchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_match, parent, false)
        return MatchViewHolder(view)

    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        holder.matchTextView.text = matches[position].title
        holder.textViewPercentage.text =
            "%.2f".format(matches[position].matchPercentage?.toDouble())

        val match = matches[position]
        holder.bind(match, itemClickListener)

        Glide.with(holder.itemView)
            .load("https://image.tmdb.org/t/p/w342${matches[position].posterPath}")
            .transform(CenterCrop())
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return matches.size
    }

    class MatchViewHolder(matchView: View) : RecyclerView.ViewHolder(matchView) {

        val matchTextView: TextView = matchView.findViewById(R.id.textView_match_title)
        val textViewPercentage: TextView = matchView.findViewById(R.id.textView_match_percentage)
        val imageView: ImageView = matchView.findViewById(R.id.image_view)
        val buttonViewSwipes: Button = matchView.findViewById(R.id.button_view_swipes)

        fun bind(match: Match, clickListener: OnClickListener) {
            buttonViewSwipes.setOnClickListener {
                clickListener.onViewSwipesClick(match)
            }
        }

    }

    interface OnClickListener {
        fun onViewSwipesClick(match: Match)
    }
}

