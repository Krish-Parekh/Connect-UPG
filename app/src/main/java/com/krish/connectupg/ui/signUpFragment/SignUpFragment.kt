package com.krish.connectupg.ui.signUpFragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.*
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.krish.connectupg.R
import com.krish.connectupg.databinding.FragmentSignUpBinding
import com.krish.connectupg.model.User
import com.krish.connectupg.utils.dismissDialogBox
import com.krish.connectupg.utils.getDialogBox
import java.util.*
import java.util.regex.Pattern

private const val TAG = "SIGNUPFRAGMENT"

class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFirebaseStorage: FirebaseStorage
    private lateinit var mFirebaseDatabase: FirebaseDatabase
    var selectedPhotoUri: Uri? = null
    private val PASSWORD_PATTERN: Pattern = Pattern.compile(
        "^" +  //"(?=.*[0-9])" +         //at least 1 digit
                //"(?=.*[a-z])" +         //at least 1 lower case letter
                //"(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +  //any letter
                "(?=.*[@#$%^&+=])" +  //at least 1 special character
                "(?=\\S+$)" +  //no white spaces
                ".{4,}" +  //at least 4 characters
                "$"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(inflater, container, false)

        mAuth = FirebaseAuth.getInstance()
        mFirebaseDatabase = FirebaseDatabase.getInstance()
        mFirebaseStorage = FirebaseStorage.getInstance()

        binding.btnPhotoPick.setOnClickListener {
            getPhoto()
        }
        binding.circularImage.setOnClickListener {
            getPhoto()
        }

        binding.btnSignUp.setOnClickListener {
            getDialogBox(requireContext())
            val username = binding.etUsername.text.toString().trim { it <= ' ' }
            val email = binding.etEmail.text.toString().trim { it <= ' ' }
            val password = binding.etPassword.text.toString().trim { it <= ' ' }

            if (validateUsername(username) && validateEmail(email) && validatePassword(password)) {
                Log.d(TAG, "onCreateView: ${User(username, email)}")
                authenticateUser(username, email, password)
                clearField()
            } else if (selectedPhotoUri == null) {
                Toast.makeText(requireContext(), "Please Select Photo", Toast.LENGTH_SHORT).show()
                dismissDialogBox()
            } else {
                dismissDialogBox()
            }
        }
        return binding.root
    }

    private fun clearField() {
        binding.etUsername.text.clear()
        binding.etEmail.text.clear()
        binding.etPassword.text.clear()
        binding.circularImage.setImageBitmap(null)
        binding.btnPhotoPick.visibility = View.VISIBLE
    }

    private fun authenticateUser(username: String, email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = mAuth.currentUser?.uid!!
                    uploadImageToDatabase(username, email, uid)
                }
            }
            .addOnFailureListener { exception ->
                dismissDialogBox()
                try {
                    throw exception
                } catch (e: FirebaseAuthWeakPasswordException) {
                    Toast.makeText(requireContext(), "Error : Weak Password", Toast.LENGTH_SHORT)
                        .show()
                } catch (e: FirebaseAuthEmailException) {
                    Toast.makeText(requireContext(), "Error : Email Not Valid", Toast.LENGTH_SHORT)
                        .show()
                } catch (e: FirebaseAuthUserCollisionException){
                    Toast.makeText(requireContext(), "Error : Email id already taken", Toast.LENGTH_SHORT)
                        .show()
                } catch (e : FirebaseAuthInvalidUserException){
                    Toast.makeText(requireContext(), "Error : Invalid User", Toast.LENGTH_SHORT)
                        .show()
                } catch (e : FirebaseAuthInvalidCredentialsException){
                    Toast.makeText(requireContext(), "Error : Credentials Invalid", Toast.LENGTH_SHORT)
                        .show()
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error : $exception", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    private fun uploadImageToDatabase(username: String, email: String, uid: String) {
        val fileName = UUID.randomUUID().toString()
        val ref = mFirebaseStorage.getReference("/images/$fileName")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { uri ->
                    saveUserToDatabase(username, email, uid, uri)
                }
            }
            .addOnFailureListener { exception ->
                dismissDialogBox()
                Toast.makeText(requireContext(), "Error : $exception", Toast.LENGTH_SHORT).show()
            }

    }

    private fun saveUserToDatabase(username: String, email: String, uid: String, uri: Uri?) {
        val ref = mFirebaseDatabase.getReference("users/$uid")
        val user = User(username, email, uid, uri.toString())
        ref.setValue(user)
            .addOnSuccessListener {
                dismissDialogBox()
                findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
            }
            .addOnFailureListener { exception ->
                dismissDialogBox()
                Toast.makeText(requireContext(), "Error : $exception", Toast.LENGTH_SHORT).show()
            }
    }


    // Validation for user input
    private fun validateEmail(email: String): Boolean {
        return if (email.isBlank()) {
            binding.etEmail.error = "Field can't be set empty"
            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Please enter valid email"
            false
        } else {
            binding.etEmail.error = null
            true
        }
    }

    private fun validatePassword(password: String): Boolean {
        return if (password.isBlank()) {
            binding.etPassword.error = "Field can't be set empty"
            false
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            binding.etPassword.error =
                "a-z or A-Z letter\n1 Special Character\nNo White Spaces\nAt-least 4 Character"
            false
        } else {
            binding.etPassword.error = null
            true
        }
    }

    private fun validateUsername(username: String): Boolean {
        return if (username.isBlank()) {
            binding.etUsername.error = "Field can't be set empty"
            false
        } else {
            binding.etUsername.error = null
            true
        }
    }

    // Taking Photo From Gallery
    private fun getPhoto() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(
                requireContext().contentResolver,
                selectedPhotoUri
            )
            binding.btnPhotoPick.visibility = View.INVISIBLE
            binding.circularImage.setImageBitmap(bitmap)
        }
    }

}