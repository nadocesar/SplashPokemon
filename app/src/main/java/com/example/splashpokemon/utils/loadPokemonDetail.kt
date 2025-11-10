package com.example.splashpokemon.utils

import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.splashpokemon.data.pokemonDetail
import com.example.splashpokemon.databinding.ActivityDetailBinding
import com.example.splashpokemon.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun loadPokemonDetail(
    id: Int,
    binding: ActivityDetailBinding,
    activity: AppCompatActivity,
    onLoaded: (String) -> Unit
) {

    // Esconde enquanto carrega
    binding.imagePokemon.visibility = View.GONE
    binding.textName.visibility = View.GONE
    binding.textId.visibility = View.GONE
    binding.textHeight.visibility = View.GONE
    binding.textWeight.visibility = View.GONE
    binding.textTypes.visibility = View.GONE
    binding.textStats.visibility = View.GONE

    // Usa a corrotina vinculado ao Activity
    activity.lifecycleScope.launch {
        try {
            // chama api no thread
            val p = withContext(Dispatchers.IO) {
                RetrofitClient.api.getPokemonDetail(id)
            }
        // Atualiza a UI na main thread
        binding.textName.text = p.name.replaceFirstChar { it.uppercaseChar() }
        binding.textId.text = "ID: ${p.id}"
        binding.textHeight.text = "Altura: ${p.height}"
        binding.textWeight.text = "Peso: ${p.weight}"

        // chama callback Tipo
        val mainType = p.types.getOrNull(0)?.type?.name?.lowercase()?.trim() ?: "normal"
        onLoaded(mainType)

        // Status do pokemon
        val stats = p.stats.joinToString(",") { "${it.stat.name}: ${it.base_stat}" }
        binding.textStats.text = stats

        // Carrega imagem
        Glide.with(activity)
            .load(p.sprites.front_default)
            .into(binding.imagePokemon)

       // Exibe elementos
        fadeInViews(
            binding.imagePokemon,
            binding.textName,
            binding.textId,
            binding.textHeight,
            binding.textWeight,
            binding.textTypes,
            binding.textStats
        )

    } catch (e: Exception) {
        Log.e("loadPokemonDetail", "Erro ao carregar detalhes do Pokemon: ${e.message}")
        e.printStackTrace()
    }
  }
}

