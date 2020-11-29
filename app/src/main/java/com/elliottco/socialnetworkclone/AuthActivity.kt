package com.elliottco.socialnetworkclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.elliottco.socialnetworkclone.ui.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        // Check to see if a user is still logged in. If so, bring them directly to the MainActivity
        if(FirebaseAuth.getInstance().currentUser != null) {
            Intent(this, MainActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }
    }
}