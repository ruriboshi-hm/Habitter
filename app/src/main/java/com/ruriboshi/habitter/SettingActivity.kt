package com.ruriboshi.habitter

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer,PreferenceFragment())
            .commit()

        val settingLayout:ConstraintLayout = findViewById(R.id.settingLayout)
        val toolbar: Toolbar = findViewById(R.id.settings_toolbar)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // SDKのバージョンがR以降である場合にダークモード設定が導入されたため、それを判定する
            if (this.theme.resources.configuration.isNightModeActive) {
                // ダークモードの場合にこのスコープに入る
                settingLayout.setBackgroundColor(Color.parseColor("#003249"))
                toolbar.setBackgroundColor(Color.parseColor("#96ADB8"))
            }
        }

        val backBtn:ImageButton = findViewById(R.id.backBtn)

        backBtn.setOnClickListener {
            finish()
        }
    }
}