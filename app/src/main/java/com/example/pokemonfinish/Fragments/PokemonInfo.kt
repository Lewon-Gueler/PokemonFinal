package com.example.pokemonfinish.Fragments


import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.View
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.fragmentViewModel
import com.example.pokemonfinish.Database.PokemonDatas

import de.ffuf.android.architecture.mvrx.MvRxEpoxyController
import de.ffuf.android.architecture.mvrx.MvRxViewModel
import de.ffuf.android.architecture.mvrx.simpleController
import de.ffuf.android.architecture.realm.classes.toUnmanaged
import de.ffuf.android.architecture.ui.base.binding.fragments.EpoxyFragment
import io.realm.Realm
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.airbnb.mvrx.MvRx
import com.example.pokemonfinish.*
import com.example.pokemonfinish.Database.PokemonEvoChain
import com.example.pokemonfinish.databinding.*
import com.google.android.material.tabs.TabLayout
import android.view.animation.DecelerateInterpolator
import android.animation.ObjectAnimator
import android.icu.lang.UCharacter.GraphemeClusterBreak.T




/**
 * A simple [Fragment] subclass.
 */

data class InfoState(val pokemon: PokemonDatas? = null, val selectedTab:TabBarTyp? = TabBarTyp.STATS, val evoChain: PokemonEvoChain? = null): MvRxState


class InfoModel(initialState: InfoState): MvRxViewModel<InfoState>(initialState) {




    fun getRealmData(pokeId: Int) {
        val realm = Realm.getDefaultInstance()
        val evoChain = realm.where(PokemonEvoChain::class.java).equalTo("id",pokeId).findFirst()?.toUnmanaged()
        val pokemon = realm.where(PokemonDatas::class.java).equalTo("id", pokeId).findFirst()?.toUnmanaged()


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

    fun progressAnimaton(state: Int) {

        val progressBar: ProgressBar
        var progressStatus: Int = 0
        val handler = Handler()

//
//        val animation = ObjectAnimator.ofInt(progressBar, "progress", 100, 0)
//        animation.duration = 3500 // 3.5 second
//        animation.interpolator = DecelerateInterpolator()
//        animation.start()

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

        return simpleController(viewModel) { state -> state.pokemon
            val firstName = state.pokemon?.types?.get(0)?.type?.name
            val firstColor = firstName?.let { it1 -> ColorsTyp.valueOf(it1) }


                datas {
                    id(state.pokemon?.id)
                    title(state.pokemon?.name)
                    heigth(state.pokemon?.height.toString())
                    weigth(state.pokemon?.weight.toString())
                    backColor(Color.parseColor(firstColor?.color))

                    //First Type
                        typ1(firstName)
                        colorHexString(firstColor?.color)
                        colorHexString2("#121212")

                    if (state.pokemon?.types?.size == 2) {
                        val secondN = state.pokemon?.types?.get(1)?.type?.name
                        val secondC = secondN?.let { it1 -> ColorsTyp.valueOf(it1) }

                        //Second Type
                        typ2(secondN)
                        backColor(Color.parseColor(secondC?.color))
                        colorHexString2(secondC?.color)

                    }


                    onBind { model, view, position ->
                        (view.dataBinding as? ListItemDatasBinding)?.iVFullImage?.setImageURI(state.pokemon?.imageUri)
                    }
                }


            tabs {
                id(0)
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
                        id(1)
                        title(it.move?.name)
                    }
                }

                TabBarTyp.STATS ->
                    state.pokemon?.stats?.forEach {
                        states {
                            id(2)
                            baseState(it.baseStat.toString())
                            stateName(it.stat?.name)

                            val baseStat = it.baseStat
                            pokeProgress(baseStat)
                          // colorHex(firstColor?.color)

                        }
                    }

                TabBarTyp.EVOLUTIONS -> evolutions {
                    id(3)
                    onBind { model, view, position ->
                        (view.dataBinding as? ListItemEvolutionsBinding)?.iVShinyFront?.setImageURI(state.pokemon?.sprites?.front_shiny)
                        (view.dataBinding as? ListItemEvolutionsBinding)?.iVShinyBack?.setImageURI(state.pokemon?.sprites?.back_shiny)
                        (view.dataBinding as? ListItemEvolutionsBinding)?.iVShinyWFront?.setImageURI(state.pokemon?.sprites?.front_shiny_female)
                        (view.dataBinding as? ListItemEvolutionsBinding)?.iVShinyWBack?.setImageURI(state.pokemon?.sprites?.back_shiny_female)
                    }
                }

                TabBarTyp.EVOCHAIN ->
                    evochain {

                        val pokeEvo1 = state.evoChain?.chain?.species?.name
                        val pokeEvo2 = state.evoChain?.chain?.evoles?.get(0)?.species?.name
                        val pokeEvo3 = state.evoChain?.chain?.evoles?.get(0)?.evoTo?.get(0)?.species3?.name

                            id(4)
                        poke1(pokeEvo1)
                        poke2(pokeEvo2)
                        poke3(pokeEvo2)
                        poke4(pokeEvo3)

                        onBind { model, view, position ->
                            (view.dataBinding as? ListItemEvochainBinding)?.iV1?.setImageURI(state.evoChain?.imageUri) }


                        }

                    }
            }
        }

    }


//@BindingAdapter("ProColor")
//fun setProColor(progress: ProgressBar, colorHex: String?) {
//    val background = progress.background
//    colorHex?.let {
//        if (background is GradientDrawable) {
//            background.setColor(Color.parseColor(colorHex))
//        }
//    }
//}
