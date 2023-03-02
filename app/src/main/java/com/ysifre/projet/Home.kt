package com.ysifre.projet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.*

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

    @OptIn(DelicateCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val text = view.findViewById<TextView>(R.id.text)

        val origin = "4 rue fernand leger saint cyr"
        val destination = "45 avenue des etats unis versailles"
        GlobalScope.launch(Dispatchers.Main) {
            val response = withContext(Dispatchers.IO) {
                NetworkManagerDuration.getDuration(origin, destination).await();
            }
            activity?.runOnUiThread(java.lang.Runnable {
                text.text = response.routes[0].legs[0].duration.text
                println("TEST : ${response.routes[0].legs[0].duration.text}")
            })
        }
    }
}