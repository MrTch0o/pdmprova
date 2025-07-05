package br.com.newlibrarybookstore.data

data class PurchaseRecord(
    val date: String,
    val items: List<BookSale>
)