package com.example.conferencingapp.domain.repository

import com.example.conferencingapp.domain.model.Message
import com.example.conferencingapp.domain.model.User
import kotlinx.coroutines.flow.Flow

interface ConferenceRepository {
    fun connectToServer(username: String)
    fun disconnectFromServer()
    fun joinRoom(roomId: String)
    fun leaveRoom(roomId: String)
    fun sendMessage(message: String)
    fun observeMessages(): Flow<List<Message>>
    fun observeParticipants(): Flow<List<User>>
    fun observeConnectionStatus(): Flow<Boolean>
}