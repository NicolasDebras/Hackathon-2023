package com.ysifre.projet.model
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


data class Employe (
    var id: String?,
    var nom: String?,
    var prenom: String?,
    var mail: String?,
    var phone: String?,
    var photo : String?,
)

object GetEmployeesNetwork{
    suspend fun getEmployees(): List<Employe> = suspendCoroutine {
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
        }}
}
