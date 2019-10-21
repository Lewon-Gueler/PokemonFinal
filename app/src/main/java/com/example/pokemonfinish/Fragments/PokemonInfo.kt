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
import android.graphics.Color
import android.util.Log
import android.widget.ProgressBar
import androidx.navigation.findNavController
import com.airbnb.mvrx.MvRx
import com.example.pokemonfinish.*
import com.example.pokemonfinish.Database.PokemonEvoChain
import com.example.pokemonfinish.databinding.*
import kotlinx.android.synthetic.main.list_item_datas.*
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.list_item_tabs.*


/**
 * A simple [Fragment] subclass.
 */

data class InfoState(val pokemon: PokemonDatas? = null, val selectedTab:TabBarTyp? = TabBarTyp.STATS, val evoChain: PokemonEvoChain? = null): MvRxState


class InfoModel(initialState: InfoState): MvRxViewModel<InfoState>(initialState) {

    fun getRealmData(pokeId: Int) {
        val realm = Realm.getDefaultInstance()
        val pokemon = realm.where(PokemonDatas::class.java).equalTo("id", pokeId).findFirst()?.toUnmanaged()
        val evoChain = realm.where(PokemonEvoChain::class.java).equalTo("id",pokeId).findFirst()?.toUnmanaged()

        setState {
            copy(pokemon = pokemon)
        }

        setState {
            copy(evoChain = evoChain)
        }

    }

    fun onTab(index:Int?){
        setState {
            when(index) {
                0 ->  copy(selectedTab = TabBarTyp.STATS)
                1 ->  copy(selectedTab = TabBarTyp.EVOLUTIONS)
                2 ->  copy(selectedTab = TabBarTyp.MOVES)
                3 ->  copy(selectedTab = TabBarTyp.EVOCHAIN)

                else -> copy(selectedTab = TabBarTyp.STATS)
            }
        }
    }

    fun progressAnimaton(ani:StatesBindingModelBuilder) {

        val progressBar: ProgressBar
        var progressStatus: Int = 0
        val handler = Handler()


        Thread(Runnable {
            while (progressStatus < 100)
                progressStatus += 1

            try {
                Thread.sleep(200)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            handler.post(Runnable{


        })

        }).start()

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

        //viewModel.progressAnimaton()

       val PokeID = arguments?.getInt(MvRx.KEY_ARG)
        PokeID?.let { viewModel.getRealmData(it) }

    }



    override fun epoxyController(): MvRxEpoxyController {

        return simpleController(viewModel) { state ->
            state.pokemon
                datas {
                    id(state.pokemon?.id)
                    title(state.pokemon?.name)
                    heigth(state.pokemon?.height.toString())
                    weigth(state.pokemon?.weight.toString())

                    //Checking the types NEED TO REFECTOR!!
                    if (state.pokemon?.types?.size == 2) {
                        val firstN = state.pokemon.types.get(0)?.type?.name
                        typ1(firstN)

                        val firstC = firstN?.let { it1 -> ColorsTyp.valueOf(it1) }
                       // onColorTyp(Color.parseColor(firstC?.color))

                        val secondN = state.pokemon.types.get(1)?.type?.name
                        typ2(secondN)

                        val secondC = secondN?.let { it1 -> ColorsTyp.valueOf(it1) }
                        //onColorTypTwo(Color.parseColor(secondC?.color))

                    } else {
                        val oneTyp = state.pokemon?.types?.get(0)?.type?.name
                        typ1(oneTyp)

                        val oneTypC = oneTyp?.let {ColorsTyp.valueOf(it) }
                        //onColorTyp(Color.parseColor(oneTypC?.color))

                        // Setting visisbilty false
                        typ2("")
                    }



                    onBind { model, view, position ->
                        (view.dataBinding as? ListItemDatasBinding)?.iVFullImage?.setImageURI(state.pokemon?.imageUri)
                    }
                }


            tabs {
                id()
                onBind{model, view, position ->
                    (view.dataBinding as? ListItemTabsBinding)?.let {

                        it.TabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                            override fun onTabReselected(p0: TabLayout.Tab?) {

                            }

                            override fun onTabUnselected(p0: TabLayout.Tab?) {

                            }

                            override fun onTabSelected(p0: TabLayout.Tab?) {
                             val position = p0?.position
                                viewModel.onTab(position)
                            }
                        })
                    }

                }
            }


            when(state.selectedTab) {
                TabBarTyp.MOVES -> state.pokemon?.moves?.forEach {
                    moves {
                        id()
                        title(it.move?.name)
                    }
                }

                TabBarTyp.STATS ->
                    state.pokemon?.stats?.forEach {
                        states {
                            id()
                            baseState(it.baseStat.toString())
                            stateName(it.stat?.name)
                            pokeProgress(it.baseStat)

                        }
                    }


                TabBarTyp.EVOLUTIONS -> evolutions {
                    id()
                    onBind { model, view, position ->
                        (view.dataBinding as? ListItemEvolutionsBinding)?.iVShinyFront?.setImageURI(state.pokemon?.sprites?.front_shiny)
                        (view.dataBinding as? ListItemEvolutionsBinding)?.iVShinyBack?.setImageURI(state.pokemon?.sprites?.back_shiny)
                        (view.dataBinding as? ListItemEvolutionsBinding)?.iVShinyWFront?.setImageURI(state.pokemon?.sprites?.front_shiny_female)
                        (view.dataBinding as? ListItemEvolutionsBinding)?.iVShinyWBack?.setImageURI(state.pokemon?.sprites?.back_shiny_female)
                    }
                }

                TabBarTyp.EVOCHAIN ->
                    evochain {
                            id()

                        }
                    }
            }
        }

    }


