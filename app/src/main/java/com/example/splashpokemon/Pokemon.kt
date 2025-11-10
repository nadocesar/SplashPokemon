package com.example.splashpokemon

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.splashpokemon.ui.pokemonListFragment

class Pokemon : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pokemon)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val layoutWelcome = findViewById<android.view.View>(R.id.layoutWelcome)
        val fragmentContainer = findViewById<android.view.View>(R.id.fragment_list)

        // Bem vindos
        Handler(Looper.getMainLooper()).postDelayed({
            //
            layoutWelcome.visibility = android.view.View.GONE
            fragmentContainer.visibility = android.view.View.VISIBLE

            // Fragmento-lista
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_list, pokemonListFragment())
                .commit()
        }, 1200)
    }
}

