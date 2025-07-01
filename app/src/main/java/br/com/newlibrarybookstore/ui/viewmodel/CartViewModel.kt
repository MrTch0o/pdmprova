// file: ui/viewmodel/CartViewModel.kt
package br.com.newlibrarybookstore.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.newlibrarybookstore.data.Book
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class CartItem(val book: Book, val quantity: Int)

class CartViewModel : ViewModel() {
    private val _cartItems = MutableStateFlow<Map<Int, CartItem>>(emptyMap())
    val cartItems: StateFlow<Map<Int, CartItem>> = _cartItems

    // NOVO: StateFlow para a QUANTIDADE TOTAL de itens no carrinho
    val totalQuantity: StateFlow<Int> = _cartItems.map { cart ->
        cart.values.sumOf { it.quantity }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val totalPrice: StateFlow<Double> = _cartItems.map { cart ->
        cart.values.sumOf { it.book.price.toDouble() * it.quantity } / 100.0
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    private val _purchasedItems = MutableStateFlow<List<Book>>(emptyList())
    val purchasedItems: StateFlow<List<Book>> = _purchasedItems

    fun addToCart(book: Book) {
        val currentCart = _cartItems.value.toMutableMap()
        val existingItem = currentCart[book.id]
        if (existingItem != null) {
            currentCart[book.id] = existingItem.copy(quantity = existingItem.quantity + 1)
        } else {
            currentCart[book.id] = CartItem(book = book, quantity = 1)
        }
        _cartItems.value = currentCart
    }

    fun removeFromCart(book: Book) {
        val currentCart = _cartItems.value.toMutableMap()
        val existingItem = currentCart[book.id]
        if (existingItem != null) {
            if (existingItem.quantity > 1) {
                currentCart[book.id] = existingItem.copy(quantity = existingItem.quantity - 1)
            } else {
                currentCart.remove(book.id)
            }
        }
        _cartItems.value = currentCart
    }

    fun clearCart() {
        _cartItems.value = emptyMap()
    }

    // FUNÇÃO CHECKOUT CORRIGIDA
    fun checkout() {
        // Usa flatMap para criar uma lista de livros que respeita a quantidade
        // Ex: 2x Livro A -> [Livro A, Livro A]
        val booksToPurchase = _cartItems.value.values.flatMap { cartItem ->
            List(cartItem.quantity) { cartItem.book }
        }
        // Adiciona a lista de livros comprados ao histórico
        _purchasedItems.value = booksToPurchase + _purchasedItems.value
        // Limpa o carrinho após a compra
        clearCart()
    }
}