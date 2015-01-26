package org.mutiaraiman.droid.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmHelper {

    private static final String LOG = AlarmHelper.class.getName();
    
    public static final int syncperiod = 4;
    public static final int[] period = { 06, 12, 18 };

    public static void setNotifyPrayer(Context ctx) {
        Intent intent = new Intent(ctx, NotifyPrayerReceiver.class);
        AlarmManager alarms = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        
        for(Calendar calendar : getPeriodicCalendar()){
            intent.putExtra("notifId", calendar.get(Calendar.HOUR_OF_DAY));
            if((PendingIntent.getBroadcast(ctx, calendar.get(Calendar.HOUR_OF_DAY), 
                    intent, PendingIntent.FLAG_NO_CREATE) == null)){
            	
            	PendingIntent actionIntent = PendingIntent.getBroadcast(ctx, calendar.get(Calendar.HOUR_OF_DAY), 
                        intent, PendingIntent.FLAG_CANCEL_CURRENT);
                    alarms.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 
                        AlarmManager.INTERVAL_DAY, actionIntent);
                    Log.d(LOG, "setup notify "+calendar.get(Calendar.HOUR_OF_DAY));
            }else{
            	Log.d(LOG, "alarm already set");
            }
        }
        Log.d(LOG, "finishing setup notify");
        
    }
    
    public static List<Calendar> getPeriodicCalendar() {
        List<Calendar> periodicalendar = new ArrayList<Calendar>();
        for (int p : period) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, p);
            calendar.set(Calendar.MINUTE, 00);
            calendar.set(Calendar.SECOND, 00);
            periodicalendar.add(calendar);
        }
        return periodicalendar;
    }
}
