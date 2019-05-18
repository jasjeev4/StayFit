package science.logarithmic.stayfit;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.SensorEventListener;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;


/*
*
*   Creates a foreground service for tracking steps walked
*   The service periodically checks the steps walked.
*   The steps walked are used to check with predefined milestones.
*   The time is saved everytime the step count changes.
*   When the time exceeds and hour the notification for the foreground service is updated.
*
 */


public class PedometerService extends Service {

    private static final String TAG = "PedometerService";
    private static final double STEPS_TO_FEET = 2.5;
    private long milestones[] = {1000, 5000, 10000, 50000};
    private int milestoneIdx = 0;
    private Notification notification;
    private NotificationManager manager;
    private NotificationCompat.Builder builder;
    String NOTIFICATION_CHANNEL_ID = "science.logarithmic.stayfit";
    private int NOTIFICATION_ID = 1;


    public PedometerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Used only in case if services are bound (Bound Services).
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Check service for start or stop
        if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {

            // Show the persistent notification
            showNotification();

            // Inform the user of the service's start
            Toast.makeText(this, "Tracking your walking!", Toast.LENGTH_SHORT).show();

            // Make an initial call to trackSteps
            trackSteps();
            // Start tracking steps using a timer that tracks steps walked every 5 seconds
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            // Call trackSteps every 5 seconds
                            trackSteps();
                        }
                    },
                    5000
            );

        }
        else if (intent.getAction().equals(
                Constants.ACTION.STOPFOREGROUND_ACTION)) {
            stopForeground(true);
            stopSelf();
        }
        return START_STICKY;
    }

    private void trackSteps() {

        // Get the steps walked by the user
        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener(
                        new OnSuccessListener<DataSet>() {
                            @Override
                            public void onSuccess(DataSet dataSet) {
                                long total =
                                        dataSet.isEmpty()
                                                ? 0
                                                : dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();

                                // Process the current step count
                                processSteps(total);


                                Log.i(TAG, "Total steps: " + total);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "There was a problem getting the step count.", e);
                            }
                        });
    }

    private void processSteps(long steps) {
        // Get previous step count and last step move from local storage
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        long prevCount = pref.getLong("step_count", -1);
        long lastMove = pref.getLong("changed", -1); //


        // Check for milestone progress
        checkMilestone(steps, prevCount);

        // Check for hourly alert for person stationary say in their office
        checkIdle(lastMove);

        // store step count in local storage

        SharedPreferences.Editor editor = pref.edit();
        editor.putLong("step_count", steps);

        // Also save the if the step count changed at the current time
        if(steps!=prevCount) {
            long now = System.currentTimeMillis();
            editor.putLong("changed", now);
        }
    }

    // This method checks for hourly alert for person stationary say in their office
    private void checkIdle(long lastMove) {
        if(lastMove>0) {
            long now = System.currentTimeMillis();

            // Get the difference between the current time and the previous time in milliseconds
            long diff = now - lastMove;

            final long dayInMilliseconds = 360000;

            // Check if difference is greater than the number of milliseconds in an hour
            if(diff>dayInMilliseconds) {
                // Set the notification message
                String msg = "It's time to get moving!";

                // Update the  text
                builder.setContentText(msg);
                manager.notify(NOTIFICATION_ID, builder.build());
            }
        }
    }

    private void checkMilestone(long steps, long prevCount) {
        // Get the difference in step count
        long delta;
        if(steps>prevCount) {
            delta = steps - prevCount;
        }
        else {
            delta = steps;
        }

        double feet = delta * STEPS_TO_FEET;


        // Check for milestone
        if((milestoneIdx<4) && (feet > milestones[milestoneIdx])) {
            // Set the milestone message
            String msg = "You've walked over " + milestones[milestoneIdx] + " feet!";

            // Update the notification text
            builder.setContentText(msg);
            manager.notify(NOTIFICATION_ID, builder.build());

            // Increment to the next milestone
            milestoneIdx++;
        }
    }

    private void showNotification() {

        // Set up the persistent notification
        Intent notificationIntent = new Intent(this, StatsActivity.class);
        notificationIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0,
                notificationIntent, 0);


        //Create our own notification channel
        String channelName = "My Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        // Display the notification
        builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(getText(R.string.notification_title))
                .setTicker(getText(R.string.ticker_text))
                .setContentText(getText(R.string.notification_message))
                .setSmallIcon(R.drawable.ic_dashboard_black_24dp)
                .setContentIntent(pendingIntent)
                .setOngoing(true);
        notification = builder.build();
        manager.notify(NOTIFICATION_ID, notification);

        // Start the service in foreground
        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                notification);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Stopped tracking!", Toast.LENGTH_SHORT).show();
    }


}

