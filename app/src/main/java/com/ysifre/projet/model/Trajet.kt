package com.ysifre.projet.model

import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

data class Trajet(
    var id: String,
    var conducteurID: String?,
    var passagerID: ArrayList<String>?,
    var nbPlaces: Int?,
    var plaque: String?,
    var depart: String?,
    var arrivee: String?,
    val dateDepart: Date?,
)

object GetTrajetsNetwork {
    suspend fun getTrajets(): List<Trajet> = suspendCoroutine {
        val db = FirebaseFirestore.getInstance()
        val trajetsRef = db.collection("Course")
        trajetsRef.get().addOnCompleteListener { task ->
            val trajets = mutableListOf<Trajet>()
            if (task.isSuccessful) {
                for (document in task.result) {
                    val id = document.id
                    val conducteurId = document.getString("conducteurId")
                    val passagerId = document.get("passagerId") as? ArrayList<String>
                    val nbPlace = document.getLong("nbPlace")?.toInt()
                    val plaque = document.getString("plaque")
                    val lieuDepart = document.getString("lieuDepart")
                    val lieuArrive = document.getString("lieuArrive")
                    val dateHeure = document.getTimestamp("dateHeure")
                    trajets.add(
                        Trajet(
                            id,
                            conducteurId,
                            passagerId,
                            nbPlace,
                            plaque,
                            lieuDepart,
                            lieuArrive,
                            dateHeure?.toDate()
                        ))
                }
                it.resume(trajets)
            }
        }
    }
}