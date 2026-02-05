package com.ruriboshi.habitter

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.ruriboshi.habitter.databinding.ActivityEditBinding
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Locale

class EditActivity : AppCompatActivity() {

    private lateinit var realm: Realm
    private lateinit var binding: ActivityEditBinding
    private lateinit var adView: AdView
    var image:Bitmap? = null
    var myHour1:Long = 7L
    var myMinute1:Long = 0L
    var myHour2:Long = 7L
    var myMinute2:Long = 0L
    var myHour3:Long = 7L
    var myMinute3:Long = 0L
    var colorId:Long = 0L
    var calendarId:Long = 0L

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setContentView(R.layout.activity_edit)

        realm = Realm.getDefaultInstance()

        //AdMob
        MobileAds.initialize(this){}

        adView = findViewById(R.id.adView_edit)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        adView.setBackgroundColor(Color.parseColor("#E6FAFB"))
        //AdMob

        val remindCommentText1:TextView = findViewById(R.id.remind_comment_text_1)
        val remindCommentText2:TextView = findViewById(R.id.remind_comment_text_2)
        val remindCommentText3:TextView = findViewById(R.id.remind_comment_text_3)

        val finishedCommentText1:TextView = findViewById(R.id.finish_comment_text_1)
        val finishedCommentText2:TextView = findViewById(R.id.finish_comment_text_2)
        val finishedCommentText3:TextView = findViewById(R.id.finish_comment_text_3)
        val editTitle:TextView = findViewById(R.id.edit_habit_title)

        val getId = intent.getLongExtra("id",0)

        val locale = Locale.getDefault()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // SDKのバージョンがR以降である場合にダークモード設定が導入されたため、それを判定する
            if (this.theme.resources.configuration.isNightModeActive) {
                // ダークモードの場合にこのスコープに入る
                binding.editLayout.setBackgroundColor(Color.parseColor("#003249"))
                binding.toolbar.setBackgroundColor(Color.parseColor("#96ADB8"))
            }
        }

        var finishedComment = 0
        var remindComment = 0
        var remindDate = 0

        editTitle.setOnKeyListener {
                view, keyCode, _ -> handleKeyEvent(view, keyCode)
        }

        /*finishedCommentText1.setOnKeyListener {
                view, keyCode, _ -> handleKeyEvent(view, keyCode)
        }

        finishedCommentText2.setOnKeyListener {
                view, keyCode, _ -> handleKeyEvent(view, keyCode)
        }

        finishedCommentText3.setOnKeyListener {
                view, keyCode, _ -> handleKeyEvent(view, keyCode)
        }

        remindCommentText1.setOnKeyListener {
                view, keyCode, _ -> handleKeyEvent(view, keyCode)
        }*/


        if (getId == 0L){
            if (locale == Locale.JAPAN){
                binding.ToolbarTitle.text = "習慣を作成"
            }else{
                binding.ToolbarTitle.text = "Create a habit"
            }
            binding.deleteHabitBtn.visibility = View.INVISIBLE
            binding.remind1.visibility = View.GONE
            binding.remind2.visibility = View.GONE
            binding.remind3.visibility = View.GONE
            binding.remindComment1.visibility = View.GONE
            binding.remindComment2.visibility = View.GONE
            binding.remindComment3.visibility = View.GONE
            binding.finishComment1.visibility = View.GONE
            binding.finishComment2.visibility = View.GONE
            binding.finishComment3.visibility = View.GONE
            binding.addRemindBtn.visibility = View.VISIBLE
            binding.addFinishCommentBtn.visibility = View.VISIBLE
            binding.addRemindCommentBtn.visibility = View.VISIBLE
            binding.colorSkyblue.setImageResource(R.drawable.circle_icon)
            colorId = 5
        }else{
            if (locale == Locale.JAPAN){
                binding.ToolbarTitle.text = "習慣を編集"
            }else{
                binding.ToolbarTitle.text = "Edit the habit"
            }
            val model = realm.where(MyModel::class.java).equalTo("id", getId).findFirst()
            editTitle.text = model?.title.toString()

            if (model?.iconImage.contentEquals(ByteArray(0))){
                binding.iconImage.setImageResource(R.drawable.app_mainicon)
            }else{
                val previousImage:Bitmap = byteToBitmap(model?.iconImage!!)
                binding.iconImage.setImageBitmap(previousImage)
                image = previousImage
            }

            if (model?.remind1 == 1L){
                binding.remind1.visibility = View.VISIBLE
                myHour1 = model.hour1
                myMinute1 = model.minute1
                binding.addRemindBtn.visibility = View.GONE
                remindDate = 1
                if (myMinute1 < 10){
                    binding.remindDate1.text = "$myHour1:0$myMinute1"
                }else{
                    binding.remindDate1.text = "$myHour1:$myMinute1"
                }
            }else{
                binding.remind1.visibility = View.GONE
                binding.addRemindBtn.visibility = View.VISIBLE
                remindDate = 0
            }

            binding.remind2.visibility = View.GONE
            binding.remind3.visibility = View.GONE

            /*if (model?.remind2 == 1L){
                binding.remind2.visibility = View.VISIBLE
                myHour2 = model.hour2
                myMinute2 = model.minute2
                if (myMinute2 < 10){
                    binding.remindDate2.text = "$myHour2:0$myMinute2"
                }else{
                    binding.remindDate2.text = "$myHour2:$myMinute2"
                }
            }else{
                binding.remind2.visibility = View.GONE
            }

            if (model?.remind3 == 1L){
                binding.remind3.visibility = View.VISIBLE
                myHour3 = model.hour3
                myMinute3 = model.minute3
                if (myMinute3 < 10){
                    binding.remindDate3.text = "$myHour3:0$myMinute3"
                }else{
                    binding.remindDate3.text = "$myHour3:$myMinute3"
                }
            }else{
                binding.remind3.visibility = View.GONE
            }*/

            /*remindDate =
                if (model?.remind3 == 1L){
                    3
                }else if (model?.remind2 == 1L){
                    2
                }else if (model?.remind1 == 1L){
                    1
                }else{
                    0
                }*/

            when (model?.notificationComments) {
               /* 3L -> {
                    binding.remindComment3.visibility = View.VISIBLE
                    binding.remindComment2.visibility = View.VISIBLE
                    binding.remindComment1.visibility = View.VISIBLE
                    remindCommentText1.text = model.notification1
                    remindCommentText2.text = model.notification2
                    remindCommentText3.text = model.notification3
                    remindComment = 3
                }
                2L -> {
                    binding.remindComment3.visibility = View.GONE
                    binding.remindComment2.visibility = View.VISIBLE
                    binding.remindComment1.visibility = View.VISIBLE
                    remindCommentText1.text = model.notification1
                    remindCommentText2.text = model.notification2
                    remindComment = 2
                }*/
                1L -> {
                    binding.remindComment3.visibility = View.GONE
                    binding.remindComment2.visibility = View.GONE
                    binding.remindComment1.visibility = View.VISIBLE
                    remindCommentText1.text = model.notification1
                    binding.addRemindCommentBtn.visibility = View.GONE
                    remindComment = 1
                }
                else -> {
                    binding.remindComment3.visibility = View.GONE
                    binding.remindComment2.visibility = View.GONE
                    binding.remindComment1.visibility = View.GONE
                    binding.addRemindCommentBtn.visibility = View.VISIBLE
                    remindComment = 0
                }
            }

            when (model?.comments) {
                3L -> {
                    binding.finishComment3.visibility = View.VISIBLE
                    binding.finishComment2.visibility = View.VISIBLE
                    binding.finishComment1.visibility = View.VISIBLE
                    finishedCommentText1.text = model.message1
                    finishedCommentText2.text = model.message2
                    finishedCommentText3.text = model.message3
                    finishedComment = 3
                    binding.addFinishCommentBtn.visibility = View.GONE
                }
                2L -> {
                    binding.finishComment3.visibility = View.GONE
                    binding.finishComment2.visibility = View.VISIBLE
                    binding.finishComment1.visibility = View.VISIBLE
                    finishedCommentText1.text = model.message1
                    finishedCommentText2.text = model.message2
                    binding.addFinishCommentBtn.visibility = View.VISIBLE
                    finishedComment = 2
                }
                1L -> {
                    binding.finishComment3.visibility = View.GONE
                    binding.finishComment2.visibility = View.GONE
                    binding.finishComment1.visibility = View.VISIBLE
                    finishedCommentText1.text = model.message1
                    binding.addFinishCommentBtn.visibility = View.VISIBLE
                    finishedComment = 1
                }
                else -> {
                    binding.finishComment3.visibility = View.GONE
                    binding.finishComment2.visibility = View.GONE
                    binding.finishComment1.visibility = View.GONE
                    binding.addFinishCommentBtn.visibility = View.VISIBLE
                    finishedComment = 0
                }
            }

            createNotificationChannel()

            when(model?.color){
                0L ->{
                    colorId = 0
                    binding.colorRed.setImageResource(R.drawable.circle_icon)
                }
                1L ->{
                    colorId = 1
                    binding.colorOrange.setImageResource(R.drawable.circle_icon)
                }
                2L ->{
                    colorId = 2
                    binding.colorYellow.setImageResource(R.drawable.circle_icon)
                }
                3L ->{
                    colorId = 3
                    binding.colorPink.setImageResource(R.drawable.circle_icon)
                }
                4L ->{
                    colorId = 4
                    binding.colorBlue.setImageResource(R.drawable.circle_icon)
                }
                5L ->{
                    colorId = 5
                    binding.colorSkyblue.setImageResource(R.drawable.circle_icon)
                }
                6L ->{
                    colorId = 6
                    binding.colorGreen.setImageResource(R.drawable.circle_icon)
                }
                7L ->{
                    colorId = 7
                    binding.colorYellowgreen.setImageResource(R.drawable.circle_icon)
                }
                8L ->{
                    colorId = 8
                    binding.colorPurple.setImageResource(R.drawable.circle_icon)
                }
                9L ->{
                    colorId = 9
                    binding.colorBrawn.setImageResource(R.drawable.circle_icon)
                }
                10L ->{
                    colorId = 10
                    binding.colorWhite.setImageResource(R.drawable.circle_icon)
                }
                11L ->{
                    colorId = 11
                    binding.colorBlack.setImageResource(R.drawable.circle_icon)
                }

            }

            binding.deleteHabitBtn.visibility = View.VISIBLE
        }

        binding.colorRed.setOnClickListener {
            binding.colorOrange.setImageResource(0)
            binding.colorYellow.setImageResource(0)
            binding.colorPink.setImageResource(0)
            binding.colorBlack.setImageResource(0)
            binding.colorBlue.setImageResource(0)
            binding.colorBrawn.setImageResource(0)
            binding.colorGreen.setImageResource(0)
            binding.colorPurple.setImageResource(0)
            binding.colorWhite.setImageResource(0)
            binding.colorYellowgreen.setImageResource(0)
            binding.colorSkyblue.setImageResource(0)

            colorId = 0
            binding.colorRed.setImageResource(R.drawable.circle_icon)
        }

        binding.colorOrange.setOnClickListener {
            binding.colorRed.setImageResource(0)
            binding.colorYellow.setImageResource(0)
            binding.colorPink.setImageResource(0)
            binding.colorBlack.setImageResource(0)
            binding.colorBlue.setImageResource(0)
            binding.colorBrawn.setImageResource(0)
            binding.colorGreen.setImageResource(0)
            binding.colorPurple.setImageResource(0)
            binding.colorWhite.setImageResource(0)
            binding.colorYellowgreen.setImageResource(0)
            binding.colorSkyblue.setImageResource(0)

            colorId = 1
            binding.colorOrange.setImageResource(R.drawable.circle_icon)
        }

        binding.colorYellow.setOnClickListener {
            binding.colorOrange.setImageResource(0)
            binding.colorRed.setImageResource(0)
            binding.colorPink.setImageResource(0)
            binding.colorBlack.setImageResource(0)
            binding.colorBlue.setImageResource(0)
            binding.colorBrawn.setImageResource(0)
            binding.colorGreen.setImageResource(0)
            binding.colorPurple.setImageResource(0)
            binding.colorWhite.setImageResource(0)
            binding.colorYellowgreen.setImageResource(0)
            binding.colorSkyblue.setImageResource(0)

            colorId = 2
            binding.colorYellow.setImageResource(R.drawable.circle_icon)
        }

        binding.colorPink.setOnClickListener {
            binding.colorOrange.setImageResource(0)
            binding.colorYellow.setImageResource(0)
            binding.colorRed.setImageResource(0)
            binding.colorBlack.setImageResource(0)
            binding.colorBlue.setImageResource(0)
            binding.colorBrawn.setImageResource(0)
            binding.colorGreen.setImageResource(0)
            binding.colorPurple.setImageResource(0)
            binding.colorWhite.setImageResource(0)
            binding.colorYellowgreen.setImageResource(0)
            binding.colorSkyblue.setImageResource(0)

            colorId = 3
            binding.colorPink.setImageResource(R.drawable.circle_icon)
        }

        binding.colorBlue.setOnClickListener {
            binding.colorOrange.setImageResource(0)
            binding.colorYellow.setImageResource(0)
            binding.colorPink.setImageResource(0)
            binding.colorBlack.setImageResource(0)
            binding.colorRed.setImageResource(0)
            binding.colorBrawn.setImageResource(0)
            binding.colorGreen.setImageResource(0)
            binding.colorPurple.setImageResource(0)
            binding.colorWhite.setImageResource(0)
            binding.colorYellowgreen.setImageResource(0)
            binding.colorSkyblue.setImageResource(0)

            colorId = 4
            binding.colorBlue.setImageResource(R.drawable.circle_icon)
        }

        binding.colorSkyblue.setOnClickListener {
            binding.colorOrange.setImageResource(0)
            binding.colorYellow.setImageResource(0)
            binding.colorPink.setImageResource(0)
            binding.colorBlack.setImageResource(0)
            binding.colorBlue.setImageResource(0)
            binding.colorBrawn.setImageResource(0)
            binding.colorGreen.setImageResource(0)
            binding.colorPurple.setImageResource(0)
            binding.colorWhite.setImageResource(0)
            binding.colorYellowgreen.setImageResource(0)
            binding.colorRed.setImageResource(0)

            colorId = 5
            binding.colorSkyblue.setImageResource(R.drawable.circle_icon)
        }

        binding.colorGreen.setOnClickListener {
            binding.colorOrange.setImageResource(0)
            binding.colorYellow.setImageResource(0)
            binding.colorPink.setImageResource(0)
            binding.colorBlack.setImageResource(0)
            binding.colorBlue.setImageResource(0)
            binding.colorBrawn.setImageResource(0)
            binding.colorRed.setImageResource(0)
            binding.colorPurple.setImageResource(0)
            binding.colorWhite.setImageResource(0)
            binding.colorYellowgreen.setImageResource(0)
            binding.colorSkyblue.setImageResource(0)

            colorId = 6
            binding.colorGreen.setImageResource(R.drawable.circle_icon)
        }

        binding.colorYellowgreen.setOnClickListener {
            binding.colorOrange.setImageResource(0)
            binding.colorYellow.setImageResource(0)
            binding.colorPink.setImageResource(0)
            binding.colorBlack.setImageResource(0)
            binding.colorBlue.setImageResource(0)
            binding.colorBrawn.setImageResource(0)
            binding.colorGreen.setImageResource(0)
            binding.colorPurple.setImageResource(0)
            binding.colorWhite.setImageResource(0)
            binding.colorRed.setImageResource(0)
            binding.colorSkyblue.setImageResource(0)

            colorId = 7
            binding.colorYellowgreen.setImageResource(R.drawable.circle_icon)
        }

        binding.colorPurple.setOnClickListener {
            binding.colorOrange.setImageResource(0)
            binding.colorYellow.setImageResource(0)
            binding.colorPink.setImageResource(0)
            binding.colorBlack.setImageResource(0)
            binding.colorBlue.setImageResource(0)
            binding.colorBrawn.setImageResource(0)
            binding.colorGreen.setImageResource(0)
            binding.colorRed.setImageResource(0)
            binding.colorWhite.setImageResource(0)
            binding.colorYellowgreen.setImageResource(0)
            binding.colorSkyblue.setImageResource(0)

            colorId = 8
            binding.colorPurple.setImageResource(R.drawable.circle_icon)
        }

        binding.colorBrawn.setOnClickListener {
            binding.colorOrange.setImageResource(0)
            binding.colorYellow.setImageResource(0)
            binding.colorPink.setImageResource(0)
            binding.colorBlack.setImageResource(0)
            binding.colorBlue.setImageResource(0)
            binding.colorRed.setImageResource(0)
            binding.colorGreen.setImageResource(0)
            binding.colorPurple.setImageResource(0)
            binding.colorWhite.setImageResource(0)
            binding.colorYellowgreen.setImageResource(0)
            binding.colorSkyblue.setImageResource(0)

            colorId = 9
            binding.colorBrawn.setImageResource(R.drawable.circle_icon)
        }

        binding.colorWhite.setOnClickListener {
            binding.colorOrange.setImageResource(0)
            binding.colorYellow.setImageResource(0)
            binding.colorPink.setImageResource(0)
            binding.colorBlack.setImageResource(0)
            binding.colorBlue.setImageResource(0)
            binding.colorBrawn.setImageResource(0)
            binding.colorGreen.setImageResource(0)
            binding.colorPurple.setImageResource(0)
            binding.colorRed.setImageResource(0)
            binding.colorYellowgreen.setImageResource(0)
            binding.colorSkyblue.setImageResource(0)

            colorId = 10
            binding.colorWhite.setImageResource(R.drawable.circle_icon)
        }

        binding.colorBlack.setOnClickListener {
            binding.colorOrange.setImageResource(0)
            binding.colorYellow.setImageResource(0)
            binding.colorPink.setImageResource(0)
            binding.colorRed.setImageResource(0)
            binding.colorBlue.setImageResource(0)
            binding.colorBrawn.setImageResource(0)
            binding.colorGreen.setImageResource(0)
            binding.colorPurple.setImageResource(0)
            binding.colorWhite.setImageResource(0)
            binding.colorYellowgreen.setImageResource(0)
            binding.colorSkyblue.setImageResource(0)

            colorId = 11
            binding.colorBlack.setImageResource(R.drawable.circle_icon)
        }

        binding.addRemindBtn.setOnClickListener {
            if (remindDate == 0) {
                binding.remind1.visibility = View.VISIBLE
                remindDate += 1
                myHour1 = 7L
                myMinute1 = 0L
                binding.remindDate1.text = "7:00"
                binding.addRemindBtn.visibility = View.GONE
            }
            /*}else if (remindDate == 1){
                binding.remind2.visibility = View.VISIBLE
                remindDate += 1
                myHour2 = 7L
                myMinute2 = 0L
                binding.remindDate2.text = "7:00"
            }else if (remindDate == 2){
                binding.remind3.visibility = View.VISIBLE
                remindDate += 1
                myHour3 = 7L
                myMinute3 = 0L
                binding.remindDate3.text = "7:00"
            }else{
                if (locale == Locale.JAPAN){
                    Toast.makeText(this,"リマインダーは３つまで登録できます",Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this,"You can register up to 3 reminders.",Toast.LENGTH_SHORT).show()
                }
            }*/
        }

        binding.remind1.setOnClickListener {
            val getTime = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hourOfDay, minute ->
                getTime.set(Calendar.HOUR_OF_DAY,hourOfDay)
                getTime.set(Calendar.MINUTE,minute)
                myHour1 = hourOfDay.toLong()
                myMinute1 = minute.toLong()
                if (myMinute1 < 10){
                    binding.remindDate1.text = "$myHour1:0$myMinute1"
                }else{
                    binding.remindDate1.text = "$myHour1:$myMinute1"
                }
            }
            //TimePickerDialog(this,timeSetListener,getTime.get(Calendar.HOUR_OF_DAY),getTime.get(Calendar.MINUTE),true).show()
            TimePickerDialog(this,timeSetListener,myHour1.toInt(),myMinute1.toInt(),true).show()
        }

        /*binding.remind2.setOnClickListener {
            val getTime = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hourOfDay, minute ->
                getTime.set(Calendar.HOUR_OF_DAY,hourOfDay)
                getTime.set(Calendar.MINUTE,minute)
                myHour2 = hourOfDay.toLong()
                myMinute2 = minute.toLong()
                if (myMinute2 < 10){
                    binding.remindDate2.text = "$myHour2:0$myMinute2"
                }else{
                    binding.remindDate2.text = "$myHour2:$myMinute2"
                }
            }
            TimePickerDialog(this,timeSetListener,getTime.get(Calendar.HOUR_OF_DAY),getTime.get(Calendar.MINUTE),true).show()
        }

        binding.remind3.setOnClickListener {
            val getTime = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hourOfDay, minute ->
                getTime.set(Calendar.HOUR_OF_DAY,hourOfDay)
                getTime.set(Calendar.MINUTE,minute)
                myHour3 = hourOfDay.toLong()
                myMinute3 = minute.toLong()
                if (myMinute3 < 10){
                    binding.remindDate3.text = "$myHour3:0$myMinute3"
                }else{
                    binding.remindDate3.text = "$myHour3:$myMinute3"
                }
            }
            TimePickerDialog(this,timeSetListener,getTime.get(Calendar.HOUR_OF_DAY),getTime.get(Calendar.MINUTE),true).show()
        }*/

        binding.cancelRemindBtn1.setOnClickListener {
            binding.remind1.visibility = View.GONE
            binding.addRemindBtn.visibility = View.VISIBLE
            remindDate = 0
            myHour1 = 7
            myMinute1 = 0
        }

        /*binding.cancelRemindBtn2.setOnClickListener {
            if (remindDate == 3){
                binding.remind3.visibility = View.GONE
                remindDate = 2
                myHour2 = myHour3
                myMinute2 = myMinute3
                if (myMinute2 < 10){
                    binding.remindDate2.text = "$myHour2:0$myMinute2"
                }else{
                    binding.remindDate2.text = "$myHour2:$myMinute2"
                }
                myHour3 = 7
                myMinute3 = 0
            }else{
                binding.remind2.visibility = View.GONE
                remindDate = 1
                myHour2 = 7
                myMinute2 = 0
            }*/

        binding.changeIconBtn.setOnClickListener {
            selectPhoto()
        }

        binding.addRemindCommentBtn.setOnClickListener {
            if (remindComment == 0){
                remindCommentText1.text = ""
                binding.remindComment1.visibility = View.VISIBLE
                binding.addRemindCommentBtn.visibility = View.GONE
                remindComment = 1
            }/*else{
                if (locale == Locale.JAPAN){
                    Toast.makeText(this,"リマインドのコメントは３つまで登録できます",Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this,"You can register up to 3 reminder comments.",Toast.LENGTH_SHORT).show()
                }
            }*/

            /*}else if (remindComment == 1){
                remindCommentText2.text = ""
                binding.remindComment2.visibility = View.VISIBLE
                remindComment = 2
            }else if (remindComment == 2){
                remindCommentText3.text = ""
                binding.remindComment3.visibility = View.VISIBLE
                remindComment = 3*/
        }

        binding.cancelRemindComment1.setOnClickListener {
            binding.remindComment1.visibility = View.GONE
            binding.addRemindCommentBtn.visibility = View.VISIBLE
            remindComment = 0
            /*if (remindComment > 1){
                if (remindComment == 3){
                    remindCommentText1.text = remindCommentText2.text
                    remindCommentText2.text = remindCommentText3.text
                    binding.remindComment3.visibility = View.GONE
                    remindComment = 2
                }else{
                    remindCommentText1.text = remindCommentText2.text
                    binding.remindComment2.visibility = View.GONE
                    remindComment = 1
                }
            }else{
                binding.remindComment1.visibility = View.GONE
                remindComment = 0
            }*/
        }

        /*binding.cancelRemindComment2.setOnClickListener {
            if (remindComment == 3){
                remindCommentText2.text = remindCommentText3.text
                binding.remindComment3.visibility = View.GONE
                remindComment = 2
            }else{
                binding.remindComment2.visibility = View.GONE
                remindComment = 1
            }
        }

        binding.cancelRemindComment3.setOnClickListener {
            binding.remindComment3.visibility = View.GONE
            remindComment = 2
        }*/

        binding.addFinishCommentBtn.setOnClickListener {
            when (finishedComment) {
                0 -> {
                    finishedCommentText1.text = ""
                    binding.finishComment1.visibility = View.VISIBLE
                    finishedComment = 1
                }
                1 -> {
                    finishedCommentText2.text = ""
                    binding.finishComment2.visibility = View.VISIBLE
                    finishedComment = 2
                }
                2 -> {
                    finishedCommentText3.text = ""
                    binding.finishComment3.visibility = View.VISIBLE
                    finishedComment = 3
                    binding.addFinishCommentBtn.visibility = View.GONE
                }
            }/*else{
                if (locale == Locale.JAPAN){
                    Toast.makeText(this,"完了後のコメントは３つまで登録できます",Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this,"You can register up to 3 comments after completion.",Toast.LENGTH_SHORT).show()
                }
            }*/
        }

        binding.cancelFinishCom1.setOnClickListener {
            if (finishedComment > 1){
                if (finishedComment == 3){
                    finishedCommentText1.text = finishedCommentText2.text
                    finishedCommentText2.text = finishedCommentText3.text
                    binding.finishComment3.visibility = View.GONE
                    binding.addFinishCommentBtn.visibility = View.VISIBLE
                    finishedComment = 2
                }else{
                    finishedCommentText1.text = finishedCommentText2.text
                    binding.finishComment2.visibility = View.GONE
                    finishedComment = 1
                }
            }else{
                binding.finishComment1.visibility = View.GONE
                finishedComment = 0
            }
        }

        binding.cancelFinishCom2.setOnClickListener {
            if (finishedComment == 3){
                finishedCommentText2.text = finishedCommentText3.text
                binding.finishComment3.visibility = View.GONE
                binding.addFinishCommentBtn.visibility = View.VISIBLE
                finishedComment = 2
            }else{
                binding.finishComment2.visibility = View.GONE
                finishedComment = 1
            }
        }

        binding.cancelFinishCom3.setOnClickListener {
            binding.finishComment3.visibility = View.GONE
            binding.addFinishCommentBtn.visibility = View.VISIBLE
            finishedComment = 2
        }

        binding.saveHabit.setOnClickListener {
            var title = ""
            var message1 = ""
            var message2 = ""
            var message3 = ""
            var notification1 = ""
            var notification2 = ""
            var notification3 = ""

            if (!binding.editHabitTitle.text.isNullOrEmpty()){
                title = binding.editHabitTitle.text.toString()
                message1 = finishedCommentText1.text.toString()
                message2 = finishedCommentText2.text.toString()
                message3 = finishedCommentText3.text.toString()
                notification1 = remindCommentText1.text.toString()
                notification2 = remindCommentText2.text.toString()
                notification3 = remindCommentText3.text.toString()

                if (getId == 0L){
                    realm.executeTransaction {
                        val currentId = realm.where<MyModel>().max("id")
                        val nextId = (currentId?.toLong() ?: 0L) + 1L

                        val myModel = realm.createObject<MyModel>(nextId)
                        myModel.title = title
                        myModel.color = colorId
                        myModel.iconImage = ByteArray(0)

                        if (image != null){
                            val bmp: Bitmap = image as Bitmap
                            val cropImage = centerCropBitmap(bmp)
                            val stream = ByteArrayOutputStream()
                            cropImage.compress(Bitmap.CompressFormat.PNG, 100, stream)
                            val byteArray = stream.toByteArray()
                            myModel.iconImage = byteArray
                        }

                        myModel.remind1 = 0L
                        myModel.remind2 = 0L
                        myModel.remind3 = 0L
                        myModel.hour1 = 0L
                        myModel.minute1 = 0L
                        myModel.hour2 = 0L
                        myModel.minute2 = 0L
                        myModel.hour3 = 0L
                        myModel.minute3 = 0L
                        myModel.todayFinished = 0L
                        myModel.messageOfToday = 0L
                        myModel.notification1 = ""
                        myModel.notification2 = ""
                        myModel.notification3 = ""
                        myModel.order = nextId
                        myModel.notificationComments = 0
                        myModel.message1 = ""
                        myModel.message2 = ""
                        myModel.message3 = ""
                        myModel.comments = 0

                        when (remindComment) {
                            3 -> {
                                myModel.notification1 = notification1
                                myModel.notification2 = notification2
                                myModel.notification3 = notification3
                                myModel.notificationComments = 3L
                            }
                            2 -> {
                                myModel.notification1 = notification1
                                myModel.notification2 = notification2
                                myModel.notificationComments = 2L
                            }
                            1 -> {
                                myModel.notification1 = notification1
                                myModel.notificationComments = 1L
                            }
                        }

                        when (finishedComment) {
                            3 -> {
                                myModel.message1 = message1
                                myModel.message2 = message2
                                myModel.message3 = message3
                                myModel.comments = 3L
                            }
                            2 -> {
                                myModel.message1 = message1
                                myModel.message2 = message2
                                myModel.comments = 2L
                            }
                            1 -> {
                                myModel.message1 = message1
                                myModel.comments = 1L
                            }
                        }

                        when (remindDate) {
                            3 -> {
                                myModel.remind1 = 1L
                                myModel.hour1 = myHour1
                                myModel.minute1 = myMinute1

                                myModel.remind2 = 1L
                                myModel.hour2 = myHour2
                                myModel.minute2 = myMinute2

                                myModel.remind3 = 1L
                                myModel.hour3 = myHour3
                                myModel.minute3 = myMinute3
                            }
                            2 -> {
                                myModel.remind1 = 1L
                                myModel.hour1 = myHour1
                                myModel.minute1 = myMinute1

                                myModel.remind2 = 1L
                                myModel.hour2 = myHour2
                                myModel.minute2 = myMinute2
                            }
                            1 -> {
                                myModel.remind1 = 1L
                                myModel.hour1 = myHour1
                                myModel.minute1 = myMinute1
                                scheduleNotification(nextId,notification1)
                            }
                        }

                    }
                }else{
                    realm.executeTransaction {
                        val myModel = realm.where(MyModel::class.java).equalTo("id",getId).findFirst()
                        myModel?.title = title
                        myModel?.color = colorId
                        myModel?.iconImage = ByteArray(0)

                        if (image != null){
                            val bmp: Bitmap = image as Bitmap
                            val cropImage = centerCropBitmap(bmp)
                            val stream = ByteArrayOutputStream()
                            cropImage.compress(Bitmap.CompressFormat.PNG, 100, stream)
                            val byteArray = stream.toByteArray()
                            myModel?.iconImage = byteArray
                        }

                        myModel?.remind1 = 0L
                        myModel?.remind2 = 0L
                        myModel?.remind3 = 0L
                        myModel?.hour1 = 0L
                        myModel?.minute1 = 0L
                        myModel?.hour2 = 0L
                        myModel?.minute2 = 0L
                        myModel?.hour3 = 0L
                        myModel?.minute3 = 0L
                        myModel?.messageOfToday = 0L
                        myModel?.notification1 = ""
                        myModel?.notification2 = ""
                        myModel?.notification3 = ""
                        myModel?.message1 = ""
                        myModel?.message2 = ""
                        myModel?.message3 = ""
                        myModel?.comments = 0

                        when (remindComment) {
                            3 -> {
                                myModel?.notification1 = notification1
                                myModel?.notification2 = notification2
                                myModel?.notification3 = notification3
                                myModel?.notificationComments = 3L
                            }
                            2 -> {
                                myModel?.notification1 = notification1
                                myModel?.notification2 = notification2
                                myModel?.notificationComments = 2L
                            }
                            1 -> {
                                myModel?.notification1 = notification1
                                myModel?.notificationComments = 1L
                            }
                        }

                        when (finishedComment) {
                            3 -> {
                                myModel?.message1 = message1
                                myModel?.message2 = message2
                                myModel?.message3 = message3
                                myModel?.comments = 3
                            }
                            2 -> {
                                myModel?.message1 = message1
                                myModel?.message2 = message2
                                myModel?.comments = 2
                            }
                            1 -> {
                                myModel?.message1 = message1
                                myModel?.comments = 1
                            }
                        }

                        when (remindDate) {
                            3 -> {
                                myModel?.remind1 = 1L
                                myModel?.hour1 = myHour1
                                myModel?.minute1 = myMinute1

                                myModel?.remind2 = 1L
                                myModel?.hour2 = myHour2
                                myModel?.minute2 = myMinute2

                                myModel?.remind3 = 1L
                                myModel?.hour3 = myHour3
                                myModel?.minute3 = myMinute3
                            }
                            2 -> {
                                myModel?.remind1 = 1L
                                myModel?.hour1 = myHour1
                                myModel?.minute1 = myMinute1

                                myModel?.remind2 = 1L
                                myModel?.hour2 = myHour2
                                myModel?.minute2 = myMinute2
                            }
                            1 -> {
                                myModel?.remind1 = 1L
                                myModel?.hour1 = myHour1
                                myModel?.minute1 = myMinute1
                                scheduleNotification(getId,notification1)
                            }
                            0 -> {
                                calendarId = myModel!!.id
                                cancelSchedule(getId)
                            }
                        }
                    }
                }
                if (locale == Locale.JAPAN){
                    Toast.makeText(applicationContext,"保存しました",Toast.LENGTH_SHORT).show()
                    finish()
                }else{
                    Toast.makeText(applicationContext,"Saved",Toast.LENGTH_SHORT).show()
                    finish()
                }
            }else{
                if (locale == Locale.JAPAN) {
                    // 日本語環境
                    Toast.makeText(applicationContext,"習慣にしたいことを入力してください", Toast.LENGTH_SHORT).show()
                } else {
                    // その他の言語環境
                    Toast.makeText(applicationContext,"Please enter what you want to make a habit.", Toast.LENGTH_SHORT).show()
                }
            }

        }

        binding.deleteHabitBtn.setOnClickListener {
            if (locale == Locale.JAPAN){
                AlertDialog.Builder(it.context)
                    .setTitle("本当に削除しますか")
                    .setPositiveButton("削除する", DialogInterface.OnClickListener { _, _ ->
                        AlertDialog.Builder(it.context)
                            .setTitle("削除すると復元することができません。本当に削除しますか")
                            .setPositiveButton("削除する", DialogInterface.OnClickListener { _, _ ->
                                val myModelResult = realm.where<MyModel>()
                                    .equalTo("id",getId).findFirst()
                                val calendarModelResult = realm.where(CalendarModel::class.java)
                                    .equalTo("listId",getId).findAll()
                                cancelSchedule(getId)
                                realm.executeTransaction {
                                    myModelResult?.deleteFromRealm()
                                    calendarModelResult.deleteAllFromRealm()
                                }
                                Toast.makeText(applicationContext,"削除しました",Toast.LENGTH_SHORT).show()
                                finish()
                            })
                            .setNegativeButton("戻る",null)
                            .show()
                    })
                    .setNegativeButton("戻る",null)
                    .show()
            } else {
                // その他の言語環境
                AlertDialog.Builder(it.context)
                    .setTitle("Are you sure you want to delete this habit?")
                    .setPositiveButton("Delete", DialogInterface.OnClickListener { _, _ ->
                        AlertDialog.Builder(it.context)
                            .setTitle("Once deleted, it cannot be restored. Are you sure you want to delete the habit?")
                            .setPositiveButton("Delete", DialogInterface.OnClickListener { _, _ ->
                                val myModelResult = realm.where<MyModel>()
                                    .equalTo("id",getId).findFirst()
                                val calendarModelResult = realm.where(CalendarModel::class.java)
                                    .equalTo("listId",getId).findAll()
                                cancelSchedule(getId)
                                realm.executeTransaction {
                                    myModelResult?.deleteFromRealm()
                                    calendarModelResult.deleteAllFromRealm()
                                }
                                Toast.makeText(applicationContext,"Deleted",Toast.LENGTH_SHORT).show()
                                finish()
                            })
                            .setNegativeButton("Cancel",null)
                            .show()
                    })
                    .setNegativeButton("Cancel",null)
                    .show()
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val locale = Locale.getDefault()
        if (resultCode != RESULT_OK) {
            return
        }
        when (requestCode) {
            READ_REQUEST_CODE -> {
                try {
                    data?.data?.also { uri ->
                        val inputStream = contentResolver?.openInputStream(uri)
                        image = BitmapFactory.decodeStream(inputStream)
                        image = centerCropBitmap(image)
                        val imageView = findViewById<ImageView>(R.id.iconImage)
                        imageView.setImageBitmap(image)
                    }
                } catch (e: Exception) {
                    if (locale == Locale.JAPAN){
                        Toast.makeText(this, "エラーが発生しました", Toast.LENGTH_LONG).show()
                    }else{
                        Toast.makeText(this, "An error has occurred.", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun byteToBitmap(bytes:ByteArray):Bitmap{
        val opt = BitmapFactory.Options()
        opt.inJustDecodeBounds = false
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, opt)
    }

    private fun selectPhoto() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        startActivityForResult(intent, READ_REQUEST_CODE)
    }

    private fun centerCropBitmap(bitmap: Bitmap?): Bitmap {
        if (bitmap?.width == bitmap?.height) {
            return if (bitmap?.width!! > 500){
                Bitmap.createScaledBitmap(bitmap,500,500,true)
            }else{
                bitmap
            }
        }
        if (bitmap?.width!! > bitmap.height) {
            val leftOffset = (bitmap.width - bitmap.height) / 2
            val myBitmap:Bitmap =  Bitmap.createBitmap(bitmap, leftOffset, 0, bitmap.height, bitmap.height, null, true)
            return if (myBitmap.width > 500){
                Bitmap.createScaledBitmap(myBitmap,500,500,true)
            }else{
                myBitmap
            }
        }
        val topOffset = (bitmap.height - bitmap.width) / 2
        val myBitmap:Bitmap =  Bitmap.createBitmap(bitmap, 0, topOffset, bitmap.width, bitmap.width, null, true)
        return if (myBitmap.width > 500){
            Bitmap.createScaledBitmap(myBitmap,500,500,true)
        }else{
            myBitmap
        }
    }

    companion object {
        const val READ_REQUEST_CODE: Int = 42
    }

    private fun cancelSchedule(id:Long) {
        /*val getId = intent.getLongExtra("ID",0L)
        val myTaskModelResult = realm.where<MyTaskModel>()
            .equalTo("id",getId).findFirst()*/
        val sIntent = Intent(applicationContext, AlarmReceiver::class.java)
        val nID = id.toInt()
        val pending = PendingIntent.getBroadcast(
            applicationContext,
            nID,
            sIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.cancel(pending)
    }

    private fun scheduleNotification(id:Long,comment:String) {
        val myLocale = Locale.getDefault()
        val scheduleIntent = Intent(applicationContext, AlarmReceiver::class.java)
        val myModelResult = realm.where<MyModel>()
            .equalTo("id",id).findFirst()
        val nfID = id.toInt()
        scheduleIntent.putExtra("titleExtra",myModelResult?.title)
        if (comment == ""){
            if (myLocale == Locale.JAPAN){
                scheduleIntent.putExtra("messageExtra","時間になりました！")
            }else{
                scheduleIntent.putExtra("messageExtra","It's time!")
            }
        }else{
            scheduleIntent.putExtra("messageExtra",comment)
        }
        scheduleIntent.putExtra("image",myModelResult?.iconImage)
        scheduleIntent.putExtra("notificationId", nfID)
        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            nfID,
            scheduleIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val sTime = getTime()
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,sTime,AlarmManager.INTERVAL_DAY,pendingIntent)
    }

    private fun getTime():Long {
        val getTime = LocalDateTime.now()
        return if (myHour1 < getTime.hour){
            val getDate = LocalDate.now().plusDays(1)
            val sCal = Calendar.getInstance()
            sCal.set(getDate.year,getDate.monthValue-1,getDate.dayOfMonth,myHour1.toInt(),myMinute1.toInt())
            sCal.timeInMillis
        }else if (myHour1 == getTime.hour.toLong() && myMinute1 < getTime.minute){
            val getDate = LocalDate.now().plusDays(1)
            val sCal = Calendar.getInstance()
            sCal.set(getDate.year,getDate.monthValue-1,getDate.dayOfMonth,myHour1.toInt(),myMinute1.toInt())
            sCal.timeInMillis
        }else{
            val getDate = LocalDate.now()
            val sCal = Calendar.getInstance()
            sCal.set(getDate.year,getDate.monthValue-1,getDate.dayOfMonth,myHour1.toInt(),myMinute1.toInt())
           sCal.timeInMillis
        }
    }

    private fun createNotificationChannel() {
        val locale = Locale.getDefault()
        if (locale == Locale.JAPAN) {
            // 日本語環境
            val name = "習慣リマインダー"
            val desc = "習慣を指定の時間に通知します。"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance)
            channel.description = desc
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        } else {
            // その他の言語環境
            val name = "Habit notification"
            val desc = "Notify the habit."
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance)
            channel.description = desc
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun handleKeyEvent(view: View, keyCode: Int): Boolean {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            // Hide the keyboard
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            return true
        }
        return false
    }
}