package com.example.pm26sactivityapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pm26sactivityapp.adapter.ListaAdapter
import com.example.pm26sactivityapp.adapter.OnListaAdapterClickListener
import com.example.pm26sactivityapp.databinding.ActivityListGoupBinding
import com.example.pm26sactivityapp.entities.Group
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class ListGoupActivity : AppCompatActivity(), OnListaAdapterClickListener {
    private lateinit var binding: ActivityListGoupBinding

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ListaAdapter

    private var groups = mutableListOf<Group>()

    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListGoupBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.btNewGroup.setOnClickListener {
            newGroupOnClick()
        }

        recyclerView = binding.rview
        adapter = ListaAdapter(groups, this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        findAllGroups()
    }

    private fun findAllGroups() {
        db.collection("Group")
            .get()
            .addOnSuccessListener { documents ->
                groups.clear()
                for (document in documents) {
                    val group = document.toObject(Group::class.java)
                    groups.add(group)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro na listagem", Toast.LENGTH_LONG).show()
            }
    }

    private fun newGroupOnClick() {
        val intentNew = Intent(this, NewGroupActivity::class.java)
        startActivity(intentNew)
    }

    override fun onItemClick(group: Group) {
        val intent = Intent(this, GroupActivity::class.java)
        intent.putExtra("GROUP_NAME", group.name)
        startActivity(intent)
    }
}
