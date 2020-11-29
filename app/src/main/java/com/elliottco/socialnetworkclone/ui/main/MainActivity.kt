package com.elliottco.socialnetworkclone.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.elliottco.socialnetworkclone.AuthActivity
import com.elliottco.socialnetworkclone.R
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment)
            as NavHostFragment

        bottomNavigationView.apply {
            // Make background transparent
            background = null

            // Disable placeholder item
            menu.getItem(2).isEnabled = false

            // Link bottomNavigationView with navigation component
            setupWithNavController(navHostFragment.findNavController())

            // Prevents reloading the fragment
            setOnNavigationItemReselectedListener { Unit }
        }

        fabNewPost.setOnClickListener {
            navHostFragment.findNavController().navigate(R.id.globalActionToCreatePostFragment)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.miLogout -> {
                FirebaseAuth.getInstance().signOut()
                Intent(this, AuthActivity::class.java).also {
                    startActivity(it)
                }
                finish()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}