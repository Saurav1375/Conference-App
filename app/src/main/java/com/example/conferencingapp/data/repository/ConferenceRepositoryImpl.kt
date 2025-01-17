package com.example.conferencingapp.data.repository

import com.example.conferencingapp.domain.model.Message
import com.example.conferencingapp.domain.model.User
import com.example.conferencingapp.domain.repository.ConferenceRepository
import com.example.conferencingapp.utils.Constants
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

class ConferenceRepositoryImpl @Inject constructor(): ConferenceRepository {
    private var socket : Socket? = null

    private val messages = MutableStateFlow<List<Message>>(emptyList())
    private val participants = MutableStateFlow<List<User>>(emptyList())
    private val isConnected = MutableStateFlow(false)

    override fun connectToServer(username: String) {
        try {
            val opts = IO.Options()
            opts.transports = arrayOf("websocket") // Disable long-polling
            socket = IO.socket(Constants.SERVER_URL, opts).apply {
                connect()
                emit("join", username)
            }
            socketListener()

        } catch (e : Exception) {
            e.printStackTrace()
        }
    }

    private fun socketListener () {
        socket?.apply {
            on(Socket.EVENT_CONNECT) {
                isConnected.value = true
            }

            on(Socket.EVENT_DISCONNECT) {
                isConnected.value = false
            }

            on(Socket.EVENT_CONNECT_ERROR) { args ->
                println("Socket Error: ${args[0]}")
            }

            on("message") { args ->
                val messageData = args[0] as JSONObject
                val message = Message(
                    id = messageData.getString("id"),
                    senderId = messageData.getString("senderId"),
                    content = messageData.getString("content"),
                    timestamp = messageData.getLong("timestamp")
                )
                messages.value += message
            }

            on("participants") { args ->
                val participantsArray = args[0] as JSONArray
                val userList = mutableListOf<User>()
                for (i in 0 until participantsArray.length()) {
                    val participantData = participantsArray.getJSONObject(i)
                    val user = User(
                        id = participantData.getString("id"),
                        name = participantData.getString("name"),
                        isInCall = participantData.getBoolean("isInCall")
                    )
                    userList.add(user)
                }
                participants.value = userList
            }
        }
    }

    override fun disconnectFromServer() {
        socket?.disconnect()
        socket = null
    }

    override fun joinRoom(roomId: String) {
        socket?.emit("joinRoom", JSONObject().put("roomId", roomId))
    }

    override fun leaveRoom(roomId: String) {
        socket?.emit("leaveRoom", JSONObject().put("roomId", roomId))
    }

    override fun sendMessage(message: String) {
        socket?.emit("message", JSONObject().put("content", message))
    }

    override fun observeMessages(): Flow<List<Message>> =  messages

    override fun observeParticipants(): Flow<List<User>> = participants

    override fun observeConnectionStatus(): Flow<Boolean> = isConnected

}