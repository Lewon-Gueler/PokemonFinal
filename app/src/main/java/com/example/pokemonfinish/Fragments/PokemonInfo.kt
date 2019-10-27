package com.example.pokemonfinish.Fragments


import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.View
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.fragmentViewModel
import com.example.pokemonfinish.Database.Pokemon

import de.ffuf.android.architecture.mvrx.MvRxEpoxyController
import de.ffuf.android.architecture.mvrx.MvRxViewModel
import de.ffuf.android.architecture.mvrx.simpleController
import de.ffuf.android.architecture.realm.classes.toUnmanaged
import de.ffuf.android.architecture.ui.base.binding.fragments.EpoxyFragment
import io.realm.Realm
import android.graphics.Color
import android.util.Log
import android.widget.ProgressBar
import com.airbnb.mvrx.MvRx
import com.example.pokemonfinish.*
import com.example.pokemonfinish.Database.EvolutionChain
import com.example.pokemonfinish.Database.Species
import com.example.pokemonfinish.databinding.*
import com.google.android.material.tabs.TabLayout


/**
 * A simple [Fragment] subclass.
 */

data class InfoState(val pokemon: Pokemon? = null, val selectedTab:TabBarTyp? = TabBarTyp.STATS, val evoChain: EvolutionChain? = null): MvRxState


class InfoModel(initialState: InfoState): MvRxViewModel<InfoState>(initialState) {

val TAG = "InfoModel"


    fun getRealmData(pokeId: Int) {
        val realm = Realm.getDefaultInstance()




        val pokemon = realm.where(Pokemon::class.java).equalTo("id", pokeId).findFirst()?.toUnmanaged()
        val species = realm.where(Species::class.java).equalTo("name",pokemon?.species?.name).sort("id").findFirst()?.toUnmanaged()

        val evoChain = realm.where(EvolutionChain::class.java).equalTo("url" , species?.evoChain?.url).findFirst()?.toUnmanaged()
        Log.d(TAG,"${pokemon?.name}")
        Log.d(TAG,"${pokemon?.species?.name}")
        Log.d(TAG,"${pokemon?.species?.id}")

        Log.d(TAG,"${pokemon?.species?.evoChain?.chain?.species?.name}")
        Log.d(TAG,"${pokemon?.species?.evoChain?.id}")

        Log.d(TAG,"${ pokemon?.species?.evoChain?.chain?.evolves?.get(0)?.species?.id}")
        Log.d(TAG,"${ pokemon?.species?.evoChain?.chain?.species?.id}")

//        Log.d(TAG,"${species?.name}")
//        Log.d(TAG,"${species?.id}")
//
//        Log.d(TAG,"${species?.evoChain?.url}")
//        Log.d(TAG,"${evoChain?.id}")

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


    fun chainImages(id1:String) :String? {

        val realm = Realm.getDefaultInstance()
        val image1 = realm.where(Pokemon::class.java).equalTo("id", id1).findFirst()?.toUnmanaged()

        val pic1 = image1?.imageUri
        return pic1

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
                             val index = p0?.position
                                viewModel.onTab(index)
                            }
                        })
                    }

                }
            }

            val list = arrayListOf("SPEED","SP-DEF","SP-ATK","DEF","ATK","HP")

            when(state.selectedTab) {
                TabBarTyp.MOVES -> state.pokemon?.moves?.forEach {
                    moves {
                        id(1)
                        title(it.move?.name)
                    }
                }

                TabBarTyp.STATS ->

                    state.pokemon?.stats?.forEachIndexed { index, it ->
                        states {
                            id(2)
                            baseState(it.baseStat.toString())
                            stateName(list[index])

                            val baseStat = it.baseStat
                            // pokeProgress(baseStat)
                            // colorHex(firstColor?.color)

                            onBind{model, view, position ->
                                it?.baseStat?.let { it1 ->
                                    (view.dataBinding as? ListItemStatesBinding)?.progressBar?.setProgress(it1,true)
//                                        progressDrawable?.colorFilter(Color.parseColor("#2a4c6b"))
                                }

                            }

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


                TabBarTyp.EVOCHAIN -> {
                    var chain = state.pokemon?.species?.evoChain?.chain?.evolves?.getOrNull(0)
                    var beforeName =  chain?.species?.name
                    var afterName: String?
                    var lvl: String?

                    while (chain != null) {
                        afterName = chain.species?.name
                        lvl = chain.evoDetails.getOrNull(0)?.minLvl.toString()
                        evochain {
                            id(4)
                            poke1(beforeName)
                            poke2(afterName)
                            lvlup(lvl)

                        onBind { model, view, position ->
                            (view.dataBinding as? ListItemEvochainBinding)?.iV1?.setImageURI(state.evoChain?.imageUri)
                            (view.dataBinding as? ListItemEvochainBinding)?.iV2?.setImageURI(state.evoChain?.imageUri)
                        }


                        }

                        //TODO itaration through evoloves
                        chain = chain.evolves.getOrNull(0)
                        beforeName = afterName
                        afterName = chain?.species?.name
                    }

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
