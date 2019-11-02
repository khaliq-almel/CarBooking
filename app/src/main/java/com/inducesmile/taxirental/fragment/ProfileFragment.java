package com.inducesmile.taxirental.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.inducesmile.taxirental.R;
import com.inducesmile.taxirental.User;


public class ProfileFragment extends Fragment {

    private static final String TAG = ProfileFragment.class.getSimpleName();



    private TextView name_on_my_account;
    private TextView email_on_my_account;
    private TextView phone_on_my_account;
    private TextView street_on_my_account;
    private TextView apt_on_my_account;
    private TextView city_on_my_account;
    private TextView zip_on_my_account;
    //private Button edit_my_account;
    private User user;

    public ProfileFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        getActivity().setTitle("Profile");



        name_on_my_account = (TextView) view.findViewById(R.id.name_on_account);
        email_on_my_account = (TextView) view.findViewById(R.id.email_on_account);
        phone_on_my_account = (TextView) view.findViewById(R.id.phone_on_account);
        street_on_my_account = (TextView) view.findViewById(R.id.street_on_account);
        apt_on_my_account = (TextView) view.findViewById(R.id.apt_on_account);
        city_on_my_account = (TextView) view.findViewById(R.id.city_on_account);
        zip_on_my_account = (TextView) view.findViewById(R.id.zip_on_account);
        //edit_my_account = (Button) view.findViewById(R.id.edit_details);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot users : dataSnapshot.getChildren()) {
                    user = users.getValue(User.class);
                    if (user.getId().equals(uid)) {
                        name_on_my_account.setText(user.getName());
                        email_on_my_account.setText(user.getEmail());
                        phone_on_my_account.setText(user.getPhoneNumber());
                        street_on_my_account.setText(user.getStreetAddress());
                        apt_on_my_account.setText(user.getAptNumber());
                        city_on_my_account.setText(user.getCity());
                        zip_on_my_account.setText(user.getZipCode());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return view;
    }

}
