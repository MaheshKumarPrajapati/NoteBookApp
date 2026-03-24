package com.maheshprajapati.notebook.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.maheshprajapati.notebook.R
import com.maheshprajapati.notebook.databinding.ActivitySplashBinding
import com.maheshprajapati.notebook.utility.AppConstants
import com.maheshprajapati.notebook.utility.CommontMethods
import com.maheshprajapati.notebook.utility.HelperClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    private lateinit var helperClass: HelperClass
    private lateinit var binding: ActivitySplashBinding

    private val lockScreenLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val returnValue: String? = data?.getStringExtra(AppConstants.BundleConstants.LOCK_SCREEN)
            if (returnValue == AppConstants.BundleConstants.FINISHED) {
                CommontMethods().customToast(this@SplashActivity, getString(R.string.app_unlock_message))
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        helperClass = HelperClass(this@SplashActivity)
        
        lifecycleScope.launch(Dispatchers.Main) {
            delay(2000)
            openActivity()
        }
    }

    private fun openActivity() {
        if (helperClass.loadBoolPreferences(helperClass.IS_APP_LOCK_ACTIVE)) {
            val intent = Intent(applicationContext, LockScreenActivity::class.java)
            intent.putExtra(AppConstants.BundleConstants.COME_FROME, AppConstants.BundleConstants.SPLASH_SCREEN)
            lockScreenLauncher.launch(intent)
        } else {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
