package com.example.pokemonfinish.Fragments


import android.animation.ObjectAnimator
import android.os.Bundle
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
import android.view.animation.DecelerateInterpolator
import android.widget.ProgressBar
import androidx.databinding.BindingAdapter
import com.airbnb.mvrx.MvRx
import com.example.pokemonfinish.*
import com.example.pokemonfinish.databinding.*
import com.facebook.drawee.view.SimpleDraweeView
import com.google.android.material.tabs.TabLayout
import android.graphics.PorterDuff
import android.graphics.drawable.LayerDrawable




/**
 * A simple [Fragment] subclass.
 */

data class InfoState(val pokemon: Pokemon? = null, val selectedTab:TabBarTyp? = TabBarTyp.STATS): MvRxState

/**
 * MvRx Model with functions
 */

class InfoModel(initialState: InfoState): MvRxViewModel<InfoState>(initialState) {

val TAG = "InfoModel"

    //Read Data from realm and set in state
    fun getandSetData(pokeId: Int) {
        val realm = Realm.getDefaultInstance()
        val pokemon = realm.where(Pokemon::class.java).equalTo("id", pokeId).findFirst()?.toUnmanaged()

        setState {
            copy(pokemon = pokemon)
        }

    }

    //checks which tab is selected
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

}


class PokemonInfo : EpoxyFragment<FragmentPokemonInfoBinding>() {

    override val layoutId: Int
        get() = R.layout.fragment_pokemon_info

    private val viewModel: InfoModel by fragmentViewModel()

    override val recyclerView: EpoxyRecyclerView
        get() = binding.recyclerView2

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //get id from MainFragment
        val PokeID = arguments?.getInt(MvRx.KEY_ARG)
        PokeID?.let { viewModel.getandSetData(it) }

    }

    /**
     * epoxyController creates ui-elements
     */
    override fun epoxyController(): MvRxEpoxyController {
    return simpleController(viewModel) { state -> state.pokemon

            //Set the primaray typ and color
            val firstName = state.pokemon?.types?.last()?.type?.name
            val firstColor = firstName?.let { it1 -> ColorsTyp.valueOf(it1) }

                //create data xml and fill datas
                datas {
                    // Set elements
                    id(state.pokemon?.id)
                    title(state.pokemon?.name)
                    heigth(state.pokemon?.height.toString())
                    weigth(state.pokemon?.weight.toString())
                    backColor(Color.parseColor(firstColor?.color))
                    image(state.pokemon?.imageUri)
                    typ1(firstName)
                    colorHexString(firstColor?.color)

                    //Second Type
                    if (state.pokemon?.types?.size == 2) {
                        val secondName = state.pokemon.types?.first()?.type?.name
                        val secondColor = secondName?.let { it1 -> ColorsTyp.valueOf(it1) }

                        typ2(secondName)
                        colorHexString2(secondColor?.color)
                    }

                }

        //creating the tab bar
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


            //List with Sortcuts for Stats
            val list = arrayListOf("SPEED","SP-DEF","SP-ATK","DEF","ATK","HP")

            //starts when expression depends on the selected Tab
            when(state.selectedTab) {

                TabBarTyp.STATS -> state.pokemon?.stats?.forEachIndexed { index, it ->

                        //creating stats
                        states {
                            id(2)
                            baseState(it.baseStat.toString())
                            stateName(list[index])
                            pokeProgress(it.baseStat)

                            //Binding Color of the Drawable
                            onBind{model, view, position ->
                                it?.baseStat?.let { it1 ->
                                    val progressBarDrawable =  (view.dataBinding as? ListItemStatesBinding)?.progressBar?.getProgressDrawable() as LayerDrawable
                                    val progressDrawable = progressBarDrawable.getDrawable(1)
                                    progressDrawable.setColorFilter(Color.parseColor(firstColor?.color), PorterDuff.Mode.SRC_IN)

                                }

                            }

                        }
                    }

                //creating Shiny Image
                TabBarTyp.EVOLUTIONS -> evolutions {
                    id(3)
                    imageShinyF(state.pokemon?.sprites?.front_shiny)
                    imageShinyB(state.pokemon?.sprites?.back_shiny)
                    imageShinyWF(state.pokemon?.sprites?.front_shiny_female)
                    imageShinyWB(state.pokemon?.sprites?.back_shiny_female)
                }

                //Creating moves for each element
                TabBarTyp.MOVES -> state.pokemon?.moves?.forEach {
                    moves {
                        id(1)
                        title(it.move?.name)
                    }
                }

                //evolution chain
                TabBarTyp.EVOCHAIN -> {
                    var chain = state.pokemon?.species?.evoChain?.chain?.evolves?.getOrNull(0)
                    var beforeName = state.pokemon?.species?.evoChain?.chain?.species?.name
                    var afterName: String?
                    var lvl: String?
                    var picEvo1 = state.pokemon?.species?.evoChain?.chain?.species?.imageUri
                    var picEvo2 = chain?.species?.imageUri

                    while (chain != null) {

                        afterName = chain.species?.name

                        lvl = chain.evoDetails.getOrNull(0)?.minLvl.toString()

                        evochain {
                            id(4)
                            poke1(beforeName)
                            poke2(afterName)
                            lvlup(lvl)
                            image1(picEvo1)
                            image2(picEvo2)
                        }

                        chain = chain.evolves.getOrNull(0)
                        beforeName = afterName
                        afterName = chain?.species?.name
                        picEvo1 = picEvo2
                        picEvo2 = chain?.species?.imageUri


                    }

                }

            }
    }
    }

}

//Binding Adapter func to set Background Image for the DraweeViews
@BindingAdapter("backImage")
fun setBack(simpleView: SimpleDraweeView, imageUri: String?) {
    imageUri?.let {
        simpleView.setImageURI(it)
    }
}

//Binding Adapter func to start animation with progressbar
@BindingAdapter("onAnimation")
fun setAnimation(progressBar: ProgressBar, pokeProgress: Int) {
    val animation = ObjectAnimator.ofInt(progressBar, "progress", 0, pokeProgress)
    animation.duration = 1000 // 1 second
    animation.interpolator = DecelerateInterpolator()
    animation.start()

}



