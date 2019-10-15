package com.example.pokemonfinish.Fragments


import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.postDelayed
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.fragmentViewModel
import com.example.pokemonfinish.Database.PokemonDatas

import com.example.pokemonfinish.R
import com.example.pokemonfinish.databinding.FragmentPokemonInfoBinding
import com.example.pokemonfinish.databinding.FragmentPokemonMainBinding
import com.example.pokemonfinish.datas
import de.ffuf.android.architecture.binding.copy
import de.ffuf.android.architecture.mvrx.MvRxEpoxyController
import de.ffuf.android.architecture.mvrx.MvRxViewModel
import de.ffuf.android.architecture.mvrx.simpleController
import de.ffuf.android.architecture.realm.classes.toUnmanaged
import de.ffuf.android.architecture.ui.base.binding.fragments.EpoxyFragment
import de.ffuf.android.architecture.ui.base.binding.fragments.MvrxFragment
import io.realm.Realm
import android.R.attr.defaultValue
import android.R.attr.key
import androidx.navigation.findNavController
import com.example.pokemonfinish.evolutions
import kotlinx.android.synthetic.main.list_item_datas.*


/**
 * A simple [Fragment] subclass.
 */

data class InfoState(val pokemon: PokemonDatas? = null): MvRxState


class InfoModel(initialState: InfoState): MvRxViewModel<InfoState>(initialState) {


    fun getRealmData(pokeId: Int) {
        val realm = Realm.getDefaultInstance()
        val pokemon = realm.where(PokemonDatas::class.java).equalTo("id", pokeId).findFirst()?.toUnmanaged()

        //State erstellen:


    }
}


class PokemonInfo : EpoxyFragment<FragmentPokemonInfoBinding>() {

    override val layoutId: Int
        get() = R.layout.fragment_pokemon_info

    private val viewModel: InfoModel by fragmentViewModel()

    override val recyclerView: EpoxyRecyclerView
        get() = binding.recyclerView2

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //arguments.getParacable und dann der Key

        viewModel.getRealmData(1)

       // TODO("Get ID from PokemonMain Fragment to load the right information from realm")
        //Auto create the buttons or use tabview

    }





    override fun epoxyController(): MvRxEpoxyController {


        return simpleController(viewModel) { state ->
                datas {
                    id()
                    heigth("test")
                    weigth("test2")
                    title("Abra")
                }
            evolutions {
                id()

            }

        }
    }
}
