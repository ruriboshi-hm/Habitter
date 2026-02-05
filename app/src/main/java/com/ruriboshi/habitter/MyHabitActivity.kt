package com.ruriboshi.habitter

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.ruriboshi.habitter.databinding.ActivityMyHabitBinding
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale


class MyHabitActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMyHabitBinding
    private lateinit var realm: Realm
    private lateinit var adView: AdView

    //カレンダー表示
    private var monthYearText: TextView? = null
    private var calendarRecyclerView: RecyclerView? = null
    private var selectedDate: LocalDate? = null

    //変更箇所
    var countTime = 0

    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyHabitBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //ad
        MobileAds.initialize(this){}

        adView = findViewById(R.id.adView_habit)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        adView.setBackgroundColor(Color.parseColor("#E6FAFB"))
        //ad

        realm = Realm.getDefaultInstance()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // SDKのバージョンがR以降である場合にダークモード設定が導入されたため、それを判定する
            if (this.theme.resources.configuration.isNightModeActive) {
                // ダークモードの場合にこのスコープに入る
                binding.mainLayout.setBackgroundColor(Color.parseColor("#003249"))
                //binding.toolbar.setBackgroundColor(Color.parseColor("#96ADB8"))
            }
        }

        val getId = intent.getLongExtra("id",0)
        val myModel = realm.where(MyModel::class.java).equalTo("id",getId).findFirst()

        if (myModel?.iconImage.contentEquals(ByteArray(0))){
            binding.imageViewTop.setImageResource(R.drawable.app_mainicon)
            binding.imageViewTop.setBackgroundColor(Color.parseColor("#C7FEFF"))
            binding.iconSet.setImageResource(R.drawable.app_mainicon)
            binding.iconSet.setBackgroundColor(Color.parseColor("#C7FEFF"))
        }else{
            binding.imageViewTop.setImageBitmap(byteToBitmap(myModel?.iconImage!!))
            binding.iconSet.setImageBitmap(byteToBitmap(myModel.iconImage))
        }

        binding.textView.text = myModel?.title.toString()

        when(myModel?.color){
            0L ->{
                binding.progressbar.progressDrawable = getDrawable(R.drawable.red_progressbar)
                binding.button.setBackgroundColor(Color.parseColor("#DA1100"))
            }
            1L ->{
                binding.progressbar.progressDrawable = getDrawable(R.drawable.orange_progressbar)
                binding.button.setBackgroundColor(Color.parseColor("#ff5c19"))
            }
            2L ->{
                binding.progressbar.progressDrawable = getDrawable(R.drawable.yellow_progressbar)
                binding.button.setBackgroundColor(Color.parseColor("#FFDF19"))
            }
            3L ->{
                binding.progressbar.progressDrawable = getDrawable(R.drawable.pink_progressbar)
                binding.button.setBackgroundColor(Color.parseColor("#ffa2c9"))
            }
            4L ->{
                binding.progressbar.progressDrawable = getDrawable(R.drawable.blue_progressbar)
                binding.button.setBackgroundColor(Color.parseColor("#139cfc"))
            }
            5L ->{
                binding.progressbar.progressDrawable = getDrawable(R.drawable.sky_blue_progressbar)
                binding.button.setBackgroundColor(Color.parseColor("#1becff"))
            }
            6L ->{
                binding.progressbar.progressDrawable = getDrawable(R.drawable.green_progressbar)
                binding.button.setBackgroundColor(Color.parseColor("#18c586"))
            }
            7L ->{
                binding.progressbar.progressDrawable = getDrawable(R.drawable.yellow_green_progressbar)
                binding.button.setBackgroundColor(Color.parseColor("#7FEC44"))
            }
            8L ->{
                binding.progressbar.progressDrawable = getDrawable(R.drawable.purple_progressbar)
                binding.button.setBackgroundColor(Color.parseColor("#8218ff"))
            }
            9L ->{
                binding.progressbar.progressDrawable = getDrawable(R.drawable.brawn_progressbar)
                binding.button.setBackgroundColor(Color.parseColor("#DD8C50"))
            }
            10L ->{
                binding.progressbar.progressDrawable = getDrawable(R.drawable.white_progressbar)
                binding.button.setBackgroundColor(Color.parseColor("#F4F4F4"))
                binding.button.setTextColor(Color.parseColor("#373737"))
            }
            11L ->{
                binding.progressbar.progressDrawable = getDrawable(R.drawable.black_progressbar)
                binding.button.setBackgroundColor(Color.parseColor("#373737"))
            }
        }

        val locale = Locale.getDefault()

        if (myModel?.todayFinished == 1L){
            //binding.progressbar.progress = 100
            binding.button.isEnabled = false
            progressChanged(100)
            binding.button.setBackgroundColor(Color.parseColor("#BABABA"))
            if (locale == Locale.JAPAN){
                binding.button.text = "完了済み"
            }else{
                binding.button.text = "Already completed"
            }

            when (myModel.messageOfToday) {
                0L -> {
                    binding.finishedCommentText.text = myModel.message1
                    if (myModel.message1 == ""){
                        if (locale == Locale.JAPAN){
                            binding.finishedCommentText.text = "お疲れ様でした！"
                        }else{
                            binding.finishedCommentText.text = "Good job!"
                        }
                    }
                }
                1L -> {
                    binding.finishedCommentText.text = myModel.message2
                    if (myModel.message2 == ""){
                        if (locale == Locale.JAPAN){
                            binding.finishedCommentText.text = "お疲れ様でした！"
                        }else{
                            binding.finishedCommentText.text = "Good job!"
                        }
                    }
                }
                2L -> {
                    binding.finishedCommentText.text = myModel.message3
                    if (myModel.message3 == ""){
                        if (locale == Locale.JAPAN){
                            binding.finishedCommentText.text = "お疲れ様でした！"
                        }else{
                            binding.finishedCommentText.text = "Good job!"
                        }
                    }
                }
            }
            binding.finishedComment.visibility = View.VISIBLE

            Handler().postDelayed({
                binding.button.isEnabled = true
            },1500)

        }else{
            binding.progressbar.progress = 0
            //binding.button.setBackgroundColor(Color.parseColor("#139cfc"))
            if (locale == Locale.JAPAN){
                binding.button.text = "完了"
            }else{
                binding.button.text = "Finish"
            }
            binding.finishedComment.visibility = View.GONE
            when(myModel?.color){
                0L ->{
                    binding.progressbar.progressDrawable = getDrawable(R.drawable.red_progressbar)
                    binding.button.setBackgroundColor(Color.parseColor("#DA1100"))
                }
                1L ->{
                    binding.progressbar.progressDrawable = getDrawable(R.drawable.orange_progressbar)
                    binding.button.setBackgroundColor(Color.parseColor("#ff5c19"))
                }
                2L ->{
                    binding.progressbar.progressDrawable = getDrawable(R.drawable.yellow_progressbar)
                    binding.button.setBackgroundColor(Color.parseColor("#FFDF19"))
                }
                3L ->{
                    binding.progressbar.progressDrawable = getDrawable(R.drawable.pink_progressbar)
                    binding.button.setBackgroundColor(Color.parseColor("#ffa2c9"))
                }
                4L ->{
                    binding.progressbar.progressDrawable = getDrawable(R.drawable.blue_progressbar)
                    binding.button.setBackgroundColor(Color.parseColor("#139cfc"))
                }
                5L ->{
                    binding.progressbar.progressDrawable = getDrawable(R.drawable.sky_blue_progressbar)
                    binding.button.setBackgroundColor(Color.parseColor("#1becff"))
                }
                6L ->{
                    binding.progressbar.progressDrawable = getDrawable(R.drawable.green_progressbar)
                    binding.button.setBackgroundColor(Color.parseColor("#18c586"))
                }
                7L ->{
                    binding.progressbar.progressDrawable = getDrawable(R.drawable.yellow_green_progressbar)
                    binding.button.setBackgroundColor(Color.parseColor("#7FEC44"))
                }
                8L ->{
                    binding.progressbar.progressDrawable = getDrawable(R.drawable.purple_progressbar)
                    binding.button.setBackgroundColor(Color.parseColor("#8218ff"))
                }
                9L ->{
                    binding.progressbar.progressDrawable = getDrawable(R.drawable.brawn_progressbar)
                    binding.button.setBackgroundColor(Color.parseColor("#DD8C50"))
                }
                10L ->{
                    binding.progressbar.progressDrawable = getDrawable(R.drawable.white_progressbar)
                    binding.button.setBackgroundColor(Color.parseColor("#F4F4F4"))
                    binding.button.setTextColor(Color.parseColor("#373737"))
                }
                11L ->{
                    binding.progressbar.progressDrawable = getDrawable(R.drawable.black_progressbar)
                    binding.button.setBackgroundColor(Color.parseColor("#373737"))
                }
            }
        }

        binding.button.setOnClickListener {
            if (myModel?.todayFinished == 0L){
                realm.executeTransaction {
                    myModel.todayFinished = 1L
                }
                binding.button.isEnabled = false
                //binding.progressbar.progress = 100
                progressChanged(100)
                //binding.progressbar.setProgress(100,true)
                binding.button.setBackgroundColor(Color.parseColor("#BABABA"))
                if (locale == Locale.JAPAN){
                    binding.button.text = "完了済み"
                }else{
                    binding.button.text = "Already completed"
                }

                when (myModel.comments) {
                    0L -> {
                        if (locale == Locale.JAPAN){
                            binding.finishedCommentText.text = "お疲れ様でした！"
                        }else{
                            binding.finishedCommentText.text = "Good job!"
                        }
                    }
                    1L -> {
                        binding.finishedCommentText.text = myModel.message1
                        if (myModel.message1 == ""){
                            if (locale == Locale.JAPAN){
                                binding.finishedCommentText.text = "お疲れ様でした！"
                            }else{
                                binding.finishedCommentText.text = "Good job!"
                            }
                        }
                        realm.executeTransaction {
                            myModel.messageOfToday = 1L
                        }
                    }
                    2L -> {
                        when (myModel.messageOfToday) {
                            0L -> {
                                binding.finishedCommentText.text = myModel.message2
                                if (myModel.message2 == ""){
                                    if (locale == Locale.JAPAN){
                                        binding.finishedCommentText.text = "お疲れ様でした！"
                                    }else{
                                        binding.finishedCommentText.text = "Good job!"
                                    }
                                }
                                realm.executeTransaction {
                                    myModel.messageOfToday = 1L
                                }
                            }
                            1L -> {
                                binding.finishedCommentText.text = myModel.message1
                                if (myModel.message1 == ""){
                                    if (locale == Locale.JAPAN){
                                        binding.finishedCommentText.text = "お疲れ様でした！"
                                    }else{
                                        binding.finishedCommentText.text = "Good job!"
                                    }
                                }
                                realm.executeTransaction {
                                    myModel.messageOfToday = 0L
                                }
                            }
                        }
                    }
                    3L -> {
                        when (myModel.messageOfToday) {
                            0L -> {
                                binding.finishedCommentText.text = myModel.message2
                                if (myModel.message2 == ""){
                                    if (locale == Locale.JAPAN){
                                        binding.finishedCommentText.text = "お疲れ様でした！"
                                    }else{
                                        binding.finishedCommentText.text = "Good job!"
                                    }
                                }
                                realm.executeTransaction {
                                    myModel.messageOfToday = 1L
                                }
                            }
                            1L -> {
                                binding.finishedCommentText.text = myModel.message3
                                if (myModel.message3 == ""){
                                    if (locale == Locale.JAPAN){
                                        binding.finishedCommentText.text = "お疲れ様でした！"
                                    }else{
                                        binding.finishedCommentText.text = "Good job!"
                                    }
                                }
                                realm.executeTransaction {
                                    myModel.messageOfToday = 2L
                                }
                            }
                            2L -> {
                                binding.finishedCommentText.text = myModel.message1
                                if (myModel.message1 == ""){
                                    if (locale == Locale.JAPAN){
                                        binding.finishedCommentText.text = "お疲れ様でした！"
                                    }else{
                                        binding.finishedCommentText.text = "Good job!"
                                    }
                                }
                                realm.executeTransaction {
                                    myModel.messageOfToday = 0L
                                }
                            }
                        }
                    }
                }

                binding.finishedComment.visibility = View.VISIBLE

                realm.executeTransaction {
                    val currentId = realm.where<CalendarModel>().max("id")
                    val nextId = (currentId?.toLong() ?: 0L) + 1L
                    val calendarModel = realm.createObject<CalendarModel>(nextId)

                    val nowDate = LocalDate.now()
                    calendarModel.listId = getId
                    calendarModel.year = nowDate.year.toLong()
                    calendarModel.month = nowDate.monthValue.toLong()
                    calendarModel.day = nowDate.dayOfMonth.toLong()
                }
                initWidgets()
                selectedDate = LocalDate.now()
                setMonthView()

                Handler().postDelayed({
                    binding.button.isEnabled = true
                },1500)
            }else{
                binding.progressbar.progress = 0
                binding.finishedComment.visibility = View.GONE
                //binding.button.setBackgroundColor(Color.parseColor("#139cfc"))
                if (locale == Locale.JAPAN){
                    binding.button.text = "完了"
                }else{
                    binding.button.text = "Finish"
                }

                when(myModel?.color){
                    0L ->{
                        binding.button.setBackgroundColor(Color.parseColor("#DA1100"))
                    }
                    1L ->{
                        binding.button.setBackgroundColor(Color.parseColor("#ff5c19"))
                    }
                    2L ->{
                        binding.button.setBackgroundColor(Color.parseColor("#FFDF19"))
                    }
                    3L ->{
                        binding.button.setBackgroundColor(Color.parseColor("#ffa2c9"))
                    }
                    4L ->{
                        binding.button.setBackgroundColor(Color.parseColor("#139cfc"))
                    }
                    5L ->{
                        binding.button.setBackgroundColor(Color.parseColor("#1becff"))
                    }
                    6L ->{
                        binding.button.setBackgroundColor(Color.parseColor("#18c586"))
                    }
                    7L ->{
                        binding.button.setBackgroundColor(Color.parseColor("#7FEC44"))
                    }
                    8L ->{
                        binding.button.setBackgroundColor(Color.parseColor("#8218ff"))
                    }
                    9L ->{
                        binding.button.setBackgroundColor(Color.parseColor("#DD8C50"))
                    }
                    10L ->{
                        binding.button.setBackgroundColor(Color.parseColor("#F4F4F4"))
                        binding.button.setTextColor(Color.parseColor("#373737"))
                    }
                    11L ->{
                        binding.button.setBackgroundColor(Color.parseColor("#373737"))
                    }
                }

                realm.executeTransaction {
                    myModel?.todayFinished = 0L
                }

                val nowDate = LocalDate.now()
                val myModelResult = realm.where<CalendarModel>()
                    .equalTo("listId",getId)
                    .equalTo("year",nowDate.year.toLong())
                    .equalTo("month",nowDate.monthValue.toLong())
                    .equalTo("day",nowDate.dayOfMonth.toLong()).findFirst()
                realm.executeTransaction {
                    myModelResult?.deleteFromRealm()
                }
                initWidgets()
                selectedDate = LocalDate.now()
                setMonthView()
            }
        }

        //カレンダー
        initWidgets()
        selectedDate = LocalDate.now()
        setMonthView()

        binding.calendarBack.setOnClickListener {
            initWidgets()
            selectedDate = selectedDate!!.minusMonths(1)
            setMonthView()
        }

        binding.calendarNext.setOnClickListener {
            initWidgets()
            selectedDate = selectedDate!!.plusMonths(1)
            setMonthView()
        }

    }

    private fun progressChanged(percentage: Int) {
        val animation = ObjectAnimator.ofInt(binding.progressbar, "progress", percentage)
        animation.duration = 1500
        animation.interpolator = DecelerateInterpolator()
        animation.start()
        binding.progressbar.progress = 100
    }

    private fun byteToBitmap(bytes:ByteArray): Bitmap {
        val opt = BitmapFactory.Options()
        opt.inJustDecodeBounds = false
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, opt)
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    //カレンダー
    private fun initWidgets() {
        calendarRecyclerView = findViewById(R.id.calendar_recyclerview)
        monthYearText = findViewById(R.id.month_text)
    }

    private fun setMonthView() {
        val getId = intent.getLongExtra("id",0)
        monthYearText!!.text = monthYearFromDate(selectedDate)
        val daysInMonth = daysInMonthArray(selectedDate)
        val month = selectedDate?.monthValue!!.toLong()
        val year = selectedDate?.year!!.toLong()
        val calendarAdapter = CalendarAdapter(daysInMonth,month,year,getId)
        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(applicationContext, 7)
        calendarRecyclerView!!.layoutManager = layoutManager
        calendarRecyclerView!!.adapter = calendarAdapter
    }

    private fun daysInMonthArray(date: LocalDate?): ArrayList<Long> {
        val daysInMonthArray = ArrayList<Long>()
        val yearMonth = YearMonth.from(date)
        val daysInMonth = yearMonth.lengthOfMonth()
        val firstOfMonth = selectedDate!!.withDayOfMonth(1)
        val dayOfWeek = firstOfMonth.dayOfWeek.value
        for (i in 1..42) {
            if (i <= dayOfWeek || i > daysInMonth + dayOfWeek) {
                daysInMonthArray.add(0)
            } else {
                daysInMonthArray.add((i - dayOfWeek).toLong())
            }
        }
        return daysInMonthArray
    }

    private fun monthYearFromDate(date: LocalDate?): String {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
        return date!!.format(formatter)
    }
}