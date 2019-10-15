package com.example.pokemonfinish.Fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner

import com.example.pokemonfinish.R
import com.example.pokemonfinish.databinding.FragmentFragmentPokemonDetailBinding
import de.ffuf.android.architecture.ui.base.binding.fragments.MvrxBaseBottomSheetDialog

/**
 * A simple [Fragment] subclass.
 */
class FragmentPokemonDetail(lifecycleOwner: LifecycleOwner) : MvrxBaseBottomSheetDialog<FragmentFragmentPokemonDetailBinding>(lifecycleOwner) {

    override val layout: Int
        get() = R.layout.fragment_fragment_pokemon_detail

    override fun invalidate() {

    }

    override fun onViewCreated(binding: FragmentFragmentPokemonDetailBinding) {
        binding.title = "Test"
    }
}
