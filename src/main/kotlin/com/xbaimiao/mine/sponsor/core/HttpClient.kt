package com.xbaimiao.mine.sponsor.core

import com.lly835.libs.okhttp3.OkHttpClient
import com.lly835.libs.okhttp3.Response
import com.lly835.libs.okhttp3.Request

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