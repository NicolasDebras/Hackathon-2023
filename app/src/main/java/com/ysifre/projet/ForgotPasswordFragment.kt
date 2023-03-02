package com.ysifre.projet

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ForgotPasswordFragment : Fragment() {
    lateinit var email : EditText
    private lateinit var sendMail : Button
    private lateinit var auth : FirebaseAuth
    private lateinit var backButton : ImageButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return LayoutInflater.from(requireContext())
            .inflate(R.layout.forgot_password_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        backButton = view.findViewById(R.id.back_button)
        email = view.findViewById(R.id.email)
        sendMail = view.findViewById(R.id.send_email_button)
        auth = Firebase.auth
        sendMail.setOnClickListener() {
            sendRecoveryMail()
        }

        backButton.setOnClickListener() {
            findNavController().navigateUp()

        }
    }
    private fun sendRecoveryMail() {
        val emailText = email.text.toString().trim()

        if(emailText.isBlank()) {
            email.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.warning,0)
            return
        }
        auth.sendPasswordResetEmail(emailText).addOnCompleteListener{
                task -> if (task.isSuccessful) {
            sendMail.setOnClickListener() {
                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
            }
            Toast.makeText(requireContext(),"Email sent", Toast.LENGTH_SHORT).show()
            Log.d(ContentValues.TAG,"Email sent") }
            else {
            Toast.makeText(requireContext(),"Email not found", Toast.LENGTH_SHORT).show()
            }
        }
    }

}