package com.example.splashpokemon.data

import android.util.Log
import com.example.splashpokemon.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap

object pokemonTypeRepository {

    // Cache que ficará na memoria os IDs -> lista de tipos
    private val typeCache = ConcurrentHashMap<Int, List<String>>()

    private const val TAG = "pokemonTypeRepository"

    // Obter os tipos - mas primeiro pegar do cache, depois buscar da API e salvar
    suspend fun getPokemonTypes(pokemonId: Int): List<String> = withContext(Dispatchers.IO) {
        typeCache[pokemonId]?.let {
            Log.d(TAG, "Cache hit para ID $pokemonId -> Tipos: $it")
            return@withContext it
        }
        try {
            val response = RetrofitClient.api.getPokemonDetail(pokemonId)
            val types = response.types.map { it.type.name }

            Log.d(TAG, "API hit: ${response.name} (ID $pokemonId) -> Tipos: $types")

            // Salva no cahce
            typeCache[pokemonId] = types
            return@withContext types

        } catch (e: Exception) {
            Log.e(TAG, "Erro ao carregar tipos para ID $pokemonId: ${e.message}")
            return@withContext emptyList()
        }
    }
    // Pre-carregar os tipos dos primeiros pokemons
    suspend fun preloadPokemonTypes(pokemonIds: List<Int>) = withContext(Dispatchers.IO) {
        Log.d(TAG, "Iniciando pré-carregamento de ${pokemonIds.size} pokemons")

        pokemonIds.forEach { id ->
            if (!typeCache.containsKey(id)) {
                try {
                    val response = RetrofitClient.api.getPokemonDetail(id)
                    val types = response.types.map { it.type.name }
                    typeCache[id] = types
                    Log.d(TAG, "Pré-carregado: ${response.name} (ID $id) -> Tipos: $types")
                } catch (e: Exception) {
                    Log.e(TAG, "Falha ao pré-carregar ID $id: ${e.message}")
                }
            }
        }
        Log.d(TAG, "Pré-carregamento finalizado (${typeCache.size} pokemons armazenados)")
    }
}