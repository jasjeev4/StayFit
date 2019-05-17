package science.logarithmic.stayfit;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import static android.content.Context.SENSOR_SERVICE;

public class StepCounter implements SensorEventListener {

    Context mContext;
    SensorManager sensorManager;
    Sensor sSensor;
    private long steps = 0;

    public StepCounter(Context mContext) {
        this.mContext = mContext;
        sensorManager = (SensorManager) mContext.getSystemService(SENSOR_SERVICE);
        sSensor= sensorManager .getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
    }

    public long getStepCount() {
        return steps;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        float[] values = event.values;
        int value = -1;

        if (values.length > 0) {
            value = (int) values[0];
        }


        if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            steps++;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
    }
}