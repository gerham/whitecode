package com.example.drt_3axis_acc;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SensorService extends Service {

	@Override
	public void onCreate() {
		// TODO 自动生成的方法存根
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO 自动生成的方法存根
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO 自动生成的方法存根
		super.onDestroy();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO 自动生成的方法存根
		return super.onUnbind(intent);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO 自动生成的方法存根
		return null;
	}

}
