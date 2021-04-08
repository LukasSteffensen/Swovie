package com.p6.swovie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.p6.swovie.fragments.AccountFragment
import com.p6.swovie.fragments.MatchFragment
import com.p6.swovie.fragments.MovieFragment
import com.p6.swovie.fragments.SearchFragment

class MainActivity : AppCompatActivity() {

    private val movieFragment = MovieFragment()
    private val matchFragment = MatchFragment()
    private val searchFragment = SearchFragment()
    private val accountFragment = AccountFragment()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        replaceFragment(movieFragment)

        val bottomNavigation: BottomNavigationView = findViewById(R.id.navigation_bar)
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.ic_movie -> replaceFragment(movieFragment)
            R.id.ic_match -> replaceFragment(matchFragment)
            R.id.ic_search -> replaceFragment(searchFragment)
            R.id.ic_account -> replaceFragment(accountFragment)

        }
        true

    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_wrapper, fragment)
            commit()
        }
    }


}
