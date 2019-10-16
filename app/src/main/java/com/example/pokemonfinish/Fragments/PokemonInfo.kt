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
import androidx.navigation.findNavController
import com.airbnb.mvrx.MvRx
import com.example.pokemonfinish.*
import com.example.pokemonfinish.databinding.*
import kotlinx.android.synthetic.main.list_item_datas.*


/**
 * A simple [Fragment] subclass.
 */

data class InfoState(val pokemon: PokemonDatas? = null): MvRxState


class InfoModel(initialState: InfoState): MvRxViewModel<InfoState>(initialState) {

    fun getRealmData(pokeId: Int) {
        val realm = Realm.getDefaultInstance()
        val pokemon = realm.where(PokemonDatas::class.java).equalTo("id", pokeId).findFirst()?.toUnmanaged()

        setState {
            copy(pokemon = pokemon)
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

       val PokeID = arguments?.getInt(MvRx.KEY_ARG)
        PokeID?.let { viewModel.getRealmData(it) }

    }



    override fun epoxyController(): MvRxEpoxyController {

        return simpleController(viewModel) { state ->
                datas {
                    id(state.pokemon?.id)
                    title(state.pokemon?.name)
                    heigth(state.pokemon?.height.toString())
                    weigth(state.pokemon?.weight.toString())

                    onBind { model, view, position ->
                        (view.dataBinding as? ListItemDatasBinding)?.iVFullImage?.setImageURI(state.pokemon?.imageUri)
                    }
                }


            tabs {
                id()
//                onBind{model, view, position ->
//                    (view.dataBinding as? ListItemTabsBinding)?.TabLayout?.onFocusChangeListener
//                }
            }



            states {
                id()
            }

            state.pokemon?.moves?.forEach {
                moves {
                    id()
                    title(it.move?.name)
                }
            }




        }
    }
}
