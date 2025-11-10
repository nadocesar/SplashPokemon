package com.example.splashpokemon.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.splashpokemon.adapter.pokemonAdapter
import com.example.splashpokemon.data.pokemonData
import com.example.splashpokemon.databinding.FragmentListBinding
import com.example.splashpokemon.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class pokemonListFragment : Fragment() {
    private var IsCacheReady = false

    private var _binding : FragmentListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: pokemonAdapter
    private val pokemonList = mutableListOf<pokemonData>()
    private val filteredList = mutableListOf<pokemonData>()

    private var selectedType: String? = null
    private var selectedGeneration: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListBinding.inflate(inflater, container, false)

        //Recycleview Setup
        adapter = pokemonAdapter(filteredList) { pokemon ->
            openDetail(pokemon.getId())
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        // Inicializa
        loadPokemons()
        setupSearch()            //
        setupTypeFilter()        // Chama filtro de tipo
        setupGenerationFilter()  // Chama filtro de geração

        return binding.root
    }

    private fun loadPokemons() {
        // Mostra o loading
        binding.loadingAnimation.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                // Busca lista pokemon
                val response = RetrofitClient.api.getPokemonList()
                val results = response.results

                pokemonList.clear()
                pokemonList.addAll(results)
                filteredList.clear()
                filteredList.addAll(results)
                adapter.notifyDataSetChanged()

                // Pré carrega os tipos de primeiros 100 pokemons no cache
                val ids = pokemonList.take(100).map { it.getId() }
                lifecycleScope.launch {
                    com.example.splashpokemon.data.pokemonTypeRepository.preloadPokemonTypes(ids)
                    IsCacheReady = true // Aqui o cache fica pronto
                    Log.d("CacheStatus", "Cache de tipos pronto!")

                    // Replica o filtro automatico quando o cache terminar
                    requireActivity().runOnUiThread {
                        applyFilters()
                        binding.loadingAnimation.visibility = View.GONE
                    }
                }

            } catch (e: Exception) {
                Log.e("loadPokemons", "Erro: ${e.message}")
            } finally {
                binding.loadingAnimation.visibility = View.GONE
            }
        }
    }
//  Busca por tipos
//    private fun loadPokemonTypes() {
//        // log
//        Log.d("PokemonTypes", "Iniciando carregamento dos tipos (${pokemonList.size} pokémons)")
//
//        lifecycleScope.launch {
//            var loadedCount = 0
//
//            for (pokemon in pokemonList.take(50)) {
//                try {
//                    val detail = RetrofitClient.api.getPokemonDetail(pokemon.getId())
//                    val types = detail.types.map { it.type.name }
//                    pokemon.types = types
//
//                    loadedCount++
//                    Log.d("PokemonTypes", "[$loadedCount ${pokemon.name} -> ${types.joinToString()}")
//
//                    // Atualiza o item sem afetar as threads
//                    adapter.notifyItemChanged(pokemonList.indexOf(pokemon))
//                } catch (e: Exception) {
//                    Log.e("PokemonTypes", "Erro ao carregar ${pokemon.name}: ${e.message}")
//                }
//            }
//
//            Log.d("PokemonTypes", "Total de Pokemons carregados: ${pokemonList.size}")
//        }
//}

    // Detalhes com animação
    private fun openDetail(id: Int) {
        binding.loadingAnimation.apply {
            visibility = View.VISIBLE
            speed = 0.6f
            progress = 0f
            playAnimation()
        }

        binding.loadingAnimation.postDelayed({
            val intent = Intent(requireContext(), detailActivity::class.java)
            intent.putExtra("POKEMON_ID", id)
            startActivity(intent)
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

            binding.loadingAnimation.postDelayed({
                binding.loadingAnimation.cancelAnimation()
                binding.loadingAnimation.visibility = View.GONE
            }, 800)
        }, 1000)
    }

    // Buscar
    private fun setupSearch() {
        binding.searchPokemon.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = applyFilters()
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        })
    }

    // Filtro por tipo
    private fun setupTypeFilter() {
        val types = listOf(
            "fire", "water", "grass", "electric", "psychic",
            "ice", "dragon", "dark", "fairy", "bug",
            "rock", "ground", "poison", "ghost", "fighting", "steel"
        )

        binding.inputType.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Tipo")
                .setItems(types.toTypedArray()) {_, which ->
                    selectedType = types[which]
                    binding.inputType.setText(selectedType)
                    applyFilters()
                }
                .setNegativeButton("Limpar") {_, _ ->
                    selectedType = null
                    binding.inputType.setText("Tipo")
                    applyFilters()
                }
                .show()
        }
    }

    // Filtro Geração
    private fun setupGenerationFilter() {
        val generation = listOf(
            "geração 1", "geração 2", "geração 3", "geração 4",
            "geração 5", "geração 6", "geração 7", "geração 8", "geração 9",
        )

        binding.inputGeneration.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Geração")
                .setItems(generation.toTypedArray()) {_, which ->
                    selectedGeneration = generation[which]
                    binding.inputGeneration.setText(selectedGeneration)
                    applyFilters()
                }
                .setNegativeButton("Limpar") { _, _ ->
                    selectedGeneration = null
                    binding.inputGeneration.setText("Geração")
                    applyFilters()
                }
                .show()
        }
    }

    // Aplicar busca + tipo + geração
    private fun applyFilters() {
        val query = binding.searchPokemon.text.toString().lowercase()
        binding.loadingAnimation.visibility = View.VISIBLE

        lifecycleScope.launch {
            val filtered = withContext(Dispatchers.IO) {
                pokemonList.filter { pokemon ->
                    // Filtro por nome
                    val matchesName = pokemon.name.lowercase().contains(query)
                    if (!matchesName) return@filter false

                    // Filtro por geração
                    val matchesGen = selectedGeneration?.let { gen ->
                        checkGeneration(pokemon.getId(), gen)
                    } ?: true
                    if (!matchesGen) return@filter false

                    // Filtro por tipo (usa cache se disponível)
                    val types = pokemon.types ?: com.example.splashpokemon.data.pokemonTypeRepository
                        .getPokemonTypes(pokemon.getId())
                        .also { pokemon.types = it }

                    val matchesType = selectedType?.let { types.contains(it) } ?: true
                    matchesType
                }
            }

            // Atualiza UI de uma vez só
            filteredList.clear()
            filteredList.addAll(filtered)
            adapter.notifyDataSetChanged()
            binding.loadingAnimation.visibility = View.GONE

            Log.d(
                "ApplyFilters",
                "Filtro aplicado: nome='$query', tipo=$selectedType, geração=$selectedGeneration, resultados=${filteredList.size}"
            )
        }
    }

    // Geração do ID
    private fun checkGeneration(id: Int, generation: String): Boolean {
        return when (generation) {
            "geração 1" -> id in 1..151
            "geração 2" -> id in 151..251
            "geração 3" -> id in 252..386
            "geração 4" -> id in 387..493
            "geração 5" -> id in 464..649
            "geração 6" -> id in 650..721
            "geração 7" -> id in 722..809
            "geração 8" -> id in 810..905
            "geração 9" -> id >= 906
            else -> true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}