package com.example.conferencingapp.presentation

import com.example.conferencingapp.domain.model.Message
import com.example.conferencingapp.domain.model.User

data class ConferenceState(
    val isConnected: Boolean = false,
    val messages: List<Message> = emptyList(),
    val participants: List<User> = emptyList()
)