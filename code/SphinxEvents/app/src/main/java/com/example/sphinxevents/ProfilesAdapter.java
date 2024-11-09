/*
 * Adapter for listviews that hold user profiles
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

public class ProfilesAdapter extends ArrayAdapter<Entrant> {

    public ProfilesAdapter(@NonNull Context context, ArrayList<Entrant> users) {
        super(context, 0, users);
    }

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

        Entrant entrant = getItem(position);
        TextView profileNameTextView = view.findViewById(R.id.name_text_view);
        TextView profileRoleTextView = view.findViewById(R.id.role_text_view);
        TextView profileEmailTextView = view.findViewById(R.id.email_text_view);
        TextView profilePhoneTextView = view.findViewById(R.id.phoneNumber_text_view);
        TextView profiledeviceIDTextView = view.findViewById(R.id.deviceID_text_view);


        profileNameTextView.setText(entrant.getName());
        profileRoleTextView.setText(entrant.getRole());
        profileEmailTextView.setText(entrant.getEmail());
        profilePhoneTextView.setText(entrant.getPhoneNumber());
        profiledeviceIDTextView.setText(entrant.getDeviceId());


        return view;
    }
}