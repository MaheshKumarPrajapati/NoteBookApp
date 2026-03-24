package com.maheshprajapati.notebook.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.maheshprajapati.notebook.R
import com.maheshprajapati.notebook.databinding.ActivityLockscreenBinding
import com.maheshprajapati.notebook.utility.AppConstants
import java.util.concurrent.Executors


class LockScreenActivity : AppCompatActivity() {

    private var camefrom = ""
    private lateinit var bitmap: Bitmap
    private val INTENT_AUTHENTICATE = 1000
    private lateinit var binding: ActivityLockscreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityLockscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set status bar icons to dark (black) because background is white
        val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = true

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

        // Handle back press using OnBackPressedDispatcher for API 36 compatibility
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Do nothing to prevent user from going back, just like the previous implementation
            }
        })

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
            .setAllowedAuthenticators(androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG or androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL)
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
}
