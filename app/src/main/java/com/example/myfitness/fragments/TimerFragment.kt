package com.example.myfitness.fragments

import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.myfitness.R
import com.example.myfitness.databinding.FragmentHomeBinding
import com.example.myfitness.databinding.FragmentTimerBinding
import java.util.Locale
import androidx.navigation.NavArgs
import androidx.navigation.NavArgsLazy
import androidx.navigation.fragment.navArgs
import com.example.myfitness.utilities.Exercise
import com.google.firebase.auth.FirebaseAuth

class TimerFragment : Fragment() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: FragmentTimerBinding
    private lateinit var navController: NavController
    private lateinit var startButton: Button
    private lateinit var timer: CountDownTimer
    private lateinit var restTimer: CountDownTimer
    private var startTimer: Boolean = false
    private var startRestTimer: Boolean = false
    private val args: TimerFragmentArgs by navArgs()
    private lateinit var exercise: Exercise
    private var set_time = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTimerBinding.inflate(inflater, container, false)

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        navController = Navigation.findNavController(view)
        exercise = args.exercise
        registerEvents()
    }

    private fun registerEvents() {
        binding.startButton.setOnClickListener {
            starTime((exercise.reps.toInt()*1000).toString(), exercise.sets.toInt())
        }
        binding.backButton.setOnClickListener {
            if (startTimer === true) {
                timer.cancel()
                startTimer = false
            }
            if (startRestTimer === true) {
                restTimer.cancel()
                startRestTimer = false
            }
            navController.navigate(R.id.action_timerFragment_to_homeFragment)
        }
    }

    private fun starTime(seconds: String, set: Int) {
        startTimer = true
        timer = object : CountDownTimer(seconds.toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.set.text = "Set " + set_time.toString()
                val minutes = ((millisUntilFinished / 1000) % 3600 ) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                val timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
                binding.time.text = timeFormatted
            }

            override fun onFinish() {
                binding.time.text = "00:00"
                Toast.makeText(context, "Time's up", Toast.LENGTH_SHORT).show()
                if (set_time != set) {
                    // Create a rest timer that lasts for 10 seconds
                    restTimer = object : CountDownTimer(10000, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            binding.set.text = "Rest for set " + set_time.toString()
                            val minutes = ((millisUntilFinished / 1000) % 3600 ) / 60
                            val seconds = (millisUntilFinished / 1000) % 60
                            val timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
                            binding.time.text = timeFormatted
                        }

                        override fun onFinish() {
                            // Start the set timer again when the rest time finishes
                            Toast.makeText(context, "Rest's up", Toast.LENGTH_SHORT).show()
                            set_time += 1
                            timer.start()
                        }
                    }
                    // Start the rest timer
                    startRestTimer = true
                    restTimer.start()
                }
                else {
                    set_time = 1
                    binding.set.text = "Set " + set_time.toString()
                }
            }
        }
        timer.start()
    }

}