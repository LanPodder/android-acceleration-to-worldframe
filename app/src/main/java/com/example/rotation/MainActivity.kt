package com.example.rotation

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.rotation.ui.theme.RotationTheme
import com.example.rotation.util.Quaternion

class MainActivity : ComponentActivity() {
    private lateinit var sensorManager: SensorManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        setContent {
            RotationTheme {
            // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    var eulerAngles = remember { mutableStateListOf<Float>() }
    var quaternion = remember { mutableStateOf(Quaternion(0.0f, 0.0f, 0.0f, 1.0f)) }
    var accelerometerReading = remember {
        mutableStateListOf<Float>()
    }

    var accelerometerReadingAdjusted = remember {
        mutableStateListOf<Float>()
    }

    var magnetometerReading = remember {
        mutableStateListOf<Float>()
    }

    eulerAngles.clear()
    accelerometerReading.clear()
    magnetometerReading.clear()

    val sensorListener = remember {
        object: SensorEventListener {
            override fun onSensorChanged(e: SensorEvent) {
                if (e.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                    accelerometerReading.clear()
                    accelerometerReadingAdjusted.clear()

                    accelerometerReading.addAll(listOf(e.values[0], e.values[1], e.values[2]))
                    accelerometerReadingAdjusted.addAll(
                        quaternion.value.rotate(
                            floatArrayOf(e.values[0], e.values[1], e.values[2])
                        ).toList()
                    )
                } else if(e.sensor.type == Sensor.TYPE_ROTATION_VECTOR){
                    eulerAngles.clear()

                    var q = FloatArray(4)
                    var rotationMatrix = FloatArray(9)
                    var orientation = FloatArray(3)

                    SensorManager.getQuaternionFromVector(q, e.values)
                    quaternion.value = Quaternion(q[1], q[2], q[3], q[0])

                    SensorManager.getRotationMatrixFromVector(rotationMatrix, e.values)
                    SensorManager.getOrientation(rotationMatrix, orientation)

                    eulerAngles.addAll(orientation.toList())
                }
            }

            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
                // nothing...
            }
        }
    }

    SensorListenerRegistration(sensorListener)
    
    Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        RotationText(eulerAngles)
        Divider(color= Color.Magenta, thickness=2.dp)
        AccelerationText(accelerometerReading)
        Divider(color= Color.Magenta, thickness=2.dp)
        AccelerationText(accelerometerReadingAdjusted)
    }
}

@Composable
fun AccelerationText(acc: SnapshotStateList<Float>){
    for (a in acc){
        Text("%.5f".format(a))
    }
}

@Composable
fun RotationText(angles: SnapshotStateList<Float>){
    for (angle in angles){
        Text("%.5f".format(angle))
    }
}

@Composable
fun SensorListenerRegistration(sensorListener: SensorEventListener){
    val sensorManager = LocalContext.current.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    val gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    val magneticFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)


    DisposableEffect(Unit) {
        sensorManager.registerListener(sensorListener, rotationSensor, SensorManager.SENSOR_DELAY_GAME)
        sensorManager.registerListener(sensorListener, gravitySensor, SensorManager.SENSOR_DELAY_GAME)
        //sensorManager.registerListener(sensorListener, magneticFieldSensor, SensorManager.SENSOR_DELAY_NORMAL)

        onDispose {
            sensorManager.unregisterListener(sensorListener)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RotationTheme {
        Text("Android")
    }
}