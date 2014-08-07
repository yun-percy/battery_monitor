package com.yusun.batterymonitor;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

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
			String batteryText, percent, connect, statusshow;
			int level = i.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
			int plugged = i.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
			int status = i.getIntExtra(BatteryManager.EXTRA_STATUS, 0);
			System.out.println("status");
//			int status = i.getIntExtra(BatteryManager.EXTRA_STATUS,-1);
//			 
//			boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||status == BatteryManager.BATTERY_STATUS_FULL;
//			// temperature = (temperature - 32) * 5/9;
			if (plugged == 0) {
				connect = "未连接";
			} else if (plugged == 1) {
				connect = "电源";
			} else {
				connect = "USB";
			}
			if (status == 2) {
				statusshow = "正在充电";
			} else {
				statusshow = "放电中";
			}
			batteryText = "连接到:"+ connect + "\n" + statusshow;
			percent = level + "%";
			ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar1);
			pb.setProgress(level);
			TextView BatteryShow = (TextView) findViewById(R.id.BatteryLevel);
			TextView Batteryp = (TextView) findViewById(R.id.batterypercent);
			BatteryShow.setText(batteryText);
			Batteryp.setText(percent);
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
