package com.ysifre.projet.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.snapshots
import com.ysifre.projet.ListTrajets
import com.ysifre.projet.R

class ConfirmationPage : Fragment() {
    private lateinit var database: FirebaseDatabase
    private lateinit var test : TextView
    private lateinit var backButton : ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_confirmation_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val totalTime = ConfirmationPageArgs.fromBundle(requireArguments()).totalTimeToSend
        val numberID = ConfirmationPageArgs.fromBundle(requireArguments()).lastNameToSend
        test = view.findViewById(R.id.textviewtest)
        backButton = view.findViewById(R.id.back_button)
        var final_seats : Long = 0
        backButton.setOnClickListener {
            findNavController().navigateUp()
        }
        database = FirebaseDatabase.getInstance()
        val driversReference = database.getReference().child("Rides").child("drivers")

        getDriversFromDatabase(object : DriverListCallback {
            override fun onCallback(drivers: MutableList<ListTrajets.Driver>) {
                for(driver in drivers) {
                    if(driver.number == numberID) {
                        final_seats = driver.seats
                    }
                }
                val eventListener = object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        driversReference.child(numberID).child("totalTime").setValue(totalTime)
                        driversReference.child(numberID).child("seats").setValue(final_seats-1)
                    }
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                }
                driversReference.child(numberID).addListenerForSingleValueEvent(eventListener)
            }
        })




    }


    fun getDriversFromDatabase(myCallback: DriverListCallback) {
        val driversReference = database.getReference().child("Rides").child("drivers")
        val drivers_test = mutableListOf<ListTrajets.Driver>()
        driversReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (snapshot: DataSnapshot in snapshot.children) {
                    val driver = ListTrajets.Driver(
                        snapshot.child("firstName").getValue().toString(),
                        snapshot.child("lastName").getValue().toString(),
                        snapshot.child("address").getValue().toString(),
                        snapshot.child("totalTime").getValue() as Long,
                        snapshot.child("seats").getValue() as Long,
                        snapshot.child("number").getValue().toString()
                    )
                    drivers_test.add(driver)
                }
                myCallback.onCallback(drivers_test)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    interface DriverListCallback {
        fun onCallback(drivers: MutableList<ListTrajets.Driver>)
    }
}

