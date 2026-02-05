package com.ruriboshi.habitter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.realm.Realm
import java.time.LocalDate


class CalendarAdapter(daysInMonth: ArrayList<Long>,month:Long,year:Long,listId:Long) : RecyclerView.Adapter<CalendarViewHolder>() {

    //realmResults: RealmResults<CalendarModel>
    //private val rResults:RealmResults<CalendarModel> = realmResults
    private lateinit var realm: Realm

    private var daysOfMonth = daysInMonth
    private var calendarMonth = month
    private var calendarYear = year
    private var habitListId = listId

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val oneXml = LayoutInflater.from(parent.context)
            .inflate(R.layout.calendar_day_layout,parent,false)
        return CalendarViewHolder(oneXml)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        //val myModel = rResults[position]
        realm = Realm.getDefaultInstance()

        if (daysOfMonth[position] == 0L){
            holder.oneDay.text = ""
        }else{
            holder.oneDay.text = daysOfMonth[position].toString()
        }
        val habits = realm.where(CalendarModel::class.java)
            .equalTo("listId",habitListId).equalTo("month",calendarMonth)
            .equalTo("year",calendarYear).equalTo("day",daysOfMonth[position])
            .findFirst()

        if (habits != null){
            holder.background.setBackgroundColor(Color.parseColor("#0BBE11"))
        }
        val today = LocalDate.now()
        /*if (calendarYear == today.year.toLong() && calendarMonth == today.monthValue.toLong()
            && daysOfMonth[position].toLong() == today.dayOfMonth.toLong()){
            holder.oneDay.setTextColor(Color.parseColor("#FF77CA"))
        }*/
    }

    override fun getItemCount(): Int {
        //return  rResults.size
        return daysOfMonth.size
    }

    interface OnItemListener {
        fun onItemClick(position: Int, dayText: String?)
    }
}



