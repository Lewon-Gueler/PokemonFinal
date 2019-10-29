package com.example.pokemonfinish.Fragments


import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
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
 * MvRxState
 * @param emptylist which will be later set and have all the information
 */
data class PokemonState(val pokeList: List<Pokemon> = emptyList()) : MvRxState


/**
 * MvRxViewModel creating network and database instance.
 * starts the server request functions.
 */
class PokemonViewModel(initialState: PokemonState) : MvRxViewModel<PokemonState>(initialState) {

    //Creating Realm Instance
    private val realm = Realm.getDefaultInstance()

    //Creating Retrofit Instance
    private val pokemonNetwork: DownloadPokemon = DownloadPokemon()
    private val service = pokemonNetwork.service

    //Logging
    val TAG = "PokemonViewModel"

    //First Server Request get the amount of pokemon
    fun getPokemon() {

        service.getAllPokemonDatas(50, 0).enqueue(object : Callback<PokemonList> {
            override fun onFailure(call: Call<PokemonList>, t: Throwable) {

            }

            override fun onResponse(call: Call<PokemonList>, response: Response<PokemonList>) {
                response.body()?.results?.forEach { pokemon ->
                    getPokemonDetails(pokemon)
                }
            }
        })
    }

    //Second Server Request get the url for all PokemonDatas
    fun getPokemonDetails(pokemon: Pokemon) {
        val newURL = pokemon.url

        //Second Server Request
        newURL?.let {
            service.getPokeURL(newURL).enqueue(object : Callback<Pokemon> {
                override fun onFailure(call: Call<Pokemon>, t: Throwable) {

                }

                override fun onResponse(call: Call<Pokemon>, response: Response<Pokemon>) {
                    val allData = response.body()
                    val newId = allData?.id

                    Log.d(TAG, "Pokemon ID: $newId")
                    setOfState(allData)

                    //Set url
                    allData?.url = pokemon.url
                    allData?.let { it1 -> getPokemonSpcies(it1) }

                }
            })
        }
    }

    //Third Server Request
    fun getPokemonSpcies(pokemon: Pokemon) {
        val speciesURL = pokemon.species?.url

        Log.d(TAG, "Pokemon ID for Species: $speciesURL")

        speciesURL?.let { service.getChain(it).enqueue(object : Callback<Species> {
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
        Log.d(TAG, "URL for Evo Chain: $chainURL")

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
                    val test = chainURL
                    body?.url = chainURL
                    pokemon.species?.evoChain = body

                    startRealm(pokemon)

                }
            })
        }
    }

//    fun checkChain(chain: Chain?, pokemon: Pokemon, body: EvolutionChain?){
//
//        val chainURL = pokemon.species?.evoChain?.url
//        while (chain != null) {
//
//            //Splitt URL to get ID and save in species
//            val splittetURL = chain.species?.url?.split("/")
//            chain.species?.id = splittetURL?.get(6)?.toInt()
//
//            chain.species?.evoChain = body
//            chain = chain.evolves.getOrNull(0)
//
//        }
//
//        body?.url = chainURL
//        pokemon.species?.evoChain = body
//    }


    override fun onCleared() {
        super.onCleared()
        realm.close()
    }

    //Set list with sorted data and set it into the state
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


    //function for saving data in realm database
    fun startRealm(pokemonData: Pokemon) {
        realm.beginTransaction()
        realm.copyToRealmOrUpdate(pokemonData)
        realm.commitTransaction()
    }

    //checks if the database is empty or not. If data are empty it starts Server Requests
    fun databaseCheck() {
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

/**
 * Epoxy Fragment loading fragment layout and create View
 */
class PokemonMain : EpoxyFragment<FragmentPokemonMainBinding>() {

    override val layoutId: Int
        get() = R.layout.fragment_pokemon_main

    private val viewViewModel: PokemonViewModel by fragmentViewModel()

    override val recyclerView: EpoxyRecyclerView
        get() = binding.recyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewViewModel.databaseCheck()

        setHasOptionsMenu(true)

    }

    /**
     * Epoxy Conroller creates the pokemon xml Layout and generates
     * ui elemtents which will be set with data from state
     */
    override fun epoxyController(): MvRxEpoxyController {
        return simpleController(viewViewModel) { state ->

            state.pokeList.forEach {
                pokemon {
                    Log.d("pokemon exist", "${it.id}")

                    //set UI-Elements
                    id(it.id)
                    title(it.name)
                    index(it.id.toString())
                    image(it.imageUri)

                    //Types checking and set typecolor
                    val firstName = it.types?.last()?.type?.name
                    val firstColor = firstName?.let { typeName ->
                        ColorsTyp.valueOf(typeName) }

                    //Set Tyoe and Color
                    typ1(firstName)
                    colorHexString(firstColor?.color)

                    if (it.types?.size == 2) {

                        val secondName = it.types?.first()?.type?.name
                        val secondColor = secondName?.let { typeName ->
                            ColorsTyp.valueOf(typeName) }

                        typ2(secondName)
                        colorHexString2(secondColor?.color)

                    }

                    //On CLick Listener which load a new fragment
                    onClick { view: View ->
                        view.findNavController()
                            .navigate(R.id.action_pokemonMain_to_pokemonInfo, Bundle().apply { ->
                                it.id?.let { pokemonId -> putInt(MvRx.KEY_ARG, pokemonId) }
                            }
                            )
                    }
                }
            }
        }
    }
}

/**
 * set color on textView Types
 */
@BindingAdapter("tagColor")
fun setTagColor(txtView: TextView, colorHexString: String?) {
    val background = txtView.background
    colorHexString?.let {
        if (background is GradientDrawable) {
            background.setColor(Color.parseColor(colorHexString))
        }
    }
}