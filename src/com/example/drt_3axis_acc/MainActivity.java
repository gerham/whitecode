package com.example.drt_3axis_acc;


import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
//import android.os.Handler;
//import android.os.PowerManager;
//import android.os.PowerManager.WakeLock;
import android.widget.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.LinkedList;


public class MainActivity extends Activity {
	private TextView textViewInfo = null;
	private TextView textViewX = null;
	private TextView textViewY = null;
	private TextView textViewZ = null;
	private TextView textViewText = null;
	private SensorManager sensorManager = null;
	private Sensor sensor = null;
	private Sensor lightsensor = null;
	private float gravity[] = new float[3];
	private float light = 20000,light_acc;
	private String sb,line,datafile = "lightacc.txt";
	private LinkedList<String> data = new LinkedList<String>();
	private WakeLock wklock;
	int count = 0,time_step = 0,tmstep1 = 0;
	private int STATE = config.STATE_OUT;	// �Ƿ�ʼ�ɼ�
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		textViewInfo = (TextView) findViewById(R.id.TextView01);
		textViewX = (TextView) findViewById(R.id.TextView02);
		textViewY = (TextView) findViewById(R.id.TextView03);
		textViewZ = (TextView) findViewById(R.id.TextView04);
		textViewText = (TextView) findViewById(R.id.text);
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wklock=pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "wklock");	// ��õ�Դ�������
		sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);	// ��ü��ٶȴ�����
		lightsensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);	// ��ù��ߴ�����

		Log.e("sensor", "create");
		this.set_button();		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO �Զ����ɵķ������
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO �Զ����ɵķ������
		switch(item.getItemId()){
		case R.id.light_threashing:
			Toast.makeText(MainActivity.this, "�ղ���", Toast.LENGTH_SHORT).show();
			break;
		case R.id.rm_data:
			rm_data();
			break;
		default:
			break;
		}
		return true;
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
				Log.e("sensor",e.toString());
				break;
			}    
		    textViewInfo.setText(sb);  
			textViewX.setText("X�����ϼ��ٶȣ�       " + gravity[0] + "m/s^2");
			textViewY.setText("Y�����ϼ��ٶȣ�       " + gravity[1] + "m/s^2");
			textViewZ.setText("Z�����ϼ��ٶȣ�       " + gravity[2] + "m/s^2");
			textViewText.setText(line);
			if (update_state() != 0)return ;
//			if (STATE == config.STATE_OUT) return;
//			Log.e("sensor","count ++");
			count ++;
			// ��ʼ��������
			if (count %2 == 0){
				time_step ++;
				if (time_step >= config.SENSOR_STEPS){
					data.pop();
				}
				line = gravity[0] + "\t" + gravity[1] + "\t" +gravity[2] + "\t" + light + "\n";
				data.add(line);			
			}
		}
	};
		
	@Override
	protected void onResume() {// ���ü���
		super.onResume();
		sensorManager.registerListener(listener, sensor,config.SENSOR_RATE);
		sensorManager.registerListener(listener, lightsensor,config.SENSOR_RATE);
	}

	@Override
	protected void onStop() {
		sensorManager.unregisterListener(listener);
		wklock.release();
		Log.e("sensor","app stop");
		super.onStop();
	}	
	
	private void set_button(){										// ���ð�ť�¼�
		final Button bt_start = (Button)findViewById(R.id.bt_data_start);
		final Button bt_stop = (Button)findViewById(R.id.bt_data_stop);
		final Button bt_read = (Button)findViewById(R.id.read_txt);
		bt_stop.setEnabled(false);
		bt_start.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				// TODO �Զ����ɵķ������
				Toast.makeText(MainActivity.this, "start collect data", Toast.LENGTH_SHORT).show();
				bt_stop.setEnabled(true);
				bt_start.setEnabled(false);
				wklock.acquire();
				// ��ʼ�ռ�
//				STATE = config.STATE_IN;
			}
		});
		bt_stop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO �Զ����ɵķ������
				Toast.makeText(MainActivity.this, "stop to collect data", Toast.LENGTH_SHORT).show();
				wklock.release();
				bt_start.setEnabled(true);
				bt_stop.setEnabled(false);
//				STATE = config.STATE_OUT;
			}
		});
		bt_read.setEnabled(false);
//		bt_read.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				// TODO �Զ����ɵķ������
//				Toast.makeText(MainActivity.this,"reader:" + read_txt(), Toast.LENGTH_SHORT);
//			}
//		});;
	}
	String read_txt(){
		Log.e("sensor", "��ȡ����");
		String data = "";
		BufferedReader reader = null;			//sys
		try{
			FileInputStream input = openFileInput(datafile);
			reader = new BufferedReader(new InputStreamReader(input));
			data = reader.readLine();
		}catch(IOException e){
			Log.e("sensor","read error");
			Toast.makeText(MainActivity.this, "output error!!", Toast.LENGTH_SHORT).show();
		}finally{
			if (reader != null){
				try {
					reader.close();
				} catch (IOException e) {
					// TODO �Զ����ɵ� catch ��
					e.printStackTrace();
				}
			}
		}
		return data;
	}
	void save_txt(String datas){
		Log.e("sensor","save text");
		FileOutputStream out;
		BufferedWriter writer = null;			//sys
		try{
			out = openFileOutput(datafile,Context.MODE_APPEND);	// sys
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
					// TODO �Զ����ɵ� catch ��
					e.printStackTrace();
				}
			}
		}
	}
	void save_txt_sd(String datas){
		//�ж��ⲿ�洢���Ƿ����
		if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			Toast.makeText(getApplicationContext(), "SD�洢�������ڣ�", Toast.LENGTH_LONG).show();
			save_txt(datas);
			return;
		}
		String path = Environment.getExternalStorageDirectory().toString()+File.separator+datafile;
		File sdFile = new File(path); // sd card
		if (!sdFile.exists()){
			Log.e("sensor","�ļ�������");				// sd card
			try {
				sdFile.createNewFile();
			} catch (IOException e) {
				Log.e("sensor","�����ļ�ʧ��");				// sd card
				// TODO �Զ����ɵ� catch ��
				e.printStackTrace();
			}
		}
		try {
//			PrintStream out = new PrintStream(new FileOutputStream(sdFile));
//			out.print(datas);
			FileOutputStream out = openFileOutput(datafile,Context.MODE_APPEND);	// sys
			out.write(datas.getBytes());
			out.flush();
			out.close();
			Log.e("sensor","����һ������");				// sd card
		} catch (FileNotFoundException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
			Toast.makeText(this, "sd �� д��ʧ��", Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			// TODO �Զ����ɵ� catch ��
			Toast.makeText(this, "sd �� д��ʧ��", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}		
	}
	private int update_state(){
		if (STATE == config.STATE_OUT){		// �ֻ�δ������
			if (light < config.THRE_LIGHT){
				STATE = config.STATE_IN;
				Log.e("sensor","state == �ֻ��ڶ���");			}
			else return 1;
		}else if (STATE == config.STATE_IN){ // �ֻ��ڶ���
			if (light > config.THRE_LIGHT){
				STATE = config.STATE_OUTING;
				tmstep1 = time_step + config.SENSOR_STEPS/2;
				Log.e("sensor","state == �ֻ����ͳ�ȥ");			}
		}else{								// �ֻ������ͳ�ȥ
			if (time_step >= tmstep1){
				data.add("|\n");
				save_txt_sd(data.clone().toString());
				data.clear();
				count = time_step = 0;
				STATE = config.STATE_OUT;
//				Log.e("sensor","����һ������");
				return 1;
			}
		}
		return 0;
	}
	void rm_data(){
		String path = Environment.getExternalStorageDirectory().toString()+File.separator+datafile;
		File sdFile = new File(path); // sd card
		if (sdFile.exists()){
			Log.e("sensor","�ļ�������");				// sd card
			sdFile.delete();
			Toast.makeText(this, "��ɾ���ļ�", Toast.LENGTH_SHORT).show();
		}else{
			Toast.makeText(this, "�ļ�������", Toast.LENGTH_SHORT).show();
		}
	}
}
