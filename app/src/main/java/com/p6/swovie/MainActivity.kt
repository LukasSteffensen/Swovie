package com.p6.swovie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.p6.swovie.fragments.*

class MainActivity : AppCompatActivity() {

    private val TAG: String = "MainActivity"

    private val movieFragment = MovieFragment()
    private val matchFragment = MatchFragment()
    private val secondMatchFragment = SecondMatchFragment()
    private val searchFragment = SearchFragment()
    private val accountFragment = AccountFragment()
    private var isInGroup: Boolean = false

    private var auth: FirebaseAuth = Firebase.auth
    private val db = Firebase.firestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        replaceFragment(movieFragment)
        val bottomNavigation: BottomNavigationView = findViewById(R.id.navigation_bar)

        bottomNavigation.isClickable = false

        if(auth.currentUser != null) {
            db.collection("rooms").whereArrayContains("users", auth.currentUser.uid).get()
                .addOnSuccessListener { document ->
                    isInGroup = !document.isEmpty
                    bottomNavigation.isClickable = true
                    Log.i(TAG, "isInGroup is True")
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                }
        }


        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        if (item.itemId == R.id.ic_movie){
            replaceFragment(movieFragment)
        } else if (item.itemId == R.id.ic_match){
            if(isInGroup){
                replaceFragment(secondMatchFragment)
            } else {
                replaceFragment(matchFragment)
            }
        } else if (item.itemId == R.id.ic_search){
            replaceFragment(searchFragment)
        } else if (item.itemId == R.id.ic_account){
            replaceFragment(accountFragment)
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
