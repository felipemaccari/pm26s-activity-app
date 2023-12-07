package com.example.pm26sactivityapp.services

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

class CaptureMotion  : AppCompatActivity() {

    private var sumX: Float = 0.0f
    private var sumY: Float = 0.0f
    private var sumZ: Float = 0.0f
    private var readingsCount: Int = 0
    private val readingsLimit: Int = 1000
    private var userInformation: UserInformation = UserInformation()

    fun startCapture(
        sensorManager: SensorManager,
        accelerometer: Sensor,
        _context: Context
    ): Boolean {
        sensorManager.registerListener(accelerometerListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        //Toast.makeText(_context, "Captura iniciada", Toast.LENGTH_SHORT).show()
        return true;
    }

    fun stopCapture(
        sensorManager: SensorManager,
        _context: Context,
        db: FirebaseFirestore,
        groupName: String
    ): Boolean {

        sensorManager.unregisterListener(accelerometerListener)

        val averageX = sumX / readingsCount
        val averageY = sumY / readingsCount
        val averageZ = sumZ / readingsCount

        if (readingsCount >= readingsLimit) {
            sumX = 0.0f
            sumY = 0.0f
            sumZ = 0.0f
            val total = (averageX + averageY + averageZ) / 3// aqui gravo a média das 3 direções,

            saveGroupActivities(db, groupName, total, _context)
            readingsCount = 0
        } else {

            val total = (averageX + averageY + averageZ) / 3
            saveGroupActivities(db, groupName, total, _context)
            //Toast.makeText(_context, "Captura finalizada. Média: ($averageX, $averageY, $averageZ)", Toast.LENGTH_SHORT).show()
        }
        return false
    }


    private val accelerometerListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            sumX += event.values[0]
            sumY += event.values[1]
            sumZ += event.values[2]
            readingsCount++
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        }
    }

    private fun saveGroupActivities(db: FirebaseFirestore, groupName: String?, total: Float?, _context: Context ){

        val groupUserAverageMotion = db.collection("groupUserAverageMotion")

        val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val formato = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
            val dataHoraAtual = LocalDateTime.now()
            hashMapOf(
                "id" to UUID.randomUUID().toString(),
                "groupName" to groupName,
                "averageMotion" to total,
                "currentDate" to dataHoraAtual.format(formato),
                "user" to userInformation.userLogged()
            )
        } else {
            hashMapOf(
                "id" to UUID.randomUUID().toString(),
                "groupName" to groupName,
                "averageMotion" to total,
                "user" to userInformation.userLogged()
            )
        }

        groupUserAverageMotion.add(data)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(_context, "Salvo com sucesso", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                // Ocorreu um erro ao adicionar o documento
                Toast.makeText(_context, "Falha ao salvar dados ${e.message}", Toast.LENGTH_SHORT).show()
            }

    }

}