package com.ruriboshi.habitter

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.LayerDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import java.util.Locale


class RecyclerAdapter (realmResults: RealmResults<MyModel>,activity :HabitListActivity): RecyclerView.Adapter<ViewHolderItem>(){
    private val rResults:RealmResults<MyModel> = realmResults
    private lateinit var realm:Realm
    private val noImage:ImageView = activity.findViewById(R.id.noImage)
    private val noText:TextView = activity.findViewById(R.id.noText)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderItem {
        val oneXml = LayoutInflater.from(parent.context)
            .inflate(R.layout.one_layout,parent,false)
        return ViewHolderItem(oneXml)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolderItem, position: Int) {
        val myModel = rResults[position]
        val context: Context = holder.itemView.context
        holder.habitTitle.text = myModel?.title.toString()
        holder.progressBar.progress = 0

        val locale = Locale.getDefault()

        when(myModel?.color){
            0L ->{
                //holder.oneCardView.setBackgroundColor(Color.parseColor("#FFFAF4"))
                holder.progressBar.progressDrawable = getDrawable(context,R.drawable.red_progressbar)
            }
            1L ->{
                //holder.oneCardView.setBackgroundColor(Color.parseColor("#FFF8EF"))
                holder.progressBar.progressDrawable = getDrawable(context,R.drawable.orange_progressbar)
            }
            2L ->{
                //holder.oneCardView.setBackgroundColor(Color.parseColor("#FFF8BB"))
                holder.progressBar.progressDrawable = getDrawable(context,R.drawable.yellow_progressbar)
            }
            3L ->{
                //holder.oneCardView.setBackgroundColor(Color.parseColor("#FFFAF4"))
                holder.progressBar.progressDrawable = getDrawable(context,R.drawable.pink_progressbar)
            }
            4L ->{
                //holder.oneCardView.setBackgroundColor(Color.parseColor("#E6FAFB"))
                holder.progressBar.progressDrawable = getDrawable(context,R.drawable.blue_progressbar)
            }
            5L ->{
                //holder.oneCardView.setBackgroundColor(Color.parseColor("#E6FAFB"))
                holder.progressBar.progressDrawable = getDrawable(context,R.drawable.sky_blue_progressbar)
            }
            6L ->{
                //holder.oneCardView.setBackgroundColor(Color.parseColor("#EBF4EF"))
                holder.progressBar.progressDrawable = getDrawable(context,R.drawable.green_progressbar)
            }
            7L ->{
                //holder.oneCardView.setBackgroundColor(Color.parseColor("#EBF4EF"))
                holder.progressBar.progressDrawable = getDrawable(context,R.drawable.yellow_green_progressbar)
            }
            8L ->{
                //holder.oneCardView.setBackgroundColor(Color.parseColor("#EAF1F2"))
                holder.progressBar.progressDrawable = getDrawable(context,R.drawable.purple_progressbar)
            }
            9L ->{
                //holder.oneCardView.setBackgroundColor(Color.parseColor("#FCF9EF"))
                holder.progressBar.progressDrawable = getDrawable(context,R.drawable.brawn_progressbar)
            }
            10L ->{
                //holder.oneCardView.setBackgroundColor(Color.parseColor("#FFFFFF"))
                holder.progressBar.progressDrawable = getDrawable(context,R.drawable.white_progressbar)
            }
            11L ->{
                //holder.oneCardView.setBackgroundColor(Color.parseColor("#CFCFCF"))
                holder.progressBar.progressDrawable = getDrawable(context,R.drawable.black_progressbar)
            }
        }

        //#E6FAFB
        //holder.progressBar.progress = 0

        /*val d = ContextCompat.getDrawable(
            item.getContext(),
            R.drawable.progress_circle_yellow
        ) as LayerDrawable?
        mProgressBar.setProgressDrawable(d)*/

        if (myModel?.iconImage.contentEquals(ByteArray(0))){
            holder.iconImage.setImageResource(R.drawable.app_mainicon)
            holder.iconImage.setBackgroundColor(Color.parseColor("#C7FEFF"))
        }else{
            holder.iconImage.setImageBitmap(byteToBitmap(myModel?.iconImage!!))
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(it.context,MyHabitActivity::class.java)
            intent.putExtra("id",myModel?.id)
            it.context.startActivity(intent)
        }

        holder.editBtn.setOnClickListener {
            val intent = Intent(it.context,EditActivity::class.java)
            intent.putExtra("id",myModel?.id)
            it.context.startActivity(intent)
        }

        if (myModel?.todayFinished == 1L){
            holder.progressBar.progress = 0
            val animation = ObjectAnimator.ofInt(holder.progressBar, "progress", 100)
            animation.duration = 1500
            animation.interpolator = DecelerateInterpolator()
            animation.start()
        }else{
            holder.progressBar.progress = 0
        }

        holder.deleteBtn.setOnClickListener {
            if (locale == Locale.JAPAN) {
                // 日本語環境
                AlertDialog.Builder(it.context)
                    .setTitle("本当に削除しますか？")
                    .setPositiveButton("削除する", DialogInterface.OnClickListener { _, _ ->
                        AlertDialog.Builder(it.context)
                            .setTitle("削除すると復元することができません。本当に削除しますか？")
                            .setPositiveButton("削除する", DialogInterface.OnClickListener { _, _ ->
                                realm = Realm.getDefaultInstance()
                                val calendarModelResult = realm.where(CalendarModel::class.java)
                                    .equalTo("listId",myModel?.id).findAll()
                                cancelSchedule(it.context,myModel!!.id)
                                realm.executeTransaction {
                                    myModel.deleteFromRealm()
                                    calendarModelResult.deleteAllFromRealm()
                                }
                                realm.close()
                                notifyItemRemoved(position)
                                Toast.makeText(it.context, "削除しました", Toast.LENGTH_SHORT).show()
                                notifyDataSetChanged()
                                emptyView()
                            })
                            .setNegativeButton("戻る", null)
                            .show()

                    })
                    .setNegativeButton("戻る", null)
                    .show()
            }else {
                // その他の言語環境
                AlertDialog.Builder(it.context)
                    .setTitle("Are you sure you want to delete this habit?")
                    .setPositiveButton("Delete", DialogInterface.OnClickListener { _, _ ->
                        AlertDialog.Builder(it.context)
                            .setTitle("Once deleted, it cannot be restored. Are you sure you want to delete the habit?")
                            .setPositiveButton("Delete", DialogInterface.OnClickListener { _, _ ->
                                realm = Realm.getDefaultInstance()
                                val calendarModelResult = realm.where(CalendarModel::class.java)
                                    .equalTo("listId",myModel?.id).findAll()
                                cancelSchedule(it.context,myModel!!.id)
                                realm.executeTransaction {
                                    myModel.deleteFromRealm()
                                    calendarModelResult.deleteAllFromRealm()
                                }
                                realm.close()
                                notifyItemRemoved(position)
                                Toast.makeText(it.context,"Deleted", Toast.LENGTH_SHORT).show()
                                notifyDataSetChanged()
                                emptyView()
                            })
                            .setNegativeButton("Cancel",null)
                            .show()
                    })
                    .setNegativeButton("Cancel",null)
                    .show()
            }
        }
    }

    private fun emptyView(){
        val realmResults = realm.where(MyModel::class.java)
            .findAll().sort("id", Sort.DESCENDING)
        if (realmResults.isEmpty()){
            noText.visibility = View.VISIBLE
            noImage.visibility = View.VISIBLE
        }else{
            noText.visibility = View.GONE
            noImage.visibility = View.GONE
        }
    }

    private fun byteToBitmap(bytes:ByteArray): Bitmap {
        val opt = BitmapFactory.Options()
        opt.inJustDecodeBounds = false
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, opt)
    }

    override fun getItemCount(): Int {
        return  rResults.size
    }

    //変更箇所
    /*override fun getItemId(position: Int): Long {
        //return super.getItemId(position)
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        //return super.getItemViewType(position)
        return position
    }*/
    //変更箇所

    private fun cancelSchedule(context:Context,id:Long) {
        /*val getId = intent.getLongExtra("ID",0L)
        val myTaskModelResult = realm.where<MyTaskModel>()
            .equalTo("id",getId).findFirst()*/
        val sIntent = Intent(context, AlarmReceiver::class.java)
        val nID = id.toInt()
        val pending = PendingIntent.getBroadcast(
            context,
            nID,
            sIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val am = ContextCompat.getSystemService(context,AlarmManager::class.java)
        am?.cancel(pending)

    }
}