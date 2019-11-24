package com.example.ContextAwareRinger.Activities

import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import com.example.ContextAwareRinger.R

class DNDPermissionActivity : AppCompatActivity() {

    lateinit var mNotificationManager : NotificationManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dndpermission)

        mNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    public override fun onRestart() {
        super.onRestart()

        if (mNotificationManager.isNotificationPolicyAccessGranted) {
            val intent = Intent(this@DNDPermissionActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    fun openSettings(view : View){

        val intent = Intent(
            Settings
                .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS
        )

        startActivity(intent)
    }
}
