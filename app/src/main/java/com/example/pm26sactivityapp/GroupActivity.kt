package com.example.pm26sactivityapp

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.Group
import com.example.pm26sactivityapp.databinding.ActivityGroupBinding
import com.example.pm26sactivityapp.services.CaptureMotion
import com.example.pm26sactivityapp.services.UserInformation
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class GroupActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGroupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var group: Group
    private val db = Firebase.firestore
    private var groupName = ""
    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private var isCapturing: Boolean = false
    private var text: String = ""
    private var captureMotion: CaptureMotion = CaptureMotion()
    private var userInformation: UserInformation = UserInformation()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupBinding.inflate(layoutInflater)

        setContentView(binding.root)

        groupName = intent.getStringExtra("GROUP_NAME").toString()

        binding.tvName.setText(groupName)

        auth = Firebase.auth

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!!


        binding.btAddParticipant.setOnClickListener {
            btOnClick()
        }
        userLoggedIsGroupParticipant()
        loadHistorical()

        binding.btRegisterActivity.setOnClickListener{
            if(isCapturing){
                isCapturing = captureMotion.stopCapture(sensorManager, this, db, groupName)
                binding.btRegisterActivity.text = "Iniciar Atividade"
                loadHistorical()
            } else {
                isCapturing = captureMotion.startCapture(sensorManager,accelerometer,this)
                binding.btRegisterActivity.text = "Parar Atividade"
            }
        }
    }


    fun btOnClick() {
        val docRef = db.collection("users").document(userInformation.userLogged())

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

    fun userLoggedIsGroupParticipant() {
        val docRef = db.collection("users").document(userInformation.userLogged())

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

    private fun loadHistorical(){
        val groupUserAverageMotion = db.collection("groupUserAverageMotion")
        groupUserAverageMotion.whereEqualTo("groupName", groupName)
            .get()
            .addOnSuccessListener { result ->
                text = ""
                for (document in result) {
                    // Acesso aos dados do documento filtrado por nome
                    val currentDate = document.get("currentDate")
                    val averageMotion = document.getLong("averageMotion")

                    text += "Data/Hora: ${currentDate} - Media de movimentaçãp: ${averageMotion} \n"

                }
                binding.viewTimes.text = text
            }
            .addOnFailureListener { e ->
            }
    }
}