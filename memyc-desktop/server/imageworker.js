const processImage = (imageData) => {
    var uInt8Array = imageData;
    var i = uInt8Array.length;
    var binaryString = [i];
    while (i--) {
        binaryString[i] = String.fromCharCode(uInt8Array[i]);
    }
    var data = binaryString.join('');
    let src = 'data:image/jpeg;base64,' + new Buffer(data, 'binary').toString('base64')

    return {
        imgSrc: src
    }
}

module.exports = processImage;