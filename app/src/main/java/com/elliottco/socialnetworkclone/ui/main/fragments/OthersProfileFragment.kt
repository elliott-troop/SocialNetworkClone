package com.elliottco.socialnetworkclone.ui.main.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.elliottco.socialnetworkclone.R
import com.elliottco.socialnetworkclone.data.entities.User
import com.elliottco.socialnetworkclone.misc.EventObserver
import kotlinx.android.synthetic.main.fragment_profile.*

class OthersProfileFragment : ProfileFragment() {

    private val args: OthersProfileFragmentArgs by navArgs()

    override val uid: String
        get() = args.uid

    private var currentUser: User? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()

        btnToggleFollow.setOnClickListener {
            viewModel.toggleFollowForUser(uid)
        }
    }

    private fun subscribeToObservers() {
        viewModel.profileMeta.observe(viewLifecycleOwner, EventObserver {
            btnToggleFollow.isVisible = true
            setUpToggleFollowButton(it)
            currentUser = it
        })

        viewModel.followStatus.observe(viewLifecycleOwner, EventObserver {
            currentUser?.isFollowing = it
            setUpToggleFollowButton(currentUser ?: return@EventObserver)
        })

    }

    private fun setUpToggleFollowButton(user: User) {
        btnToggleFollow.apply {
            val changeBounds = ChangeBounds().apply {
                duration = 300
                interpolator = OvershootInterpolator()
            }

            val set1 = ConstraintSet()
            val set2 = ConstraintSet()
            set1.clone(requireContext(), R.layout.fragment_profile)
            set2.clone(requireContext(), R.layout.fragment_profile_anim)
            TransitionManager.beginDelayedTransition(clProfile, changeBounds)
            if(user.isFollowing) {
                text = requireContext().getString(R.string.unfollow)
                setBackgroundColor(Color.RED)
                setTextColor(Color.WHITE)
                set1.applyTo(clProfile)
            } else {
                text = requireContext().getString(R.string.follow)
                setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorAccent))
                setTextColor(ContextCompat.getColor(requireContext(), R.color.darkBackground))
                set2.applyTo(clProfile)
            }
        }
    }

}