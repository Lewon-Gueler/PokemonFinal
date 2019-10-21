package com.example.pokemonfinish.Fragments



import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.BindingAdapter
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.mvrx.MvRx
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.fragmentViewModel
import com.example.pokemonfinish.ColorsTyp
import com.example.pokemonfinish.Database.*
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
import kotlinx.coroutines.handleCoroutineException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


/**
 * A simple [Fragment] subclass.
 */
//Data Class Later Database

data class PokemonState(val pokeList: List<PokemonDatas> = emptyList()): MvRxState


class PokemonModel(initialState: PokemonState): MvRxViewModel<PokemonState>(initialState) {

    val realm = Realm.getDefaultInstance()
    val pokemonNetwork:DownloadPokemon = DownloadPokemon()

    fun getNetworkStuff() {

        val service = pokemonNetwork.service

        //First Server Request
        service.getAllPokemonDatas(50,0).enqueue(object : Callback<PokemonList> {
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

                                setOfState(allData)
                                allData?.let { it1 -> startRealm(it1) }


                                //third Server Request:
                                newId?.let { it1 -> service.getChain(it1).enqueue(object: Callback<PokemonSpecies> {
                                    override fun onFailure(call: Call<PokemonSpecies>, t: Throwable) {

                                    }

                                    override fun onResponse(call: Call<PokemonSpecies>, response: Response<PokemonSpecies>) {
                                        response.body()?.evoChainURL?.forEach {chainlist ->
                                            val chainURL = chainlist.urlChain

                                            //fourth Server Request
                                            chainURL?.let { it2 -> service.getEvos(it2).enqueue(object: Callback<PokemonEvoChain>{
                                                override fun onFailure(call: Call<PokemonEvoChain>, t: Throwable) {

                                                }

                                                override fun onResponse(call: Call<PokemonEvoChain>, response: Response<PokemonEvoChain>) {
                                                  val body = response.body()

                                                }

                                            }) }




                                        }
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



//   suspend fun firstServerRequest(): String? = suspendCoroutine { cont ->
//
//           val service = pokemonNetwork.service
//
//           service.getAllPokemonDatas(50, 0).enqueue(object : Callback<PokemonList> {
//               override fun onFailure(call: Call<PokemonList>, t: Throwable) {
//
//               }
//
//               override fun onResponse(call: Call<PokemonList>, response: Response<PokemonList>) {
//                   response.body()?.results?.forEach { pokemon ->
//
//                       val newURL = pokemon.url
//                       cont.resume(newURL)
//
//
//                   }
//
//               }
//           })
//       }


//
//    fun secondServerRequest(newURL:String):Int {
//
//        val service = pokemonNetwork.service
//
//        //Second Server Request
//        newURL?.let {
//            service.getPokeURL(newURL).enqueue(object: Callback<PokemonDatas> {
//                override fun onFailure(call: Call<PokemonDatas>, t: Throwable) {
//                }
//
//                override fun onResponse(
//                    call: Call<PokemonDatas>,
//                    response: Response<PokemonDatas>) {
//
//                    val allData = response.body()
//                    val newId = allData?.id
//
//                    setOfState(allData)
//                    allData?.let { it1 -> startRealm(it1) }
//                }
//            })
//        }
//        return newId
//    }
//

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


    fun startRealm(allData: PokemonDatas) { //, newData: PokemonEvoChain  realm.copyToRealmOrUpdate(newData)
        realm.beginTransaction()
        realm.copyToRealmOrUpdate(allData)
        //Log.d("current Pokemon", "${allData?.id} ${allData?.types?.get(0)?.type?.name}")
        //val db = realm.where(PokemonDatas::class.java).sort("id").findAll()
        realm.commitTransaction()
    }


    fun realmCheck() {
        val db = realm.where(PokemonDatas::class.java).sort("id").findAll()
        val dbp = realm.copyFromRealm(db)

        if (db.isEmpty()) {
          getNetworkStuff()
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
                  onBind { model, view, position ->
                      (view.dataBinding as? ListItemPokemonBinding)?.let { drawee ->
                          drawee.iVShinyFront.setImageURI(it.imageUri)

                      }
                  }


                  //Types Checking and Color Setting: NEED To Refector
//                  val listSize = it.types.size
//                  val firstName = it.types.get(0)?.type?.name
//                  val secondName = it.types.get(1)?.type?.name

                  if (it.types.size == 2) {
                      val firstN = it.types.get(0)?.type?.name
                      typ1(firstN)

                      val firstC = firstN?.let { it1 -> ColorsTyp.valueOf(it1) }
                      colorHexString2(firstC?.color)

                      val secondN = it.types.get(1)?.type?.name
                      typ2(secondN)

                      val secondC = secondN?.let { it1 -> ColorsTyp.valueOf(it1) }
                      colorHexString(secondC?.color)


                  } else {
                      val oneTyp = it.types.get(0)?.type?.name

                      if (oneTyp != null) {
                          typ1(oneTyp)

                          val oneTypC = oneTyp.let {ColorsTyp.valueOf(it) }
                          colorHexString2(oneTypC.color)

                      } else {
                          // Setting visisbilty false
                          typ2("")
                      }

                  }


                  //On CLick Listener which load a new fragment
                  onClick { view : View ->
                      view.findNavController().navigate(
                          R.id.action_pokemonMain_to_pokemonInfo,
                          Bundle().apply { it.id?.let { it1 -> putInt(MvRx.KEY_ARG, it1) }  }
                      )
                  }

              }
          }
        }
    }
}



@BindingAdapter("tagColor")
fun setTagColor(txtView:TextView, colorHexString: String?) {
    val background = txtView.background
    colorHexString?.let {


        if (background is GradientDrawable) {
            background.setColor(Color.parseColor(colorHexString))
        }
    }
}
