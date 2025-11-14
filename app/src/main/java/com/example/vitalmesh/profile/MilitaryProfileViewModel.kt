package com.example.vitalmesh.profile

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MilitaryProfileViewModel : ViewModel() {

    private val _profile = MutableStateFlow(
        MilitaryProfile(
            fullName = "",
            rank = "",
            serviceNumber = "",
            unit = "",
            email = null,
            phone = null,
            emergencyContactName = null,
            emergencyContactPhone = null
        )
    )
    val profile: StateFlow<MilitaryProfile> = _profile

    // CAMBIO: Ahora empieza en false (NO editable)
    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun toggleEditing() {
        _isEditing.value = !_isEditing.value
    }

    fun updateProfile(updatedProfile: MilitaryProfile) {
        _profile.value = updatedProfile
    }

    // FUNCIÓN PARA GUARDAR EN FIREBASE
    fun saveProfileToFirebase(profile: MilitaryProfile) {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            db.collection("profiles").document(uid).set(profile)
                .addOnSuccessListener {
                    println("Perfil guardado exitosamente")
                }
                .addOnFailureListener { e ->
                    println("Error al guardar: ${e.message}")
                }
        }
    }

    // FUNCIÓN PARA CARGAR DESDE FIREBASE
    fun loadProfileFromFirebase() {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            db.collection("profiles").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val profile = document.toObject(MilitaryProfile::class.java)
                        if (profile != null) {
                            _profile.value = profile
                            // CAMBIO: Después de cargar, pone isEditing en false (NO editable)
                            _isEditing.value = false
                        }
                    } else {
                        // Si NO hay documento (primera vez), pone isEditing en true (editable)
                        _isEditing.value = true
                    }
                }
                .addOnFailureListener { e ->
                    println("Error al cargar: ${e.message}")
                    // Si hay error, permite editar
                    _isEditing.value = true
                }
        }
    }
}
