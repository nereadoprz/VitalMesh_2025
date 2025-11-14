package com.example.vitalmesh.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.vitalmesh.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MilitaryProfileScreen(
    viewModel: MilitaryProfileViewModel,
    onLogout: suspend () -> Unit,
    navigateToPrivacyPolicy: () -> Unit
) {
    val profile by viewModel.profile.collectAsState()
    val isEditing by viewModel.isEditing.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    // Cargar el perfil cuando se abre la pantalla
    LaunchedEffect(Unit) {
        viewModel.loadProfileFromFirebase()
    }


    var fullName by remember { mutableStateOf(profile.fullName) }
    var rank by remember { mutableStateOf(profile.rank) }
    var serviceNumber by remember { mutableStateOf(profile.serviceNumber) }
    var unit by remember { mutableStateOf(profile.unit) }
    var email by remember { mutableStateOf(profile.email ?: "") }
    var phone by remember { mutableStateOf(profile.phone ?: "") }
    var emergencyContactName by remember { mutableStateOf(profile.emergencyContactName ?: "") }
    var emergencyContactPhone by remember { mutableStateOf(profile.emergencyContactPhone ?: "") }

    Column(
        Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Text(
            "Military Profile",
            color = colorResource(id = R.color.military_khaki),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(16.dp))

        if (isEditing) {
            OutlinedTextField(fullName, { fullName = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(rank, { rank = it }, label = { Text("Rank") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(serviceNumber, { serviceNumber = it }, label = { Text("Service Number") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(unit, { unit = it }, label = { Text("Unit") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(email, { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(phone, { phone = it }, label = { Text("Phone") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(emergencyContactName, { emergencyContactName = it }, label = { Text("Emergency Contact Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(emergencyContactPhone, { emergencyContactPhone = it }, label = { Text("Emergency Contact Phone") }, modifier = Modifier.fillMaxWidth())

            Spacer(Modifier.height(16.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = {
                    viewModel.toggleEditing()
                    // Reset fields to stored profile values if cancelled
                    fullName = profile.fullName
                    rank = profile.rank
                    serviceNumber = profile.serviceNumber
                    unit = profile.unit
                    email = profile.email ?: ""
                    phone = profile.phone ?: ""
                    emergencyContactName = profile.emergencyContactName ?: ""
                    emergencyContactPhone = profile.emergencyContactPhone ?: ""
                }) {
                    Text("Cancel")
                }
                Spacer(Modifier.width(8.dp))

                Button(onClick = {
                    val updatedProfile = MilitaryProfile(
                        fullName = fullName,
                        rank = rank,
                        serviceNumber = serviceNumber,
                        unit = unit,
                        email = email.ifBlank { null },
                        phone = phone.ifBlank { null },
                        emergencyContactName = emergencyContactName.ifBlank { null },
                        emergencyContactPhone = emergencyContactPhone.ifBlank { null }
                    )
                    viewModel.updateProfile(updatedProfile)
                    viewModel.saveProfileToFirebase(updatedProfile)  // <-- ESTA LÍNEA ES LA NUEVA
                    viewModel.toggleEditing()
                }) {
                    Text("Save")
                }

            }
        } else {
            // Non-editable profile display
            Text("Full Name: ${profile.fullName}", color = colorResource(id = R.color.military_khaki))
            Text("Rank: ${profile.rank}", color = colorResource(id = R.color.military_khaki))
            Text("Service Number: ${profile.serviceNumber}", color = colorResource(id = R.color.military_khaki))
            Text("Unit: ${profile.unit}", color = colorResource(id = R.color.military_khaki))
            Text("Email: ${profile.email ?: "Not set"}", color = colorResource(id = R.color.military_khaki))
            Text("Phone: ${profile.phone ?: "Not set"}", color = colorResource(id = R.color.military_khaki))
            Text("Emergency Contact Name: ${profile.emergencyContactName ?: "Not set"}", color = colorResource(id = R.color.military_khaki))
            Text("Emergency Contact Phone: ${profile.emergencyContactPhone ?: "Not set"}", color = colorResource(id = R.color.military_khaki))

            Spacer(Modifier.height(8.dp))

            Button(onClick = { viewModel.toggleEditing() }) {
                Text("Edit Profile")
            }
        }

        Spacer(Modifier.height(24.dp))

        Divider(color = colorResource(id = R.color.military_khaki).copy(alpha = 0.5f), thickness = 1.dp)

        Spacer(Modifier.height(16.dp))

        Text(
            "Privacy Policy",
            color = colorResource(id = R.color.military_khaki),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { navigateToPrivacyPolicy() }
                .padding(8.dp)
        )

        Spacer(Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.card_background))
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    "Customer Support",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.military_khaki)
                )
                Spacer(Modifier.height(8.dp))
                Text("VitalMesh Inc.", color = colorResource(id = R.color.military_khaki))
                Text("Email: support@vitalmesh.com", color = colorResource(id = R.color.military_khaki))
                Text("Phone: +1 234 567 8900", color = colorResource(id = R.color.military_khaki))
            }
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    onLogout()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign Out")
        }
    }
}
