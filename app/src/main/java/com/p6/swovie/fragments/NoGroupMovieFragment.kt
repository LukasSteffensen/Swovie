package com.p6.swovie.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.p6.swovie.R

class NoGroupMovieFragment: Fragment(), View.OnClickListener {

    private lateinit var buttonCreateJoin: Button
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.no_swipe_fragment, container, false)
        bottomNavigation = activity!!.findViewById(R.id.navigation_bar)

        buttonCreateJoin = root.findViewById(R.id.button)
        buttonCreateJoin.setOnClickListener(this)

        return root
    }

    override fun onClick(v: View?) {

        when (v) {
            buttonCreateJoin -> {
                replaceFragment(CreateGroupFragment())
            }
        }
    }

    private fun replaceFragment(fragment: Fragment){
        bottomNavigation.selectedItemId = R.id.ic_match
        activity?.supportFragmentManager?.beginTransaction()?.apply {
            replace(R.id.fl_wrapper, fragment)
            commit()
        }
    }
}