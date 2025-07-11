package br.com.newlibrarybookstore.data

class BookRepository (private val apiService: ApiService) {
    suspend fun getBooks(): List<Book> {
        return apiService.getBooks()
    }
}