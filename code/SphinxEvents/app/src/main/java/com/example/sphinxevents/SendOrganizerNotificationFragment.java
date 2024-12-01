package com.example.sphinxevents;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;

/**
 * Fragment that allows the organizer to send a notification to selected recipient groups.
 * The recipient groups are represented by checkboxes for the waiting list, cancelled list,
 * and joined list. After entering a title and message, the notification is sent to the selected groups.
 */
public class SendOrganizerNotificationFragment extends DialogFragment {

    private Event event;

    /**
     * Called when the fragment is attached to its host context.
     *
     * @param context The context to which the fragment is attached.
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }


    /**
     * Creates and returns the dialog that allows the organizer to send a notification.
     * It includes input fields for the notification title and message, as well as checkboxes
     * to select recipient groups.
     *
     * @param savedInstanceState The saved state of the fragment.
     * @return The dialog used to send the notification.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        // Converts the fragment_edit_book.xml layout file into a View object
        View view =
                LayoutInflater.from(getContext()).inflate(R.layout.fragment_send_organizer_notification, null);

        // Gets the input fields from the view
        EditText titleEditText = view.findViewById(R.id.etNotificationTitle);
        EditText messageEditText = view.findViewById(R.id.etNotificationMessage);

        CheckBox cbWaitingList = view.findViewById(R.id.cbWaitingList);
        CheckBox cbCancelled = view.findViewById(R.id.cbCancelled);
        CheckBox cbJoined = view.findViewById(R.id.cbJoined);

        event = (Event) getArguments().getSerializable("event");

        // Create an AlertDialog with a custom view, title, and Cancel/Ok buttons.
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view)
                .setTitle("Send a Notification")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Send", null);

        AlertDialog dialog = builder.create();

        // Override PositiveButton to perform input validation
        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String title = titleEditText.getText().toString().trim();
                String message = messageEditText.getText().toString().trim();

                // Validate inputs
                if (title.isEmpty()) {
                    titleEditText.setError("Notification title cannot be empty");
                } else if (message.isEmpty()) {
                    messageEditText.setError("Notification message cannot be empty");
                } else {
                    // Build the recipient list based on selected checkboxes
                    ArrayList<String> recipients = new ArrayList<>();
                    if (cbWaitingList.isChecked()) {
                        recipients.addAll(event.getEntrants());
                    }
                    if (cbCancelled.isChecked()) {
                        //recipients.addAll(event.getCancelled());
                    }
                    if (cbJoined.isChecked()) {
                        //recipients.addAll(event.getJoined());
                    }

                    // Check if the recipient list is empty
                    if (recipients.isEmpty()) {
                        Toast.makeText(getContext(), "Please select at least one group", Toast.LENGTH_SHORT).show();
                    } else {
                        // Send the notification if valid
                        sendNotification(title, message, recipients);
                        dialog.dismiss();  // Close the dialog
                    }
                }
            });
        });

        return dialog;
    }


    /**
     * Creates a new instance of SendOrganizerNotificationFragment with the specified event.
     *
     * @param event The event to associate with the fragment.
     * @return A new instance of SendOrganizerNotificationFragment.
     */
    static SendOrganizerNotificationFragment newInstance(Event event) {
        Bundle args = new Bundle();
        args.putSerializable("event", event);

        SendOrganizerNotificationFragment fragment = new SendOrganizerNotificationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Sends a notification to the specified recipients with the provided title and message.
     *
     * @param title      The title of the notification.
     * @param message    The message content of the notification.
     * @param recipients The list of recipients to send the notification to.
     */
    private void sendNotification(String title, String message, ArrayList<String> recipients) {
        // Call the NotificationsHelper to send the notification
        NotificationsHelper.sendCustomNotification(title, message, recipients, new DatabaseManager.NotificationCreationCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(getContext(), "Notification sent successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Failed to send notification: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}