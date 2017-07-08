const { ipcRenderer } = require('electron');
const canvas = document.getElementById('memyc-canvas');
const ctx = canvas.getContext("2d");
const img = new Image();

ipcRenderer.on('memyc-frame-ready', (e, result) => {
    img.src = result.imgSrc;
    ctx.drawImage(img, 0, 0, canvas.width, canvas.height);
});

console.log(ipcRenderer)