package com.example.vitalmesh.dashboard

import com.google.firebase.database.PropertyName

// Modelo para DHT22
data class DHT22Data(
    @get:PropertyName("humidity_%")
    @set:PropertyName("humidity_%")
    var humidity_percent: Double = 0.0,

    @get:PropertyName("temperature_C")
    @set:PropertyName("temperature_C")
    var temperature_C: Double = 0.0
)

// Modelo para ECG (Heart Rate)
data class ECGData(
    @get:PropertyName("heart_rate_bpm")
    @set:PropertyName("heart_rate_bpm")
    var heart_rate_bpm: Int = 0
)

// Modelo para GPS
data class GPSData(
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var alt: Double = 0.0,
    @get:PropertyName("timestamp_ms")
    @set:PropertyName("timestamp_ms")
    var timestamp_ms: Long = 0L
)

// Modelo para GSR
data class GSRData(
    @get:PropertyName("conductance_uS")
    @set:PropertyName("conductance_uS")
    var conductance_uS: Double = 0.0,

    @get:PropertyName("resistance_kOhm")
    @set:PropertyName("resistance_kOhm")
    var resistance_kOhm: Double = 0.0,

    @get:PropertyName("stress_level_0_100")
    @set:PropertyName("stress_level_0_100")
    var stress_level_0_100: Double = 0.0
)

// Modelo para IMU - Acelerómetro
data class AccelData(
    var x: Double = 0.0,
    var y: Double = 0.0,
    var z: Double = 0.0
)

// Modelo para IMU - Giroscopio
data class GyroData(
    var x: Double = 0.0,
    var y: Double = 0.0,
    var z: Double = 0.0
)

// Modelo completo del IMU
data class IMUData(
    @get:PropertyName("accel_g")
    @set:PropertyName("accel_g")
    var accel_g: AccelData = AccelData(),

    @get:PropertyName("gyro_deg_s")
    @set:PropertyName("gyro_deg_s")
    var gyro_deg_s: GyroData = GyroData(),

    @get:PropertyName("timestamp_ms")
    @set:PropertyName("timestamp_ms")
    var timestamp_ms: Long = 0L
)

// Modelo para puntos históricos del GSR
data class GSRHistoricalPoint(
    val sampleNumber: Long,
    val stressLevel: Double
)
