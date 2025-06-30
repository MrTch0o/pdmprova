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
    // Instanciando o repositório (em um app real, usaríamos Injeção de Dependência)
    private val repository = BookRepository(RetrofitInstance.api)

    // StateFlow para guardar a lista de livros e expor para a UI
    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books: StateFlow<List<Book>> = _books

    init {
        // Busca os livros assim que o ViewModel é criado
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