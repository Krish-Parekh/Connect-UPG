package com.krish.connectupg.ui.userDetailFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.google.firebase.auth.FirebaseAuth
import com.krish.connectupg.R
import com.krish.connectupg.databinding.FragmentUserDetailBinding


class UserDetailFragment : Fragment() {

    private lateinit var binding: FragmentUserDetailBinding
    private val args by navArgs<UserDetailFragmentArgs>()
    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUserDetailBinding.inflate(inflater, container, false)

        binding.apply {
            profilePic.load(args.currentUser?.imageURL)
            username.text = args.currentUser?.username
            email.text = args.currentUser?.email
        }

        binding.back.setOnClickListener {
            val action =
                UserDetailFragmentDirections.actionUserDetailFragmentToHomeFragment(args.currentUser)
            findNavController().navigate(action)
        }

        binding.btnLogout.setOnClickListener {
            mAuth.signOut()
            findNavController().navigate(R.id.action_userDetailFragment_to_loginFragment)
        }

        return binding.root
    }

}