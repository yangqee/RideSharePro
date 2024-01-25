package com.yangqee.ridesharepro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;

import com.yangqee.ridesharepro.Base.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MenuActivity extends AppCompatActivity {
    private Button joinFriend, addCar, joinCar, myRides, signOut;
    private TextView myId;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Initialize Views
        joinFriend = findViewById(R.id.friend);
        addCar = findViewById(R.id.addCar);
        joinCar = findViewById(R.id.joinRide);
        myRides = findViewById(R.id.rides);
        signOut = findViewById(R.id.signOut);
        myId = findViewById(R.id.myId);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        fetchUserData();

        setupButtonListeners();
    }

    private void fetchUserData() {
        String userId = mAuth.getUid();
        if (userId != null) {
            db.collection("users").document(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot != null && snapshot.exists()) {
                        User user = snapshot.toObject(User.class);
                        if (user != null) {
                            String mileage = String.valueOf(user.getMileage());
                            myId.setText("My ID: " + userId + "\nMileage: " + mileage);
                        }
                    } else {
                        Log.d("MenuActivity", "No such document");
                    }
                } else {
                    Log.e("MenuActivity", "Error getting documents: ", task.getException());
                }
            });
        }
    }

    private void setupButtonListeners() {
        addCar.setOnClickListener(view -> startActivity(new Intent(MenuActivity.this, AddVehicleActivity.class)));
        joinFriend.setOnClickListener(view -> startActivity(new Intent(MenuActivity.this, friend.class)));
        joinCar.setOnClickListener(view -> startActivity(new Intent(MenuActivity.this, VehiclesInfoActivity.class)));
        myRides.setOnClickListener(view -> startActivity(new Intent(MenuActivity.this, UserRidesInfo.class)));
        signOut.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MenuActivity.this, MainActivity.class));
            finish();
        });
    }
}
