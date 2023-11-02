package com.example.myfitness.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavController
import com.example.myfitness.R
import com.example.myfitness.databinding.FragmentAddExerciseBinding
import com.example.myfitness.databinding.FragmentHomeBinding
import com.example.myfitness.utilities.Exercise
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

class AddExerciseFragment : DialogFragment() {
    private lateinit var navController: NavController
    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var binding: FragmentAddExerciseBinding
    private lateinit var listener : OnDialogNextButtonClickListener
    private var exercise: Exercise? = null

    fun setListener(listener: OnDialogNextButtonClickListener) {
        this.listener = listener
    }

    companion object {
        const val TAG = "AddExerciseFragment"
        @JvmStatic
        fun newInstance(id: String, name: String, sets: String, reps: String) = AddExerciseFragment().apply {
            arguments = Bundle().apply {
                putString("id", id)
                putString("name", name)
                putString("sets", sets)
                putString("reps", reps)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddExerciseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            exercise = Exercise(arguments?.getString("id").toString(), arguments?.getString("name").toString(),arguments?.getString("sets").toString(),arguments?.getString("reps").toString())
            binding.exName.setText(exercise!!.name)
            binding.sets.setText(exercise!!.sets)
            binding.reps.setText(exercise!!.reps)
        }

        registerEvents()
    }

    private fun registerEvents() {
        binding.nextButton.setOnClickListener {
            val ex_name = binding.exName.text.toString()
            val sets = binding.sets.text.toString()
            val reps = binding.reps.text.toString()

            if (ex_name.isNotEmpty() && sets.isNotEmpty() && reps.isNotEmpty()) {
                // Check if sets and reps are integers
                if (sets.toIntOrNull() != null && reps.toIntOrNull() != null) {
                    // Check if exercise name is a string and does not contain any numbers
                    if (ex_name.all { it.isLetter() }) {
                        if (exercise == null) {
                            listener.saveTask(ex_name, binding.exName, sets, binding.sets, reps, binding.reps)
                        } else {
                            exercise?.name = ex_name
                            exercise?.sets = sets
                            exercise?.reps = reps
                            exercise?.let { it1 -> listener.updateTask(it1, ex_name, binding.exName, sets, binding.sets, reps, binding.reps) }
                        }
                    } else {
                        Toast.makeText(context, "Please enter a valid name", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Please enter valid numbers for sets and reps", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.closeButton.setOnClickListener {
            dismiss()
        }

    }

    interface OnDialogNextButtonClickListener{
        fun saveTask(exName:String , exNameEdit: TextInputEditText, sets:String, setsEdit: TextInputEditText, reps:String, repsEdit: TextInputEditText)
        fun updateTask(exercise: Exercise, exName:String , exNameEdit: TextInputEditText, sets:String, setsEdit: TextInputEditText, reps:String, repsEdit: TextInputEditText)
    }
}