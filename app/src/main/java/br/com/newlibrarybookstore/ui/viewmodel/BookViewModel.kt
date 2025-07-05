package br.com.newlibrarybookstore.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.newlibrarybookstore.data.Book
import br.com.newlibrarybookstore.data.BookRepository
import br.com.newlibrarybookstore.data.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BookViewModel : ViewModel() {
    private val repository = BookRepository(RetrofitInstance.api)

    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books: StateFlow<List<Book>> = _books

    init {
        fetchBooks()
    }

    fun fetchBooks() {
        viewModelScope.launch {
            try {
                _books.value = repository.getBooks()
            } catch (e: Exception) {
                // Tratar erro (ex: mostrar uma mensagem na UI)
                println("Erro ao buscar livros: ${e.message}")
            }
        }
    }
}