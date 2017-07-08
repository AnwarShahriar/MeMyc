const processImage = (imageData) => {
    let binary = '';
    let bytes = new Uint8Array(data);
    let len = bytes.byteLength;

    for (let i = 0; i < len; i++) {
        binary += String.fromCharCode(bytes[i]);
    }

    return {
        imgSrc: 'data:image/jpeg;base64,' + new Buffer(binary).toString('base64')
    }
}

module.exports = processImage;