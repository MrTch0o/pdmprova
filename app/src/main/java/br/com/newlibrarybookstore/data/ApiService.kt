package br.com.newlibrarybookstore.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// Interface que define os endpoints da API
interface ApiService {
    @GET("book/ls")
    suspend fun getBooks(): List<Book>
/*
    @POST("books")
    suspend fun addBook(@Body book: Book) // O ID será ignorado pela API na criação

    // Funções de update e delete (vamos implementar depois)
    @PUT("books/{id}")
    suspend fun updateBook(@Path("id") id: String, @Body book: Book)

    @DELETE("books/{id}")
    suspend fun deleteBook(@Path("id") id: String) */
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