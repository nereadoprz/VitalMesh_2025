// HomeScreen.kt
package com.example.vitalmesh

//Para el Logout.
import androidx.credentials.CredentialManager
import androidx.credentials.ClearCredentialStateRequest

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.vitalmesh.R
import com.example.vitalmesh.navigation.NavigationGraph
//Profile files
import com.example.vitalmesh.profile.MilitaryProfileScreen
import com.example.vitalmesh.profile.MilitaryProfileViewModel

import com.example.vitalmesh.gpsmap.GPSScreen



data class TabItem(val label: String, val icon: ImageVector)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    user: User,
    monitoringActive: Boolean,
    authViewModel: AuthViewModel,
    credentialManager: CredentialManager,
    onLogout: () -> Unit // optional if you still use elsewhere
) {
    val navController = rememberNavController()

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf(
        TabItem("Dashboard", Icons.Filled.MilitaryTech),
        TabItem("GPS", Icons.Filled.Map),
        TabItem("Chat", Icons.Filled.Chat),
        TabItem("Profile", Icons.Filled.Person),
    )


    //Profile user var...
    val militaryProfileViewModel = remember { MilitaryProfileViewModel() }

    // Cargar el perfil cuando se abre HomeScreen
    LaunchedEffect(Unit) {
        militaryProfileViewModel.loadProfileFromFirebase()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(id = R.color.military_green)
                ),
                title = {
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .size(65.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(3.dp, colorResource(id = R.color.military_green), CircleShape)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.logo_vitalmesh),
                                contentDescription = "VitalMesh Logo",
                                modifier = Modifier
                                    .size(60.dp)
                                    .align(Alignment.Center)
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(end = 32.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(if (monitoringActive) Color.Green else Color.Red)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = if (monitoringActive) "Monitoring Active" else "Monitoring Inactive",
                                style = MaterialTheme.typography.titleMedium,
                                color = colorResource(id = R.color.military_khaki)
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .background(colorResource(id = R.color.military_olive))
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier
                .height(16.dp)
                .fillMaxWidth()
                .background(colorResource(id = R.color.military_green))
            )

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = colorResource(id = R.color.military_green),
                contentColor = colorResource(id = R.color.military_khaki),
            ) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = {
                            Icon(tab.icon, contentDescription = tab.label, tint = colorResource(id = R.color.military_khaki))
                        },
                        text = {
                            Text(tab.label, color = colorResource(id = R.color.military_khaki))
                        },
                        selectedContentColor = colorResource(id = R.color.military_khaki),
                        unselectedContentColor = colorResource(id = R.color.military_khaki).copy(alpha = 0.7f)
                    )
                }
            }

            when (selectedTab) {
                0 -> {
                    NavigationGraph(navController)
                }
                1 -> GPSScreen()  // Llama el mapa del sensor GPS
                2 -> ChatContent(user)
                3 -> MilitaryProfileScreen(
                    viewModel = militaryProfileViewModel,
                    onLogout = {
                        authViewModel.signOut(credentialManager)
                    },
                    navigateToPrivacyPolicy = {
                        // Provide your navigation code here
                    }
                )
            }
        }
    }
}

@Composable
fun GPSContent() {
    Column(
        Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Text("Real-time location map and route", color = colorResource(id = R.color.military_khaki))
    }
}

@Composable
fun ChatContent(user: User) {
    Column(
        Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Text("Unit and emergency communication chat", color = colorResource(id = R.color.military_khaki))
    }
}

@Composable
fun ProfileContent(user: User, onLogout: () -> Unit) {
    Column(
        Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Text(
            "Name: ${user.name}",
            color = colorResource(id = R.color.military_khaki),
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(16.dp))
        Text("Fill in/edit personal details and calibration", color = colorResource(id = R.color.military_khaki))
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("Sign out")
        }
    }
}

data class User(val name: String)
