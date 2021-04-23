package com.p6.swovie.dataClasses

import androidx.recyclerview.widget.DiffUtil

class CardStackCallback(private var old: List<Movie>, private var new: List<Movie>): DiffUtil.Callback() {



    override fun getOldListSize(): Int {
        return old.size
    }

    override fun getNewListSize(): Int {
        return new.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old[oldItemPosition].posterPath == new[newItemPosition].posterPath
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old[oldItemPosition] == new[newItemPosition]
    }

}