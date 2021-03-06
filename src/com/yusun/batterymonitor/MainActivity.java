package com.yusun.batterymonitor;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Message;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private Button settingbutton;
	private Button powersave;
	private int screenMode;  
    private static final String TAG = "ScreenLuminance";  
    private int screenBrightness; 
    boolean isEnabled;
//    CircleProgress round;
    private static final int WIFI = 0;  

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		

//	       powersave=(Button)findViewById(R.id.powersave);
//			powersave.setOnClickListener(powersaveListener);
			 final CircularProgressButton circularButton1 = (CircularProgressButton) findViewById(R.id.circularButton1);
		        circularButton1.setOnClickListener(new View.OnClickListener() {
		            @Override
		            public void onClick(View v) {
		                if (circularButton1.getProgress() == 0) {
		                    simulateSuccessProgress(circularButton1);
		                } 
		                    else {
		                    circularButton1.setProgress(0);
		                }
		            
		        
//	            	closedata();
//
//	    			closebluetooth_wifi_time();
//
//	    			close_app();
//	 
//	    	        turndown();
	    	        
//	                if (circularButton1.getProgress() == 0) {
//	                    circularButton1.setProgress(50);
//	                } else if (circularButton1.getProgress() == 100) {
//	                    circularButton1.setProgress(0);
//	                } else {
//	                }
	            }
	        });
			 
	}
//	OnClickListener powersaveListener = new OnClickListener() {
//		
//		@Override
//		public void onClick(View v) {
////			closedata();
////			closebluetooth_wifi_time();
////			close_app();
////	        turndown();
////	       
//           
//	       
//			}
	
		
	    	
//	};
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
	 int level;
	private BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context c, Intent i) {
			String batteryText, percent, connect = null, statusshow;
			level = i.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
			int plugged = i.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
			final int status = i.getIntExtra(BatteryManager.EXTRA_STATUS, 0);
			int vate = i.getIntExtra(BatteryManager.EXTRA_VOLTAGE,0);
			int mhealth = i.getIntExtra(BatteryManager.EXTRA_HEALTH,0);
			int mtempearture =i.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,0);
			Message msg = new Message();
            msg.what = RoundImageView.FLAG;
            msg.obj=level+"";
            RoundImageView.handler.sendMessage(msg); 
			
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
		//	ImageView battery_image = (ImageView) findViewById(R.id.battery_image);
			TextView vote =(TextView)findViewById(R.id.vote);
			vote.setText("充电电压 ： " + vate/1000 + "V");
//			round= (CircleProgress) findViewById(R.id.round);
			health.setText("电池状态 ：" + healthString);
			tempearture.setText("电池温度 ："+mtempearture/10+"℃");
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
			}
			else {
					
					Charge_Mode_Volume=res.getInteger(R.integer.usb_charge);
					Charge_factor=1.4;
					
			}
			double Battery_Level = (double)level;
			if (level <90){
				
				t=Battery_Capacity*Charge_factor*(0.9-Battery_Level/100)/Charge_Mode_Volume+Battery_Capacity*mCharge_factor*0.1/Charge_Mode_Volume;
				h=(int)t ;
				m=(int)((t-h)*60);
			}
			else if (level >= 90){
				t=Battery_Capacity*mCharge_factor*(1-Battery_Level/100)/Charge_Mode_Volume;
				h=(int)t;
				m=(int)((t-h)*60);
			}
			
			//h=(int)(Battery_Capacity*Charge_factor*(1-Battery_Level/100)/Charge_Mode_Volume) ;
			//m=(int) ((Battery_Capacity*Charge_factor*(1-Battery_Level/100)/Charge_Mode_Volume -h)*60);
			charge_time.setText( h +"小时 "+m+"分钟");
			
		
			
			
			//----------------------分割线------------
			// 电量显示
			
//			round.setType(CircleProgress.ROUND);
			 new AsyncTask<Integer, Integer, Integer>() {
                 @Override
                 protected Integer doInBackground(Integer... params) {
                	 if(status == 2 && level < 90){
                		 for(int i=0;i<=level;i++){
                             publishProgress(i);
                             try {
                                 Thread.sleep(150);
                             } catch (InterruptedException e) {
                                 e.printStackTrace();
                             }
                         }
                	 }
                	 else if (status == 2 && level >= 90 && level !=100){
                     for(int i=0;i<=level;i++){
                         publishProgress(i);
                         try {
                             Thread.sleep(500);
                         } catch (InterruptedException e) {
                             e.printStackTrace();
                         }
                     }
                	 }
                	 else {
                             publishProgress(level);
                             try {
                                 Thread.sleep(500);
                             } catch (InterruptedException e) {
                                 e.printStackTrace();
                             }
                	 }
                     return null;
                 }
                 @Override
                 protected void onProgressUpdate(Integer... values) {
                     super.onProgressUpdate(values);
//                     round.setmSubCurProgress(values[0]);
                 }
             }.execute(0);
			//----------------------分割线---------
			if (plugged == 0) {//此处表示未链接电源或者USB
				BatteryShow.setVisibility(8);//去掉已连接到USB
				//battery_image.setImageResource(R.drawable.battery);//将电量动画改为静态动画
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
				charge_time.setTextSize(28);
				charge_time.setText( h +"小时 "+m+"分钟");
			} 
			else if(level == 100){
				statusshow = "正在涓流保护充电";
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
        setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);
    }
	 public static void toggle(Context context, int idx) {  
	    	WifiManager manager =null;
	    	manager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
	    	manager.setWifiEnabled(false);   
	    }
	 @Override
	 public boolean onKeyDown(int keyCode, KeyEvent event) {
	     if(keyCode == KeyEvent.KEYCODE_BACK)
	        {  
	            exitBy2Click();      //调用双击退出函数
	        }
	     return false;
	 }
	 /**
	  * 双击退出函数
	  */
	 private static Boolean isExit = false;
	      
	 private void exitBy2Click() {
	     Timer tExit = null;
	     if (isExit == false) {
	         isExit = true; // 准备退出
	         Toast.makeText(this, "再按一次退出电量管家", Toast.LENGTH_SHORT).show();
	         tExit = new Timer();
	         tExit.schedule(new TimerTask() {
	             @Override
	             public void run() {
	                 isExit = false; // 取消退出
	             }
	         }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
	      
	     } else {
	         finish();
	         System.exit(0);
	     }
	 }
	 private void closedata() {
//		 Toast.makeText(MainActivity.this, "正在关闭数据链接" ,Toast.LENGTH_SHORT ).show();
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

	}
	 private void closebluetooth_wifi_time() {
//		 Toast.makeText(MainActivity.this, "正在关闭蓝牙" ,Toast.LENGTH_SHORT ).show();
			BluetoothAdapter bluetoothadapter = BluetoothAdapter.getDefaultAdapter();
	       bluetoothadapter.disable();
//	       Toast.makeText(MainActivity.this, "正在关闭WIFI" ,Toast.LENGTH_SHORT ).show();
	       toggle(MainActivity.this,WIFI);
//	       Toast.makeText(MainActivity.this, "调整休眠时间" ,Toast.LENGTH_SHORT ).show();
	       Settings.System.putInt(getContentResolver(),android.provider.Settings.System.SCREEN_OFF_TIMEOUT,-1);

	}
	 private void close_app() {
//		 Toast.makeText(MainActivity.this, "清理一大波后台应用" ,Toast.LENGTH_SHORT ).show();
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
                 for(int j=0;j<pkgList.length;j++)
                 {
                     activityManger.killBackgroundProcesses(pkgList[j]);
                     System.out.println("now kill:"+pkgList[j]);
                 } 
             }
         }

	}
	 Integer value=0;
	 private void turndown() {
		 Toast.makeText(MainActivity.this, "调整屏幕亮度" ,Toast.LENGTH_SHORT ).show();
	       try {  
	            screenMode = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);  
	       //     Log.i(TAG, "screenMode = " + screenMode);  
	            // 获得当前屏幕亮度值 0--255  
	            screenBrightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);  
	       //   //  Log.i(TAG, "screenBrightness = " + screenBrightness);  
	            // 如果当前的屏幕亮度调节调节模式为自动调节，则改为手动调节屏幕亮度  
	            if (screenMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {  
	                setScreenMode(Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);  
	            	}  
	            // 设置屏幕亮度值为最大值0.0  
	            setScreenBrightness(3.0F);  
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
	        // 保存设置的屏幕亮度值  
	        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, (int) value);  
	    }  
	 private void simulateSuccessProgress(final CircularProgressButton button) {
	        ValueAnimator widthAnimation = ValueAnimator.ofInt(1, 100);
	        widthAnimation.setDuration(3500);
	        
	        widthAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
	        
	        widthAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
	            @Override
	            public void onAnimationUpdate(ValueAnimator animation) {
	            	value = (Integer) animation.getAnimatedValue();
	                
	                button.setProgress(value);
	            }
	        });
	        closedata();   	    	        
            closebluetooth_wifi_time();
            close_app();
            turndown();
	        widthAnimation.start();
	        Toast.makeText(MainActivity.this, "恭喜！ 您的手机已达到最佳省电状态" ,0 ).show();
	    }
	 public void savepower(View v) {
		 closedata();   	    	        
         closebluetooth_wifi_time();
         close_app();
         turndown();
		 Message msg = new Message();
         msg.what = RoundImageView.FLAG;
         msg.obj="savepawer";
//         for(int i=level;i<0;i--){
//        	 msg.obj=i+"";
//
//        	 try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//        	 RoundImageView.handler.sendMessage(msg); 
//         }
//         for(int m=0;m<level;m++){
//        	 msg.obj=m+"";
//        	 try {
// 				Thread.sleep(100);
// 			} catch (InterruptedException e) {
// 				// TODO Auto-generated catch block
// 				e.printStackTrace();
// 			}
         new Thread(){
        	 public void run() {
        		for (int i = level; i >0 ; i=i-4) {
        			try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
        			Message msg = new Message();
                    msg.what = RoundImageView.FLAG;
                    msg.obj=i+"";
        			RoundImageView.handler.sendMessage(msg);
				}
        		for (int i = 0; i <level ; i=i+4) {
        			try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
        			Message msg = new Message();
                    msg.what = RoundImageView.FLAG;
                    msg.obj=i+"";
        			RoundImageView.handler.sendMessage(msg);
				}
    			Message msg = new Message();
                msg.what = RoundImageView.FLAG;
                msg.obj=level+"";
    			RoundImageView.handler.sendMessage(msg);
        	 };
         }.start();
        	 
//         }
//         
         Toast.makeText(MainActivity.this, "恭喜！ 您的手机已达到最佳省电状态" ,0 ).show();
	}


}


