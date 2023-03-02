package com.ysifre.projet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class Home: Fragment() {
    private lateinit var validateButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home, container, false).apply {
            validateButton = findViewById<Button>(R.id.validateButton) as Button
            validateButton.setOnClickListener {
                findNavController().navigate(
                    HomeDirections.actionHomeToListTrajets()
                )
            }
        }
    }
}