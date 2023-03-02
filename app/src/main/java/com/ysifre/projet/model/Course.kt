package com.ysifre.projet.model

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

data class Course(
    var id: String,
    var conducteurID: String?,
    var passagerID: ArrayList<String>?,
    var nbPlaces: Int?,
    var plaque: String?,
    var depart: String?,
    var arrivee: String?,
    val dateDepart: Date?,
)
