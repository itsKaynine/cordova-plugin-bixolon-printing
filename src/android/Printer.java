package com.itskaynine.cordova.plugin.BixolonPrinting;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import java.nio.ByteBuffer;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import jpos.JposException;
import jpos.POSPrinter;
import jpos.POSPrinterConst;

import com.bxl.config.editor.BXLConfigLoader;

public class Printer extends CordovaPlugin {
	private static final String TAG = "BixolonPrinting.Printer";

	// Actions
	public static final String ACTION_CONNECT = "connect";
	public static final String ACTION_DISCONNECT = "disconnect";
    public static final String ACTION_PRINT_BITMAP = "printBitmap";

    private Context context;
    private CallbackContext callbackContext;

    public boolean isConnected;
    public String connectedLogicalName;

    static POSPrinter posPrinter;

	public static Bitmap decodeBase64Image(String input) {
		String trimmed = input.replace("data:image/png;base64,", "").replace("data:image/jpg;base64,", "").replace("data:image/jpeg;base64,", "");

	    byte[] arr = Base64.decode(trimmed, 0);
	    return BitmapFactory.decodeByteArray(arr, 0, arr.length);
	}

	@Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        Log.d(TAG, "initialize_BEGIN");

        super.initialize(cordova, webView);

        this.context = cordova.getActivity();
        posPrinter = new POSPrinter(this.context);
        
        initVariables();
    }

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) {
        Log.d(TAG, "execute_BEGIN");

        this.callbackContext = callbackContext;
        Log.i(TAG, "action: " + action);

        Boolean isActionValid = true;

        try {
			if (ACTION_CONNECT.equals(action)) {
	            this.connect(args.getString(0), args.getInt(1), args.getString(2), args.getBoolean(3));
	        }
	        else if (ACTION_DISCONNECT.equals(action)) {
	            this.disconnect();
	        }
	        else if (ACTION_PRINT_BITMAP.equals(action)) {
	            this.printBitmap(args.getString(0), args.getInt(1), args.getInt(2), args.getInt(3), args.optJSONObject(4));
	        } 
	        else {
	        	isActionValid = false;

	            this.callbackContext.error("Invalid Action");
	            Log.d(TAG, "Invalid action : " + action);
	        }
    	}
    	catch (JSONException e) {
    		this.callbackContext.error(e.getMessage());
    		isActionValid = false;
    	}

        return isActionValid;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy_BEGIN");

        super.onDestroy();
        this.disconnect();
    }

    private void initVariables() {
    	Log.d(TAG, "initVariables_BEGIN");

    	this.isConnected = false;
        this.connectedLogicalName = null;
    }

    private void connect(String logicalDeviceName, int deviceBus, String address, Boolean secure) {
        Log.d(TAG, "connect_BEGIN");

        if (!this.isConnected) {
        	try {
	        	BXLConfigLoader configLoader = new BXLConfigLoader(this.context);
	            configLoader.addEntry(logicalDeviceName, BXLConfigLoader.DEVICE_CATEGORY_POS_PRINTER, logicalDeviceName, deviceBus, address, secure);
	            configLoader.saveFile();

	            posPrinter.open(logicalDeviceName);
	            posPrinter.claim(0);
	            posPrinter.setDeviceEnabled(true);

	            this.isConnected = true;
	            this.callbackContext.success("success");
        	}
	       	catch (Exception e) {
	    		this.callbackContext.error(e.getMessage());
	    	}
        }
        else {
        	this.callbackContext.success("already connected");
        }
    }

    private void disconnect() {
    	Log.d(TAG, "disconnect_BEGIN");

    	try {
		    posPrinter.setDeviceEnabled(false);
	        posPrinter.release();
	        posPrinter.close();

	        this.callbackContext.success("success");
    	}
    	catch (Exception e) {
    		this.callbackContext.error(e.getMessage());
    	}
    }

    private void printBitmap(String base64Image, int width, int brightness, int alignment, JSONObject printConfig) {
    	Log.d(TAG, "printBitmap_BEGIN");

    	int lineFeed = 3;
    	Boolean formFeed = false;

    	try {
    		lineFeed = printConfig.optInt("lineFeed");
    		formFeed = printConfig.getBoolean("formFeed");
    	}
    	catch (JSONException jse) {}

        try {
        	ByteBuffer buffer = ByteBuffer.allocate(4);
            buffer.put((byte) POSPrinterConst.PTR_S_RECEIPT);
            buffer.put((byte) brightness);
            buffer.put((byte) 0x00);
            buffer.put((byte) 0x00);

            Bitmap bmp = decodeBase64Image(base64Image);
            posPrinter.printBitmap(buffer.getInt(0), bmp, width, alignment);

            // TODO: Implement
	        if (formFeed) {
	            // posPrinter.formFeed();
	        } 
	        else {
	            // posPrinter.lineFeed(lineFeed);
	        }

            try {
            	posPrinter.cutPaper(100);
        	}
        	catch (JposException jpe) {}

            this.callbackContext.success("success");
        }
        catch (Exception e) {
            this.callbackContext.error(e.getMessage());
            return;
        }
    }
}