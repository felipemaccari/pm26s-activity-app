package com.example.pm26sactivityapp.services

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CaptureMotion  : AppCompatActivity() {

    private var sumX: Float = 0.0f
    private var sumY: Float = 0.0f
    private var sumZ: Float = 0.0f
    private var readingsCount: Int = 0
    private val readingsLimit: Int = 1000

    fun startCapture(sensorManager: SensorManager, accelerometer: Sensor, _context: Context): Boolean {
        sensorManager.registerListener(accelerometerListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        //Toast.makeText(_context, "Captura iniciada", Toast.LENGTH_SHORT).show()
        return true;
    }

    fun stopCapture(sensorManager: SensorManager, _context: Context): Boolean {

        sensorManager.unregisterListener(accelerometerListener)

        val averageX = sumX / readingsCount
        val averageY = sumY / readingsCount
        val averageZ = sumZ / readingsCount

        if (readingsCount >= readingsLimit) {
            sumX = 0.0f
            sumY = 0.0f
            sumZ = 0.0f
            val total = (averageX + averageY + averageZ) / 3

            readingsCount = 0
        } else {
            Toast.makeText(_context, "Captura finalizada. Média: ($averageX, $averageY, $averageZ)", Toast.LENGTH_SHORT).show()
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

}