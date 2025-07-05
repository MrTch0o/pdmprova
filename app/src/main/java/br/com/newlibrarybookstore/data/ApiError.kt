package br.com.newlibrarybookstore.data

data class ApiError(
    val errcode: Int,
    val errmsg: String,
    val extra: String?
)