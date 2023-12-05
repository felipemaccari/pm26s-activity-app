package com.example.pm26sactivityapp

import android.content.Context
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.Group
import com.example.pm26sactivityapp.databinding.ActivityGroupBinding
import com.example.pm26sactivityapp.databinding.ActivityListGoupBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class GroupActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGroupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var group: Group
    private val db = Firebase.firestore
    private var groupName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupBinding.inflate(layoutInflater)

        setContentView(binding.root)

        groupName = intent.getStringExtra("GROUP_NAME").toString()

        binding.tvName.setText(groupName)

        auth = Firebase.auth

        binding.btAddParticipant.setOnClickListener {
            btOnClick()
        }

        userLoggedIsGroupParticipant()
    }

    fun btOnClick() {
        val docRef = db.collection("users").document(userLogged())

        docRef.get().addOnSuccessListener { document ->
            if (document != null) {
                val groupParticipant = document["groupParticipant"] as? MutableList<String> ?: mutableListOf()

                groupParticipant.add(groupName)

                docRef.update("groupParticipant", groupParticipant)
                    .addOnSuccessListener {
                        Toast.makeText( this, "Você entrou no grupo", Toast.LENGTH_LONG ).show()
                        recreate()
                    }
                    .addOnFailureListener {
                        Toast.makeText( this, "Erro ao entrar no grupo", Toast.LENGTH_LONG ).show()
                    }
            }
        }.addOnFailureListener {
        }
    }

    fun userLogged(): String {
        return "nH0PQ1fR8wg18MkkkWHF"
    }

    fun userLoggedIsGroupParticipant() {
        val docRef = db.collection("users").document(userLogged())

        docRef.get().addOnSuccessListener { document ->
            if (document != null) {
                val groupParticipant =
                    document["groupParticipant"] as? MutableList<String> ?: mutableListOf()

                if(!groupParticipant.isEmpty()) {
                    val isGroupMember = groupName in groupParticipant

                    if(isGroupMember){
                        binding.btAddParticipant.visibility = View.INVISIBLE
                        binding.btRegisterActivity.visibility = View.VISIBLE
                    }
                }
            }
        }.addOnFailureListener {
        }
    }
}