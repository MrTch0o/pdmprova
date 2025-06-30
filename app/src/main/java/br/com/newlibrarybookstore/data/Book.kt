// file: data/Book.kt
package br.com.newlibrarybookstore.data

import com.google.gson.annotations.SerializedName

data class Book(
    // O ID na nova API é um número inteiro
    val id: Int,

    val title: String,
    val author: String,
    val description: String?, // O '?' indica que este campo pode ser nulo
    val publisher: String,
    val year: Int,
    val unities: Int,

    // O preço vem como um inteiro (ex: 4000 para R$40,00)
    val price: Int,

    // Mapeia o campo "img_res" do JSON para a nossa variável "imageResourceId"
    @SerializedName("img_res")
    val imageResourceId: String? // Também pode ser nulo
) {
    /**
     * Propriedade calculada que monta a URL completa da imagem.
     * Ela só retorna uma URL se 'imageResourceId' não for nulo.
     */
    val coverImageUrl: String?
        get() = imageResourceId?.let { "${RetrofitInstance.BASE_URL}img/$it" }

    /**
     * Propriedade calculada que formata o preço de centavos para um formato legível.
     * Ex: 4000 -> "R$ 40,00"
     */
    val formattedPrice: String
        get() = "R$ %.2f".format(price / 100.0)
}