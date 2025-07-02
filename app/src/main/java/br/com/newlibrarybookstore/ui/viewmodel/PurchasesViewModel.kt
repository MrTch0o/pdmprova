package br.com.newlibrarybookstore.ui.viewmodel

import androidx.lifecycle.ViewModel
import br.com.newlibrarybookstore.data.BookSale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PurchasesViewModel : ViewModel() {
    private val _purchases = MutableStateFlow<List<BookSale>>(emptyList())
    val purchases: StateFlow<List<BookSale>> = _purchases

    fun addPurchase(bookSales: List<BookSale>) {
        _purchases.value = _purchases.value + bookSales
    }
}