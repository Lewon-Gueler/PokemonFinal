package com.example.pokemonfinish

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.pokemonfinish.databinding.ActivityMainBinding
import com.facebook.drawee.backends.pipeline.Fresco
import de.ffuf.android.architecture.ui.base.binding.activities.BindingMvrxActivity
import io.realm.Realm

/**
 * BindingMvrxActivity
 */
class MainActivity : BindingMvrxActivity<ActivityMainBinding>() {
    override fun invalidate() {

    }

    private lateinit var appBarConfiguration: AppBarConfiguration

    private val navController by lazy {
        Navigation.findNavController(findViewById<View>(R.id.myNavHostFragment))
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Initialite Realm
        Realm.init(this)

        //Fresco Libary Initialize
        Fresco.initialize(this)

        appBarConfiguration = AppBarConfiguration.Builder(navController.graph).build()

        NavigationUI.setupActionBarWithNavController(this, navController)
    }
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
