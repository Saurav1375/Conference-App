const express = require('express');
const http = require('http');
const socketIo = require('socket.io');
const cors = require('cors');
const { v4: uuidv4 } = require('uuid');

const app = express();
app.use(cors());

const server = http.createServer(app);
const io = socketIo(server, {
    cors: { origin: "*" }
});

const rooms = new Map();
const users = new Map();

io.on('connection', (socket) => {
    console.log('Client connected:', socket.id);


    socket.on('join', (data) => {
        const username = data.username;
        users.set(socket.id, {
            id: socket.id,
            name: username,
            isInCall: false
        });

    
        io.emit('participants', Array.from(users.values()));
    });

    socket.on('joinRoom', (data) => {
        const roomId = data.roomId;
        const user = users.get(socket.id);

        if (!rooms.has(roomId)) {
            rooms.set(roomId, new Set());
        }

        socket.join(roomId);
        rooms.get(roomId).add(socket.id);
        user.isInCall = true;

        io.to(roomId).emit('participants',
            Array.from(rooms.get(roomId))
                .map(id => users.get(id))
        );
    });


    socket.on('leaveRoom', (data) => {
        const roomId = data.roomId;
        const user = users.get(socket.id);

        if (rooms.has(roomId)) {
            socket.leave(roomId);
            rooms.get(roomId).delete(socket.id);
            user.isInCall = false;

        
            if (rooms.get(roomId).size === 0) {
                rooms.delete(roomId);
            } else {
            
                io.to(roomId).emit('participants',
                    Array.from(rooms.get(roomId))
                        .map(id => users.get(id))
                );
            }
        }
    });

 
    socket.on('message', (data) => {
        const user = users.get(socket.id);
        const message = {
            id: uuidv4(),
            senderId: socket.id,
            content: data,
            timestamp: Date.now()
        };

    
        socket.rooms.forEach(roomId => {
            if (roomId !== socket.id) { 
                io.to(roomId).emit('message', message);
            }
        });
    });

    
    socket.on('disconnect', () => {
        const user = users.get(socket.id);
        if (user) {
    
            rooms.forEach((participants, roomId) => {
                if (participants.has(socket.id)) {
                    participants.delete(socket.id);
                    if (participants.size === 0) {
                        rooms.delete(roomId);
                    } else {
                        io.to(roomId).emit('participants',
                            Array.from(participants)
                                .map(id => users.get(id))
                        );
                    }
                }
            });

            users.delete(socket.id);
            io.emit('participants', Array.from(users.values()));
        }
        console.log('Client disconnected:', socket.id);
    });
});

const PORT = process.env.PORT || 3000;
server.listen(PORT, () => {
    console.log(`Server running on port ${PORT}`);
});