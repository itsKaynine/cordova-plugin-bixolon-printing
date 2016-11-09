var Loader = function (require, exports, module) {
	var exec = require('cordova/exec');

	// Constructor
	var BixolonPrinting = function () {
        this.DEVICE_BUS = {
            BLUETOOTH: 0,
            ETHERNET: 1,
            USB: 2,
            WIFI: 3,
            WIFI_DIRECT: 4
        };

        this.ALIGNMENT = {
            ASIS: -11,
            LEFT: -1,
            CENTER: -2,
            RIGHT: -3
        };

        this.printConfig = {
            lineFeed: 3,
            formFeed: false
        };
	};

	BixolonPrinting.prototype._isObject = function (obj) {
        return typeof obj === 'object' && !!obj;
    };
    BixolonPrinting.prototype._isFunction = function (obj) {
        return typeof obj === 'function';
    };
    BixolonPrinting.prototype._isUndefined = function (obj) {
        return typeof obj === 'undefined' || obj === null;
    };
    BixolonPrinting.prototype._extend  = function(out) {
        out = out || {};

        for (var i = 1; i < arguments.length; i++) {
            if (!arguments[i])
                continue;

            for (var key in arguments[i]) {
                if (arguments[i].hasOwnProperty(key))
                    out[key] = arguments[i][key];
            }
        }

        return out;
    };

    BixolonPrinting.prototype.connect = function (successCallback, errorCallback, config) {
        var logicalDeviceName = config.logicalDeviceName;
        var deviceBus = config.deviceBus;
        var address = config.address;
        var secure = Boolean(config.secure);

        exec(
            successCallback,
            errorCallback,
            "Printer",
            "connect",
            [logicalDeviceName, deviceBus, address, secure]
        );
    };

    BixolonPrinting.prototype.disconnect = function (successCallback, errorCallback) {
        exec(
            successCallback,
            errorCallback,
            "Printer",
            "disconnect",
            []
        );
    };

	BixolonPrinting.prototype.printBitmap = function (successCallback, errorCallback, config) {
        var base64Image = config.base64Image;

        var width = !this._isUndefined(config.width) ? config.width : 100;
        var brightness = !this._isUndefined(config.brightness) ? config.brightness : 50;
        var alignment = !this._isUndefined(config.alignment) ? config.alignment : this.ALIGNMENT.CENTER;
        var printConfig = this.printConfig;

        exec(
            successCallback,
            errorCallback,
            "Printer",
            "printBitmap",
            [base64Image, width, brightness, alignment, printConfig]
        );
	};

	module.exports = new BixolonPrinting();
};

Loader(require, exports, module);
cordova.define("cordova/plugins/BixolonPrinting", Loader);