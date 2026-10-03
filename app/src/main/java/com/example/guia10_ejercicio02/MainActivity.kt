package com.example.guia10_ejercicio02

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.guia10_ejercicio02.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var repoAdapter: RepoAdapter
    private val repos: MutableList<GithubRepo> = ArrayList()

    // Llamada en curso (para cancelarla si el usuario hace otra búsqueda)
    private var currentCall: Call<List<GithubRepo>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Barra de estado transparente con íconos claros (el encabezado es oscuro)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))

        // Inflar el diseño de la actividad y establecerlo como el contenido de la vista
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, 0)
            // El encabezado se extiende bajo la barra de estado
            binding.header.setPadding(20.dp(), systemBars.top + 16.dp(), 20.dp(), 20.dp())
            // La lista no queda tapada por la barra de navegación
            binding.listRepos.setPadding(0, 4.dp(), 0, systemBars.bottom + 8.dp())
            insets
        }

        initRecyclerView()
        styleSearchView()
        binding.searchUsers.setOnQueryTextListener(this)
    }

    private fun Int.dp(): Int = (this * resources.displayMetrics.density).toInt()

    // Aplica los colores de la paleta al texto del buscador
    private fun styleSearchView() {
        val searchText = binding.searchUsers.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchText.setTextColor(ContextCompat.getColor(this, R.color.palette_1))
        searchText.setHintTextColor(ContextCompat.getColor(this, R.color.palette_3))
    }

    private fun initRecyclerView() {
        repoAdapter = RepoAdapter(repos) { repo -> openRepo(repo) }
        binding.listRepos.layoutManager = LinearLayoutManager(this)
        binding.listRepos.adapter = repoAdapter
    }

    // Llama a la API de GitHub de forma asíncrona
    private fun searchRepos(username: String) {
        currentCall?.cancel()
        showLoading()

        val call = RetrofitClient.instance.getUserRepos(username, 100, "updated")
        currentCall = call

        call.enqueue(object : Callback<List<GithubRepo>> {
            override fun onResponse(call: Call<List<GithubRepo>>, response: Response<List<GithubRepo>>) {
                if (response.isSuccessful) {
                    val result = response.body().orEmpty()
                    repos.clear()
                    repos.addAll(result)
                    repoAdapter.notifyDataSetChanged()

                    if (result.isEmpty()) {
                        showMessage("El usuario \"$username\" no tiene repositorios públicos")
                    } else {
                        showRepos(username, result.size)
                    }
                } else {
                    clearList()
                    when (response.code()) {
                        404 -> showMessage("No se encontró el usuario \"$username\"")
                        403 -> showMessage("Se alcanzó el límite de peticiones de la API de GitHub. Intenta de nuevo más tarde")
                        else -> showMessage("Ocurrió un error (código ${response.code()})")
                    }
                }
            }

            override fun onFailure(call: Call<List<GithubRepo>>, t: Throwable) {
                // Si la llamada fue cancelada por una nueva búsqueda, no se muestra error
                if (call.isCanceled) return
                clearList()
                showMessage("No se pudo conectar. Revisa tu conexión a internet")
            }
        })
    }

    private fun clearList() {
        repos.clear()
        repoAdapter.notifyDataSetChanged()
    }

    // Abre el repositorio en el navegador
    private fun openRepo(repo: GithubRepo) {
        val url = repo.htmlUrl ?: return
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "No se encontró un navegador", Toast.LENGTH_SHORT).show()
        }
    }

    // ---------- Estados de la pantalla ----------

    private fun showLoading() {
        binding.progress.visibility = View.VISIBLE
        binding.tvStatus.visibility = View.GONE
        binding.tvCount.visibility = View.GONE
        binding.listRepos.visibility = View.GONE
    }

    private fun showMessage(message: String) {
        binding.progress.visibility = View.GONE
        binding.tvCount.visibility = View.GONE
        binding.listRepos.visibility = View.GONE
        binding.tvStatus.text = message
        binding.tvStatus.visibility = View.VISIBLE
    }

    private fun showRepos(username: String, count: Int) {
        binding.progress.visibility = View.GONE
        binding.tvStatus.visibility = View.GONE
        binding.tvCount.text = "Repositorios públicos de $username: $count"
        binding.tvCount.visibility = View.VISIBLE
        binding.listRepos.visibility = View.VISIBLE
        binding.listRepos.scrollToPosition(0)
    }

    // ---------- Buscador ----------

    // Se busca al presionar el botón de buscar (evita agotar el límite de peticiones de GitHub)
    override fun onQueryTextSubmit(query: String?): Boolean {
        val username = query?.trim().orEmpty()
        if (username.isEmpty()) {
            Toast.makeText(this, "Ingresa un nombre de usuario", Toast.LENGTH_SHORT).show()
        } else {
            binding.searchUsers.clearFocus() // oculta el teclado
            searchRepos(username)
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        currentCall?.cancel()
    }
}
