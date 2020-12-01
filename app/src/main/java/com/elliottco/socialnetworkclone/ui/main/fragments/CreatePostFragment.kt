package com.elliottco.socialnetworkclone.ui.main.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.elliottco.socialnetworkclone.R
import com.elliottco.socialnetworkclone.misc.EventObserver
import com.elliottco.socialnetworkclone.ui.main.viewmodels.CreatePostViewModel
import com.elliottco.socialnetworkclone.ui.snackbar
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_create_post.*
import javax.inject.Inject

@AndroidEntryPoint
class CreatePostFragment : Fragment() {

    @Inject
    lateinit var glide: RequestManager

    private val viewModel: CreatePostViewModel by viewModels()

    private lateinit var cropContent: ActivityResultLauncher<String>

    private val cropActivityResultContract = object : ActivityResultContract<String, Uri>() {
        override fun createIntent(context: Context, input: String?): Intent {
            return CropImage.activity()
                    .setAspectRatio(16, 9)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .getIntent(requireContext())
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri {
            // Check if the CropImage activity returns a URI. If not, return Uri.Empty
            return if(resultCode == -1)
             CropImage.getActivityResult(intent).uri
            else
                Uri.EMPTY
        }
    }

    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Register callback for activity result
        cropContent = registerForActivityResult(cropActivityResultContract) {
            if(it != Uri.EMPTY)
                viewModel.setCurretImageUri(it)
        }
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

        btnSetPostImage.setOnClickListener {
            cropContent.launch("image/*")
        }

        ivPostImage.setOnClickListener {
            cropContent.launch("image/*")
        }

        btnPost.setOnClickListener {
            currentImageUri?.let { uri ->
                viewModel.createPost(uri, etPostDescription.text.toString())
            } ?: snackbar(getString(R.string.error_no_image_chosen))
        }
    }

    private fun subscribeToObservers() {
        viewModel.currentImageUri.observe(viewLifecycleOwner) { uri ->
            currentImageUri = uri

            if(uri != Uri.EMPTY) {
                btnSetPostImage.isVisible = false
                glide.load(currentImageUri).into(ivPostImage)
            }

        }

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