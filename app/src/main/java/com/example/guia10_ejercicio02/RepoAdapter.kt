package com.example.guia10_ejercicio02

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.guia10_ejercicio02.databinding.ItemRepoBinding

class RepoAdapter(
    private val repos: List<GithubRepo>,
    private val onItemClick: (GithubRepo) -> Unit
) : RecyclerView.Adapter<RepoViewHolder>() {

    // Se llama cuando RecyclerView necesita un nuevo ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
        val binding = ItemRepoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RepoViewHolder(binding)
    }

    // Muestra los datos en una posición específica
    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
        holder.bind(repos[position], onItemClick)
    }

    // Número total de elementos
    override fun getItemCount(): Int = repos.size
}
