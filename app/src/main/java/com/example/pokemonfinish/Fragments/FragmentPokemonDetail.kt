package com.example.pokemonfinish.Fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import com.example.pokemonfinish.Database.PokemonDatas

import com.example.pokemonfinish.R
import com.example.pokemonfinish.databinding.FragmentFragmentPokemonDetailBinding
import de.ffuf.android.architecture.ui.base.binding.fragments.MvrxBaseBottomSheetDialog
import io.realm.Realm

/**
 * A simple [Fragment] subclass.
 */
class FragmentPokemonDetail(lifecycleOwner: LifecycleOwner) : MvrxBaseBottomSheetDialog<FragmentFragmentPokemonDetailBinding>(lifecycleOwner) {


    override val layout: Int
        get() = R.layout.fragment_fragment_pokemon_detail

    override fun invalidate() {

    }

    override fun onViewCreated(binding: FragmentFragmentPokemonDetailBinding) {
        val realm = Realm.getDefaultInstance()
        val db = realm.where(PokemonDatas::class.java).sort("id").findFirst()

            binding.title = db?.name
            binding.iVFullImage.setImageURI(db?.imageUri)

    }
}
