package qoute_generator

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


import kotlinx.coroutines.*
import java.net.SocketTimeoutException

private suspend fun getQuote() {
    try {
        // Set timeout for network call
        withTimeout(5000) { // 5 seconds max
            val response = RetrofitInstance.quoteApi.getQuote()
            val quote = response.body()?.firstOrNull()
            if (quote != null) {
                println(quote.q)
                println("- by ${quote.a}")
            } else {
                println("No quotes found")
            }
        }
    } catch (e: SocketTimeoutException) {
        println("Network timeout!")
    } catch (e: Exception) {
        println("Error: $e")
    }
}

fun main() = runBlocking {
    getQuote()
    println("Program finished.")
}
