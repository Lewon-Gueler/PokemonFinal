package com.example.pokemonfinish.Fragments


import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.postDelayed

import com.example.pokemonfinish.R

/**
 * A simple [Fragment] subclass.
 */
class PokemonInfo : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pokemon_info, container, false)
    }

    override fun onResume() {
        super.onResume()
        Handler().postDelayed(200) {
            fragmentManager?.let {
                FragmentPokemonDetail(this@PokemonInfo)
                    .show(it,"dialog")

            }
        }
    }

}
