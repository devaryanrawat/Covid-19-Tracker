package com.example.android.covidtrackerapp

import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    lateinit var stateAdapter: StateAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        listV.addHeaderView(LayoutInflater.from(this).inflate(R.layout.item_header,listV,false))
        fetchResults()
    }

    private fun fetchResults() {
        //For network call we
        //have to use coroutines

        GlobalScope.launch {
            val response = withContext(Dispatchers.IO){Client.api.execute()}
            if(response.isSuccessful){
                //Parses JSON into Kotlin Objects
                    // GSON is Google's JSON parser and generator for Java
                val data = Gson().fromJson(response.body?.string(),Response::class.java)
                launch(Dispatchers.Main){
                    bindCombinedData(data.statewise[0])
                    bindStatewiseData(data.statewise.subList(0,data.statewise.size))
                }
            }
        }
    }

    private fun bindStatewiseData(subList: List<StatewiseItem>) {
        stateAdapter = StateAdapter(subList)
        listV.adapter = stateAdapter
    }

    private fun bindCombinedData(statewiseItem: StatewiseItem) {
        val prevTime = statewiseItem.lastupdatedtime
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        lastUpdatedTv.text = "Last Updated\n${getTimeAgo(sdf.parse(prevTime))}"
        confirmedCasesTv.text = statewiseItem.confirmed
        recoveredCasesTv.text = statewiseItem.recovered
        activeCasesTv.text = statewiseItem.active
        deceasedCasesTv.text = statewiseItem.deaths
    }

    private fun getTimeAgo(prevTime:Date) :String{
        val currTime = Date()
        val secAgo:Long = TimeUnit.MILLISECONDS.toSeconds(currTime.time-prevTime.time)
        val minAgo:Long = TimeUnit.MILLISECONDS.toMinutes(currTime.time-prevTime.time)
        val hrsAgo:Long = TimeUnit.MILLISECONDS.toHours(currTime.time-prevTime.time)
        return when{
            secAgo<60 ->{
                "${secAgo} secs ago"
            }
            minAgo<60 ->{
                "${minAgo} mins ago"
            }
            hrsAgo<24 ->{
                "${hrsAgo} hrs\n${minAgo%60} mins ago"
            }
            else -> {
                SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(prevTime).toString()
            }
        }
    }
}