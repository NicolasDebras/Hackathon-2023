package com.ysifre.projet.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TimePicker
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ysifre.projet.ListTrajets
import com.ysifre.projet.R


class AddRide : Fragment() {
    private lateinit var hour: TimePicker
    private lateinit var date: DatePicker
    private lateinit var firstName: EditText
    private lateinit var lastName: EditText
    private lateinit var address: EditText
    private lateinit var seats: EditText
    private lateinit var imatriculation: EditText
    private lateinit var database: FirebaseDatabase
    private lateinit var validateButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_ride, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hour = view.findViewById(R.id.TimePicker1)
        date = view.findViewById(R.id.datePicker1)
        firstName = view.findViewById(R.id.firstName)
        lastName = view.findViewById(R.id.lastName)
        address = view.findViewById(R.id.address)
        seats = view.findViewById(R.id.seatsNumber)
        imatriculation = view.findViewById(R.id.IDnumber)
        database = FirebaseDatabase.getInstance()
        validateButton = view.findViewById(R.id.button3)
        val backButton = view.findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        val driversReference = database.getReference().child("Rides").child("drivers")
        validateButton.setOnClickListener {
            val eventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val driver = ListTrajets.Driver(
                        firstName.text.toString(),
                        lastName.text.toString(),
                        address.text.toString(),
                        0,
                        3,
                        imatriculation.text.toString()
                    )
                    driversReference.child(imatriculation.text.toString()).setValue(driver)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            }
            driversReference.child(imatriculation.text.toString()).addListenerForSingleValueEvent(eventListener)
            findNavController().navigate(AddRideDirections.actionAddRideToHome())
        }


    }


}