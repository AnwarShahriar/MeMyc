const http = require('http');
const server = http.createServer();
const io = require('socket.io')(server);
const { requireTaskPool } = require('electron-remote');
const processImage = require('./imageworker');

let frameListener = null;

io.on('connection', (socket) => {
    console.log('a user connected');
    socket.on('memyc-frame-android', frameHandler);
});
let count = 0;
const frameHandler = (payload) => {
    if (frameListener) {
        console.log(count++);
        frameListener(processImage(payload));
    }
}

server.listen(3000, () => {
    console.log('listening on *:3000');
});

module.exports = (listener) => {
    frameListener = listener;
}