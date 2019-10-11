package com.example.pokemonfinish.Fragments


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.fragmentViewModel
import com.example.pokemonfinish.Database.PokemonDatas
import com.example.pokemonfinish.Database.PokemonList
import com.example.pokemonfinish.Networking.DownloadPokemon

import com.example.pokemonfinish.R
import com.example.pokemonfinish.databinding.FragmentPokemonMainBinding
import com.example.pokemonfinish.pokemon
import de.ffuf.android.architecture.mvrx.MvRxEpoxyController
import de.ffuf.android.architecture.mvrx.MvRxViewModel
import de.ffuf.android.architecture.mvrx.simpleController
import de.ffuf.android.architecture.realm.ui.base.binding.fragments.RealmEpoxyFragment
import de.ffuf.android.architecture.ui.base.binding.fragments.EpoxyFragment
import io.realm.Realm
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * A simple [Fragment] subclass.
 */
//Data Class Later Database

data class PokemonState(val id: Int=0, val name: String = "Bisasam"): MvRxState


class PokemonModel(initialState: PokemonState): MvRxViewModel<PokemonState>(initialState) {

    val realm = Realm.getDefaultInstance()

    fun getNetworkStuff() {
        val pokemonNetwork:DownloadPokemon = DownloadPokemon()
        val service = pokemonNetwork.service

        //First Server Request
        service.getAllPokemonDatas(10,0).enqueue(object : Callback<PokemonList> {
            override fun onFailure(call: Call<PokemonList>, t: Throwable) {

            }

            override fun onResponse(call: Call<PokemonList>, response: Response<PokemonList>) {
              val resource = response.body()

                response.body()?.results?.forEach { pokemon ->

                    val newURL = pokemon.url

                    //Second Server Request
                    newURL?.let {
                        service.getPokeURL(newURL).enqueue(object: Callback<PokemonDatas> {
                            override fun onFailure(call: Call<PokemonDatas>, t: Throwable) {

                            }

                            override fun onResponse(call: Call<PokemonDatas>, response: Response<PokemonDatas>) {


                                val allData = response.body()
                                realm.beginTransaction()

                                realm.copyToRealmOrUpdate(allData)

                                Log.d("current Pokemon", "${allData?.id} ${allData?.types?.get(0)?.type?.name}")

                                realm.where(PokemonDatas::class.java).findAll()
                                realm.commitTransaction()

                            }
                        })
                    }
                }
            }
        })
    }

}


class PokemonMain : EpoxyFragment<FragmentPokemonMainBinding>() {

    override val layoutId: Int
        get() = R.layout.fragment_pokemon_main

    private val viewModel: PokemonModel by fragmentViewModel()

    override val recyclerView: EpoxyRecyclerView
        get() = binding.recyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getNetworkStuff()

        setHasOptionsMenu(true)

        binding.recyclerView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
    }

    override fun epoxyController(): MvRxEpoxyController {
        return simpleController(viewModel) { state ->
            pokemon {
                id("name")
                title("${state.name}")
            }
        }
    }
}
