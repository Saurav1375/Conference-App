package com.example.conferencingapp.domain.model

data class Message(
    val id: String,
    val senderId: String,
    val content: String,
    val timestamp: Long
)