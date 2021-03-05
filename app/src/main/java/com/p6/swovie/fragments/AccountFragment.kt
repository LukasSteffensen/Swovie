package com.p6.swovie.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.p6.swovie.R

class AccountFragment : Fragment() {

    private val JSON_URL_LOGO = "https://www.themoviedb.org/assets/2/v4/logos/v2/blue_square_2-d537fb228cf3ded904ef09b136fe3fec72548ebc1fea3fbbd1ad9e36364db38b.svg"
    private lateinit var imageViewLogo: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_account, container, false)

        imageViewLogo = root.findViewById(R.id.imageView_logo)

        Glide.with(root)
                .load(JSON_URL_LOGO)
                .into(imageViewLogo)

        return root
    }
}