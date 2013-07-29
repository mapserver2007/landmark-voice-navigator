package com.mapserverframework.lvn;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class AccelerationController implements SensorEventListener {
    /** センサマネージャ */
    private SensorManager manager;
    /** センサ */
    private Sensor sensor;
    
    public AccelerationController(Context context) {
        manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }
    
    public void update() {
        manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
    
    public void remove() {
        manager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        
        
        // TODO 自動生成されたメソッド・スタブ
        
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // 直線加速度(加速度から重力加速度を引いた値)
        if (event.sensor.getType() != Sensor.TYPE_LINEAR_ACCELERATION) {
            return;
        }
        // x軸
        float x = event.values[0];
        // y軸
        //float y = event.values[1];
        // z軸
        //float z = event.values[2];
        Log.d("lvn-sensor", "x:" + x);
        
    }
    
}
