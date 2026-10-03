package com.example.guia10_ejercicio02

import com.google.gson.annotations.SerializedName

// Representa al dueño del repositorio (objeto "owner" del JSON)
data class Owner(
    @SerializedName("login")
    val login: String?
)

// Representa cada repositorio de la lista devuelta por la API de GitHub
data class GithubRepo(
    @SerializedName("id")
    val id: Long,

    @SerializedName("name")
    val name: String?,

    @SerializedName("full_name")
    val fullName: String?,

    @SerializedName("description")
    val description: String?,

    @SerializedName("language")
    val language: String?,

    @SerializedName("stargazers_count")
    val stars: Int,

    @SerializedName("forks_count")
    val forks: Int,

    @SerializedName("html_url")
    val htmlUrl: String?,

    @SerializedName("private")
    val isPrivate: Boolean,

    @SerializedName("owner")
    val owner: Owner?
)
