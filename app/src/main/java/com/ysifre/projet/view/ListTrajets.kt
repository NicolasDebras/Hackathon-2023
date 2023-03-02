package com.ysifre.projet

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.ysifre.projet.model.Employee
import com.ysifre.projet.model.GetEmployeesNetwork
import com.ysifre.projet.model.GetTrajetsNetwork
import com.ysifre.projet.model.Trajet
import com.ysifre.projet.view.ItemsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ListTrajets : Fragment() {
    lateinit var recyclerView: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.list_trajets, container, false).apply {
            recyclerView = findViewById<RecyclerView>(R.id.list_trajet)
            recyclerView.layoutManager = LinearLayoutManager(activity)
            GlobalScope.launch(Dispatchers.IO) {
                val data = getCourses()
                println("TEST DATA $data")
//                val data_trajets = mutableListOf<ItemsViewModel>()
//                val test1 = ItemsViewModel("nom", "prenom", "123", "123")
//                data_trajets.add(test1)
                activity?.runOnUiThread(java.lang.Runnable {
                    val adapter = CustomAdapter(data)
                    recyclerView.adapter = adapter
                })

            }
        }
    }

    private suspend fun getCourses(): List<ItemsViewModel> {
        var courses: ArrayList<Trajet> = ArrayList()
        val db = FirebaseFirestore.getInstance()
        val courseRef = db.collection("Course")
        val data = ArrayList<ItemsViewModel>()

        val trajets = GetTrajetsNetwork.getTrajets()
        for (trajet in trajets) {
            println("conducteur = ${trajet.conducteurID}")
            val employee = GetEmployeesNetwork.getEmployeeFromId(trajet.conducteurID!!)
            println("employee = $employee")
            data.add(
                ItemsViewModel(
                    employee.prenom!!, employee.nom!!, employee.photo!!, trajet.dateDepart.toString()
                )
            )
        }

        return data
    }
}

class CustomAdapter(private val mList: List<ItemsViewModel>) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    private var selectedItemPosition: Int = 0
    private lateinit var onItemClickListener: OnItemClickListener

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context).inflate(R.layout.info_cell, parent, false)

        return ViewHolder(view)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    private fun getItem(position: Int): ItemsViewModel {
        return mList[position]
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val itemsViewModel = mList[position]
        holder.firstNameText.text = itemsViewModel.firstName
        holder.lastNameText.text = itemsViewModel.lastName
        holder.heureText.text = itemsViewModel.heure
        holder.itemView.setOnClickListener(View.OnClickListener {
            selectedItemPosition = position
            notifyDataSetChanged()
            onItemClickListener.onItemClick(
                position
            )
        })
    }


    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.profile_image)
        val firstNameText: TextView = itemView.findViewById(R.id.firstNameText)
        val lastNameText: TextView = itemView.findViewById(R.id.lastNameText)
        val heureText: TextView = itemView.findViewById(R.id.hour)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun getItemCount(): Int {
        return mList.size
    }
}