package com.ruriboshi.habitter

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import io.realm.Realm
import io.realm.kotlin.where
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Locale

const val channelId = "channel1"
class AlarmReceiver : BroadcastReceiver(){
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    private lateinit var realm: Realm
    override fun onReceive(context: Context, intent: Intent) {
        val titleExtra = intent.getStringExtra("titleExtra")
        val messageExtra = intent.getStringExtra("messageExtra")
        val imageExtra = intent.getByteArrayExtra("image")
        val nID = intent.getIntExtra("notificationId",0)
        val action = intent.action
        val iT = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            iT,
            PendingIntent.FLAG_IMMUTABLE
        )

        if (Intent.ACTION_BOOT_COMPLETED == action || Intent.ACTION_MY_PACKAGE_REPLACED == action){
            //Log.d("reboot",action.toString())
            NotificationManagerCompat.from(context).cancelAll()
            realm = Realm.getDefaultInstance()
            val myModel = realm.where(MyModel::class.java)
                .equalTo("remind1",1L).findAll()
            for(model in myModel){
                scheduleNotification(context,model.id,model.notification1,model.hour1,model.minute1)
            }
            realm.close()

        }else{
            if (imageExtra.contentEquals(ByteArray(0))){
                val notification: Notification = NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.icon_oshihabi_notification)
                    .setContentTitle(titleExtra)
                    .setContentText(messageExtra)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build()

                val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.notify(nID,notification)
            }else{
                val iconImage = imageExtra?.let { byteToBitmap(it) }
                val notification: Notification = NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.icon_oshihabi_notification)
                    .setContentTitle(titleExtra)
                    .setContentText(messageExtra)
                    .setLargeIcon(iconImage)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build()

                val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.notify(nID,notification)
            }
        }
    }

    private fun byteToBitmap(bytes:ByteArray):Bitmap{
        val opt = BitmapFactory.Options()
        opt.inJustDecodeBounds = false
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, opt)
    }

    private fun scheduleNotification(context: Context, id:Long, comment:String, hour:Long, minute:Long) {
        val scheduleIntent = Intent(context, AlarmReceiver::class.java)
        val myModelResult = realm.where<MyModel>()
            .equalTo("id",id).findFirst()
        val nfID = id.toInt()
        scheduleIntent.putExtra("titleExtra",myModelResult?.title)
        if (comment == ""){
            val locale = Locale.getDefault()
            if (locale == Locale.JAPAN){
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
            context,
            nfID,
            scheduleIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val sTime = getTime(hour,minute)
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,sTime,
            AlarmManager.INTERVAL_DAY,pendingIntent)
        //Log.d("tag", "$id${myModelResult?.title}$hour:$minute$comment")
    }

    private fun getTime(hour:Long,minute:Long):Long {
        val getTime = LocalDateTime.now()
        return if (hour < getTime.hour){
            val getDate = LocalDate.now().plusDays(1)
            val sCal = Calendar.getInstance()
            sCal.set(getDate.year,getDate.monthValue-1,getDate.dayOfMonth,hour.toInt(),minute.toInt())
            sCal.timeInMillis
        }else if (hour == getTime.hour.toLong() && minute < getTime.minute){
            val getDate = LocalDate.now().plusDays(1)
            val sCal = Calendar.getInstance()
            sCal.set(getDate.year,getDate.monthValue-1,getDate.dayOfMonth,hour.toInt(),minute.toInt())
            sCal.timeInMillis
        }else{
            val getDate = LocalDate.now()
            val sCal = Calendar.getInstance()
            sCal.set(getDate.year,getDate.monthValue-1,getDate.dayOfMonth,hour.toInt(),minute.toInt())
            sCal.timeInMillis
        }
    }

}