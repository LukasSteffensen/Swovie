package com.p6.swovie

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.p6.swovie.dataClasses.Match

class MatchAdapter (private val matches: MutableList<Match>) : RecyclerView.Adapter<MatchAdapter.MatchViewHolder>(){

    class MatchViewHolder(matchView: View) : RecyclerView.ViewHolder(matchView) {

        val matchTextView: TextView = matchView.findViewById(R.id.textView_match_title)

        val textViewPercentage: TextView = matchView.findViewById(R.id.textView_match_percentage)

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_match, parent, false)
        return MatchViewHolder(view)
    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        holder.matchTextView.text = matches[position].title
        holder.textViewPercentage.text = matches[position].matchPercentage

    }

    override fun getItemCount(): Int {
        return matches.size
    }
}