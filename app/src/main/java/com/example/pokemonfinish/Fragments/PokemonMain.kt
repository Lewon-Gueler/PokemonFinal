package com.example.pokemonfinish.Fragments


import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.navigation.findNavController
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.mvrx.MvRx
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.fragmentViewModel
import com.example.pokemonfinish.ColorsTyp
import com.example.pokemonfinish.Database.*
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * A simple [Fragment] subclass.
 */
//Data Class Later Database

/**
 * MvRxState with a list
 */
data class PokemonState(val pokeList: List<Pokemon> = emptyList()) : MvRxState


/**
 * MvRxViewModel with all logic functions
 */
class PokemonModel(initialState: PokemonState) : MvRxViewModel<PokemonState>(initialState) {

    //Creating Realm Instance
    val realm = Realm.getDefaultInstance()

    //Creating Retrofit Instance
    val pokemonNetwork: DownloadPokemon = DownloadPokemon()
    val service = pokemonNetwork.service

    //Logging
    val TAG = "PokemonModel"

    //First Server Request get the amount of pokemons
   fun getPokemon() {

       service.getAllPokemonDatas(200, 0).enqueue(object : Callback<PokemonList> {
           override fun onFailure(call: Call<PokemonList>, t: Throwable) {

               }

               override fun onResponse(call: Call<PokemonList>, response: Response<PokemonList>) {
                   response.body()?.results?.forEach { pokemon ->
                       getPokemonDetails(pokemon)
                   }
               }
           })
       }

    //Second Server Request get the url
    fun getPokemonDetails(pokemon: Pokemon) {
        val newURL = pokemon.url

        //Second Server Request
        newURL?.let {
            service.getPokeURL(newURL).enqueue(object: Callback<Pokemon> {
                override fun onFailure(call: Call<Pokemon>, t: Throwable) {

                }

                override fun onResponse(call: Call<Pokemon>, response: Response<Pokemon>) {
                    val allData = response.body()
                    val newId = allData?.id

                    Log.d(TAG,"Pokemon ID: $newId")
                    setOfState(allData)

                    //Set url
                    allData?.url = pokemon.url
                    getPokemonSpcies(allData)

                    //response.body()?.species?.let { it1 -> getPokemonSpcies(it1) }
                }
            })
        }
    }

    //Third Server Request
    fun getPokemonSpcies(pokemon: Pokemon?) {
        val speciesURL = pokemon?.species?.url

        Log.d(TAG,"Pokemon ID for Species: $speciesURL")

        speciesURL?.let {
            service.getChain(it).enqueue(object : Callback<Species> {
                override fun onFailure(call: Call<Species>, t: Throwable) {

                }

                override fun onResponse(call: Call<Species>, response: Response<Species>) {

                    val resSpec = response.body()
                    Log.d(TAG, "ChainURL: $resSpec")

                    //Set url from species
                    resSpec?.url = pokemon.species?.url
                    pokemon.species = resSpec

                    getEvolutionChain(pokemon)

                }
            })
        }
    }

    fun getEvolutionChain(pokemon: Pokemon) {
        val chainURL = pokemon.species?.evoChain?.url
        Log.d(TAG,"URL for Evo Chain: $chainURL")

        chainURL?.let {
            service.getEvos(it).enqueue(object : Callback<EvolutionChain> {
                override fun onFailure(call: Call<EvolutionChain>, t: Throwable) {
                    Log.d("ohNoo", "${t.message}")
                }

                override fun onResponse(call: Call<EvolutionChain>, response: Response<EvolutionChain>) {
                    val body = response.body()

                    Log.d(TAG, "Pokemon Evolution Chain Content: $body.")

                    var chain = body?.chain

                    while (chain != null) {

                        //Splitt URL to get ID and save in species
                        val splittetURL = chain.species?.url?.split("/")
                        chain.species?.id = splittetURL?.get(6)?.toInt()

                        chain.species?.evoChain = body
                        chain = chain.evolves.getOrNull(0)

                    }

                    body?.url = chainURL
                   // body?.chain?.species
                    pokemon.species?.evoChain = body

                    startRealm(pokemon)

                }
            })
        }
    }

    override fun onCleared() {
        super.onCleared()
        realm.close()
    }


    fun setOfState(datas: Pokemon?) {
        datas?.let {
            setState {
                copy(pokeList = pokeList.copy {
                    this.sortBy {
                        it.id
                    }
                    add(it)
                })
            }
        }
    }


    fun startRealm(pokemonData: Pokemon) {
        realm.beginTransaction()
        realm.copyToRealmOrUpdate(pokemonData)
        realm.commitTransaction()
    }


    fun realmCheck() {
        val db = realm.where(Pokemon::class.java).sort("id").findAll()
        val dbp = realm.copyFromRealm(db)

        if (db.isEmpty()) {
            getPokemon()
        } else {
            setState {
                copy(pokeList = dbp)
            }
        }
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

        viewModel.realmCheck()

        setHasOptionsMenu(true)

    }

    override fun epoxyController(): MvRxEpoxyController {
        return simpleController(viewModel) { state ->

//            state.evoList.forEach {
//                it.chain?.species?.name
//                it.chain?.evolves?.get(0)?.species?.name
//                it.chain?.evolves?.get(0)?.evoTo?.get(0)?.species3?.name
//            }

            state.pokeList.forEach {
                pokemon {
                    Log.d("pokemon exist","${it.id}")

                    //set UI-Elements
                    id(it.id)
                    title(it.name)
                    index(it.id.toString())

                    //Binding Image
                    onBind { model, view, position -> (view.dataBinding as? ListItemPokemonBinding)?.let { drawee ->
                        drawee.iVShinyFront.setImageURI(it.imageUri)
                        }
                    }

                    //Types Checking and Color Setting: NEED To Refector
                    if (it.types?.size == 2) {

                        val firstN = it.types?.get(0)?.type?.name
                        val firstC = firstN?.let { it1 -> ColorsTyp.valueOf(it1) }

                        val secondN = it.types?.get(1)?.type?.name
                        val secondC = secondN?.let { it1 -> ColorsTyp.valueOf(it1) }

                        typ1(secondN)
                        colorHexString(secondC?.color)

                        typ2(firstN)
                        colorHexString2(firstC?.color)

                    } else {
                        val firstN = it.types?.get(0)?.type?.name
                        val firstC = firstN?.let { it1 -> ColorsTyp.valueOf(it1) }
                        typ1(firstN)
                        colorHexString(firstC?.color)

                        //2nd Type = Black
                        colorHexString2("#121212")

                    }


                    //On CLick Listener which load a new fragment
                    onClick { view: View ->
                        view.findNavController().navigate(R.id.action_pokemonMain_to_pokemonInfo, Bundle().apply {
                            it.id?.let { it1 -> putInt(MvRx.KEY_ARG, it1) } }
                        )
                    }

                }
            }
        }
    }
}


@BindingAdapter("tagColor")
fun setTagColor(txtView: TextView, colorHexString: String?) {
    val background = txtView.background
    colorHexString?.let {
        if (background is GradientDrawable) {
            background.setColor(Color.parseColor(colorHexString))
        }
    }
}