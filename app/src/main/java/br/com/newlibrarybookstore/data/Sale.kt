package br.com.newlibrarybookstore.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class BookSale(
    @SerializedName("book_id") val bookId: Int,
    @SerializedName("book_price") val bookPrice: Int,
    @SerializedName("book_title") val bookTitle: String,
    @SerializedName("unities") val unities: Int
) : Parcelable

@Parcelize
data class Sale(
    @SerializedName("books_sales") val booksSales: List<BookSale>,
    val id: Int,
    @SerializedName("pix_b64") val pixB64: String,
    @SerializedName("pix_str") var pixStr: String,
    @SerializedName("sale_ts") val saleTs: String,
    val total: Int,
    val uuid: String
) : Parcelable {
    val formattedTotal: String
        get() = "R$ %.2f".format(total / 100.0)
}