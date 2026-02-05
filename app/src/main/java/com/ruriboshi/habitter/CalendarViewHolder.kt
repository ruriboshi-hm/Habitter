package com.ruriboshi.habitter

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView


class CalendarViewHolder(v: View): RecyclerView.ViewHolder(v) {
    var oneDay:TextView = v.findViewById(R.id.day_text)
    var background:ConstraintLayout = v.findViewById(R.id.day_layout)
}