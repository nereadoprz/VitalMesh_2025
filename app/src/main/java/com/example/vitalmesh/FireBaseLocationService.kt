package com.example.vitalmesh

import android.util.Log
import com.google.firebase.database.FirebaseDatabase

object FirebaseLocationService {
    private val database = FirebaseDatabase.getInstance()
    // Ajusta la ruta en la base de datos según cómo almacenes la ubicación

    fun getUserCurrentLocation(onLocationReceived: (latitude: Double, longitude: Double) -> Unit) {
        database.reference
            .child("userLocations")
            .child("current")
            .get()
            .addOnSuccessListener { snapshot ->
                val lat = snapshot.child("latitude").getValue(Double::class.java) ?: 40.7128
                val lng = snapshot.child("longitude").getValue(Double::class.java) ?: -74.0060
                onLocationReceived(lat, lng)
                Log.d("FirebaseLocationService", "Ubicación: $lat, $lng")
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseLocationService", "Error: ${e.message}")
                onLocationReceived(40.7128, -74.0060) // fallback: Nueva York
            }
    }
}
