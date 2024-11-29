package com.example.sphinxevents;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;

public class SendOrganizerNotificationFragment extends DialogFragment {

    private Event event;

    public static SendOrganizerNotificationFragment newInstance() {
        return new SendOrganizerNotificationFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the fragment's layout
        View view = inflater.inflate(R.layout.fragment_send_organizer_notification, container, false);

        // Initialize views
        CheckBox cbWaitingList = view.findViewById(R.id.cbWaitingList);
        CheckBox cbCancelled = view.findViewById(R.id.cbCancelled);
        CheckBox cbJoined = view.findViewById(R.id.cbJoined);
        EditText etNotificationTitle = view.findViewById(R.id.etNotificationTitle);
        EditText etNotificationMessage = view.findViewById(R.id.etNotificationMessage);

        // Dialog builder for handling buttons
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setView(view)
                .setTitle("Send Notification")
                .setCancelable(true)
                .setPositiveButton("Send", (dialog, which) -> {
                    String title = etNotificationTitle.getText().toString().trim();
                    String message = etNotificationMessage.getText().toString().trim();

                    // Validate input fields
                    if (TextUtils.isEmpty(title) || TextUtils.isEmpty(message)) {
                        Toast.makeText(getContext(), "Title and message cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Collect selected recipients
                    ArrayList<String> recipients = new ArrayList<>();
                    if (cbWaitingList.isChecked()) {
                        recipients.addAll(event.getEventEntrants());
                    }
                    if (cbCancelled.isChecked()) {
                        recipients.addAll(event.getEventEntrants());
                    }
                    if (cbJoined.isChecked()) {
                        recipients.addAll(event.getEventEntrants());
                    }

                    if (recipients.isEmpty()) {
                        Toast.makeText(getContext(), "Select at least one group", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Send the notification using the NotificationsHelper
                    NotificationsHelper.sendCustomNotification(title, message, recipients,
                            new DatabaseManager.NotificationCreationCallback() {
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(getContext(), "Notification sent successfully", Toast.LENGTH_SHORT).show();
                                    dismiss();
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    Toast.makeText(getContext(), "Failed to send notification: " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("Cancel", (dialog, which) -> dismiss());

        // Return the dialog directly
        return view;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}


