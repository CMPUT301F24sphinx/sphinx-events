/*
 * Class Name: NotificationListener
 * Date: 2024-11-06
 *
 * Copyright (c) 2024
 * All rights reserved.
 *
 */


package com.example.sphinxevents;


import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

/**
 * Listens for notification changes in Firestore and triggers system notifications on the user's device.
 */
public class NotificationListener {

    private final FirebaseFirestore db;
    private final Context context;
    private ListenerRegistration notificationsListener;

    /**
     * Constructor to initialize Firebase and Context for the notification listener.
     *
     * @param context The application context.
     */
    public NotificationListener(Context context) {
        this.db = FirebaseFirestore.getInstance();
        this.context = context;
    }

    /**
     * Starts listening for changes in the user's notification sub-collection in Firestore.
     * This method triggers notifications based on added or modified documents.
     *
     * @param userId The ID of the user whose notifications are to be monitored.
     */
    public void startListeningForNotifications(String userId) {
        // Reference to the user's notifications sub-collection
        notificationsListener = db.collection("users")
                .document(userId)
                .collection("notifications")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.e("NotificationListener", "Error listening to notifications", e);
                        return;
                    }

                    if (snapshots != null) {
                        // Iterate over each change in the notifications
                        for (DocumentChange documentChange : snapshots.getDocumentChanges()) {
                            // Handle only newly added or modified notifications
                            if (documentChange.getType() == DocumentChange.Type.ADDED ||
                                    documentChange.getType() == DocumentChange.Type.MODIFIED) {

                                DocumentSnapshot doc = documentChange.getDocument();
                                String title = doc.getString("title");
                                String message = doc.getString("message");
                                String channelID = doc.getString("channelID");

                                // Use Firestore document ID as a unique notification ID
                                String documentID = doc.getId();
                                int notificationID = documentID.hashCode();

                                if (title != null && message != null && channelID != null) {
                                    // Trigger a system notification
                                    sendSystemNotification(title, message, notificationID, channelID);
                                    // Remove the notification from the database
                                    db.collection("users")
                                            .document(userId)
                                            .collection("notifications")
                                            .document(documentID)
                                            .delete();
                                } else {
                                    Log.w("NotificationListener", "Missing fields in notification document: " + doc.getId());
                                }
                            }
                        }
                    }
                });
    }


    /**
     * Stops listening for notifications and removes the Firestore snapshot listener.
     */
    public void stopListeningForNotifications() {
        if (notificationsListener != null) {
            notificationsListener.remove();
        }
    }

    /**
     * Sends a system notification to the user's device with the provided title, message, and notification details.
     *
     * @param title           The title of the notification.
     * @param message         The message content of the notification.
     * @param notificationID  The unique ID for the notification.
     * @param channelID       The notification channel ID.
     */
    private void sendSystemNotification(String title, String message, int notificationID, String channelID) {

        // Create a Notification Builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message));

        // Create a Notification Manager
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        // Issue the notification
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManagerCompat.notify(notificationID, builder.build());
    }
}
