package com.example.pokemonfinish.Fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DividerItemDecoration
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.fragmentViewModel
import com.example.pokemonfinish.Database.PokemonDatas
import com.example.pokemonfinish.R
import com.example.pokemonfinish.databinding.BottomsheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import de.ffuf.android.architecture.mvrx.MvRxEpoxyController
import de.ffuf.android.architecture.mvrx.MvRxViewModel
import de.ffuf.android.architecture.mvrx.simpleController
import de.ffuf.android.architecture.ui.base.binding.fragments.EpoxyFragment
import de.ffuf.android.architecture.ui.base.binding.fragments.MvrxBaseBottomSheetDialog
import io.realm.Realm

/**
 * A simple [Fragment] subclass.
 */


class PokemonDetail(lifecycleOwner: LifecycleOwner) : MvrxBaseBottomSheetDialog<BottomsheetBinding>(lifecycleOwner) {
    override val layout: Int
        get() = R.layout.bottomsheet

    override fun invalidate() {
    }

    override fun onViewCreated(binding: BottomsheetBinding) {
        binding.textH.text = "Test"
    }
}






