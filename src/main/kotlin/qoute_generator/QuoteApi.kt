package qoute_generator

import retrofit2.Response
import retrofit2.http.GET
interface QuoteApi{
    @GET("random")
    suspend fun getQuote(): Response<List<QuoteData>>
}