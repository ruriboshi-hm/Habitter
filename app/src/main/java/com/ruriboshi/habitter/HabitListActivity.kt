package com.ruriboshi.habitter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentForm
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import io.realm.Realm
import io.realm.Sort
import java.time.LocalDate
import java.util.Locale
import java.util.concurrent.atomic.AtomicBoolean

class HabitListActivity : AppCompatActivity() {

    private lateinit var rv:RecyclerView
    private lateinit var addBtn:ImageButton
    private lateinit var settingsBtn:ImageButton
    private lateinit var filterBtn:ImageButton
    private lateinit var noImage:ImageView
    private lateinit var noText:TextView
    private lateinit var allFinishedText:TextView
    private lateinit var realm:Realm
    private lateinit var recyclerAdapter: RecyclerAdapter
    private lateinit var lManager: LayoutManager
    private lateinit var adView:AdView

    private lateinit var consentInformation: ConsentInformation
    private var isMobileAdsInitializeCalled = AtomicBoolean(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_habit_list)

        rv = findViewById(R.id.recyclerView)
        addBtn = findViewById(R.id.addBtn)
        settingsBtn = findViewById(R.id.settingBtn)
        filterBtn = findViewById(R.id.filterBtn)
        noImage = findViewById(R.id.noImage)
        noText = findViewById(R.id.noText)
        allFinishedText = findViewById(R.id.allFinishText)

        val habitListLayout:ConstraintLayout = findViewById(R.id.habitListLayout)
        val toolbar:Toolbar = findViewById(R.id.toolbar)

        realm = Realm.getDefaultInstance()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // SDKのバージョンがR以降である場合にダークモード設定が導入されたため、それを判定する
            if (this.theme.resources.configuration.isNightModeActive) {
                // ダークモードの場合にこのスコープに入る
                habitListLayout.setBackgroundColor(Color.parseColor("#003249"))
                toolbar.setBackgroundColor(Color.parseColor("#96ADB8"))
                noImage.setImageResource(R.drawable.noimage)
            }
        }

        //AdMob
        /*MobileAds.initialize(this){}

        adView = findViewById(R.id.adView_list)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        adView.setBackgroundColor(Color.parseColor("#E6FAFB"))*/
        //AdMob

        val sharedPrefDate = getSharedPreferences("todayDate", Context.MODE_PRIVATE)
        val yearText = sharedPrefDate.getLong("year", 0)
        val monthText = sharedPrefDate.getLong("month",0)
        val dayText = sharedPrefDate.getLong("day",0)

        val nowDate = LocalDate.now()
        if (yearText == 0L && monthText == 0L && dayText == 0L){
            sharedPrefDate.edit().putLong("year",nowDate.year.toLong()).apply()
            sharedPrefDate.edit().putLong("month",nowDate.monthValue.toLong()).apply()
            sharedPrefDate.edit().putLong("day",nowDate.dayOfMonth.toLong()).apply()
        }else if (!(yearText == nowDate.year.toLong() && monthText == nowDate.monthValue.toLong() && dayText == nowDate.dayOfMonth.toLong())){

            realm.executeTransaction {
                val realmResults = realm.where(MyModel::class.java).findAll()
                for (results in realmResults){
                    results.todayFinished = 0L
                }
            }

            sharedPrefDate.edit().putLong("year",nowDate.year.toLong()).apply()
            sharedPrefDate.edit().putLong("month",nowDate.monthValue.toLong()).apply()
            sharedPrefDate.edit().putLong("day",nowDate.dayOfMonth.toLong()).apply()
        }

        //レビュー依頼
        val sharedPref = getSharedPreferences("StartTheApp", Context.MODE_PRIVATE)
        val savedText = sharedPref.getString("key", "0")
        val savedNum = savedText?.toInt()
        if (savedNum != null) {
            if (savedNum > 0 && savedNum % 10 == 0){
                val manager = ReviewManagerFactory.create(this)
                val request = manager.requestReviewFlow()
                request.addOnCompleteListener { requestReview ->
                    when {
                        requestReview.isSuccessful -> {
                            val reviewInfo = requestReview.result
                            val flow = manager.launchReviewFlow(this, reviewInfo)
                            flow.addOnCompleteListener {
                                // The flow has finished. The API does not indicate whether the user
                                // reviewed or not, or even whether the review dialog was shown. Thus, no
                                // matter the result, we continue our app flow.
                            }
                        }
                        else -> {
                            // error or something
                        }
                    }
                }
            }
        }
        val nextNumText = (savedNum?.plus(1)).toString()
        sharedPref.edit().putString("key", nextNumText).apply()
        //レビュー依頼

        //↓GDPR対応↓
        /*val debugSettings = ConsentDebugSettings.Builder(this)
            .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
            .addTestDeviceHashedId("8610A629BBD26610BF083D2ABFB4CC96")
            .build()*/

        val params = ConsentRequestParameters
            .Builder()
            .setTagForUnderAgeOfConsent(false)
            .build()

        //.setConsentDebugSettings(debugSettings)
        consentInformation = UserMessagingPlatform.getConsentInformation(this)
        //consentInformation.reset()
        consentInformation.requestConsentInfoUpdate(
            this,
            params,
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                    this@HabitListActivity,
                    ConsentForm.OnConsentFormDismissedListener {

                        // Consent gathering failed.
                        /*Log.w(TAG, String.format("%s: %s",
                            loadAndShowError?.errorCode,
                            loadAndShowError?.message
                        ))*/
                        if (consentInformation.canRequestAds()) {
                            initializeMobileAdsSdk()
                        }
                        // Consent has been gathered.
                    }
                )
            },
            {

            // Consent gathering failed.
                /*Log.w(TAG, String.format("%s: %s",
                    requestConsentError.errorCode,
                    requestConsentError.message
                ))*/
            })
        if (consentInformation.canRequestAds()) {
            initializeMobileAdsSdk()
        }
        //↑GDPR対応↑

        addBtn.setOnClickListener {
            val intent = Intent(this,EditActivity::class.java)
            startActivity(intent)
        }

        filterBtn.setOnClickListener {
            val locale = Locale.getDefault()
            if (locale == Locale.JAPAN){
                val choices = arrayOf("全て","未完了の習慣のみ")
                androidx.appcompat.app.AlertDialog.Builder(it.context)
                    .setTitle("表示")
                    .setSingleChoiceItems(choices, sharedPrefDate.getLong("filter", 0).toInt()) { dialog, which ->
                        sharedPrefDate.edit().putLong("filter",which.toLong()).apply()
                    }
                    .setPositiveButton("OK") { dialog, which ->
                        onStart()
                    }
                    .show()
            }else{
                val choices = arrayOf("All","Unfinished")
                androidx.appcompat.app.AlertDialog.Builder(it.context)
                    .setTitle("Filter")
                    .setSingleChoiceItems(choices, sharedPrefDate.getLong("filter", 0).toInt()) { dialog, which ->
                        sharedPrefDate.edit().putLong("filter",which.toLong()).apply()
                    }
                    .setPositiveButton("OK") { dialog, which ->
                        onStart()
                    }
                    .show()
            }
        }

        settingsBtn.setOnClickListener {
            val intent = Intent(this,SettingActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        val sharedPrefDate = getSharedPreferences("todayDate", Context.MODE_PRIVATE)
        when(sharedPrefDate.getLong("filter", 0)){
            0L ->{
                val realmResults = realm.where(MyModel::class.java)
                    .findAll().sort("id",Sort.DESCENDING)
                recyclerAdapter = RecyclerAdapter(realmResults,this)
                //追加箇所
                //recyclerAdapter.setHasStableIds(true)
                //追加箇所
                rv.adapter = recyclerAdapter
                lManager = GridLayoutManager(this,2,RecyclerView.VERTICAL,false)
                rv.layoutManager = lManager

                if (realmResults.isEmpty()){
                    noText.visibility = View.VISIBLE
                    noImage.visibility = View.VISIBLE
                    allFinishedText.visibility = View.GONE
                }else{
                    noText.visibility = View.GONE
                    noImage.visibility = View.GONE
                    allFinishedText.visibility = View.GONE
                }
            }
            1L ->{
                val realmResults = realm.where(MyModel::class.java).equalTo("todayFinished",0L)
                    .findAll().sort("id",Sort.DESCENDING)
                recyclerAdapter = RecyclerAdapter(realmResults,this)
                rv.adapter = recyclerAdapter
                lManager = GridLayoutManager(this,2,RecyclerView.VERTICAL,false)
                rv.layoutManager = lManager

                if (realmResults.isEmpty()){
                    noText.visibility = View.GONE
                    noImage.visibility = View.VISIBLE
                    allFinishedText.visibility = View.VISIBLE
                }else{
                    noText.visibility = View.GONE
                    noImage.visibility = View.GONE
                    allFinishedText.visibility = View.GONE
                }
            }
        }

    }

    //gdpr
    private fun initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.get()) {
            return
        }
        isMobileAdsInitializeCalled.set(true)

        // Initialize the Google Mobile Ads SDK.
        MobileAds.initialize(this)

        // TODO: Request an ad.
        // InterstitialAd.load(...)

        adView = findViewById(R.id.adView_list)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        adView.setBackgroundColor(Color.parseColor("#E6FAFB"))
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

}