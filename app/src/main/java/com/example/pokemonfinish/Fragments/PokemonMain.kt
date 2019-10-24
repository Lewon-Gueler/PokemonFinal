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

data class PokemonState(val pokeList: List<PokemonDatas> = emptyList()) : MvRxState


class PokemonModel(initialState: PokemonState) : MvRxViewModel<PokemonState>(initialState) {

    //Creating Realm Instance
    val realm = Realm.getDefaultInstance()

    //Creating Retrofit Instance
    val pokemonNetwork: DownloadPokemon = DownloadPokemon()
    val service = pokemonNetwork.service


   fun firstRequest() {

           service.getAllPokemonDatas(20, 0).enqueue(object : Callback<PokemonList> {
               override fun onFailure(call: Call<PokemonList>, t: Throwable) {

               }

               override fun onResponse(call: Call<PokemonList>, response: Response<PokemonList>) {
                   response.body()?.results?.forEach { pokemon ->

                       val newURL = pokemon.url
                       newURL?.let { secondRequest(it) }

                   }

               }
           })
       }


    fun secondRequest(newURL:String) {

        //Second Server Request
        newURL.let {
            service.getPokeURL(newURL).enqueue(object: Callback<PokemonDatas> {
                override fun onFailure(call: Call<PokemonDatas>, t: Throwable) {
                }

                override fun onResponse(call: Call<PokemonDatas>, response: Response<PokemonDatas>) {

                    val allData = response.body()
                    val newId = allData?.id

                    setOfState(allData)

//                    val newList: ArrayList<PokemonDatas> = ArrayList()
//                    allData?.let { it1 -> newList.add(it1) }

                    allData?.let { it1 -> startRealm(it1) }

                    newId?.let { it1 -> thirdRequest(it1) }

                }
            })
        }
    }

    fun thirdRequest(NewId:Int) {

        service.getChain(NewId).enqueue(object : Callback<PokemonSpecies> {
            override fun onFailure(call: Call<PokemonSpecies>, t: Throwable) {

            }

            override fun onResponse(call: Call<PokemonSpecies>, response: Response<PokemonSpecies>) {


                val chainURL = response.body()?.evoChain?.url
                Log.d("king", "$chainURL")

                chainURL?.let { fourthRequest(it) }
            }

        })
    }

    fun fourthRequest(chainURL: String) {
        service.getEvos(chainURL).enqueue(object : Callback<PokemonEvoChain> {
            override fun onFailure(call: Call<PokemonEvoChain>, t: Throwable) {
                Log.d("ohNoo", "${t.message}")
            }

            override fun onResponse(call: Call<PokemonEvoChain>, response: Response<PokemonEvoChain>) {
                val body = response.body()
                Log.d("ling", "$body")
                body?.let { startRealm2(it) }

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
                copy(pokeList = pokeList.copy {
                    this.sortBy {
                        it.id
                    }
                    add(it)
                })
            }
        }
    }


    fun startRealm(allData: PokemonDatas) { //, newData: PokemonEvoChain  realm.copyToRealmOrUpdate(newData)
        realm.beginTransaction()
        realm.copyToRealmOrUpdate(allData)
        realm.commitTransaction()
    }

    fun startRealm2(bodyData: PokemonEvoChain) {
        realm.beginTransaction()
        realm.copyToRealmOrUpdate(bodyData)
        realm.commitTransaction()
    }


//    fun saveInReaml(data: List<RealmObject>) { Oder List<PokemonDatas>
//
//        realm.beginTransaction()
//        realm.copyToRealmOrUpdate(data)
//        realm.commitTransaction()
//    }


    fun realmCheck() {
        val db = realm.where(PokemonDatas::class.java).sort("id").findAll()
        val dbp = realm.copyFromRealm(db)

        if (db.isEmpty()) {
            firstRequest()
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
            state.pokeList.forEach {
                pokemon {
                    id(it.id)

                    //Name and ID
                    title(it.name)
                    index(it.id.toString())

                    //Binding Image
                    onBind { model, view, position -> (view.dataBinding as? ListItemPokemonBinding)?.let { drawee ->
                        drawee.iVShinyFront.setImageURI(it.imageUri)
                        }
                    }


                    //Types Checking and Color Setting: NEED To Refector

                    if (it.types.size == 2) {

                        val firstN = it.types.get(0)?.type?.name
                        val firstC = firstN?.let { it1 -> ColorsTyp.valueOf(it1) }

                        val secondN = it.types.get(1)?.type?.name
                        val secondC = secondN?.let { it1 -> ColorsTyp.valueOf(it1) }

                        typ1(secondN)
                        colorHexString(secondC?.color)

                        typ2(firstN)
                        colorHexString2(firstC?.color)


                    } else {
                        val firstN = it.types.get(0)?.type?.name
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
