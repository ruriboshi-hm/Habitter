package com.ruriboshi.habitter

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.constraintlayout.widget.ConstraintLayout

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val titleLayout:ConstraintLayout = findViewById(R.id.titleLayout)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // SDKのバージョンがR以降である場合にダークモード設定が導入されたため、それを判定する
            if (this.theme.resources.configuration.isNightModeActive) {
                // ダークモードの場合にこのスコープに入る
                titleLayout.setBackgroundColor(Color.parseColor("#003249"))
            }
        }

        /*window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.parseColor("#C7FEFF")*/

        /*if(AppLaunchChecker.hasStartedFromLauncher(this)){
            loadingDelay()
        } else {
            firstLoading()
        }*/

        loadingDelay()
    }

    private fun loadingDelay(){
        Handler().postDelayed({
            val intent = Intent(this,HabitListActivity::class.java)
            startActivity(intent)
            finish()
        },1500)
    }
}