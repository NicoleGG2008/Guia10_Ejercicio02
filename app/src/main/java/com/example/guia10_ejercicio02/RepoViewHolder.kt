package com.example.guia10_ejercicio02

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.guia10_ejercicio02.databinding.ItemRepoBinding

class RepoViewHolder(private val binding: ItemRepoBinding) : RecyclerView.ViewHolder(binding.root) {

    // Vincula los datos del repositorio con las vistas del diseño del elemento
    fun bind(repo: GithubRepo, onClick: (GithubRepo) -> Unit) {
        binding.tvRepoName.text = repo.name ?: "Sin nombre"
        binding.tvRepoFullName.text = repo.fullName ?: repo.owner?.login.orEmpty()
        binding.tvRepoDescription.text = repo.description ?: "Sin descripción"
        binding.tvStars.text = "★ ${repo.stars}"
        binding.tvForks.text = "Forks ${repo.forks}"

        // Si el repositorio no tiene lenguaje, se oculta la etiqueta
        if (repo.language.isNullOrEmpty()) {
            binding.tvLanguage.visibility = View.GONE
        } else {
            binding.tvLanguage.visibility = View.VISIBLE
            binding.tvLanguage.text = repo.language
        }

        binding.root.setOnClickListener { onClick(repo) }
    }
}
