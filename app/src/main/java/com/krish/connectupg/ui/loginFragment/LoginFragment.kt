package com.krish.connectupg.ui.loginFragment

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.database.*
import com.krish.connectupg.R
import com.krish.connectupg.databinding.FragmentLoginBinding
import com.krish.connectupg.model.User
import com.krish.connectupg.utils.dismissDialogBox
import com.krish.connectupg.utils.getDialogBox

private const val TAG = "LoginFragment"

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference

        if (mAuth.currentUser != null) {
            getDialogBox(requireContext())
            try{
                getUserFromDatabase(mAuth.currentUser?.uid!!)
            }catch (e: Exception){
                Log.d(TAG, "onCreateView: ${e.message}")
            }
        }

        binding.btnLogin.setOnClickListener {
            getDialogBox(requireContext())
            val email = binding.etEmail.text.toString().trim { it <= ' ' }
            val password = binding.etPassword.text.toString().trim { it <= ' ' }
            if (validateEmail(email) && validatePassword(password)) {
                signInUser(email, password)
                clearField()
            } else {
                dismissDialogBox()
            }
        }

        binding.tvSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }



        return binding.root
    }

    private fun clearField() {
        binding.etEmail.text.clear()
        binding.etPassword.text.clear()
    }

    private fun signInUser(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val currentUserUid = mAuth.currentUser?.uid!!
                getUserFromDatabase(currentUserUid)
            }
            .addOnFailureListener { exception ->
                dismissDialogBox()
                try {
                    throw exception
                } catch (e: FirebaseAuthInvalidUserException) {
                    Log.d(TAG, "Invalid User")
                } catch (e: FirebaseAuthInvalidCredentialsException) {
                    Log.d(TAG, "Credentials are invalid")
                } catch (e: Exception) {
                    Log.d(TAG, "Error : $e")
                }
            }
    }

    private fun getUserFromDatabase(currentUserUid: String) {
        mDatabase.child("users").child(currentUserUid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val currentUser = snapshot.getValue(User::class.java)
                    val action =
                        LoginFragmentDirections.actionLoginFragmentToHomeFragment(currentUser)
                    findNavController().navigate(action)
                    dismissDialogBox()
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

    private fun validatePassword(password: String): Boolean {
        return if (password.isBlank()) {
            binding.etEmail.error = "Field can't be set empty"
            false
        } else {
            true
        }
    }

    private fun validateEmail(email: String): Boolean {
        return if (email.isBlank()) {
            binding.etEmail.error = "Field can't be set empty"
            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Please enter valid email"
            false
        } else {
            true
        }
    }

}