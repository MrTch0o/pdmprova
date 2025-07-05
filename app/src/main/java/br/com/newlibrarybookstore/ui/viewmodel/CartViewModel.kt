package br.com.newlibrarybookstore.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.newlibrarybookstore.data.ApiError
import br.com.newlibrarybookstore.data.Book
import br.com.newlibrarybookstore.data.BooksSaleRequest
import br.com.newlibrarybookstore.data.RetrofitInstance
import br.com.newlibrarybookstore.data.Sale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import com.google.gson.Gson
import retrofit2.HttpException

data class CartItem(val book: Book, val quantity: Int)

class CartViewModel : ViewModel() {
    val TOKEN = "Bearer 1d37663bd531a5dfa016f40ab3d5836b58ff310447526a04b27d83a437afa2e1"

    private val _apiErrorMessage = MutableStateFlow<String?>(null)
    val apiErrorMessage: StateFlow<String?> = _apiErrorMessage

    fun clearApiError() {
        _apiErrorMessage.value = null
    }

    private val _cartItems = MutableStateFlow<Map<Int, CartItem>>(emptyMap())
    val cartItems: StateFlow<Map<Int, CartItem>> = _cartItems

    val totalQuantity: StateFlow<Int> = _cartItems.map { it.values.sumOf { item -> item.quantity } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalPrice: StateFlow<Double> = _cartItems.map {
        it.values.sumOf { item -> item.book.price.toDouble() * item.quantity } / 100.0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    private val _purchasedItems = MutableStateFlow<List<Book>>(emptyList())
    val purchasedItems: StateFlow<List<Book>> = _purchasedItems

    private val _currentSale = MutableStateFlow<Sale?>(null)
    val currentSale: StateFlow<Sale?> = _currentSale

    fun setCurrentSale(sale: Sale) {
        _currentSale.value = sale
    }

    fun addToCart(book: Book) {
        val updatedCart = _cartItems.value.toMutableMap()
        val existing = updatedCart[book.id]
        updatedCart[book.id] = if (existing != null) {
            existing.copy(quantity = existing.quantity + 1)
        } else {
            CartItem(book, 1)
        }
        _cartItems.value = updatedCart
    }

    fun removeFromCart(book: Book) {
        val updatedCart = _cartItems.value.toMutableMap()
        val existing = updatedCart[book.id]
        if (existing != null) {
            if (existing.quantity > 1) {
                updatedCart[book.id] = existing.copy(quantity = existing.quantity - 1)
            } else {
                updatedCart.remove(book.id)
            }
        }
        _cartItems.value = updatedCart
    }

    fun clearCart() {
        _cartItems.value = emptyMap()
    }

    suspend fun createSale(): Sale? = withContext(Dispatchers.IO) {
        try {
            val request = BooksSaleRequest(
                _cartItems.value.mapKeys { it.key.toString() }.mapValues { it.value.quantity }
            )
            Log.d("CheckoutDebug", "Payload enviado: $request")
            val response = RetrofitInstance.api.createSale(token = "TOKEN", request = request)
            Log.d("CheckoutDebug", "Resposta recebida: $response")
            response
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            errorBody?.let {
                val apiError = Gson().fromJson(it, ApiError::class.java)
                _apiErrorMessage.value = apiError.errmsg
                Log.e("CheckoutDebug", "API Error: ${apiError.errmsg} (code: ${apiError.errcode})")
            }
            null
        } catch (e: Exception) {
            _apiErrorMessage.value = "Erro de conexão ou inesperado: ${e.localizedMessage}"
            Log.e("CheckoutDebug", "Erro ao chamar createSale: ${e.message}", e)
            null
        }
    }




    suspend fun confirmSale(uuid: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitInstance.api.confirmSale(TOKEN, mapOf("sale_uuid" to uuid))
            Log.d("CheckoutDebug", "Confirmação response: ${response.code()}")
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("CheckoutDebug", "Erro ao confirmar venda: ${e.message}", e)
            false
        }
    }

    suspend fun cancelSale(uuid: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitInstance.api.cancelSale(TOKEN, mapOf("sale_uuid" to uuid))
            Log.d("CheckoutDebug", "Cancelamento response: ${response.code()}")
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("CheckoutDebug", "Erro ao cancelar venda: ${e.message}", e)
            false
        }
    }
}
