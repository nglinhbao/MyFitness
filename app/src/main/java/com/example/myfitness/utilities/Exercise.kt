package com.example.myfitness.utilities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Exercise(val id: String, var name: String, var sets: String, var reps: String) :
    Parcelable
