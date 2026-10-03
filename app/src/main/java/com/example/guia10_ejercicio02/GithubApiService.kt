package com.example.guia10_ejercicio02

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubApiService {
    /*
    Obtiene los repositorios públicos de un usuario de GitHub

    @GET indica que es una solicitud HTTP GET
    @Path agrega el valor de username a la URL
    @Query agrega parámetros opcionales (?per_page=100&sort=updated)
     */
    @GET("users/{username}/repos")
    fun getUserRepos(
        @Path("username") username: String,
        @Query("per_page") perPage: Int,
        @Query("sort") sort: String
    ): Call<List<GithubRepo>>
}
