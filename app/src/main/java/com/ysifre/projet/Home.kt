package com.ysifre.projet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.ysifre.projet.model.Employe
import kotlinx.coroutines.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class Home : Fragment() {
    private lateinit var plus_button: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home, container, false).apply {
            plus_button = findViewById<Button>(R.id.plus_button) as Button
            plus_button.setOnClickListener {
                findNavController().navigate(
                    HomeDirections.actionHomeToListTrajets()
                )
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val text = view.findViewById<TextView>(R.id.text)

        val origin = "4 rue fernand leger saint cyr"
        val destination = "45 avenue des etats unis versailles"
        GlobalScope.launch(Dispatchers.Main) {
            val response = withContext(Dispatchers.IO) {
                val employees = getEmployees()
                println("TEST TEST : $employees")
                NetworkManagerDuration.getDuration(origin, destination).await()
            }
            activity?.runOnUiThread(java.lang.Runnable {
                text.text = response.routes[0].legs[0].duration.text
            })
        }
    }


    private suspend fun getEmployees(): List<Employe> = suspendCoroutine {
        val db = FirebaseFirestore.getInstance()
        val employeeRef = db.collection("Employe")

        employeeRef.get().addOnCompleteListener { task ->
            val employees = mutableListOf<Employe>()
            if (task.isSuccessful) {
                for (document in task.result) {
                    val id = document.id
                    val nom = document.getString("nom")
                    val prenom = document.getString("prenom")
                    val mail = document.getString("mail")
                    val phone = document.getString("tel")
                    val photo = document.getString("photo")
                    employees.add(Employe(id, nom, prenom, mail, phone, photo))
                }
                it.resume(employees)
            }
        }
    }
}



