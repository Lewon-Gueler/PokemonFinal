package com.example.pokemonfinish.Fragments


import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.postDelayed

import com.example.pokemonfinish.R
import com.example.pokemonfinish.databinding.FragmentPokemonInfoBinding
import de.ffuf.android.architecture.ui.base.binding.fragments.MvrxFragment

/**
 * A simple [Fragment] subclass.
 */
class PokemonInfo : MvrxFragment<FragmentPokemonInfoBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_pokemon_info

    override fun onResume() {
        super.onResume()
        Handler().postDelayed(200) {
            fragmentManager?.let {
                val dialog = FragmentPokemonDetail(this@PokemonInfo)

                dialog.isCancelable = false
                dialog.show(it, "fdsafdasf")


            }
        }
    }



}
