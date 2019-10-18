package com.example.pokemonfinish.Fragments



import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.mvrx.MvRx
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.fragmentViewModel
import com.example.pokemonfinish.ColorsTyp
import com.example.pokemonfinish.Database.PokemonDatas
import com.example.pokemonfinish.Database.PokemonEvoChain
import com.example.pokemonfinish.Database.PokemonEvoSpecies
import com.example.pokemonfinish.Database.PokemonList
import com.example.pokemonfinish.Networking.Api
import com.example.pokemonfinish.Networking.DownloadPokemon
import com.example.pokemonfinish.R
import com.example.pokemonfinish.databinding.FragmentPokemonMainBinding
import com.example.pokemonfinish.databinding.ListItemPokemonBinding
import com.example.pokemonfinish.pokemon
import de.ffuf.android.architecture.binding.copy
import de.ffuf.android.architecture.mvrx.MvRxEpoxyController
import de.ffuf.android.architecture.mvrx.MvRxViewModel
import de.ffuf.android.architecture.mvrx.simpleController
import de.ffuf.android.architecture.ui.base.binding.fragments.EpoxyFragment
import io.realm.Realm
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.list_item_pokemon.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response






/**
 * A simple [Fragment] subclass.
 */
//Data Class Later Database

data class PokemonState(val pokeList: List<PokemonDatas> = emptyList()): MvRxState


class PokemonModel(initialState: PokemonState): MvRxViewModel<PokemonState>(initialState) {

    val realm = Realm.getDefaultInstance()

    fun getNetworkStuff() {
        val pokemonNetwork:DownloadPokemon = DownloadPokemon()
        val service = pokemonNetwork.service

        //First Server Request
        service.getAllPokemonDatas(30,0).enqueue(object : Callback<PokemonList> {
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
                                val newId = allData?.id


                                //third Server Request:
                                newId.let { it1 ->
                                    service.getEvos(it1!!).enqueue(object: Callback<PokemonEvoChain> {
                                        override fun onFailure(call: Call<PokemonEvoChain>, t: Throwable) {

                                        }

                                        override fun onResponse(call: Call<PokemonEvoChain>, response: Response<PokemonEvoChain>) {

                                            val newData = response.body()

                                            allData?.let { it2 -> newData?.let { it3 ->
                                                startRealm(it2,
                                                    it3
                                                )
                                            } }

                                            setOfState(allData)
                                        }

                                    })
                                }

                            }
                        })
                    }
                }
            }
        })
    }


    override fun onCleared() {
        super.onCleared()
        realm.close()
    }


    fun setOfState(datas: PokemonDatas?) {
        datas?.let {
            setState {
                copy(pokeList= pokeList.copy {
                    this.sortBy {
                        it.id
                    }
                    add(it)
                })
            }
        }
    }


    fun startRealm(allData: PokemonDatas, newData: PokemonEvoChain) {
        realm.beginTransaction()
        realm.copyToRealmOrUpdate(allData)
        realm.copyToRealmOrUpdate(newData)
        //Log.d("current Pokemon", "${allData?.id} ${allData?.types?.get(0)?.type?.name}")
        //val db = realm.where(PokemonDatas::class.java).sort("id").findAll()
        realm.commitTransaction()
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

    }

    override fun epoxyController(): MvRxEpoxyController {
        return simpleController(viewModel) { state ->
          state.pokeList.forEach {
              pokemon {
                  id(it.id)

                  //Name and ID
                  title(it.name)
                  index(it.id.toString())

                  //Binding Image
                  onBind { model, view, position ->
                      (view.dataBinding as? ListItemPokemonBinding)?.let { drawee ->
                          drawee.iVShinyFront.setImageURI(it.imageUri)

                         // ColorsTyp.FIRE.rgb()?.let { it1 -> drawee.typ1.setBackgroundColor(it1) } }
                      }

                  }
//                  it.types.forEach {
//
//                      val typ = it.type?.name?.let { it1 -> ColorsTyp.valueOf(it1.toUpperCase())

//
//                      typ1(typ?.key)
//                      color(typ?.colorType)
//
//                  }

                  //On CLick Listener which load a new fragment
                  onClick { view : View ->
                      view.findNavController().navigate(
                          R.id.action_pokemonMain_to_pokemonInfo,
                          Bundle().apply { it.id?.let { it1 -> putInt(MvRx.KEY_ARG, it1) }  }
                      )
                  }

                  //Checking the types
                  if (it.types.size == 2) {
                      val firstN = it.types.get(0)?.type?.name
                      typ1(firstN)

//                      val firstC = firstN?.let { it1 -> ColorsTyp.valueOf(it1.toUpperCase()) } onColor(firstC?.rgb())

                      val secondN = it.types.get(1)?.type?.name
                      typ2(secondN)

                  } else {
                      typ1(it.types.get(0)?.type?.name)
                      // Setting visisbilty false
                      typ2("")
                  }

              }
          }
        }
    }
}

