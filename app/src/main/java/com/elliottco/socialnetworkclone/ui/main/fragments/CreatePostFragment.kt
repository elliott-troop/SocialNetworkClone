package com.elliottco.socialnetworkclone.ui.main.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.elliottco.socialnetworkclone.R
import com.elliottco.socialnetworkclone.misc.EventObserver
import com.elliottco.socialnetworkclone.ui.main.viewmodels.CreatePostViewModel
import com.elliottco.socialnetworkclone.ui.snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_create_post.*

@AndroidEntryPoint
class CreatePostFragment : Fragment() {

    private val viewModel: CreatePostViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()
    }

    private fun subscribeToObservers() {
        viewModel.createPostStatus.observe(viewLifecycleOwner, EventObserver(
                onError = {
                    createPostProgressBar.isVisible = false
                    snackbar(it)
                },
                onLoading = {
                    createPostProgressBar.isVisible = true
                }
        ){
            createPostProgressBar.isVisible = false
            findNavController().popBackStack()
        })
    }
}