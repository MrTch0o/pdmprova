package br.com.newlibrarybookstore.data

import com.google.gson.annotations.SerializedName

data class BookSale(
    @SerializedName("book_id")
    val bookId: Int,

    @SerializedName("book_price")
    val bookPrice: Int,

    @SerializedName("book_title")
    val bookTitle: String,

    @SerializedName("unities")
    val unities: Int
)

data class Sale(
    @SerializedName("books_sales")
    val booksSales: List<BookSale>,

    val id: Int,

    @SerializedName("pix_b64")
    val pixB64: String,

    @SerializedName("pix_str")
    var pixStr: String,

    @SerializedName("sale_ts")
    val saleTs: String,

    val total: Int,

    val uuid: String
) {
    // Propriedade calculada para exibir total formatado como "R$ XX,XX"
    val formattedTotal: String
        get() = "R$ %.2f".format(total / 100.0)
}
