package com.example.movies.data.core

import com.example.movies.domain.core.Error
import com.example.movies.domain.core.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.coroutines.coroutineContext

/**
 * Created by MarioMitrevski on 4/27/2025.
 * @author   MarioMitrevski
 * @since    4/27/2025.
 */

/**
 * @param execute the block to invoke the retrofit method.
 */
suspend inline fun <reified T> apiCall(
    crossinline execute: suspend () -> Response<T>
): Result<T, Error.Remote> {
    val response = try {
        withContext(Dispatchers.IO) {
            execute()
        }
    } catch (e: SocketTimeoutException) {
        return Result.Error(Error.Remote.REQUEST_TIMEOUT)
    } catch (e: UnknownHostException) {
        return Result.Error(Error.Remote.NO_INTERNET)
    } catch (e: IOException) {
        return Result.Error(Error.Remote.NO_INTERNET)
    } catch (e: Exception) {
        coroutineContext.ensureActive()
        return Result.Error(Error.Remote.UNKNOWN)
    }

    return withContext(Dispatchers.Default) {
        responseToResult(response)
    }
}

inline fun <reified T> responseToResult(
    response: Response<T>
): Result<T, Error.Remote> {
    return when (response.code()) {
        in 200..299 -> {
            val body = response.body()
            if (body != null) {
                Result.Success(body)
            } else {
                Result.Error(Error.Remote.SERIALIZATION)
            }
        }
        408 -> Result.Error(Error.Remote.REQUEST_TIMEOUT)
        429 -> Result.Error(Error.Remote.TOO_MANY_REQUESTS)
        in 500..599 -> Result.Error(Error.Remote.SERVER)
        else -> Result.Error(Error.Remote.UNKNOWN)
    }
}
