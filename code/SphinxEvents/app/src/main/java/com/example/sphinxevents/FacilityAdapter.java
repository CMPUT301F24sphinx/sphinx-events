/*
 * Adapter for listviews that hold facilities
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

import com.example.sphinxevents.Facility;

import java.util.ArrayList;

public class FacilityAdapter extends ArrayAdapter<Facility> {

    public FacilityAdapter(@NonNull Context context, ArrayList<Facility> facilities) {
        super(context, 0, facilities);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_facility, parent, false);
        }
        else {
            view = convertView;
        }

        Facility facility = getItem(position);
        TextView facilityNameTextView = view.findViewById(R.id.facility_name_text_view);
        TextView facilityPhoneNumberTextView = view.findViewById(R.id.facility_phone_number_text_view);

        facilityNameTextView.setText(facility.getName());
        facilityPhoneNumberTextView.setText(facility.getPhoneNumber());

        return view;
    }
}
