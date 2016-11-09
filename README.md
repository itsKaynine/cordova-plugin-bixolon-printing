Cordova Bixolon Printer Plugin
===============================

Cordova plugin for Bixolon mobile printers using the official UPOS compliant API.

> **NOTE**
> 
> For simplicity, only image (bitmap) printing are allowed.


### Supported Platforms

- Android
- iOS (TODO)

## Install

### Cordova CLI

```
$ cordova plugin add https://github.com/itsKaynine/cordova-plugin-bixolon-printing.git
```

### Phonegap CLI

```
$ phonegap local plugin add https://github.com/itsKaynine/cordova-plugin-bixolon-printing.git
```

## Global Options

```javascript
cordova.plugins.BixolonPrinting.printConfig = {
  lineFeed: 3,
  formFeed: false
};
```

## Usage

### Connect to printer

```javascript
cordova.plugins.BixolonPrinting.connect(successCallback, errorCallback, {
    logicalDeviceName: String,
    deviceBus: int, // cordova.plugins.BixolonPrinting.DEVICE_BUS
    address: String,
    secure: Boolean
});
```

### Print bitmap image

```javascript
cordova.plugins.BixolonPrinting.printBitmap(successCallback, errorCallback, {
    base64Image: String,
    width: int,
    brightness int, // 0 to 100 (Bixolon recommeded 13 to 88)
    alignment: int // cordova.plugins.BixolonPrinting.ALIGNMENT
});
```

### Disconnect from printer

```javascript
cordova.plugins.BixolonPrinting.disconnect(successCallback, errorCallback);
```

## Example

### Printing through Wi-Fi

```javascript
// Put your base64 encoded image string here
var b64Image = "<Base64 Encoded Image>";

function alertError(err) {
    alert("ERROR: " + err);
};

// Define connection to printer
var connection = {
    logicalDeviceName: "SPP-R310",
    deviceBus: cordova.plugins.BixolonPrinting.DEVICE_BUS.WIFI,
    address: "10.0.1.21",
    secure: false
};

// Connect to printer
cordova.plugins.BixolonPrinting.connect(
    function(res) {

        // Printing options
        var printObj = {
            base64Image: b64Image
            width: 500,
            brightness: 50,
            alignment: cordova.plugins.BixolonPrinting.ALIGNMENT.CENTER,
        };

        // Perform printing
        cordova.plugins.BixolonPrinting.printBitmap(
            function(res) { 
                alert("SUCCESS"); 

                // ... Do other things or just disconnect()
            }, 
            alertError, 
            printObj
        );
    },
    alertError,
    connection
);
```

## Appendix

### Constants

#### Device Bus

```javascript
{
    BLUETOOTH: 0,
    ETHERNET: 1,
    USB: 2,
    WIFI: 3,
    WIFI_DIRECT: 4
};
```

#### Alignment

```javascript
{
    ASIS: -11,
    LEFT: -1,
    CENTER: -2,
    RIGHT: -3
};
```

### Device Bus Address

| Device bus    | Address           |
|:-------------:|:-----------------:|
| BLUETOOTH     | BT MAC address    |
| ETHERNET      | IP address        |
| USB           | None              |
| WIFI          | IP address        |
| WIFI_DIRECT   | WLAN MAC address  |

## Credits
- [Bixolon][bixolon] - Bixolon official site
- [Base64 Image][base64-image-de] - Used many times while testing
- [alfonsovinti/cordova-plugin-bixolon-print][bixolon-print] - Plugin inspired by

## License

This software is released under the [Apache 2.0 License][apache2_license].

[bixolon]: http://www.bixolon.com
[base64-image-de]: https://www.base64-image.de
[bixolon-print]: https://github.com/alfonsovinti/cordova-plugin-bixolon-print
[apache2_license]: http://opensource.org/licenses/Apache-2.0