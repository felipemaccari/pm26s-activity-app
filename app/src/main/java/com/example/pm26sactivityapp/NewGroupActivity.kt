package com.example.pm26sactivityapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.pm26sactivityapp.databinding.ActivityNewGroupBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.lang.StringBuilder

class NewGroupActivity : AppCompatActivity() {
    private lateinit var binding : ActivityNewGroupBinding

    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewGroupBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.btSave.setOnClickListener {
            btSabeOnClick()
        }
    }

    private fun btSabeOnClick(){
        var saida = StringBuilder()
        val name = binding.etName.text.toString()

        val record = hashMapOf(
            "name" to name)

        db.collection("Group")
            .document(name)
            .set(record)
            .addOnSuccessListener {  documentReference ->
                Toast.makeText(this, "Registro incluído com sucesso", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao incluir registro", Toast.LENGTH_LONG).show()
                Log.w("Erro", "Error adding document", e)
            }
    }
}