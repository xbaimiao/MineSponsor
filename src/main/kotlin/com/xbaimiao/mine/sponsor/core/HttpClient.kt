package com.xbaimiao.mine.sponsor.core

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

object HttpClient {

    private val client = OkHttpClient.Builder().build()

    /**
     * get请求
     */
    fun get(url: String): Response? {
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            return response
        }
        return null
    }

}