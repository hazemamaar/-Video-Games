package com.example.videogames.core.network

import com.example.videogames.data.remote.RawgApiService
import com.google.gson.Gson
import java.lang.reflect.Type
import javax.inject.Inject

class NetworkProvider @Inject constructor(
    private val apiService: RawgApiService
) : INetworkProvider {
    override suspend fun <ResponseBody> get(
        responseWrappedModel: Type,
        pathUrl: String,
        headers: Map<String, Any>?,
        queryParams: Map<String, Any>?
    ): ResponseBody {
        val response = apiService.get(
            pathUrl = pathUrl, headerMap = headers ?: hashMapOf(),
            queryParams = queryParams ?: hashMapOf()
        )
        return response.string().getModelFromJSON(responseWrappedModel)
    }
}

fun <M> String.getModelFromJSON(tokenType: Type): M = Gson().fromJson(this, tokenType)

