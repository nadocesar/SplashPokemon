package com.example.splashpokemon.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewParent
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.example.splashpokemon.Pokemon
import com.example.splashpokemon.R
import com.example.splashpokemon.data.pokemonData
import com.example.splashpokemon.databinding.ItemPokemonBinding
import com.example.splashpokemon.ui.pokemonListFragment

class pokemonAdapter(

    private var pokemons: List<pokemonData>,
    private val onItemClick: (pokemonData)-> Unit

): RecyclerView.Adapter<pokemonAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemPokemonBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPokemonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pokemon = pokemons[position]
        holder.binding.textName.text = pokemon.name.replaceFirstChar {it.uppercaseChar()}
        Glide.with(holder.itemView.context)
            .load(pokemon.getImageUrl())
            .into(holder.binding.imagePokemon)

        holder.itemView.setOnClickListener { onItemClick(pokemon) }
    }
    override fun getItemCount(): Int = pokemons.size

    // Metodo para atualizar a lista
    fun updateList(newList: List<pokemonData>) {
        pokemons = newList
        notifyDataSetChanged()
    }
}