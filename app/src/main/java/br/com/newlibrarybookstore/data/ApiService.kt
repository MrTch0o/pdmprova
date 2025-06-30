package br.com.newlibrarybookstore.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

// Interface que define os endpoints da API
interface ApiService {
    @GET("book/ls")
    suspend fun getBooks(): List<Book>

    @GET("books/{id}")
    suspend fun getBookById(@Path("id") bookId: String): Book

}

// Objeto para criar uma instância única (singleton) do Retrofit
object RetrofitInstance {
    private const val BASE_URL = "https://minibookapi.viniciusfm.pro.br/"

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}