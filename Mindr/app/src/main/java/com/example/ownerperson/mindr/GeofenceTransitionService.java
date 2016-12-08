package com.example.ownerperson.mindr;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;


public class GeofenceTransitionService extends IntentService {


    private static final String TAG = GeofenceTransitionService.class.getSimpleName();
    public static final int GEOFENCE_NOTIFICATION_ID = 0;


    public GeofenceTransitionService() {
        super(TAG);
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        // Retrieve the Geofencing intent
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        // Handling errors
        if ( geofencingEvent.hasError() ) {

            String errorMsg = getErrorString(geofencingEvent.getErrorCode() );
            Log.e( TAG, errorMsg );
            return;
        }

        // Retrieve GeofenceTransition
        int geoFenceTransition = geofencingEvent.getGeofenceTransition();
        // Check if the transition type
        if ( geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT ) {

            // Get the geofence that were triggered
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            System.out.println("INSIDE ON HANDLE INTENT, TRIGGERING GEO: " + triggeringGeofences);
            // Create a detail message with Geofences received
            String geofenceTransitionDetails = getGeofenceTransitionDetails(geoFenceTransition, triggeringGeofences );
            // Send notification details as a String
            sendNotification( geofenceTransitionDetails );
        }
    }
    public String listIDName;
    public List<String> arrayItems;
    // Create a detail message with Geofences received
    private String getGeofenceTransitionDetails(int geoFenceTransition, List<Geofence> triggeringGeofences) {
        // get the ID of each geofence triggered
        ArrayList<String> triggeringGeofencesList = new ArrayList<>();

        for ( Geofence geofence : triggeringGeofences ) {
            triggeringGeofencesList.add( geofence.getRequestId() );
            System.out.println("INSIDE GEO TRANSITION DETAILS, TRIGGERING GEOFENCES ID: " + triggeringGeofencesList);
        }

        listIDName = TextUtils.join(", ", triggeringGeofencesList);


//        getInformation();

        String status = null;
        if ( geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ) {
            System.out.println("GEOFENCE_TRANSITION_ENTER");
            status = "Approaching Mindr Location, check list(s): ";
        } else if ( geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT )
            status = "Leaving Mindr Location, check list(s): ";

        return status;

    }

//    public void getInformation(){
//        System.out.println("We are in getInformation");
//        try {
//            AsyncTask task = new GetListItemsRequest().execute(listIDName);
//            List<String> arrayItems = (List<String>) task.get();
//            System.out.println("listItems" + arrayItems);
//
//        } catch (Exception e) {
//            System.out.println(e.toString());
//            e.printStackTrace();
//        }
//    }

    // Send a notification
    private void sendNotification( String msg ) {
        System.out.println("get into sendNotification");

        Log.i(TAG, "sendNotification: " + msg );

        // Intent to start the main Activity
        Intent notificationIntent = MainActivity.makeNotificationIntent(
                getApplicationContext(), msg
        );

        Intent showListItems = new Intent (this, ViewListItems.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
//        stackBuilder.addNextIntent(showListItems);
        PendingIntent notificationPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent showListItemsPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


        // Creating and sending Notification
        NotificationManager notificatioMng =
                (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
        notificatioMng.notify(
                GEOFENCE_NOTIFICATION_ID,
                createNotification(msg, notificationPendingIntent));

    }

    // Create a notification
    private Notification createNotification(String msg, PendingIntent notificationPendingIntent) {
        System.out.println("inside createNotification");
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder
                .setSmallIcon(R.drawable.ic_action_location)
                .setColor(Color.RED)
                .setContentTitle(msg)
                .setContentText(listIDName)
                .setContentIntent(notificationPendingIntent)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                .setAutoCancel(true);
        return notificationBuilder.build();
    }

    // Handle errors
    private static String getErrorString(int errorCode) {
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "GeoFence not available";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "Too many GeoFences";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "Too many pending intents";
            default:
                return "Unknown error.";
        }
    }
}