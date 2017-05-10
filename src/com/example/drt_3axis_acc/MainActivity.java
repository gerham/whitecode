package com.example.drt_3axis_acc;


import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
//import android.os.Handler;
//import android.os.PowerManager;
//import android.os.PowerManager.WakeLock;
import android.widget.*;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Vector;


public class MainActivity extends Activity {
	private TextView textViewInfo = null;
	private TextView textViewX = null;
	private TextView textViewY = null;
	private TextView textViewZ = null;
	private SensorManager sensorManager = null;
	private Sensor sensor = null;
	private Sensor lightsensor = null;
	private float gravity[] = new float[3];
	private float light = 20000,light_acc;
	private String sb,line;
	private LinkedList<String> data = new LinkedList<String>();
	private WakeLock wklock;
	int count = 0,time_step = 0,tmstep1 = 0;
	private int STATE = config.STATE_OUT;	// 是否开始采集
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		textViewInfo = (TextView) findViewById(R.id.TextView01);
		textViewX = (TextView) findViewById(R.id.TextView02);
		textViewY = (TextView) findViewById(R.id.TextView03);
		textViewZ = (TextView) findViewById(R.id.TextView04);
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wklock=pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "wklock");	// 获得电源管理服务
		sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);	// 获得加速度传感器
		lightsensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);	// 获得光线传感器

		Log.e("sensor", "create");
//		Toast.makeText(this, "222", Toast.LENGTH_SHORT).show();
		this.set_button();
	}

//	private Handler handler = new Handler(){
//		public void handleMassage(Massage msg){
//			switch (msg.what){
//			case 1: //更新传感器值
//				break;
//			default:
//				Toast.makeText(MainActivity.this, "错误的消息", Toast.LENGTH_SHORT).show();
//				break;
//			}
//		}
//	};
	private int update_state(){
		if (STATE == config.STATE_OUT){		// 手机未进兜里
			if (light < config.THRE_LIGHT){
				STATE = config.STATE_IN;
			}
			else return 1;
		}else if (STATE == config.STATE_IN){ // 手机在兜里
			if (light > config.THRE_LIGHT){
				STATE = config.STATE_OUTING;
				tmstep1 = time_step + config.SENSOR_STEPS/2;
			}else Log.e("sensor","state == 手机在兜里");
		}else{								// 手机正在掏出去
			if (time_step >= tmstep1){
				data.add("|");
				save_txt(data.clone().toString());
				data.clear();
				time_step = 0;
				count = 0;
				STATE = config.STATE_OUT;
				Log.e("sensor","保存一个样本");
				return 1;
			}
		}
		return 0;
	}
	private SensorEventListener listener = new SensorEventListener() {
		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
		}
		@Override
		public void onSensorChanged(SensorEvent e) {
			switch (e.sensor.getType()){
			case Sensor.TYPE_LIGHT:
				light_acc = e.accuracy;
				light = e.values[0];
				sb = "";
				sb += "acc --> " + light_acc + "\t";
				sb += "lux --> " + light + "\n";
				break;
			case Sensor.TYPE_ACCELEROMETER:
				gravity[0] = e.values[0];
				gravity[1] = e.values[1];
				gravity[2] = e.values[2];
				break;
			default:
//				Log.e("sensor",e.toString());
				break;
			}    
		    textViewInfo.setText(sb);  
			textViewX.setText("X方向上加速度：       " + gravity[0] + "m/s^2");
			textViewY.setText("Y方向上加速度：       " + gravity[1] + "m/s^2");
			textViewZ.setText("Z方向上加速度：       " + gravity[2] + "m/s^2");
			if (update_state() != 0)return ;
//			if (STATE == config.STATE_OUT) return;
//			Log.e("sensor","count ++");
			count ++;
			// 开始储存数据
			if (count %2 == 0){
				time_step ++;
				if (time_step >= config.SENSOR_STEPS){
					data.pop();
				}
				line = gravity[0] + '\t' + gravity[1] + '\t' +gravity[2] + '\t' + light + "\n";
				data.add(line);			
			}
		}
	};
		
	@Override
	protected void onResume() {// 设置监听
		super.onResume();
//		sensorManager.registerListener(listener, sensor,SensorManager.SENSOR_DELAY_NORMAL);
//		sensorManager.registerListener(listener, lightsensor,SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(listener, sensor,config.SENSOR_RATE);
		sensorManager.registerListener(listener, lightsensor,config.SENSOR_RATE);
	}

	@Override
	protected void onStop() {
		sensorManager.unregisterListener(listener);
		wklock.release();
		super.onStop();
	}	
	
	private void set_button(){										// 设置按钮事件
		final Button bt_start = (Button)findViewById(R.id.bt_data_start);
		final Button bt_stop = (Button)findViewById(R.id.bt_data_stop);
		bt_stop.setEnabled(false);
		bt_start.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				Toast.makeText(MainActivity.this, "start collect data", Toast.LENGTH_SHORT).show();
				bt_stop.setEnabled(true);
				bt_start.setEnabled(false);
				wklock.acquire();
				// 开始收集
//				STATE = config.STATE_IN;
			}
		});
		bt_stop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				Toast.makeText(MainActivity.this, "stop to collect data", Toast.LENGTH_SHORT).show();
				wklock.release();
				bt_start.setEnabled(true);
				bt_stop.setEnabled(false);
//				STATE = config.STATE_OUT;
			}
		});
	}
	void save_txt(String datas){
		Log.e("sensor","save text");
		datas += "|\n";
		FileOutputStream out;
		BufferedWriter writer = null;
		try{
			out = openFileOutput("lightAndAccDatas.txt",Context.MODE_APPEND);
			writer = new BufferedWriter(new OutputStreamWriter(out));
			writer.write(datas);
		}catch(IOException e){
			Log.e("sensor","wirte error");
			Toast.makeText(MainActivity.this, "output error!!", Toast.LENGTH_SHORT).show();
		}finally{
			if (writer != null){
				try {
					writer.close();
				} catch (IOException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
			}
		}
	}
}
