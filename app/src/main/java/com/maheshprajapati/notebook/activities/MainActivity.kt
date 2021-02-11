package com.maheshprajapati.myapplication.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.maheshprajapati.myapplication.R
import com.maheshprajapati.myapplication.fragments.AllNotesFragment
import com.maheshprajapati.myapplication.fragments.SearchFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        openHomeFragment()
    }

    private fun openHomeFragment() {
        var fragment: AllNotesFragment = AllNotesFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.frame_container, fragment)
        ft.commitAllowingStateLoss()
    }
}