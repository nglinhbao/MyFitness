package com.example.myfitness.fragments

import android.media.MediaPlayer.OnCompletionListener
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.myfitness.R
import com.example.myfitness.databinding.FragmentSignInBinding
import com.example.myfitness.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignInFragment : Fragment() {
    private lateinit var navController: NavController
    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: FragmentSignInBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignInBinding.inflate(inflater, container, false)

        val toolbar = binding.toolbarSignIn
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        mAuth = FirebaseAuth.getInstance()
        Log.d("auth", mAuth.toString())

        binding.signUpButton.setOnClickListener {
            navController.navigate(R.id.action_signInFragment_to_signUpFragment)
        }

        binding.signInButton.setOnClickListener {
            val email = binding.userEmail.text.toString()
            val pass = binding.userPass.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                userSignIn(email, pass)
            } else
                Toast.makeText(context, "Please enter something", Toast.LENGTH_SHORT).show()
        }

    }

    private fun userSignIn(email: String, pass: String) {
        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Log in successfully", Toast.LENGTH_SHORT).show()
                navController.navigate(R.id.action_signInFragment_to_homeFragment)
            }
            else {
                Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }
}