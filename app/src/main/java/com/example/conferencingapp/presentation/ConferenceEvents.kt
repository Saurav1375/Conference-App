package com.example.conferencingapp.presentation

sealed class ConferenceEvents {
    class OnUsernameChange(val username: String) : ConferenceEvents()
    class OnRoomIdChange(val roomId: String) : ConferenceEvents()
    class JoinRoom(val roomId: String) : ConferenceEvents()
    class LeaveRoom(val roomId: String) : ConferenceEvents()
    class OnMessageChange(val message: String) : ConferenceEvents()
    class SendMessage(val message: String) : ConferenceEvents()
    class ConnectToServer(val username: String) : ConferenceEvents()




}