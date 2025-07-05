package br.com.newlibrarybookstore.data

import com.google.gson.annotations.SerializedName

data class Book(
    val id: Int,

    val title: String,
    val author: String,
    val description: String?,
    val publisher: String,
    val year: Int,
    val unities: Int,

    val price: Int,

    @SerializedName("img_res")
    val imageResourceId: String?
) {

    val coverImageUrl: String?
        get() = imageResourceId?.let { "${RetrofitInstance.BASE_URL}img/$it" }

    val formattedPrice: String
        get() = "R$ %.2f".format(price / 100.0)
}