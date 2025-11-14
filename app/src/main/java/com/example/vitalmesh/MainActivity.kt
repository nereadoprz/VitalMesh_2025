package com.example.vitalmesh

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material3.MaterialTheme
import androidx.credentials.CredentialManager
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.vitalmesh.HomeScreen
import com.example.vitalmesh.User


class MainActivity : ComponentActivity() {

    // Obtenemos la instancia del ViewModel (Android lo crea automáticamente)
    private val authViewModel: AuthViewModel by viewModels()

    // Para gestionar credenciales de Google
    private lateinit var credentialManager: CredentialManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Agregar una variable de estado para controlar qué pantalla mostrar
        var showPhoneLogin by mutableStateOf(false)


        // Creamos el Credential Manager
        credentialManager = CredentialManager.create(this)

        // Establecemos el contenido Compose
        setContent {
            MaterialTheme {
                // Observamos el estado de autenticación
                val authState by authViewModel.authState.collectAsState()

                // Mostramos diferente pantalla según el estado
                when (authState) {
                    // Si el login fue exitoso, ir a HomeScreen
                    is AuthState.Success -> {
                        HomeScreen(
                            user = User(name = "Ejemplo Militar"),
                            monitoringActive = true, // o false según el estado real
                            authViewModel = authViewModel,
                            credentialManager = credentialManager,
                            onLogout = { launchLogout() } // Optional, or use as needed
                        )
                    }

                    is AuthState.CodeSent -> {
                        PhoneVerificationScreen(
                            onVerifyCode = { code ->
                                authViewModel.verifyCode(code)
                            }
                        )
                    }

                    // En cualquier otro caso (Idle, Loading, Error), mostrar LoginScreen
                    else -> {
                        LoginScreen(
                            onGoogleLogin = { launchGoogleSignIn() },
                            onPhoneLogin = { launchPhoneLogin() }
                        )
                    }
                }
            }
        }
    }

    private fun launchGoogleSignIn() {
        lifecycleScope.launch {
            authViewModel.signInWithGoogle(
                credentialManager = credentialManager,
                activity = this@MainActivity,
                webClientId = getString(R.string.default_web_client_id)
            )
        }
    }

    private fun launchPhoneLogin() {
        // Navegamos a PhoneLoginScreen
        // Por ahora, abrimos un dialog o pantalla de ingreso
        // Lo haremos con un simple estado por ahora
    }

    private fun launchLogout() {
        lifecycleScope.launch {
            authViewModel.signOut(credentialManager)
        }
    }

}
