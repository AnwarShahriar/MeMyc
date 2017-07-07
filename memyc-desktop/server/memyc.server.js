const http = require('http');
const server = http.createServer();
var io = require('socket.io')(server);

io.on('connection', (socket) => {
    console.log('a user connected');
});

server.listen(3000, () => {
    console.log('listening on *:3000');
});