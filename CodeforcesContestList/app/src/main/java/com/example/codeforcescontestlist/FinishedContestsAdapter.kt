package com.example.codeforcescontestlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class FinishedContestsAdapter(private val listener: ContestClicked): RecyclerView.Adapter<MyViewHolder>() {
    private val items: ArrayList<News> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.items_news,parent,false)
        val viewHolder = MyViewHolder(view)
        view.setOnClickListener{
            listener.onItemClicked(items[viewHolder.adapterPosition])
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = items[position]
        holder.titleView.text = currentItem.name
        holder.timeDuration.text = secondsToHrs(currentItem.durationSeconds)
        holder.beforeStartDays.text = ""
    }
    private fun secondsToHrs(sec: Int): String {
        var n = sec
        val days: Int = n / (24 * 3600)

        n %= (24 * 3600)
        val hours: Int = n / 3600

        n %= 3600
        val minutes: Int = n / 60

        n %= 60
        val seconds: Int = n
        return if(days==0) {
            if (minutes == 0) {
                "Duration: 0$hours : 00"
            } else {
                "Duration: 0$hours : $minutes "
            }
        }
        else{
            if(seconds==0){
                if(minutes==0){
                    "Duration: $days : $hours"
                }
                else{
                    "Duration: $days : $hours : $minutes"
                }
            }
            else{
                if(minutes==0){
                    "Duration: $days : $hours : 00 : $seconds"
                }
                else{
                    "Duration: $days : $hours : $minutes : $seconds"
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateNews(updatedNews: ArrayList<News>){
        items.clear()
        items.addAll(updatedNews)
//        notifyDataSetChanged()
    }
}

class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val titleView : TextView = itemView.findViewById(R.id.title)
    val timeDuration : TextView = itemView.findViewById(R.id.duration)
    val beforeStartDays: TextView = itemView.findViewById(R.id.beforeStart)
}

interface ContestClicked{
    fun onItemClicked(item: News)
}