package br.com.newlibrarybookstore.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.newlibrarybookstore.data.Book
import br.com.newlibrarybookstore.data.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BookDetailsViewModel : ViewModel() {
    private val _book = MutableStateFlow<Book?>(null)
    val book: StateFlow<Book?> = _book

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun fetchBookById(bookId: String) {
        //val idAsInt = bookId.toIntOrNull() ?: return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _book.value = RetrofitInstance.api.getBookById(bookId)
            } catch (e: Exception) {
                Log.e("BookDetailsViewModel", "Erro ao buscar detalhes do livro: ${e.message}", e)
                _book.value = null // Garante que não há dados antigos em caso de erro
            } finally {
                _isLoading.value = false
            }
        }
    }
}