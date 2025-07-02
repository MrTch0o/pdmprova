// file: ui/viewmodel/CartViewModel.kt
package br.com.newlibrarybookstore.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.newlibrarybookstore.data.Book
import br.com.newlibrarybookstore.data.BooksSaleRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import br.com.newlibrarybookstore.data.RetrofitInstance
import br.com.newlibrarybookstore.data.Sale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

    suspend fun createSale(): Sale? {
        return withContext(Dispatchers.IO) {
            try {
                val booksSaleData = _cartItems.value
                    .mapKeys { it.key.toString() }
                    .mapValues { it.value.quantity }

                val request = BooksSaleRequest(booksSaleData)


                Log.d("CheckoutDebug", "Payload enviado: $request")

                val response = RetrofitInstance.api.createSale(
                    token = "Bearer 1d37663bd531a5dfa016f40ab3d5836b58ff310447526a04b27d83a437afa2e1",
                    request = request
                )

                Log.d("CheckoutDebug", "Resposta recebida: $response")

                response
            } catch (e: Exception) {
                e.printStackTrace()
                //Log.e("CheckoutDebug", "Erro ao chamar createSale: ${e.message}", e)
                null
            }
        }
    }
    val TOKEN = "Bearer 1d37663bd531a5dfa016f40ab3d5836b58ff310447526a04b27d83a437afa2e1"

    suspend fun confirmSale(uuid: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val payload = mapOf("sale_uuid" to uuid)
                val response = RetrofitInstance.api.confirmSale(TOKEN, payload)
                Log.d("CheckoutDebug", "Confirmação response: ${response.code()}")
                response.isSuccessful
            } catch (e: Exception) {
                Log.e("CheckoutDebug", "Erro ao confirmar venda: ${e.message}", e)
                false
            }
        }
    }

    suspend fun cancelSale(uuid: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val payload = mapOf("sale_uuid" to uuid)
                val response = RetrofitInstance.api.cancelSale(TOKEN, payload)
                Log.d("CheckoutDebug", "Cancelamento response: ${response.code()}")
                response.isSuccessful
            } catch (e: Exception) {
                Log.e("CheckoutDebug", "Erro ao cancelar venda: ${e.message}", e)
                false
            }
        }
    }
}