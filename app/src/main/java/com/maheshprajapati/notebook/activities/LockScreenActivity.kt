package com.maheshprajapati.myapplication.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import com.maheshprajapati.myapplication.R
import com.maheshprajapati.myapplication.utility.AppConstants
import java.util.concurrent.Executors


class LockScreenActivity : AppCompatActivity() {

    private var camefrom = ""
    private lateinit var bitmap: Bitmap
    private val INTENT_AUTHENTICATE = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lockscreen)
        var bundle: Bundle? = intent.extras;
        if (bundle != null) {
            if (bundle.get(AppConstants.BundleConstants.COME_FROME) != null && bundle.getString(
                    AppConstants.BundleConstants.COME_FROME,
                    ""
                ).isNotEmpty()
            ) {
                camefrom = bundle.getString(AppConstants.BundleConstants.COME_FROME, "")
            }
        }

        if (Build.VERSION.SDK_INT >= 16) {
            val window = this.window
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
            if (Build.VERSION.SDK_INT >= 21) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                window.statusBarColor = Color.TRANSPARENT
                window.decorView.systemUiVisibility = (
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
                if (Build.VERSION.SDK_INT >= 23) { //Set the status bar white and the notifications grey
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                    window.statusBarColor = Color.TRANSPARENT
                    window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
                }
            }
        }

        val executor =
            Executors.newSingleThreadExecutor() // can be executed on any executor as per requirement
        val activity: AppCompatActivity = this

        val biometricPrompt =
            BiometricPrompt(activity, executor, object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    finishAffinity()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    finishActivity()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(resources.getString(R.string.app_name))
            .setSubtitle(resources.getString(R.string.lock_screen_subtitle))
            .setDescription(resources.getString(R.string.lock_screen_description))
           // .setNegativeButtonText(resources.getString(R.string.lock_screen_back))
            .setDeviceCredentialAllowed(true)
            .build()

        //launch authentication for biomaetric
        biometricPrompt.authenticate(promptInfo)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == INTENT_AUTHENTICATE) {
            if (resultCode == RESULT_OK) {
                //do something you want when pass the security
                finishActivity()
            } else {
                finishAffinity()
            }
        }
    }

    private fun finishActivity() {
        if (camefrom.equals(AppConstants.BundleConstants.SPLASH_SCREEN)) {
            val resultIntent = Intent()
            resultIntent.putExtra(
                AppConstants.BundleConstants.LOCK_SCREEN,
                AppConstants.BundleConstants.FINISHED
            )
            setResult(Activity.RESULT_OK, resultIntent)
        }
        finish()
    }

    override fun onBackPressed() {
        //commented this so user can't go back and can only exit application
        //super.onBackPressed()
    }

}