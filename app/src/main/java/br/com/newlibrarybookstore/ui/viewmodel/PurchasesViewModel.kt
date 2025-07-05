package br.com.newlibrarybookstore.ui.viewmodel

import androidx.lifecycle.ViewModel
import br.com.newlibrarybookstore.data.BookSale
import br.com.newlibrarybookstore.data.PurchaseRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PurchasesViewModel : ViewModel() {
    private val _purchases = MutableStateFlow<List<PurchaseRecord>>(emptyList())
    val purchases: StateFlow<List<PurchaseRecord>> = _purchases

    fun addPurchase(bookSales: List<BookSale>) {
        val currentDate = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())
        val record = PurchaseRecord(date = currentDate, items = bookSales)
        _purchases.value = _purchases.value + record
    }
}