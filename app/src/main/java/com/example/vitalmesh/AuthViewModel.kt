package com.example.vitalmesh

import android.app.Activity
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

// Define los posibles estados de autenticación
sealed class AuthState {
    object Idle : AuthState()  // Estado inicial, no está haciendo nada
    object Loading : AuthState()  // Está procesando (mostramos un loading)
    data class Success(val user: FirebaseUser?) : AuthState()  // Login exitoso, guardamos el usuario
    data class Error(val message: String) : AuthState()  // Hubo un error
    data class CodeSent(val verificationId: String) : AuthState()  // Se envió código SMS
}

class AuthViewModel : ViewModel() {

    // Firebase Authentication instance
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // StateFlow para que MainActivity observe los cambios de estado
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    // Guardamos el verification ID del SMS para usarlo después
    private var verificationId: String? = null

    init {
        // Al iniciar, verificamos si ya hay un usuario autenticado
        auth.currentUser?.let {
            _authState.value = AuthState.Success(it)
        }
    }

    // ============ GOOGLE SIGN-IN ============

    // Esta función es suspend porque llama a APIs asincrónicas
    suspend fun signInWithGoogle(
        credentialManager: CredentialManager,
        activity: Activity,
        webClientId: String
    ) {
        try {
            _authState.value = AuthState.Loading

            val googleIdOption = GetGoogleIdOption.Builder()
                .setServerClientId(webClientId)
                .setFilterByAuthorizedAccounts(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                request = request,
                context = activity
            )

            handleGoogleSignInResult(result)

        } catch (e: Exception) {
            android.util.Log.e("AuthViewModel", "Google Sign-In Error: ${e.message}", e)
            _authState.value = AuthState.Error(e.message ?: "Google Sign-In error")
        }
    }

    // Procesa el resultado de Google Sign-In
    private suspend fun handleGoogleSignInResult(result: GetCredentialResponse) {
        val credential = result.credential

        // Verificamos que sea un Google ID Token válido
        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {

            // Extraemos el token
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val idToken = googleIdTokenCredential.idToken

            // Creamos una credencial de Firebase usando el token de Google
            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)

            // Intentamos autenticarnos con Firebase
            val authResult = auth.signInWithCredential(firebaseCredential).await()

            // Si llega aquí, el login fue exitoso
            _authState.value = AuthState.Success(authResult.user)
        } else {
            // Si la credencial no es válida
            _authState.value = AuthState.Error("Invalid credential")
        }
    }

    // ============ PHONE AUTHENTICATION ============

    // Envía el código de verificación por SMS
    fun sendVerificationCode(phoneNumber: String, activity: Activity) {
        _authState.value = AuthState.Loading

        // Callbacks para manejar los eventos de Phone Auth
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            // Si el dispositivo verifica automáticamente (algunos casos especiales)
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithPhoneCredential(credential)
            }

            // Si hay error en la verificación
            override fun onVerificationFailed(e: FirebaseException) {
                _authState.value = AuthState.Error(e.message ?: "Verification failed")
            }

            // Se envió el código correctamente, ahora el usuario debe ingresarlo
            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // Guardamos el ID para usarlo cuando el usuario ingrese el código
                this@AuthViewModel.verificationId = verificationId
                // Cambiamos a estado CodeSent para que MainActivity muestre pantalla de ingreso de código
                _authState.value = AuthState.CodeSent(verificationId)
            }
        }

        // Configuramos las opciones de Phone Auth
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)  // Ej: +34612345678
            .setTimeout(60L, TimeUnit.SECONDS)  // Timeout de 60 segundos
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()

        // Enviamos el SMS
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    // Verifica el código que el usuario ingresó
    fun verifyCode(code: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading

                // Creamos la credencial con el verification ID y el código
                val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
                signInWithPhoneCredential(credential)

            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Invalid code")
            }
        }
    }

    // Autentica con la credencial de teléfono
    private fun signInWithPhoneCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Login exitoso
                    _authState.value = AuthState.Success(task.result?.user)
                } else {
                    // Error en la autenticación
                    _authState.value = AuthState.Error(
                        task.exception?.message ?: "Authentication error"
                    )
                }
            }
    }

    // ============ SIGN OUT ============

    // Cierra la sesión del usuario
    suspend fun signOut(credentialManager: CredentialManager) {
        auth.signOut()

        try {
            // Limpiamos las credenciales guardadas en el dispositivo
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
        } catch (e: Exception) {
            // Ignoramos errores aquí
        }

        _authState.value = AuthState.Idle
    }
}
