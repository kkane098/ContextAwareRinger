package com.example.ContextAwareRinger.Activities

import android.app.NotificationManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.ContextAwareRinger.R
import android.content.Intent
import android.os.Build


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationManager.isNotificationPolicyAccessGranted) {
                Intent(this@SplashActivity, DNDPermissionActivity::class.java)
            } else {
                Intent(this@SplashActivity, MainActivity::class.java)
            }

        Handler().postDelayed({
            /* Create an Intent that will start MainActivity. */
            startActivity(intent)
            finish()
        }, 1000)
    }
}
