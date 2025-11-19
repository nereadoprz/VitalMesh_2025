package com.example.vitalmesh.dashboard

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SensorViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance()
    private val sensorsRef = database.getReference("sensors/actual")
    private val historialRef = database.getReference("sensors/historial")

    // StateFlows para cada sensor
    private val _dht22Data = MutableStateFlow<DHT22Data?>(null)
    val dht22Data: StateFlow<DHT22Data?> = _dht22Data

    private val _gsrData = MutableStateFlow<GSRData?>(null)
    val gsrData: StateFlow<GSRData?> = _gsrData

    private val _imuData = MutableStateFlow<IMUData?>(null)
    val imuData: StateFlow<IMUData?> = _imuData

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    // StateFlow para datos históricos del GSR (últimas 50 muestras)
    private val _gsrHistoricalData = MutableStateFlow<List<GSRHistoricalPoint>>(emptyList())
    val gsrHistoricalData: StateFlow<List<GSRHistoricalPoint>> = _gsrHistoricalData

    private val listeners = mutableListOf<ValueEventListener>()

    init {
        startListeningToSensors()
        loadGSRHistoricalData()
    }

    private fun startListeningToSensors() {
        // Listener para DHT22
        val dht22Listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val data = snapshot.getValue(DHT22Data::class.java)
                    _dht22Data.value = data
                    _isLoading.value = false
                    Log.d("SensorViewModel", "DHT22 Data: ${data}")
                } catch (e: Exception) {
                    Log.e("SensorViewModel", "Error parsing DHT22 data", e)
                    _isLoading.value = false
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("SensorViewModel", "DHT22 listener cancelled", error.toException())
                _isLoading.value = false
            }
        }
        sensorsRef.child("DHT22").addValueEventListener(dht22Listener)
        listeners.add(dht22Listener)

        // Listener para GSR
        val gsrListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val data = snapshot.getValue(GSRData::class.java)
                    _gsrData.value = data
                    Log.d("SensorViewModel", "GSR Data: ${data}")
                } catch (e: Exception) {
                    Log.e("SensorViewModel", "Error parsing GSR data", e)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("SensorViewModel", "GSR listener cancelled", error.toException())
            }
        }
        sensorsRef.child("GSR").addValueEventListener(gsrListener)
        listeners.add(gsrListener)

        // Listener para IMU
        val imuListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val data = snapshot.getValue(IMUData::class.java)
                    _imuData.value = data
                    Log.d("SensorViewModel", "IMU Data: ${data}")
                } catch (e: Exception) {
                    Log.e("SensorViewModel", "Error parsing IMU data", e)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("SensorViewModel", "IMU listener cancelled", error.toException())
            }
        }
        sensorsRef.child("IMU").addValueEventListener(imuListener)
        listeners.add(imuListener)
    }

    private fun loadGSRHistoricalData() {
        // Obtener las últimas 50 muestras
        historialRef.orderByKey().limitToLast(50).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val dataPoints = mutableListOf<GSRHistoricalPoint>()
                    var sampleNumber = 1

                    for (childSnapshot in snapshot.children) {
                        val gsrSnapshot = childSnapshot.child("GSR")
                        val stressLevel = gsrSnapshot.child("stress_level_0_100").getValue(Double::class.java)

                        if (stressLevel != null) {
                            dataPoints.add(GSRHistoricalPoint(sampleNumber.toLong(), stressLevel))
                            sampleNumber++
                        }
                    }

                    _gsrHistoricalData.value = dataPoints
                    Log.d("SensorViewModel", "GSR Historical Data loaded: ${dataPoints.size} samples")
                } catch (e: Exception) {
                    Log.e("SensorViewModel", "Error loading GSR historical data", e)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("SensorViewModel", "GSR historical listener cancelled", error.toException())
            }
        })
    }

    override fun onCleared() {
        super.onCleared()
        listeners.forEach { listener ->
            sensorsRef.removeEventListener(listener)
        }
        listeners.clear()
        Log.d("SensorViewModel", "ViewModel cleared, listeners removed")
    }
}

// Modelo para puntos históricos del GSR
// Ahora "timestamp" es solo el número de muestra
data class GSRHistoricalPoint(
    val sampleNumber: Long,  // Número de muestra (1, 2, 3, ...)
    val stressLevel: Double
)
