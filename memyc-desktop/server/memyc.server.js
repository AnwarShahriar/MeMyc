const { ipcRenderer } = require('electron');
const http = require('http');
const server = http.createServer();
const io = require('socket.io')(server);
const { requireTaskPool } = require('electron-remote');
const processImage = requireTaskPool(require.resolve('./imageworker'));

io.on('connection', (socket) => {
    console.log('a user connected');
});

io.on('memyc-frame-android', (payload) => {
    processImage(payload).then((result) => {
        ipcRenderer.send('memyc-frame-ready', result);
    });
});

server.listen(3000, () => {
    console.log('listening on *:3000');
});