package com.yusun.batterymonitor;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private Button settingbutton;
	private Button powersave;
	private int screenMode;  
	private ImageView charge_animation;
	private Context context;  
    private ConnectivityManager connManager;  
    private static final String TAG = "ScreenLuminance";  
    private int screenBrightness; 
    boolean isEnabled;
    private AnimationDrawable animationDrawable;

    private static final int WIFI = 0;  

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		

	       powersave=(Button)findViewById(R.id.powersave);
			powersave.setOnClickListener(powersaveListener);
			
			 
	}
	OnClickListener powersaveListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			//
			Toast.makeText(MainActivity.this, "正在关闭数据链接" ,Toast.LENGTH_SHORT ).show();
			if(isEnabled){
				isEnabled=false;
			}
			try {
				setMobileDataEnabled(MainActivity.this, isEnabled);
			} catch (SecurityException e) {
				//e.printStackTrace();
			} catch (IllegalArgumentException e) {
				//e.printStackTrace();
			} catch (ClassNotFoundException e) {
				//e.printStackTrace();
			} catch (NoSuchFieldException e) {
				//e.printStackTrace();
			} catch (IllegalAccessException e) {
				//e.printStackTrace();
			} catch (NoSuchMethodException e) {
				//e.printStackTrace();
			} catch (InvocationTargetException e) {
				//e.printStackTrace();
			}
//			 Intent psave = new Intent();
//			 psave.setClass(MainActivity.this,ActToggleGPS.class);
//			 startActivity(psave);
			Toast.makeText(MainActivity.this, "正在关闭蓝牙" ,Toast.LENGTH_SHORT ).show();
			BluetoothAdapter bluetoothadapter = BluetoothAdapter.getDefaultAdapter();
	       bluetoothadapter.disable();
	       Toast.makeText(MainActivity.this, "正在关闭WIFI" ,Toast.LENGTH_SHORT ).show();
	       toggle(MainActivity.this,WIFI);
	       Toast.makeText(MainActivity.this, "调整休眠时间" ,Toast.LENGTH_SHORT ).show();
	       Settings.System.putInt(getContentResolver(),android.provider.Settings.System.SCREEN_OFF_TIMEOUT,-1);
	       Toast.makeText(MainActivity.this, "清理一大波后台应用" ,Toast.LENGTH_SHORT ).show();
	       ActivityManager activityManger=(ActivityManager) MainActivity.this.getSystemService(ACTIVITY_SERVICE);
           List<ActivityManager.RunningAppProcessInfo> list=activityManger.getRunningAppProcesses();
           if(list!=null)
           for(int i=0;i<list.size();i++)
           {
               ActivityManager.RunningAppProcessInfo apinfo=list.get(i);
               System.out.println("pid            "+apinfo.pid);
               System.out.println("processName              "+apinfo.processName);
               System.out.println("importance            "+apinfo.importance);
               String[] pkgList=apinfo.pkgList;
               
               if(apinfo.importance>0)
               {
                  // Process.killProcess(apinfo.pid);
                   for(int j=0;j<pkgList.length;j++)
                   {
                       //2.2以上是过时的,请用killBackgroundProcesses代替
                       activityManger.killBackgroundProcesses(pkgList[j]);
                       System.out.println("now kill:"+pkgList[j]);
                      
                      
                   } 
               }
           }
           Toast.makeText(MainActivity.this, "调整屏幕亮度" ,Toast.LENGTH_SHORT ).show();
	       try {  
	            screenMode = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);  
	            Log.i(TAG, "screenMode = " + screenMode);  
	            // 获得当前屏幕亮度值 0--255  
	            screenBrightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);  
	            Log.i(TAG, "screenBrightness = " + screenBrightness);  
	            // 如果当前的屏幕亮度调节调节模式为自动调节，则改为手动调节屏幕亮度  
	            if (screenMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {  
	                setScreenMode(Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);  
	            	}  
	            // 设置屏幕亮度值为最大值0.0  
	            setScreenBrightness(3.0F);  
	            } catch (SettingNotFoundException e) {  
	                // TODO Auto-generated catch block  
	                e.printStackTrace();  
	            	}  
	       	
	       Toast.makeText(MainActivity.this, "恭喜！ 您的手机已达到最佳省电状态" ,Toast.LENGTH_SHORT ).show();
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
	  
	        // 保存设置的屏幕亮度值  
	        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, (int) value);  
	    }  
	    	
	};
	@Override
	protected void onStop() {
		super.onStop();
		unregisterReceiver(this.batteryInfoReceiver);
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(this.batteryInfoReceiver, new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED));
	}

	private BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context c, Intent i) {
			String batteryText, percent, connect = null, statusshow;
			int level = i.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
			int plugged = i.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
			int status = i.getIntExtra(BatteryManager.EXTRA_STATUS, 0);
			int vate = i.getIntExtra(BatteryManager.EXTRA_VOLTAGE,0);
			int mhealth = i.getIntExtra(BatteryManager.EXTRA_HEALTH,0);
			int mtempearture =i.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,0);
//			System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$    " +mhealth);
			
			String healthString = "";
			                 
			               switch (mhealth) {
			              case BatteryManager.BATTERY_HEALTH_UNKNOWN:
			                   healthString = "未知";
		                   break;
			                case BatteryManager.BATTERY_HEALTH_GOOD:
			                    healthString = "良好";
				                    break;
			                case BatteryManager.BATTERY_HEALTH_OVERHEAT:
				                    healthString = "过热";
				                    break;
				                case BatteryManager.BATTERY_HEALTH_DEAD:
				                    healthString = "损坏";
				                    break;
				                case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
				                    healthString = "电压过高";
				                    break;
				                case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
			                    healthString = "获取失败";
				                    break;
				                }
			
			percent = level + "%";
			TextView BatteryShow = (TextView) findViewById(R.id.BatteryLevel);
			TextView Batteryp = (TextView) findViewById(R.id.batterypercent);
			TextView health =(TextView) findViewById(R.id.health);
			TextView charge_time=(TextView) findViewById(R.id.charge_time);
			TextView hit_time=(TextView)findViewById(R.id.hit_time);
			TextView tempearture =(TextView) findViewById(R.id.tempearture);
			ImageView battery_image = (ImageView) findViewById(R.id.battery_image);
			TextView vote =(TextView)findViewById(R.id.vote);
			vote.setText("充电电压 ： " + vate/1000 + "V");
			health.setText("电池状态 ：" + healthString);
			tempearture.setText("电池温度 ："+mtempearture/10+"℃");
			charge_animation=(ImageView)findViewById(R.id.charge_anmition);
			settingbutton=(Button) findViewById(R.id.settingsbutton);
			settingbutton.setOnClickListener(settingOnClickListener);
			Animation operatingAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.charging);  
			LinearInterpolator lin = new LinearInterpolator();  
			operatingAnim.setInterpolator(lin); 
			// 计算还需充多久电
			Resources res =getResources();
			double Charge_factor=1.3;
			double mCharge_factor=1.5;
			int Battery_Capacity=res.getInteger(R.integer.battery_capacity);
			int h=0;
			int m=0;
			double t=0;
			int Charge_Mode_Volume = 500;
			if (plugged == 1 && level <90){
				Charge_factor=1.2;
				Charge_Mode_Volume=res.getInteger(R.integer.ac_charge);
			}
			else if(plugged == 1 && level >= 90){
				mCharge_factor=1.4;
				Charge_Mode_Volume=res.getInteger(R.integer.ac_charge);
			}
			else if(plugged == 2 && level >=90){
				Charge_Mode_Volume=res.getInteger(R.integer.usb_charge);
				mCharge_factor=1.7;
				System.out.println("#################"+plugged+"###############");
			}
			else {
					
					Charge_Mode_Volume=res.getInteger(R.integer.usb_charge);
					Charge_factor=1.4;
					
			}
			double Battery_Level = (double)level;
			if (level <90){
				
				t=Battery_Capacity*Charge_factor*(0.9-Battery_Level/100)/Charge_Mode_Volume+Battery_Capacity*mCharge_factor*0.1/Charge_Mode_Volume;
				h=(int)t ;
				m=(int)(t-h)*60;
			}
			else if (level >= 90){
				t=Battery_Capacity*mCharge_factor*(1-Battery_Level/100)/Charge_Mode_Volume;
				h=(int)t;
				m=(int)(t-h)*60;
			}
			
			//h=(int)(Battery_Capacity*Charge_factor*(1-Battery_Level/100)/Charge_Mode_Volume) ;
			//m=(int) ((Battery_Capacity*Charge_factor*(1-Battery_Level/100)/Charge_Mode_Volume -h)*60);
			charge_time.setText( h +"小时 "+m+"分钟");
			
		
			
			
			//----------------------分割线------------
			// 电量显示
			
//				battery_image.setImageAlpha(R.drawable.battery_charge);
				battery_image.setImageLevel(level);;
			
			
			
			
			//----------------------分割线---------
			if (operatingAnim != null && charge_animation != null && operatingAnim.hasStarted()) {  
		    	charge_animation.clearAnimation();  
		        charge_animation.startAnimation(operatingAnim);  
			}
//			charge_animation.setImageResource(R.drawable.charge_animation);
//			animationDrawable=(AnimationDrawable) charge_animation.getDrawable();
//			animationDrawable.start();
//			int status = i.getIntExtra(BatteryManager.EXTRA_STATUS,-1);
//			 
//			boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||status == BatteryManager.BATTERY_STATUS_FULL;
//			// temperature = (temperature - 32) * 5/9;
			if (plugged == 0) {//此处表示未链接电源或者USB
				charge_animation.clearAnimation();  //停止充电平移动画
				charge_animation.setVisibility(8);//去掉动画文件显示
				BatteryShow.setVisibility(8);//去掉已连接到USB
				battery_image.setImageResource(R.drawable.battery);//将电量动画改为静态动画
				vote.setVisibility(8);//去掉充电电压显示
				charge_time.setVisibility(8);//去掉充电剩余时间显示
				hit_time.setVisibility(8);//去掉剩余充电时间显示
			} else if (plugged == 1) {
				connect = "电源";
				BatteryShow.setVisibility(0);
				vote.setVisibility(0);
				charge_time.setVisibility(0);
				hit_time.setVisibility(0);
			} else {
				connect = "USB";
				BatteryShow.setVisibility(0);
				vote.setVisibility(0);
				charge_time.setVisibility(0);
				hit_time.setVisibility(0);
			}
			if (status == 2 && level !=100) {
				statusshow = "正在充电";
				battery_image.setImageResource(R.drawable.battery_charge2);
				charge_animation.setVisibility(0);
				charge_animation.startAnimation(operatingAnim);  
				charge_time.setTextSize(28);
			
			} 
			else if(level == 100){
				statusshow = "正在涓流保护充电";
				charge_animation.setVisibility(0);
				charge_animation.startAnimation(operatingAnim);  
				hit_time.setVisibility(8);
				charge_time.setText("         电池已充满\n正在进行涓流充电保养....");
				charge_time.setTextSize(15);
			}
			
				
			
			else {
				System.out.println("status is==== ===========" + status);
				statusshow = "放电中";
			}
			batteryText = "已连接到 ："+ connect ;
			BatteryShow.setText(batteryText);
			Batteryp.setText(percent);
			
		}	
		
		
		OnClickListener settingOnClickListener =new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
				Intent it = new Intent("android.intent.action.MAIN");
			     it.setClassName("com.android.settings","com.android.settings.fuelgauge.PowerUsageSummary");
			    startActivity(it);
				
			}
		};
		
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	
	}
	private void setMobileDataEnabled(Context context, boolean enabled) throws ClassNotFoundException, SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        final ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final Class conmanClass = Class.forName(conman.getClass().getName());
        final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
        iConnectivityManagerField.setAccessible(true);
        final Object iConnectivityManager = iConnectivityManagerField.get(conman);
        final Class iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
        final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
       // setMobileDataEnabledMethod.setAccessible(true);

        setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);
    }
	 public static void toggle(Context context, int idx) {  
	    	WifiManager manager =null;
	    	manager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
	    	manager.setWifiEnabled(false);   
	    }
	}


