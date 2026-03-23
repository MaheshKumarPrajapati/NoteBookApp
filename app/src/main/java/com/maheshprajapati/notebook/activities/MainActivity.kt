package com.maheshprajapati.myapplication.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.maheshprajapati.myapplication.R
import com.maheshprajapati.myapplication.databinding.ActivityMainBinding
import com.maheshprajapati.myapplication.fragments.AllNotesFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        openHomeFragment()
    }

    private fun openHomeFragment() {
        val fragment = AllNotesFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.frame_container, fragment)
        ft.commitAllowingStateLoss()
    }
}
