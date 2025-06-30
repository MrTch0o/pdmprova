// file: data/ApiService.kt
package br.com.newlibrarybookstore.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    // Aponta para o endpoint correto que você descobriu
    @GET("book/ls")
    suspend fun getBooks(): List<Book>

    // Vamos comentar a busca por ID por enquanto, pois não sabemos
    // como ela funciona nesta nova API. Isso evita erros de compilação.
     @GET("books/{id}")
     suspend fun getBookById(@Path("id") bookId: String): Book
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