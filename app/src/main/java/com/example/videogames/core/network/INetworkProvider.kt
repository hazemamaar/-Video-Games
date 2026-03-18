package com.example.videogames.core.network

import java.lang.reflect.Type

interface INetworkProvider {

    suspend fun <ResponseBody> get(
        responseWrappedModel: Type, pathUrl: String, headers: Map<String, Any>? = null,
        queryParams: Map<String, Any>? = null
    ): ResponseBody

}