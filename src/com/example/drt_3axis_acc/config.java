package com.example.drt_3axis_acc;


public class config{
	final static String axis_x = "axis_x";
	final static String axis_y = "axis_y";
	final static String axis_z = "axis_z";
	final static String val_light = "val_light";
	final static int SENSOR_RATE = 50000;	// 采样率20hz
	final static int SENSOR_STEPS = 40;		// 
	final static boolean IN_PACK = false;	// 是否在兜内
	final static int THRE_LIGHT = 70;		// 光线阈值
	final static int SENSORS = 2;			// 传感器数，
	final static int LINGHT_OUT = 20000;	// 假设外界光强
	final static int STATE_OUT = 0, STATE_IN = 1, STATE_OUTING = 2;
}