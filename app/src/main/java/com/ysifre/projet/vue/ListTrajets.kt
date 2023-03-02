package com.ysifre.projet.vue

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.ysifre.projet.R
import com.ysifre.projet.model.Course
import com.ysifre.projet.model.Employe
import com.ysifre.projet.model.GetEmployeesNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ListTrajets : Fragment() {
    var courses: ArrayList<Course> = ArrayList()
    val drivers: ArrayList<Employe> = ArrayList()
    lateinit var conducteur: Employe


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.list_trajets, container, false).apply {
            getCourses()


        }
    }

    private fun getCourses(){
        val db = FirebaseFirestore.getInstance()
        val courseRef = db.collection("Course")

        courseRef.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val id = document.id
                    val conducteurId = document.getString("conducteurId")
                    val passagerId = document.get("passagerId") as? ArrayList<String>
                    val nbPlace = document.getLong("nbPlace")?.toInt()
                    val plaque = document.getString("plaque")
                    val lieuDepart = document.getString("lieuDepart")
                    val lieuArrive = document.getString("lieuArrive")
                    val dateHeure = document.getTimestamp("dateHeure")
                    courses.add(Course(id, conducteurId, passagerId, nbPlace, plaque, lieuDepart, lieuArrive,
                        dateHeure?.toDate()
                    ));
                }
                GlobalScope.launch(Dispatchers.Main) {
                    getDriverByCourse(courses)
                    Log.d("courses --", courses.toString())
                    Log.d("drivers --", drivers.toString())
                }
            }
            .addOnFailureListener { exception ->
                Log.w("-----------------", "Erreur lors de la récupération des documents", exception)
            }
    }

    private suspend fun getDriverByCourse(courses: ArrayList<Course>){
        val employees = GetEmployeesNetwork.getEmployees()
        for (employee in employees){
            for(course in courses){
                if (employee.id == course.conducteurID){
                    drivers.add(employee)
                }
            }
        }
    }

}