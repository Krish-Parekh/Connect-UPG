package com.krish.connectupg.ui.postFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.krish.connectupg.R
import com.krish.connectupg.databinding.FragmentPostBinding
import com.krish.connectupg.model.Post
import com.krish.connectupg.utils.dismissDialogBox
import com.krish.connectupg.utils.getDialogBox

private const val TAG = "PostFragment"

class PostFragment : Fragment() {
    private lateinit var binding: FragmentPostBinding
    private val args by navArgs<PostFragmentArgs>()
    private lateinit var mDatabaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPostBinding.inflate(inflater, container, false)
        mDatabaseReference = FirebaseDatabase.getInstance().reference

        binding.btnPost.setOnClickListener {
            getDialogBox(requireContext())
            binding.apply {
                val postTitle = etTitle.text.toString()
                val postDesc = etContent.text.toString()

                if (validateTitle(postTitle) && validateDesc(postDesc)) {
                    val currentPost =
                        Post(postTitle, postDesc, System.currentTimeMillis(), args.currentUserInfo.uid)
                    savePostToDatabase(currentPost)
                    clearField()
                } else {
                    dismissDialogBox()
                }
            }
        }

        return binding.root
    }

    private fun clearField() {
        binding.etTitle.text.clear()
        binding.etContent.text.clear()
    }

    private fun savePostToDatabase(currentPost: Post) {
        val ref = mDatabaseReference.child("posts/${currentPost.postTime.toString()}")
        ref.setValue(currentPost)
            .addOnSuccessListener {
                dismissDialogBox()
                val action = PostFragmentDirections.actionPostFragmentToHomeFragment(args.currentUserInfo)
                findNavController().navigate(action)
            }
            .addOnFailureListener {
                dismissDialogBox()
            }
    }

    private fun validateDesc(postDesc: String): Boolean {
        return if (postDesc.isBlank()) {
            binding.etContent.error = "Field can't be set empty"
            false
        } else {
            true
        }
    }

    private fun validateTitle(postTitle: String): Boolean {
        return if (postTitle.isBlank()) {
            binding.etTitle.error = "Field can't be set empty"
            false
        } else {
            true
        }
    }

}