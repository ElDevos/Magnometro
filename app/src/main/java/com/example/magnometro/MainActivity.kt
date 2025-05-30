package com.example.magnetometro

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.magnetometro.ui.theme.MagnetometroAppTheme
import kotlin.math.sqrt

class MainActivity : ComponentActivity() {
    private lateinit var sensorManager: SensorManager


    private var magX by mutableStateOf(0f)
    private var magY by mutableStateOf(0f)
    private var magZ by mutableStateOf(0f)

    // Listener para eventos del sensor
    private val magListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                magX = event.values[0]
                magY = event.values[1]
                magZ = event.values[2]
            }
        }
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
            if (sensor.type == Sensor.TYPE_MAGNETIC_FIELD && accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
                Toast.makeText(this@MainActivity, "Magnetómetro poco preciso, calibra moviendo en ’8’", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // 1) Verificar disponibilidad del magnetómetro
        val magSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        if (magSensor == null) {
            Toast.makeText(this, "Este dispositivo no dispone de magnetómetro", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        setContent {
            MagnetometroAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Top
                    ) {
                        Text(
                            text = " Magnetómetro",
                            style = MaterialTheme.typography.headlineMedium
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "X: ${"%.1f".format(magX)} μT",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Y: ${"%.1f".format(magY)} μT",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Z: ${"%.1f".format(magZ)} μT",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // 2) Mostrar la intensidad total
                        val intensity = sqrt(magX * magX + magY * magY + magZ * magZ)
                        Text(
                            text = "Intensidad: ${"%.1f".format(intensity)} μT",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Registramos el listener al sensor de campo magnético
        sensorManager.registerListener(
            magListener,
            sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onPause() {
        super.onPause()
        // Detenemos updates al pausar la activity
        sensorManager.unregisterListener(magListener)
    }
}
