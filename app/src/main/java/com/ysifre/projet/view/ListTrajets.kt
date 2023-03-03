package com.ysifre.projet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

class ListTrajets : Fragment() {
    lateinit var hour : String
    lateinit var recyclerView: RecyclerView
    private lateinit var database: FirebaseDatabase
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(requireContext())
            .inflate(R.layout.list_trajets, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val textView = view.findViewById<TextView>(R.id.text)
        val backButton = view.findViewById<ImageButton>(R.id.back_button)
        recyclerView = view.findViewById(R.id.list_trajet)
        database = FirebaseDatabase.getInstance()
        hour = ListTrajetsArgs.fromBundle(requireArguments()).hour
        val user_home = ListTrajetsArgs.fromBundle(requireArguments()).address
        backButton.setOnClickListener {
            findNavController().navigateUp()
        }
        textView.text = ListTrajetsArgs.fromBundle(requireArguments()).address
        val driver1 = Driver("da", "da", "4 rue fernand leger saint cyr", "da",0,4)
        val driver2 = Driver("da2", "da", "45 avenue des etats unis", "da2",0,4)
        val driver3 = Driver("da3", "d3a", "30 avenue de paris versailles", "da3",0,4)
        val Drivers = mutableListOf<Driver>()
        Drivers.add(driver1)
        Drivers.add(driver2)
        Drivers.add(driver3)
        val rides = Rides(Drivers)
        val databaseRides = database.getReference().child("Rides").setValue(rides)
        val database_rides = database.getReference().child("Rides")
        val drivers_filtered = mutableListOf<Driver>()
        var totalTimeToSend : Long = 0
        var lastNameToSend : String = ""
        getDriversFromDatabase(object : DriverListCallback {
            override fun onCallback(drivers: MutableList<Driver>) {
                GlobalScope.launch(Dispatchers.Main) {
                    for (driver in drivers) {
                        val directTime = withContext(Dispatchers.IO) {
                            NetworkManagerDuration.getDuration(driver.address, "Viroflay").await()
                        }
                        val directTimeValue = directTime.routes[0].legs[0].duration.value

                        val part1 = withContext(Dispatchers.IO) {
                            NetworkManagerDuration.getDuration(driver.address, user_home).await()
                        }
                        val part1Value = part1.routes[0].legs[0].duration.value

                        val part2 = withContext(Dispatchers.IO) {
                            NetworkManagerDuration.getDuration(user_home, "Viroflay").await()
                        }
                        val part2Value = directTime.routes[0].legs[0].duration.value
                        if(checkIfOk(directTimeValue,part1Value,part2Value)) {
                            drivers_filtered.add(driver)
                            println(driver.address)
                        }
                        totalTimeToSend = getTotalDuration(part1Value,part2Value)

                    }
                    activity?.runOnUiThread(java.lang.Runnable {
                        recyclerView.apply {
                            layoutManager = GridLayoutManager(context, 1)
                            adapter = ListAdapter(drivers_filtered, object : OnProductListener {
                                override fun onClicked(driver: Driver, position: Int) {
                                    lastNameToSend = driver.lastName
                                    findNavController().navigate(ListTrajetsDirections.actionListTrajetsToConfirmationPage(totalTimeToSend,lastNameToSend))
                                }
                            })
                        }
                    })
                }

            }
        })
    }

    /*fun filterDrivers(): MutableList<Driver> {
        val final_list = mutableListOf<Driver>()
    }

     */

    fun checkIfOk(total : Int, part1: Int, part2: Int) : Boolean {
        val minutes_total = TimeUnit.SECONDS.toMinutes(total.toLong())
        val minutes_1 = TimeUnit.SECONDS.toMinutes(part1.toLong())
        val minutes_2 = TimeUnit.SECONDS.toMinutes(part2.toLong())
        println("$minutes_total = $minutes_1 + $minutes_2")
        return minutes_total + 10 >= minutes_1 + minutes_2;
    }

    fun getTotalDuration(part1: Int, part2: Int) : Long {
        val minutes_1 = TimeUnit.SECONDS.toMinutes(part1.toLong())
        val minutes_2 = TimeUnit.SECONDS.toMinutes(part2.toLong())
        return minutes_1 + minutes_2;
    }

    fun getDriversFromDatabase(myCallback: DriverListCallback) {
        val driversReference = database.getReference().child("Rides").child("drivers")
        val drivers_test = mutableListOf<Driver>()
        driversReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (snapshot: DataSnapshot in snapshot.children) {
                    val driver = Driver(
                        snapshot.child("firstName").getValue().toString(),
                        snapshot.child("lastName").getValue().toString(),
                        snapshot.child("address").getValue().toString(),
                        snapshot.child("number").getValue().toString(),
                        snapshot.child("totalTime").getValue() as Long,
                        snapshot.child("seats").getValue() as Long,
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
        fun onCallback(drivers: MutableList<Driver>)
    }

    data class Rides(
        var drivers: MutableList<Driver>
    )

    data class Driver(
        var firstName: String,
        var lastName: String,
        var address: String,
        var number: String,
        var totalTime: Long,
        var seats: Long,
        )


    class ListAdapter(drivers: MutableList<ListTrajets.Driver>, val listener: OnProductListener) :
        RecyclerView.Adapter<DriverViewHolder>() {

        private var drivers: MutableList<ListTrajets.Driver>
        override fun getItemCount(): Int = drivers.size

        init {
            this.drivers = drivers
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DriverViewHolder {
            return DriverViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.info_cell, parent, false
                )
            )
        }

        override fun onBindViewHolder(holder: DriverViewHolder, position: Int) {
            val driver = drivers[position]
            holder.updateGame(driver)
            holder.validateButton.setOnClickListener() {
                listener.onClicked(driver, position)
            }
        }

    }

    class DriverViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        val imageView: ImageView = itemView.findViewById(R.id.profile_image)
        val firstNameText: TextView = itemView.findViewById(R.id.firstNameText)
        val lastNameText: TextView = itemView.findViewById(R.id.lastNameText)
        val hourText: TextView = itemView.findViewById(R.id.hour)
        val validateButton = v.findViewById<ImageButton>(R.id.validate_button)


        fun updateGame(driver: ListTrajets.Driver) {
            firstNameText.text = driver.firstName
            lastNameText.text = driver.lastName
            //hourText.text =

            /*val url = driver.image

            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Glide.with(itemView.context)
                .load(url)
                .centerCrop()
                .into(imageView)

             */
        }
    }

    interface OnProductListener {
        fun onClicked(driver: Driver, position: Int) {

        }
    }
}