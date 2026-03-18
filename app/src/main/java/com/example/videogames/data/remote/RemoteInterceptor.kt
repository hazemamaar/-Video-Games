package com.example.videogames.data.remote

import com.example.videogames.utils.Constants
import okhttp3.Interceptor
import okhttp3.Response

class RemoteInterceptor : Interceptor {


    override fun intercept(chain: Interceptor.Chain): Response {

        val originalRequest = chain.request()

        // add api key as query parameter
        val newUrl = originalRequest.url.newBuilder()
            .addQueryParameter("key", Constants.API_KEY)
            .build()

        val newRequest = originalRequest.newBuilder()
            .url(newUrl)
            .build()

        // Logging request
        println("Request URL: ${newRequest.url}")
        println("Request Method: ${newRequest.method}")

        val response = chain.proceed(newRequest)

        // Logging response
        println("Response Code: ${response.code}")
        println("Response Message: ${response.message}")

        return response
    }
}