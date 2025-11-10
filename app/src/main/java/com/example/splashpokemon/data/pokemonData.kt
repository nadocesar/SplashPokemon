package com.example.splashpokemon.data

data class pokemonData(
    val name: String,
    val url: String,
    var types: List<String> = emptyList()
) {
    fun getId(): Int {
        return url.trimEnd('/').split("/").last().toInt()
    }

    fun getImageUrl(): String {
        return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${getId()}.png"
    }
}