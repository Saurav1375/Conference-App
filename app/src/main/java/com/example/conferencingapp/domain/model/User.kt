package com.example.conferencingapp.domain.model

data class User(
    val id: String,
    val name: String,
    val isInCall: Boolean = false
)
