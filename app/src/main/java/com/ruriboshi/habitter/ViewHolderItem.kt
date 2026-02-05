package com.ruriboshi.habitter

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ViewHolderItem(v: View):RecyclerView.ViewHolder(v) {
    var habitTitle:TextView = v.findViewById(R.id.title)
    var progressBar:ProgressBar = v.findViewById(R.id.progressbar)
    var iconImage:ImageView = v.findViewById(R.id.imageView_image)
    var oneCardView:LinearLayout = v.findViewById(R.id.one_cardView)
    var deleteBtn:ImageButton = v.findViewById(R.id.delete_habit)
    var editBtn:ImageButton = v.findViewById(R.id.edit_habit)
}