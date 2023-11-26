package com.example.pm26sactivityapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.pm26sactivityapp.databinding.ActivityGroupBinding
import com.example.pm26sactivityapp.databinding.ActivityListGoupBinding

class GroupActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGroupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val groupName = intent.getStringExtra("GROUP_NAME")

        binding.tvName.setText(groupName)
    }

    fun btOnClick(view: View) {}
}