package com.ysifre.projet.view

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.inflate
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ysifre.projet.NetworkManagerDuration
import com.ysifre.projet.R
import com.ysifre.projet.model.GetEmployeesNetwork
import kotlinx.coroutines.*


class Home : Fragment() {
    private lateinit var plus_button: ImageButton
    private lateinit var editText: EditText
    private lateinit var clock : TimePicker
    private lateinit var hour : String
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(requireContext())
            .inflate(R.layout.home, container, false)
    }


    @RequiresApi(Build.VERSION_CODES.M)
    @OptIn(DelicateCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textView = view.findViewById<TextView>(R.id.text)
        val rechercheButton = view.findViewById<Button>(R.id.recherche)
        editText = view.findViewById<EditText>(R.id.address)
        plus_button = view.findViewById(R.id.plus_button) as ImageButton
        clock = view.findViewById(R.id.clock)
        clock.setIs24HourView(true);
        hour = clock.hour.toString() + ":" + clock.minute.toString()

        rechercheButton.setOnClickListener {
            search()
        }

    }

    private fun search() {
        if(editText.text.toString().isBlank()) {
            editText.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.warning,0)
            return
        }
        else {
            findNavController().navigate(HomeDirections.actionHomeToListTrajets(editText.text.toString(),hour))
        }
    }



}



