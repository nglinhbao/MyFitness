package com.example.myfitness.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfitness.R
import com.example.myfitness.databinding.FragmentHomeBinding
import com.example.myfitness.databinding.FragmentSignInBinding
import com.example.myfitness.utilities.Exercise
import com.example.myfitness.utilities.ExercisesAdapter
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment(), AddExerciseFragment.OnDialogNextButtonClickListener,
    ExercisesAdapter.ExAdapterInt {

    private val TAG = "HomeFragment"
    private lateinit var navController: NavController
    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var binding: FragmentHomeBinding
    private var popUpFragment: AddExerciseFragment? = null
    private lateinit var adapter: ExercisesAdapter
    private lateinit var ExerciseList: MutableList<Exercise>
    private lateinit var exerciseAdapter: ExercisesAdapter
    private lateinit var timerFragment: TimerFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        val toolbar = binding.toolbar // Assuming you have a toolbar in your layout
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (menu != null) {
            if (inflater != null) {
                super.onCreateOptionsMenu(menu, inflater)
            }
        }
        inflater?.inflate(R.menu.nav_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout_button -> {
                mAuth.signOut()
                navController.navigate(R.id.action_homeFragment_to_splashFragment)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference.child("Exercises").child(mAuth.currentUser?.uid.toString())

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        ExerciseList = mutableListOf()
        exerciseAdapter = ExercisesAdapter(ExerciseList)
        exerciseAdapter.setListener(this)
        binding.recyclerView.adapter = exerciseAdapter

        getExerciseFromFirebase()

        registerEvents()
    }

    private fun registerEvents() {
        binding.addButton.setOnClickListener {
            if (popUpFragment != null) {
                childFragmentManager.beginTransaction().remove(popUpFragment!!).commit()
            }
            popUpFragment = AddExerciseFragment()
            popUpFragment!!.setListener(this)
            popUpFragment!!.show(
                childFragmentManager,
                "AddExerciseFragment"
            )
        }
    }

    private fun getExerciseFromFirebase() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                ExerciseList.clear()
                for (taskSnapshot in snapshot.children) {
                    var attributes: MutableList<String> = mutableListOf()

                    for (attribute in taskSnapshot.children) {
                        attributes.add(attribute.value.toString())
                    }
                    val exercise =
                        taskSnapshot.key?.let { Exercise(it, attributes[0], attributes[2], attributes[1]) }

                    if (exercise != null) {
                        ExerciseList.add(exercise)
                    }

                }
                Log.d(TAG, "onDataChange: " + ExerciseList)
                exerciseAdapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()
            }


        })
    }

    override fun saveTask(
        exName: String,
        exNameEdit: TextInputEditText,
        sets: String,
        setsEdit: TextInputEditText,
        reps: String,
        repsEdit: TextInputEditText
    ) {
        val exerciseData = hashMapOf(
            "exName" to exName,
            "sets" to sets,
            "reps" to reps
        )

        database.push().setValue(exerciseData).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Exercise added", Toast.LENGTH_SHORT).show()
                exNameEdit.text = null
                setsEdit.text = null
                repsEdit.text = null
            } else {
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }

        popUpFragment?.dismiss()
    }

    override fun updateTask(
        exercise: Exercise,
        exName: String,
        exNameEdit: TextInputEditText,
        sets: String,
        setsEdit: TextInputEditText,
        reps: String,
        repsEdit: TextInputEditText
    ) {
        val exerciseData = hashMapOf(
            "exName" to exName,
            "sets" to sets,
            "reps" to reps
        )

        val map = HashMap<String, Any>()
        map[exercise.id] = exerciseData
        database.updateChildren(map).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Updated Successfully", Toast.LENGTH_SHORT).show()
                exNameEdit.text = null
                setsEdit.text = null
                repsEdit.text = null
            } else {
                Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
            popUpFragment!!.dismiss()
        }
    }

    override fun onDelete(exercise: Exercise, position: Int) {
        database.child(exercise.id).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onClick(exercise: Exercise, position: Int) {
        val action = HomeFragmentDirections.actionHomeFragmentToTimerFragment(exercise)
        navController.navigate(action)
    }

    override fun onEdit(exercise: Exercise, position: Int) {
        if (popUpFragment != null)
            childFragmentManager.beginTransaction().remove(popUpFragment!!).commit()

        popUpFragment = AddExerciseFragment.newInstance(exercise.id, exercise.name, exercise.sets, exercise.reps)
        popUpFragment!!.setListener(this)
        popUpFragment!!.show(
            childFragmentManager,
            AddExerciseFragment.TAG
        )
    }
}