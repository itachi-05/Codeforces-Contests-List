package com.example.newsfresh


import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.example.newsfresh.databinding.ActivityNewBinding
import kotlinx.android.synthetic.main.activity_new.*
import java.util.*


@RequiresApi(Build.VERSION_CODES.O)
class NewsActivity : AppCompatActivity(), NewsItemClicked, ContestClicked {
    private lateinit var binding: ActivityNewBinding
    private lateinit var myAdapter: UpcomingContestsAdapter
    private lateinit var myAdapter2: FinishedContestsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()


        // UPCOMING CONTESTS RECYCLER VIEW
        upcoming_contests.layoutManager = LinearLayoutManager(this)
//        news_recycler_view.layoutManager = GridLayoutManager(this,3)
//        news_recycler_view.layoutManager = StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL)
        fetchData()
        myAdapter = UpcomingContestsAdapter(this)
        upcoming_contests.adapter = myAdapter

        // FINISHED CONTESTS RECYCLER VIEW
        finished_contests.layoutManager = LinearLayoutManager(this)
        fetchData()
        myAdapter2 = FinishedContestsAdapter(this)
        finished_contests.adapter = myAdapter2

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchData(){

        val url = "https://codeforces.com/api/contest.list"
        val jsonObjectRequest= JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { it ->
                val newsJsonArray = it.getJSONArray("result")
                val newsArray = ArrayList<News>()
                val newsArray2 = ArrayList<News>()
//                val timeArray = ArrayList<String>()
                // map started
                val map = hashMapOf<Float, MutableList<News>>()
                val map2 = hashMapOf<Int, MutableList<News>>()
                // map ended
                var count = 0
                val timeList2 = ArrayList<String>()
                for(i in 0 until  newsJsonArray.length()){
                    val newsJsonObject = newsJsonArray.getJSONObject(i)
                    val news = News(
                        newsJsonObject.getInt("id"),
                        newsJsonObject.getString("name"),
                        newsJsonObject.getString("phase"),
                        newsJsonObject.getInt("durationSeconds"),
                        newsJsonObject.getInt("startTimeSeconds"),
                        newsJsonObject.getInt("relativeTimeSeconds"),
                        0,0
                    )

                    if(news.phase == "BEFORE"){
                        news.checkFlag = 1
                        val startingDate = news.relativeTimeSeconds.toString().drop(1)
                        timeList2.add(startingDate)
                        val currTime = news.startTimeSeconds
                        val remTime = timeRemaining(currTime.toFloat())
                        news.remTime = remTime.toInt()
                        if(map.containsKey(remTime)){
                            map[remTime]?.add(news)
                        }
                        else{
                            map[remTime] = ArrayList<News>()
                            map[remTime]?.add(news)
                        }
                    }
                    if(news.phase == "FINISHED" && count<5){
                        news.checkFlag = 0
                        val currTime = news.relativeTimeSeconds
                        if(map2.containsKey(currTime)){
                            map2[currTime]?.add(news)
                        }
                        else{
                            map2[currTime] = ArrayList<News>()
                            map2[currTime]?.add(news)
                        }
                        count+=1
                    }
                }
                val sortedMap = TreeMap(map)
                val sortedMap2 = TreeMap(map2)

                for ((key, value) in sortedMap){
                    for(i in value){
                        newsArray.add(i)
                    }
                }
                for ((key,value) in sortedMap2){
                    for(i in value){
                        newsArray2.add(i)
                    }
                }

                myAdapter.updateNews(newsArray)
                myAdapter2.updateNews(newsArray2)
            }, {

            }
        )
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }

    override fun onItemClicked(item: News) {
        val url = "https://codeforces.com/contests/${item.id}"
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(url))
    }

    private fun timeRemaining(ms: Float): Float {
        val milliSec = "%.2f".format(ms)
        val currTimeFloat = System.currentTimeMillis().toFloat()/1000F
        val remTime = "%.2f".format(currTimeFloat)
//        Log.i(remTime,remTime)
        val floatRMT = milliSec.toFloat() - remTime.toFloat()
        val timeRem = "%.3f".format(floatRMT)
        return timeRem.toFloat()
    }
}