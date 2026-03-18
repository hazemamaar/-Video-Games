package com.example.videogames.core.error

import com.example.videogames.R
import com.example.videogames.core.VideoGameException
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class GeneralErrorHandlerImpl @Inject constructor() : ErrorHandler {

    override fun getError(throwable: Throwable): VideoGameException {
        throwable.printStackTrace()
        return when (throwable) {
            is UnknownHostException -> VideoGameException.Network.Unhandled(
                messageRes = R.string.error_network_no_internet,
                message = "Unable to connect to server. Please check your internet connection."
            )

            is SocketTimeoutException -> VideoGameException.Network.Unhandled(
                messageRes = R.string.error_network_timeout,
                message = "Connection timeout. Please try again."
            )

            is ConnectException -> VideoGameException.Network.Unhandled(
                messageRes = R.string.error_network_connection,
                message = "Failed to connect to server. Please check your internet connection."
            )

            is IOException -> VideoGameException.Network.Unhandled(
                messageRes = R.string.error_network_unexpected,
                message = throwable.message ?: "Network error occurred"
            )

            is HttpException -> mapHttpStatusCode(throwable)

            else -> VideoGameException.Unknown(throwable.message)
        }
    }

    private fun mapHttpStatusCode(exception: HttpException): VideoGameException {
        val errorBody = exception.response()?.errorBody()?.string()

        val json = try {
            if (!errorBody.isNullOrBlank()) JSONObject(errorBody) else JSONObject()
        } catch (_: Exception) {
            JSONObject()
        }

        val message = exception.message()
        val code = json.optInt("code", exception.code())


        return when (exception.code()) {
            401 -> VideoGameException.Client.Unauthorized
            422 -> {
                VideoGameException.Client.ResponseValidation( message = message)
            }
            in 400..499 -> VideoGameException.Client.Unhandled(code, message)

            in 500..599 -> VideoGameException.Server.InternalServerError(code, message)

            // Fallback
            else -> VideoGameException.Unknown(message)
        }
    }



}