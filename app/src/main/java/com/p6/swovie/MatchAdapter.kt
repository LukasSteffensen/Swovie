package com.p6.swovie

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.p6.swovie.dataClasses.Match

class MatchAdapter(private val matches: MutableList<Match>) : RecyclerView.Adapter<MatchAdapter.MatchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_match, parent, false)
        return MatchViewHolder(view)

    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        holder.matchTextView.text = matches[position].title
        holder.textViewPercentage.text =
            "%.2f".format(matches[position].matchPercentage?.toDouble())

        holder.buttonViewSwipes.setOnClickListener {
            listener?.onViewButtonClick()
        }

        Glide.with(holder.itemView)
            .load("https://image.tmdb.org/t/p/w342${matches[position].posterPath}")
            .transform(CenterCrop())
            .into(holder.imageView)
    }

    private var listener: OnClickListener? = null

    fun setListener(listener: OnClickListener) {
        this.listener = listener
    }

    interface OnClickListener {
        fun onViewButtonClick()
    }

    override fun getItemCount(): Int {
        return matches.size
    }

    class MatchViewHolder(matchView: View) : RecyclerView.ViewHolder(matchView) {

        val matchTextView: TextView = matchView.findViewById(R.id.textView_match_title)
        val textViewPercentage: TextView = matchView.findViewById(R.id.textView_match_percentage)
        val imageView: ImageView = matchView.findViewById(R.id.image_view)
        val buttonViewSwipes: Button = matchView.findViewById(R.id.button_view_swipes)

    }
}