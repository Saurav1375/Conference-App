package com.example.conferencingapp.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ConferenceScreen(
    viewModel: ConferenceViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val username = viewModel.username.value
    val roomId = viewModel.roomId.value
    val message = viewModel.message.value


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = if (state.isConnected) "Connected" else "Disconnected",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (!state.isConnected) {
            OutlinedTextField(
                value = username,
                onValueChange = {
                    viewModel.onEvent(ConferenceEvents.OnUsernameChange(it))
                },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { viewModel.onEvent(ConferenceEvents.ConnectToServer(username)) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Connect")
            }
        } else {
            OutlinedTextField(
                value = roomId,
                onValueChange = {
                    viewModel.onEvent(ConferenceEvents.OnRoomIdChange(it))
                },
                label = { Text("Room ID") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = {
                    viewModel.onEvent(ConferenceEvents.JoinRoom(roomId))
                }) {
                    Text("Join Room")
                }
                Button(onClick = {
                    viewModel.onEvent(ConferenceEvents.LeaveRoom(roomId))
                }) {
                    Text("Leave Room")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Participants:", style = MaterialTheme.typography.titleMedium)
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(state.participants) { participant ->
                    Text(participant.name)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Messages:", style = MaterialTheme.typography.titleMedium)
            LazyColumn(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxWidth()
            ) {
                items(state.messages) { message ->
                    Card(
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(message.content)
                            Text(
                                "Sent by: ${message.senderId}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = message,
                    onValueChange = {
                        viewModel.onEvent(ConferenceEvents.OnMessageChange(it))
                    },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type a message") }
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        viewModel.onEvent(ConferenceEvents.SendMessage(message))
                        viewModel.onEvent(ConferenceEvents.OnMessageChange(""))
                    }
                ) {
                    Text("Send")
                }
            }
        }
    }
}