package com.ruriboshi.habitter

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class MyModel: RealmObject(){
    @PrimaryKey
    var id:Long = 0
    var title:String = ""
    var todayFinished:Long = 0
    //var image:ByteArray = ByteArray(0)
    lateinit var iconImage:ByteArray
    var color:Long = 0
    var order:Long = 0
    var comments:Long = 0
    var messageOfToday:Long = 0
    var message1:String = ""
    var message2:String = ""
    var message3:String = ""
    var notificationComments:Long = 0
    var notification1:String = ""
    var notification2:String = ""
    var notification3:String = ""
    var remind1:Long = 0
    var remind2:Long = 0
    var remind3:Long = 0
    var hour1:Long = 0
    var hour2:Long = 0
    var hour3:Long = 0
    var minute1:Long = 0
    var minute2:Long = 0
    var minute3:Long = 0
    var idolName:String = ""
}