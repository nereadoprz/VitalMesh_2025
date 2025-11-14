package com.example.vitalmesh.profile

data class MilitaryProfile(
    val fullName: String = "",
    val rank: String = "",
    val serviceNumber: String = "",
    val unit: String = "",
    val email: String? = null,
    val phone: String? = null,
    val emergencyContactName: String? = null,
    val emergencyContactPhone: String? = null
)
