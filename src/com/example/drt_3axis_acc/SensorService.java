package com.example.drt_3axis_acc;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SensorService extends Service {

	@Override
	public void onCreate() {
		// TODO �Զ����ɵķ������
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO �Զ����ɵķ������
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO �Զ����ɵķ������
		super.onDestroy();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO �Զ����ɵķ������
		return super.onUnbind(intent);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO �Զ����ɵķ������
		return null;
	}

}
