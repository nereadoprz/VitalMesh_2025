package com.example.vitalmesh

import android.util.Log
import com.google.firebase.database.FirebaseDatabase

object FirebaseLocationService {
    private val database = FirebaseDatabase.getInstance()

    fun getUserCurrentLocation(onLocationReceived: (latitude: Double, longitude: Double, alt: Double?) -> Unit) {
        database.reference
            .child("sensors")
            .child("actual")
            .child("GPS")
            .get()
            .addOnSuccessListener { snapshot ->
                val lat = snapshot.child("latitude").getValue(Double::class.java) ?: 40.7128
                val lng = snapshot.child("longitude").getValue(Double::class.java) ?: -74.0060
                val alt = snapshot.child("alt").getValue(Double::class.java)
                onLocationReceived(lat, lng, alt)
                Log.d("FirebaseLocationService", "Ubicación: $lat, $lng, alt: $alt")
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseLocationService", "Error: ${e.message}")
                onLocationReceived(40.7128, -74.0060, null) // fallback: Nueva York
            }
    }
}
