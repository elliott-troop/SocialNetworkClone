package com.elliottco.socialnetworkclone.ui.auth.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.elliottco.socialnetworkclone.R
import com.elliottco.socialnetworkclone.misc.EventObserver
import com.elliottco.socialnetworkclone.ui.auth.AuthViewModel
import com.elliottco.socialnetworkclone.ui.main.MainActivity
import com.elliottco.socialnetworkclone.ui.snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_login.*
import timber.log.Timber

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private val viewModel: AuthViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()

        btnLogin.setOnClickListener {
            Timber.i("Login button pressed")
            viewModel.login(
                etEmail.text.toString(),
                etPassword.text.toString()
            )
        }

        tvRegisterNewAccount.setOnClickListener {
            if (findNavController().previousBackStackEntry != null) {
                findNavController().popBackStack()
            } else findNavController().navigate(
                R.id.action_loginFragment_to_registerFragment
            )
        }
    }

    private fun subscribeToObservers() {
        viewModel.loginStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                loginProgressBar.isVisible = false
                snackbar(it)
            },
            onLoading = { loginProgressBar.isVisible = true }
        ){
            loginProgressBar.isVisible = false

            Intent(requireContext(), MainActivity::class.java).also {
                startActivity(it)

                // Pop AuthActivity to prevent the user from navigating back
                requireActivity().finish()
            }
        })
    }
}