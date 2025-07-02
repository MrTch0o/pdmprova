// file: data/ApiService.kt
package br.com.newlibrarybookstore.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    // já existente
    @GET("book/ls")
    suspend fun getBooks(): List<Book>

    @GET("books/{id}")
    suspend fun getBookById(@Path("id") bookId: String): Book

    // 🔹 NOVO ENDPOINT
    @POST("sale/new")
    suspend fun createSale(
        @Header("Authorization") token: String,
        @Body request: BooksSaleRequest
    ): Sale
}
object RetrofitInstance {
    // A URL base da nova API
    const val BASE_URL = "https://minibookapi.viniciusfm.pro.br/"

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}