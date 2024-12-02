/*
 * Class Name: ProfilesAdapter
 * Date: 2024-11-27
 *
 * Description:
 * Adapter for profiles in list
 * Used when admin searches for profiles
 *
 */

package com.example.sphinxevents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * Adapter for profiles in list
 * Used when admin searches for profiles
 */
public class ProfilesAdapter extends ArrayAdapter<Entrant> {

    /**
     * Constructor for FacilityAdapter
     * @param context the context as to where this adapter is used
     * @param users the array of profiles to display
     */
    public ProfilesAdapter(@NonNull Context context, ArrayList<Entrant> users) {
        super(context, 0, users);
    }

    /**
     * Displays the profiles within the list view
     * @param position index of the profile in the list
     * @param convertView reference to the existing view if it exists
     * @param parent parent view
     * @return view of profile
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_profile, parent, false);
        }
        else {
            view = convertView;
        }

        // Obtains UI elements
        Entrant entrant = getItem(position);
        TextView profileNameTextView = view.findViewById(R.id.name_text_view);
        TextView profileRoleTextView = view.findViewById(R.id.role_text_view);
        TextView profileEmailTextView = view.findViewById(R.id.email_text_view);
        TextView profilePhoneTextView = view.findViewById(R.id.phoneNumber_text_view);
        TextView profiledeviceIDTextView = view.findViewById(R.id.deviceID_text_view);

        // Sets display of user information
        profileNameTextView.setText(entrant.getName());
        profileRoleTextView.setText(entrant.getRole());
        profileEmailTextView.setText(entrant.getEmail());
        profilePhoneTextView.setText(entrant.getPhoneNumber());
        profiledeviceIDTextView.setText(entrant.getDeviceId());

        return view;
    }
}