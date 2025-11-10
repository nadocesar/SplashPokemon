package com.example.splashpokemon.data

import androidx.resourceinspection.annotation.Attribute
import java.lang.reflect.Type

data class pokemonDetail(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val stats: List<Stat>,
    val types: List<TypeSlot>,
    val sprites: Sprites
) {
    data class Stat(
        val base_stat: Int,
        val stat: StatInfo
    )

    data class StatInfo(
        val name: String
    )

    data class TypeSlot(
        val slot: Int,
        val type: TypeName
    )

    data class TypeName(
        val name: String,
        val url: String
    )

    data class Sprites(
        val front_default: String
    )
}