package br.com.newlibrarybookstore.data

import com.google.gson.annotations.SerializedName

data class BooksSaleRequest(
    @SerializedName("books_sale_data")
    val booksSaleData: Map<String, Int>
)