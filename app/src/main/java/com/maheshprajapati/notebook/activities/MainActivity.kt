package com.maheshprajapati.notebook.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.maheshprajapati.notebook.R
import com.maheshprajapati.notebook.databinding.ActivityMainBinding
import com.maheshprajapati.notebook.fragments.AllNotesFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set status bar icons to dark (black) because background is white
        val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = true

        openHomeFragment()
    }

    private fun openHomeFragment() {
        val fragment = AllNotesFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.frame_container, fragment)
        ft.commitAllowingStateLoss()
    }
}
