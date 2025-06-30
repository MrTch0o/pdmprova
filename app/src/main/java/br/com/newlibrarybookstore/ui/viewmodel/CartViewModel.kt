package br.com.newlibrarybookstore.ui.viewmodel

import androidx.lifecycle.ViewModel
import br.com.newlibrarybookstore.data.Book
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CartViewModel : ViewModel() {
    private val _cartItems = MutableStateFlow<List<Book>>(emptyList())
    val cartItems: StateFlow<List<Book>> = _cartItems

    private val _purchasedItems = MutableStateFlow<List<Book>>(emptyList())
    val purchasedItems: StateFlow<List<Book>> = _purchasedItems

    fun addToCart(book: Book) {
        // Adiciona o livro à lista do carrinho
        _cartItems.value = _cartItems.value + book
    }

    fun removeFromCart(book: Book) {
        // Remove a primeira ocorrência do livro do carrinho
        _cartItems.value = _cartItems.value - book
    }

    fun checkout() {
        // Simula o pagamento:
        // 1. Adiciona os itens do carrinho ao histórico de compras
        _purchasedItems.value = _purchasedItems.value + _cartItems.value
        // 2. Limpa o carrinho
        _cartItems.value = emptyList()
    }
}