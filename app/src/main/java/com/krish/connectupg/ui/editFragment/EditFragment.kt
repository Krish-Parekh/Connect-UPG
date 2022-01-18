package com.krish.connectupg.ui.editFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.krish.connectupg.databinding.FragmentEditBinding
import com.krish.connectupg.model.Post

private const val TAG = "EditFragment"

class EditFragment : Fragment() {
    private lateinit var binding: FragmentEditBinding
    private val args by navArgs<EditFragmentArgs>()
    private val mDatabaseReference: DatabaseReference by lazy { FirebaseDatabase.getInstance().reference }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditBinding.inflate(inflater, container, false)

        binding.etTitle.setText(args.currentPost?.title)
        binding.etContent.setText(args.currentPost?.content)

        binding.btnSubmitEdit.setOnClickListener {
            val postTitle = binding.etTitle.text.toString()
            val postDesc = binding.etContent.text.toString()
            if (validateTitle(postTitle) && validateDesc(postDesc)) {
                args.currentPost?.title = postTitle
                args.currentPost?.content = postDesc
                updateValueInDatabase(args.currentPost!!)
            }
        }


        return binding.root
    }

    private fun updateValueInDatabase(currentPost: Post) {
        mDatabaseReference.child("posts").child("${currentPost.postTime}").setValue(currentPost)
            .addOnSuccessListener {
                Log.d(TAG, "updateValueInDatabase: ")
                val action = EditFragmentDirections.actionEditFragmentToHomeFragment(args.currentUser)
                findNavController().navigate(action)
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