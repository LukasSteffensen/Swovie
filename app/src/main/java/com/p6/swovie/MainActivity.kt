package com.p6.swovie

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.p6.swovie.fragments.*
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    companion object DataHolder{
        var isInGroup: Boolean = false
    }

    private val TAG: String = "MainActivity"

    private lateinit var bottomNavigation: BottomNavigationView

    private var auth: FirebaseAuth = Firebase.auth
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomNavigation = findViewById(R.id.navigation_bar)

        bottomNavigation.isClickable = false

        if(auth.currentUser != null) {
            db.collection("groups").whereArrayContains("users", auth.currentUser.uid).get()
                .addOnSuccessListener { document ->
                    isInGroup = !document.isEmpty
                    bottomNavigation.isClickable = true
                    Log.i(TAG, "isInGroup is True")
                    if (isInGroup){
                        replaceFragment(MovieFragment())
                    } else {
                        replaceFragment(NoGroupMovieFragment())
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                }
        }

        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        if (item.itemId == R.id.ic_movie){
            if (isInGroup){
                replaceFragment(MovieFragment())
            } else {
                replaceFragment(NoGroupMovieFragment())
            }
        } else if (item.itemId == R.id.ic_match){
            if(isInGroup){
                replaceFragment(MatchFragment())
            } else {
                replaceFragment(CreateGroupFragment())
            }
        } else if (item.itemId == R.id.ic_search){
            replaceFragment(SearchFragment())
        } else if (item.itemId == R.id.ic_account){
            replaceFragment(AccountFragment())
        }
        true
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_wrapper, fragment)
            addToBackStack(null)
            commit()
        }
    }

    override fun onBackPressed() {
        var fm: FragmentManager = supportFragmentManager
        when {
            fm.backStackEntryCount > 1 -> {
                Log.i(TAG, "popping back stack")
                fm.popBackStack()
            }
            fm.backStackEntryCount == 1 -> {
                Log.i(TAG, "Nothing on back stack, closing app")
                moveTaskToBack(true)
                exitProcess(-1)
            }
            else -> {
                super.onBackPressed()
            }
        }
    }
}
