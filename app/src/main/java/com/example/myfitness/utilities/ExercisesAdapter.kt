package com.example.myfitness.utilities

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.myfitness.databinding.ExerciseLayoutBinding
import com.example.myfitness.databinding.FragmentAddExerciseBinding

class ExercisesAdapter(private val exercises: List<Exercise>): RecyclerView.Adapter<ExercisesAdapter.ExerciseViewHolder>() {

    private var listener:ExAdapterInt? = null

    fun setListener(listener:ExAdapterInt) {
        this.listener = listener
    }

    inner class ExerciseViewHolder(val binding: ExerciseLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val binding = ExerciseLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExerciseViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return exercises.size
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        with(holder) {
            with(exercises[position]) {
                binding.exName.text = this.name
                binding.sets.text = this.sets
                binding.reps.text = this.reps

                binding.delete.setOnClickListener {
                    listener?.onDelete(this, position)
                }

                binding.edit.setOnClickListener {
                    listener?.onEdit(this, position)
                }

                binding.card.setOnClickListener {
                    listener?.onClick(this, position)
                }
            }
        }
    }

    interface ExAdapterInt{
        fun onDelete(exercise: Exercise , position : Int)
        fun onClick(exercise: Exercise , position : Int)
        fun onEdit(exercise: Exercise , position : Int)
    }
}