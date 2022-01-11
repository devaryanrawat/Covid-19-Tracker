package com.example.android.covidtrackerapp

import okhttp3.OkHttpClient
import okhttp3.Request

object Client {

    //First we must instantiate okHttpClient and
    // create a Request object
    private val okHttpClient = OkHttpClient()

    private val request = Request.Builder()
        .url("https://api.covid19india.org/data.json")
        .build()

    //passing the request to the
    //okHttpClient
    val api = okHttpClient.newCall(request)
}