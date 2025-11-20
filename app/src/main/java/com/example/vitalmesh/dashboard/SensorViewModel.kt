package com.example.vitalmesh.dashboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.delay
import kotlin.math.min

class SensorViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance()
    private val sensorsRef = database.getReference("sensors/actual")
    private val historialRef = database.getReference("sensors/historial")

    // StateFlows para datos actuales
    private val _dht22Data = MutableStateFlow<DHT22Data?>(null)
    val dht22Data: StateFlow<DHT22Data?> = _dht22Data

    private val _ecgData = MutableStateFlow<ECGData?>(null)
    val ecgData: StateFlow<ECGData?> = _ecgData

    private val _gpsData = MutableStateFlow<GPSData?>(null)
    val gpsData: StateFlow<GPSData?> = _gpsData

    private val _gsrData = MutableStateFlow<GSRData?>(null)
    val gsrData: StateFlow<GSRData?> = _gsrData

    private val _imuData = MutableStateFlow<IMUData?>(null)
    val imuData: StateFlow<IMUData?> = _imuData

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    // StateFlow para histórico de GSR (últimas 40 muestras)
    private val _gsrHistoricalData = MutableStateFlow<List<GSRHistoricalPoint>>(emptyList())
    val gsrHistoricalData: StateFlow<List<GSRHistoricalPoint>> = _gsrHistoricalData

    // StateFlow para indicador de actualización (luz azul parpadeante)
    private val _isUpdating = MutableStateFlow(false)
    val isUpdating: StateFlow<Boolean> = _isUpdating

    init {
        startPollingSensors()
        startPollingGSRHistory()
    }

    private fun startPollingSensors() {
        viewModelScope.launch {
            while (isActive) {
                pollAllSensors()
                delay(3000L)
            }
        }
    }

    private suspend fun pollAllSensors() {
        try {
            _isUpdating.value = true

            // Leer DHT22
            val dht22Snapshot = sensorsRef.child("DHT22").get().await()
            _dht22Data.value = dht22Snapshot.getValue(DHT22Data::class.java)
            Log.d("SensorViewModel", "DHT22 Data: ${_dht22Data.value}")

            // Leer ECG
            val ecgSnapshot = sensorsRef.child("ECG").get().await()
            _ecgData.value = ecgSnapshot.getValue(ECGData::class.java)
            Log.d("SensorViewModel", "ECG Data: ${_ecgData.value}")

            // Leer GPS
            val gpsSnapshot = sensorsRef.child("GPS").get().await()
            _gpsData.value = gpsSnapshot.getValue(GPSData::class.java)
            Log.d("SensorViewModel", "GPS Data: ${_gpsData.value}")

            // Leer GSR
            val gsrSnapshot = sensorsRef.child("GSR").get().await()
            _gsrData.value = gsrSnapshot.getValue(GSRData::class.java)
            Log.d("SensorViewModel", "GSR Data: ${_gsrData.value}")

            // Leer IMU
            val imuSnapshot = sensorsRef.child("IMU").get().await()
            _imuData.value = imuSnapshot.getValue(IMUData::class.java)
            Log.d("SensorViewModel", "IMU Data: ${_imuData.value}")

            _isLoading.value = false

            delay(500L)
            _isUpdating.value = false

            Log.d("SensorViewModel", "All sensors polled successfully")
        } catch (e: Exception) {
            Log.e("SensorViewModel", "Error polling sensors", e)
            _isLoading.value = false
            _isUpdating.value = false
        }
    }

    private fun startPollingGSRHistory() {
        viewModelScope.launch {
            while (isActive) {
                loadGSRHistoricalDataPolled()
                delay(3000L)
            }
        }
    }

    private suspend fun loadGSRHistoricalDataPolled() {
        try {
            val snapshot = historialRef
                .orderByKey()
                .limitToLast(100)
                .get()
                .await()

            val dataPoints = mutableListOf<GSRHistoricalPoint>()
            var sampleNumber = 1

            for (childSnapshot in snapshot.children) {
                val gsrSnapshot = childSnapshot.child("GSR")
                val stressLevel = gsrSnapshot
                    .child("stress_level_0_100")
                    .getValue(Double::class.java)

                if (stressLevel != null) {
                    dataPoints.add(
                        GSRHistoricalPoint(
                            sampleNumber = sampleNumber.toLong(),
                            stressLevel = stressLevel
                        )
                    )
                    sampleNumber++
                }
            }

            val window = min(40, dataPoints.size)
            val last40Samples = dataPoints.takeLast(window)

            _gsrHistoricalData.value = last40Samples
            Log.d(
                "SensorViewModel",
                "GSR Historical Data polled: ${last40Samples.size} samples"
            )
        } catch (e: Exception) {
            Log.e("SensorViewModel", "Error polling GSR historical data", e)
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("SensorViewModel", "ViewModel cleared")
    }

    // Centralized movement state logic
    fun determineMovementState(accelMagnitude: Double, gyroMagnitude: Double): String {
        return when {
            accelMagnitude < 0.5 -> "Free Fall"
            accelMagnitude in 0.9..1.1 && gyroMagnitude < 5 -> "Stationary"
            accelMagnitude in 0.7..1.3 && gyroMagnitude in 5.0..20.0 -> "Gentle Movement"
            gyroMagnitude in 20.0..50.0 -> "Moderate Rotation"
            gyroMagnitude > 50 -> "Fast Rotation"
            accelMagnitude > 2.0 -> "Intense Movement"
            accelMagnitude < 0.7 -> "Tilted Position"
            else -> "Motion Detected"
        }
    }

    fun determineMovementStatus(accelMagnitude: Double, gyroMagnitude: Double): SensorStatus {
        return when {
            accelMagnitude < 0.5 -> SensorStatus.CRITICAL
            gyroMagnitude > 50 -> SensorStatus.WARNING
            else -> SensorStatus.NORMAL
        }
    }

    fun determineMovementColor(accelMagnitude: Double, gyroMagnitude: Double): androidx.compose.ui.graphics.Color {
        return when {
            accelMagnitude < 0.5 -> androidx.compose.ui.graphics.Color.Red
            accelMagnitude in 0.9..1.1 && gyroMagnitude < 5 -> androidx.compose.ui.graphics.Color.Green
            accelMagnitude > 2.0 -> androidx.compose.ui.graphics.Color.Red
            gyroMagnitude > 50 -> androidx.compose.ui.graphics.Color.Yellow
            else -> androidx.compose.ui.graphics.Color.Yellow
        }
    }
}
