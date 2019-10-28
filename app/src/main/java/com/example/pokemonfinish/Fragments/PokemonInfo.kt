package com.example.pokemonfinish.Fragments


import android.animation.ObjectAnimator
import android.content.res.ColorStateList
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
import android.graphics.ColorFilter
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.animation.DecelerateInterpolator
import android.widget.ProgressBar
import androidx.databinding.BindingAdapter
import com.airbnb.mvrx.MvRx
import com.example.pokemonfinish.*
import com.example.pokemonfinish.Database.EvolutionChain
import com.example.pokemonfinish.Database.Species
import com.example.pokemonfinish.databinding.*
import com.facebook.drawee.view.SimpleDraweeView
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
        val species = realm.where(Species::class.java).equalTo("name",pokemon?.species?.name).findFirst()?.toUnmanaged()
        val evoChain = realm.where(EvolutionChain::class.java).equalTo("url" , species?.evoChain?.url).findFirst()?.toUnmanaged()

        Log.d(TAG,"${pokemon?.name}")
        Log.d(TAG,"${pokemon?.species?.name}")
        Log.d(TAG,"${pokemon?.species?.id}")

        Log.d(TAG,"${pokemon?.species?.evoChain?.chain?.species?.name}")
        Log.d(TAG,"${pokemon?.species?.evoChain?.id}")

        Log.d(TAG,"${pokemon?.species?.evoChain?.chain?.evolves?.get(0)?.species?.id}")
        Log.d(TAG,"${pokemon?.species?.evoChain?.chain?.species?.id}")

        Log.d(TAG,"${pokemon?.species?.evoChain?.chain?.species?.evolvesfrom?.name}")



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
                            pokeProgress(it.baseStat)

                            // colorHex(firstColor?.color)

                            onBind{model, view, position ->
                                it?.baseStat?.let { it1 ->

                                    (view.dataBinding as? ListItemStatesBinding)?.progressBar?.progressDrawable?.setColorFilter(Color.parseColor(firstColor?.color), android.graphics.PorterDuff.Mode.SRC_IN)

                                    //(view.dataBinding as? ListItemStatesBinding)?.progressBar?.setProgress(it1,true)


                                    //(view.dataBinding as? ListItemStatesBinding)?.progressBar?.startAnimation()

                                    //.indeterminateTintList = ColorStateList.valueOf(Color.MAGENTA)
                                    //.setProgress(it1,true)

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
                    var beforeName =  state.pokemon?.species?.evoChain?.chain?.species?.name
                    var afterName: String?

                    var picEvo1 = state.pokemon?.species?.evoChain?.chain?.species?.imageUri
                    var picEvo2 = chain?.species?.imageUri


                    var lvl: String?


                    while (chain != null) {

                        afterName = chain.species?.name
                        lvl = chain.evoDetails.getOrNull(0)?.minLvl.toString()
                        evochain {
                            id(4)
                            poke1(beforeName)
                            poke2(afterName)
                            image1(picEvo1)
                            image2(picEvo2)


                            lvlup(lvl)
                            Log.d("PICS","$picEvo1")
                            Log.d("PICS","$picEvo2")

                            //TODO Rewrite logic for images
//                        onBind { model, view, position ->
//                            (view.dataBinding as? ListItemEvochainBinding)?.iV1?.setImageURI(picEvo1)
//                            Log.d("PICS","$picEvo1")
//                            (view.dataBinding as? ListItemEvochainBinding)?.iV2?.setImageURI(picEvo2)
//                            Log.d("PICS","$picEvo2")
//                        }

                        }


                        //TODO itaration through evoloves
                        chain = chain.evolves.getOrNull(0)

                        beforeName = afterName
                        afterName = chain?.species?.name

                        picEvo1 = picEvo2
                        Log.d("PICS","$picEvo1")
                        picEvo2 =  chain?.species?.imageUri
                        Log.d("PICS","$picEvo2")

                    }

                }

                    }
            }
        }

    }

@BindingAdapter("backImage")
fun setBack(simpleView: SimpleDraweeView, imageUri: String?) {
    imageUri?.let {
        simpleView.setImageURI(it)
    }
}

@BindingAdapter("onAnimation")
fun setAnimation(progressBar: ProgressBar, pokeProgress: Int) {
    val animation = ObjectAnimator.ofInt(progressBar, "progress", 0, pokeProgress)
    animation.duration = 1000 // 1 second
    animation.interpolator = DecelerateInterpolator()
    animation.start()
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
