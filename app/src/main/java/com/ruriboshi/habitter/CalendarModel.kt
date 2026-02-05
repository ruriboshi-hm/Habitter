package com.ruriboshi.habitter

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class CalendarModel :RealmObject(){
    @PrimaryKey
    var id:Long = 0
    var listId:Long = 0
    var year:Long = 0
    var month:Long = 0
    var day:Long = 0
}