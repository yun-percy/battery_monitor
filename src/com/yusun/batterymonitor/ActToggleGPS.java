package com.yusun.batterymonitor;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
  
public class ActToggleGPS extends Activity implements OnClickListener {  

    private static final int WIFI = 0;  

    private int screenMode;  
    private static final String TAG = "ScreenLuminance";  
    private int screenBrightness;  
  
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        BluetoothAdapter bluetoothadapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothadapter.disable();
        Settings.System.putInt(getContentResolver(),android.provider.Settings.System.SCREEN_OFF_TIMEOUT,-1);
        toggle(this, WIFI); 
        try {  
        screenMode = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);  
        Log.i(TAG, "screenMode = " + screenMode);  
        screenBrightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);  
        Log.i(TAG, "screenBrightness = " + screenBrightness);  
        if (screenMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {  
            setScreenMode(Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);  
        }  
        setScreenBrightness(0.0F);  
        } catch (SettingNotFoundException e) {  
            e.printStackTrace();  
        }  
    }
    private void setScreenMode(int value) {  
        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, value);  
    }  
    private void setScreenBrightness(float value) {  
        Window mWindow = getWindow();  
        WindowManager.LayoutParams mParams = mWindow.getAttributes();  
        float f = value / 255.0F;  
        mParams.screenBrightness = f;  
        mWindow.setAttributes(mParams);  
        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, (int) value);  
    }  
    public static void toggle(Context context, int idx) {  
    	WifiManager manager =null;
    	manager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
    	manager.setWifiEnabled(false);   
    }
	@Override
	public void onClick(View arg0) {

	}  
  
   
  
}  