package com.innovativetools.assignment.view

import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.innovativetools.assignment.R
import com.innovativetools.assignment.databinding.ActivityMainBinding
import androidx.navigation.navOptions
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var bottom_nav: BottomNavigationView
    private lateinit var frag_title: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        toolbar = findViewById(R.id.toolbar)
        frag_title = findViewById(R.id.tv_frag_title)

        setSupportActionBar(toolbar)
        bottom_nav = binding.bottomNav
        bottom_nav.background = null
        bottom_nav.menu.getItem(2).isEnabled = false
        binding.bottomAppBar.setBackgroundColor(resources.getColor(R.color.white))

        val navController = findNavController(R.id.nav_host_fragment)

        val fab: FloatingActionButton = findViewById(R.id.floating_action)
        val tintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white))
        fab.imageTintList = tintList

        bottom_nav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            frag_title.text = destination.label
        }

        val noAnimNavOptions = navOptions {
            anim {
                enter = 0
                exit = 0
                popEnter = 0
                popExit = 0
            }
        }

        bottom_nav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    navController.navigate(R.id.navigation_home, null, noAnimNavOptions)
                    true
                }

                R.id.navigation_courses -> {
                    navController.navigate(R.id.navigation_courses, null, noAnimNavOptions)
                    true
                }

                R.id.navigation_campaign -> {
                    navController.navigate(R.id.navigation_campaign, null, noAnimNavOptions)
                    true
                }

                R.id.navigation_profile -> {
                    navController.navigate(R.id.navigation_profile, null, noAnimNavOptions)
                    true
                }

                else -> false
            }
        }
    }
}











