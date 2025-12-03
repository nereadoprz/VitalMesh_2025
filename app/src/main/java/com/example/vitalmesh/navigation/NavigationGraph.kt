// ui/navigation/NavigationGraph.kt
package com.example.vitalmesh.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.vitalmesh.dashboard.DashboardScreen
import com.example.vitalmesh.dashboard.GPSDetailScreen
import com.example.vitalmesh.dashboard.IMUDetailScreen
import com.example.vitalmesh.dashboard.GSRDetailScreen
import com.example.vitalmesh.dashboard.HeartRateDetailScreen

// Definir las rutas como objetos para mayor seguridad
object NavigationDestinations {
    const val DASHBOARD = "dashboard"
    const val GPS_DETAIL = "gps_detail"
    const val IMU_DETAIL = "imu_detail"
    const val GSR_DETAIL = "gsr_detail"
    const val HEARTRATE_DETAIL = "heartrate_detail"
}

// ✅ NUEVO: Typealias para el callback
typealias OnNavigateToGPSTab = () -> Unit

@Suppress("UNUSED_PARAMETER")
@Composable
fun NavigationGraph(
    navController: NavHostController,
    onNavigateToGPSTab: OnNavigateToGPSTab = {}  // ✅ NUEVO PARÁMETRO
) {
    NavHost(
        navController = navController,
        startDestination = NavigationDestinations.DASHBOARD  // Pantalla inicial
    ) {
        // Pantalla principal: Dashboard
        composable(NavigationDestinations.DASHBOARD) {
            DashboardScreen(
                onSensorClick = { sensorName ->
                    // Navegar a la pantalla detallada según el sensor
                    when (sensorName) {
                        "GPS" -> navController.navigate(NavigationDestinations.GPS_DETAIL)
                        "IMU" -> navController.navigate(NavigationDestinations.IMU_DETAIL)
                        "GSR" -> navController.navigate(NavigationDestinations.GSR_DETAIL)
                        "HeartRate" -> navController.navigate(NavigationDestinations.HEARTRATE_DETAIL)
                    }
                }
            )
        }

        // Pantalla detallada: GPS
        composable(NavigationDestinations.GPS_DETAIL) {
            GPSDetailScreen(
                onBackClick = { navController.popBackStack() },
                onRequestFullscreenMap = onNavigateToGPSTab  // ✅ AGREGADO: Pasa el callback
            )
        }

        // Pantalla detallada: IMU
        composable(NavigationDestinations.IMU_DETAIL) {
            IMUDetailScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // Pantalla detallada: GSR
        composable(NavigationDestinations.GSR_DETAIL) {
            GSRDetailScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // Pantalla detallada: Heart Rate
        composable(NavigationDestinations.HEARTRATE_DETAIL) {
            HeartRateDetailScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
