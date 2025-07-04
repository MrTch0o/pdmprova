package br.com.newlibrarybookstore.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.newlibrarybookstore.data.Book
import br.com.newlibrarybookstore.data.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BookListViewModel : ViewModel() {
    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books: StateFlow<List<Book>> = _books

    init {
        fetchBooks()
    }

    private fun fetchBooks() {
        viewModelScope.launch {
            try {
                _books.value = RetrofitInstance.api.getBooks()
            } catch (e: Exception) {
                Log.d("fetchBooksDebug", "Erro ao buscar livros: ${e.message}", e)
            }
        }
    }

    fun loadBooks() {
        fetchBooks()
    }
}