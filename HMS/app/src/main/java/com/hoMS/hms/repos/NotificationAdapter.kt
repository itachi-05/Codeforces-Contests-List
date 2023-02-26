package com.hoMS.hms.repos

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import androidx.recyclerview.widget.RecyclerView
import com.hoMS.hms.R

class NotificationAdapter(private var notificationList: ArrayList<String>) : RecyclerView.Adapter<NotificationAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.notificationlistitems, parent, false)
        Log.i("checkingNoti",notificationList.toString())
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentTask = notificationList[position]
        holder.myNotification.text = currentTask
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val myNotification: CheckedTextView = itemView.findViewById(R.id.notiItem)
    }
}
