package com.maheshprajapati.myapplication.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.maheshprajapati.myapplication.R
import com.maheshprajapati.myapplication.utility.AppConstants
import com.maheshprajapati.myapplication.utility.CommontMethods
import com.maheshprajapati.myapplication.utility.HelperClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    val LOCK_SCREEN_RESULT = 578
    private lateinit var helperClass: HelperClass
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        helperClass = HelperClass(this@SplashActivity)
        GlobalScope.launch(context = Dispatchers.IO) {
            Thread.sleep(2000)
            openActivity()
        }
    }
    private fun openActivity() {
        if(helperClass.loadBoolPreferences(helperClass.IS_APP_LOCK_ACTIVE)) {
            val intent = Intent(applicationContext, LockScreenActivity::class.java)
            intent.putExtra(AppConstants.BundleConstants.COME_FROME, AppConstants.BundleConstants.SPLASH_SCREEN)
            startActivityForResult(intent, LOCK_SCREEN_RESULT)
        } else {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LOCK_SCREEN_RESULT) {
            if (resultCode == Activity.RESULT_OK) {
                val returnValue: String = data!!.getStringExtra(AppConstants.BundleConstants.LOCK_SCREEN)!!
                if (returnValue.equals(AppConstants.BundleConstants.FINISHED)) {
                    CommontMethods().customToast(this@SplashActivity, getString(R.string.app_unlock_message))
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }
}