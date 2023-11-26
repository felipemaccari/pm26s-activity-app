package com.example.pm26sactivityapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.pm26sactivityapp.databinding.ActivityListGoupBinding
import com.example.pm26sactivityapp.databinding.ActivityMainMenuBinding

class MainMenuActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainMenuBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainMenuBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.btGroup.setOnClickListener {
            btListGroups()
        }
    }

    private fun btListGroups() {
        val intentNew = Intent(this, ListGoupActivity::class.java)
        startActivity(intentNew)
    }
}