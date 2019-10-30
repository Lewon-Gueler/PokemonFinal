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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


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

    //starts the first Server Request with an limit of Pokemons
    fun getPokemonAsync() {
        CoroutineScope(Dispatchers.IO).launch {

            val response = service.getAllPokemonDatas(50, 0)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    response.body()?.results?.forEach { pokemon ->
                        getPokemonDetailsAsync(pokemon)
                    }
                } else {
                    Log.d(TAG, "Error: ${response.code()}")
                }
            }
        }
    }

    fun getPokemonDetailsAsync(pokemon: Pokemon) {
        val newURL = pokemon.url
        CoroutineScope(Dispatchers.IO).launch {

            val response = newURL?.let { service.getPokeURL(it) }
            withContext(Dispatchers.Main) {
                if (response?.isSuccessful == true) {
                    onPokemmonDetailSuccess(response.body(),pokemon)
                } else {
                    Log.d(TAG, "Error: ${response?.code()}")
                }
            }
        }
    }

    fun getPokemonSpciesAsync(pokemon: Pokemon) {
        val speciesURL = pokemon.species?.url
        CoroutineScope(Dispatchers.IO).launch {

            val response = speciesURL?.let { service.getChain(it) }
            withContext(Dispatchers.Main) {
                if (response?.isSuccessful == true) {

                    //Set url from species
                    val resSpec = response.body()
                    resSpec?.url = pokemon.species?.url!!
                    pokemon.species = resSpec

                    getEvolutionChainAsync(pokemon)

                } else {
                    Log.d(TAG, "Error: ${response?.code()}")
                }
            }
        }
    }

    fun getEvolutionChainAsync(pokemon:Pokemon) {
        val chainURL = pokemon.species?.evoChain?.url
        CoroutineScope(Dispatchers.IO).launch {

            val response = chainURL?.let { service.getEvos(it) }
            withContext(Dispatchers.Main) {
                if (response?.isSuccessful == true) {
                    onChainSuccess(pokemon, response.body())
                    savePokemon(pokemon)

                } else {
                    Log.d(TAG, "Error: ${response?.code()}")
                }
            }
        }
    }

    fun onPokemmonDetailSuccess(res: Pokemon?, pokemon: Pokemon?) {
        val allData = res
        setOfState(allData)
        //Set url
        allData?.url = pokemon?.url
        allData?.let { getPokemonSpciesAsync(it) }
    }

    fun onChainSuccess(pokemon: Pokemon, body: EvolutionChain?){

        var chain = body?.chain
        val chainURL = pokemon.species?.evoChain?.url
        while (chain != null) {

            //Splitt URL to get ID and save in species
            val splittetURL = chain.species?.url?.split("/")
            chain.species?.id = splittetURL?.get(6)?.toInt()

            chain.species?.evoChain = body
            chain = chain.evolves.getOrNull(0)

        }
        body?.url = chainURL
        pokemon.species?.evoChain = body

    }

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
    fun savePokemon(pokemonData: Pokemon) {
        realm.beginTransaction()
        realm.copyToRealmOrUpdate(pokemonData)
        realm.commitTransaction()
    }

    //checks if the database is empty or not. If data are empty it starts Server Requests
    fun dataBinding() {
        val db = realm.where(Pokemon::class.java).sort("id").findAll()
        val dbp = realm.copyFromRealm(db)

        if (db.isEmpty()) {
            getPokemonAsync()
        } else {
            setState {
                copy(pokeList = dbp)
            }
        }
    }
}

/**
 * Epoxy Fragment loading fragment layout and create View
 * bind recyclerView
 */
class PokemonMain : EpoxyFragment<FragmentPokemonMainBinding>() {

    override val layoutId: Int
        get() = R.layout.fragment_pokemon_main

    private val viewViewModel: PokemonViewModel by fragmentViewModel()

    override val recyclerView: EpoxyRecyclerView
        get() = binding.recyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewViewModel.dataBinding()

        setHasOptionsMenu(true)

    }

    /**
     * Epoxy Controller creates the pokemon xml Layout and generates
     * ui elements which will be set with data from state
     */
    override fun epoxyController(): MvRxEpoxyController = simpleController(viewViewModel) { state ->

            state.pokeList.forEach {
                val firstName = it.types?.last()?.type?.name
                val firstColor = firstName?.let { typeName ->
                    ColorsTyp.valueOf(typeName) }
                var secondName: String? = null
                var secondColor: ColorsTyp? = null
                if (it.types?.size == 2) {
                    secondName = it.types?.first()?.type?.name
                    secondColor = secondName?.let { typeName -> ColorsTyp.valueOf(typeName) }
                }

                pokemon {
                    //set UI-Elements
                    id(it.id)
                    title(it.name)
                    index(it.id.toString())
                    image(it.imageUri)
                    typ1(firstName)
                    colorHexString(firstColor?.color)
                    typ2(secondName)
                    colorHexString2(secondColor?.color)


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