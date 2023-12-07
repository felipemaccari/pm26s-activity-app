package com.example.pm26sactivityapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.Group
import com.example.pm26sactivityapp.databinding.ActivityListGoupBinding
import com.example.pm26sactivityapp.databinding.ActivityMainMenuBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore

class MainMenuActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainMenuBinding

    private val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainMenuBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.btGroup.setOnClickListener {
            btListGroups()
        }

        var saida = StringBuilder()

        db.collection("Group")
            .orderBy("averagaeMotions", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val name = document.get("name")
                    val average = document.get("averagaeMotions")

                    saida.append("{$name - $average}")
                }

                binding.textView2.setText(saida)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro na listagem $e", Toast.LENGTH_LONG).show()
                Log.w("#USER_LIST", "Error getting documents. ", e)
            }
    }

    private fun btListGroups() {
        val intentNew = Intent(this, ListGoupActivity::class.java)
        startActivity(intentNew)
    }
}