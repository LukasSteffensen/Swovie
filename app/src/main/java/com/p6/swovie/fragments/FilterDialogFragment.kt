package com.p6.swovie.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.p6.swovie.MainActivity
import com.p6.swovie.R

class FilterDialogFragment : AppCompatActivity(), View.OnClickListener {

    private lateinit var applyButton: Button
    private lateinit var filterChipGroup: ChipGroup
    private lateinit var filterChip1: Chip
    private lateinit var filterChip2: Chip


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.filter_dialog)

        applyButton = findViewById(R.id.button_apply)
        filterChipGroup = findViewById(R.id.chip_group)
        filterChip1 = findViewById(R.id.chip_1)
        filterChip2 = findViewById(R.id.chip_2)
        val filterChipListener = CompoundButton.OnCheckedChangeListener { buttonView, _ -> Toast.makeText(this, buttonView.id, Toast.LENGTH_LONG).show()}
        filterChip1.setOnCheckedChangeListener(filterChipListener)
        filterChip2.setOnCheckedChangeListener(filterChipListener)
        applyButton.setOnClickListener(this)

    }
    override fun onClick(view: View?) { // All OnClick for the buttons in this Fragment
        when (view) {
            applyButton -> changeToFilters()
        }
    }

    private fun toast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun changeToFilters() {
        val intent = Intent (this, MainActivity::class.java)
        toast("Filters coming in an update soon")
        startActivity(intent)
    }

    private fun AppCompatActivity.replaceFragment(fragment:Fragment){
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_movie,fragment)
        //transaction.addToBackStack(null)
        transaction.commit()
    }

}
