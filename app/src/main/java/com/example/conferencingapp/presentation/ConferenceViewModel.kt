package com.example.conferencingapp.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.conferencingapp.domain.repository.ConferenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConferenceViewModel @Inject constructor(
    private val repository: ConferenceRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ConferenceState())
    val state: StateFlow<ConferenceState> = _state.asStateFlow()

    private val _message = mutableStateOf("")
    val message: State<String> = _message
    private val _username = mutableStateOf("")
    val username: State<String> = _username
    private val _roomId = mutableStateOf("")
    val roomId: State<String> = _roomId

    init {
        observeMessage()
        observeParticipants()
        observeConnectionStatus()
    }

    fun onEvent(event: ConferenceEvents) {
        when (event) {
            is ConferenceEvents.OnUsernameChange -> {
                _username.value = event.username
            }

            is ConferenceEvents.OnRoomIdChange -> {
                _roomId.value = event.roomId
            }

            is ConferenceEvents.JoinRoom -> {
                joinRoom(event.roomId)
            }

            is ConferenceEvents.LeaveRoom -> {
                leaveRoom(event.roomId)
            }

            is ConferenceEvents.OnMessageChange -> {
                _message.value = event.message
            }

            is ConferenceEvents.SendMessage -> {
                sendMessage(event.message)
            }

            is ConferenceEvents.ConnectToServer -> {
                connectToServer(event.username)
            }
        }

    }

    private fun observeMessage() {
        viewModelScope.launch {
            repository.observeMessages().collect { message ->
                _state.value = _state.value.copy(
                    messages = _state.value.messages + message
                )
            }
        }
    }

    private fun observeParticipants() {
        viewModelScope.launch {
            repository.observeParticipants().collect { participants ->
                _state.value = _state.value.copy(
                    participants = participants
                )
            }
        }
    }

    private fun observeConnectionStatus() {
        viewModelScope.launch {
            repository.observeConnectionStatus().collect { isConnected ->
                _state.value = _state.value.copy(
                    isConnected = isConnected
                )
            }

        }
    }

    fun connectToServer(username: String) {
        repository.connectToServer(username)
    }

    fun disconnectFromServer() {
        repository.disconnectFromServer()
    }

    fun joinRoom(roomId: String) {
        repository.joinRoom(roomId)
    }

    fun leaveRoom(roomId: String) {
        repository.leaveRoom(roomId)
    }

    fun sendMessage(message: String) {
        repository.sendMessage(message)
    }


}