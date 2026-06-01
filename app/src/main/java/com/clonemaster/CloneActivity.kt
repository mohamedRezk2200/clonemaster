package com.clonemaster

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CloneActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clone)

        val packageName = intent.getStringExtra("package_name") ?: return
        val appName = intent.getStringExtra("app_name") ?: packageName

        val tvTitle = findViewById<TextView>(R.id.tvAppName)
        val tvPkg = findViewById<TextView>(R.id.tvPackageName)
        val ivIcon = findViewById<ImageView>(R.id.ivAppIcon)
        val btnMethod1 = findViewById<Button>(R.id.btnMethod1)
        val btnMethod2 = findViewById<Button>(R.id.btnMethod2)
        val btnMethod3 = findViewById<Button>(R.id.btnMethod3)
        val btnOpenSettings = findViewById<Button>(R.id.btnOpenSettings)

        tvTitle.text = appName
        tvPkg.text = packageName

        try {
            val icon = packageManager.getApplicationIcon(packageName)
            ivIcon.setImageDrawable(icon)
        } catch (e: Exception) { }

        // Method 1: Open Android Multiple Users settings
        btnMethod1.setOnClickListener {
            try {
                val intent = Intent(Settings.ACTION_SYNC_SETTINGS)
                startActivity(intent)
            } catch (e: Exception) {
                openUserSettings()
            }
            Toast.makeText(this,
                "Go to 'Add user' → install $appName in the new profile → log in with a second account",
                Toast.LENGTH_LONG).show()
        }

        // Method 2: Open app in Play Store to install in work profile
        btnMethod2.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$packageName"))
                intent.setPackage("com.android.vending")
                startActivity(intent)
                Toast.makeText(this,
                    "If you have a Work Profile enabled, install this app there for a second instance.",
                    Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Play Store not available", Toast.LENGTH_SHORT).show()
            }
        }

        // Method 3: Share package name for use with ADB / parallel space apps
        btnMethod3.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Clone: $appName")
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                "App: $appName\nPackage: $packageName\n\n" +
                "To clone via ADB:\n" +
                "adb shell pm install-existing --user 10 $packageName\n\n" +
                "Or use Parallel Space / Dual Space and add this package.")
            startActivity(Intent.createChooser(shareIntent, "Share App Info"))
        }

        // Open app system settings
        btnOpenSettings.setOnClickListener {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        }
    }

    private fun openUserSettings() {
        try {
            val intent = Intent("android.settings.USER_SETTINGS")
            startActivity(intent)
        } catch (e: Exception) {
            try {
                val intent = Intent(Settings.ACTION_SETTINGS)
                startActivity(intent)
                Toast.makeText(this,
                    "Navigate to: Accounts → Users / Multiple Users",
                    Toast.LENGTH_LONG).show()
            } catch (ex: Exception) {
                Toast.makeText(this, "Could not open settings", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
