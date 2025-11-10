package com.example.splashpokemon.ui

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.splashpokemon.R
import com.example.splashpokemon.databinding.ActivityDetailBinding
import com.example.splashpokemon.utils.loadPokemonDetail


class detailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Background sem cor
        window.setBackgroundDrawableResource(android.R.color.transparent)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.getIntExtra("POKEMON_ID", 1)

        // Função loadPokemon detalhes
        loadPokemonDetail(id, binding, this) { type ->
            // Ajustar a cor do fundo
            setBakgroundColorByType(type)
        }

        // Botão voltar
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
        // função cor do background detail
        private fun setBakgroundColorByType(type: String) {
            val colorRes = when (type.lowercase()) {
                "fire" -> R.color.fire
                "water" -> R.color.water
                "grass" -> R.color.grass
                "electric" -> R.color.electric
                "psychic" -> R.color.psychic
                "ice" -> R.color.ice
                "dragon" -> R.color.dragon
                "dark" -> R.color.dark
                "fairy" -> R.color.fairy
                "bug" -> R.color.bug
                "rock" -> R.color.rock
                "ground" -> R.color.ground
                "poison" -> R.color.poison
                "ghost" -> R.color.ghost
                "fighting" -> R.color.fighting
                "stell" -> R.color.steel
                else -> android.R.color.white
            }

            val rootView: View = binding.root
            val colorTo = ContextCompat.getColor(this, colorRes)
            val currentBg = (rootView.background as? ColorDrawable)?.color
            val colorFrom = (rootView.background as? ColorDrawable)?.color ?: Color.WHITE

            // Animação para trocar a cor
            val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
                colorAnimation.duration = 600L // duração da transição
                colorAnimation.addUpdateListener { animator ->
                    rootView.setBackgroundColor(animator.animatedValue as Int)
                }
                colorAnimation.start()
            }
            //findViewById<View>(R.id.detailRoot).setBackgroundColor(color)
}