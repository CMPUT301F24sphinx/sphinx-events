/*
 * Class Name: CancelEntrantDialogFragment
 * Version: 1.0
 * Date: 2024-12-01
 *
 * Copyright (c) 2024 Adam Paunovic
 * All rights reserved.
 *
 */

package com.example.sphinxevents;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * A dialog fragment that allows an organizer to confirm the cancellation of an entrant from an event.
 * Displays the entrant's profile picture, name, and email, and provides options to cancel or dismiss the action.
 */
public class CancelEntrantDialogFragment extends DialogFragment {

    Entrant entrant;
    Event event;

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
                LayoutInflater.from(getContext()).inflate(R.layout.fragment_cancel_entrant, null);

        ImageView pfpDisplay = view.findViewById(R.id.entrant_profile_picture);
        TextView entrantName = view.findViewById(R.id.entrant_name);
        TextView entrantEmail = view.findViewById(R.id.entrant_email);

        entrant = (Entrant) getArguments().getSerializable("entrant");
        event = (Event) getArguments().getSerializable("event");

        Glide.with(this)
                .load(entrant.getProfilePictureUrl())
                .centerCrop()
                .into(pfpDisplay);

        entrantName.setText(entrant.getName());
        entrantEmail.setText(entrant.getEmail());

        // Create an AlertDialog with a custom view, title, and Cancel/Ok buttons.
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view)
                .setTitle("Do you want to cancel this user?")
                .setNegativeButton("No", null)
                .setPositiveButton("Yes", null);

        AlertDialog dialog = builder.create();

        // Override PositiveButton to perform input validation
        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                DatabaseManager.getInstance().cancelEntrant(event.getEventId(), entrant.getDeviceId(),
                        new DatabaseManager.CancelEntrantCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getContext(), "Entrant successfully cancelled", Toast.LENGTH_SHORT).show();
                        Organizer currentOrganizer = (Organizer) UserManager.getInstance().getCurrentUser();
                        String facilityName = currentOrganizer.getFacility().getName();
                        ArrayList<String> recipientId = new ArrayList<>();
                        recipientId.add(entrant.getDeviceId());
                        NotificationsHelper.sendCancelledNotification(facilityName, event.getName(), recipientId, new DatabaseManager.NotificationCreationCallback() {
                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(getContext(), "Failed to notify user", Toast.LENGTH_SHORT).show();
                            }
                        } );
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(getContext(), "Failed to cancel entrant", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
            });
        });

        return dialog;
    }


    /**
     * Creates a new instance of CancelEntrantDialogFragment with the specified event and entrant.
     *
     * @param event The event to associate with the fragment.
     * @param entrant The entrant to cancel.
     * @return A new instance of CancelEntrantDialogFragment.
     */
    static CancelEntrantDialogFragment newInstance(Event event, Entrant entrant) {
        Bundle args = new Bundle();
        args.putSerializable("event", event);
        args.putSerializable("entrant", entrant);

        CancelEntrantDialogFragment fragment = new CancelEntrantDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
