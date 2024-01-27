package com.yangqee.ridesharepro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.yangqee.ridesharepro.BaseClasses.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MenuActivity extends AppCompatActivity {
    private Button joinFriend;
    private Button addCar;
    private Button joinCar;
    private Button myRides;
    private Button signOut;
    private TextView myId;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        joinFriend = (Button) this.findViewById(R.id.friend);
        addCar = (Button) this.findViewById(R.id.addCar);
        joinCar = (Button) this.findViewById(R.id.joinRide);
        myRides = (Button) this.findViewById(R.id.rides);
        signOut = (Button) this.findViewById(R.id.signOut);
        db = FirebaseFirestore.getInstance();
        myId = this.findViewById(R.id.myId);
        mAuth = FirebaseAuth.getInstance();
        db.collection("users").document(mAuth.getUid()).get();
        db.collection("users").document(mAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@androidx.annotation.NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot snapshot = task.getResult();
                User x = snapshot.toObject(User.class);
                String mileage = String.valueOf(x.getMileage());
                String name = String.valueOf(x.getName());
                myId.setText("Welecom "+ name + "\n" + "Your Mileage: " + mileage);


            }
        });


        addCar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, AddVehicleActivity.class);
                startActivity(intent);
            }
        });
        joinFriend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, friend.class);
                startActivity(intent);

            }
        });
        joinCar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, VehiclesInfoActivity.class);
                startActivity(intent);

            }
        });
        myRides.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, UserRidesInfo.class);
                startActivity(intent);

            }
        });
        signOut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(MenuActivity.this,MainActivity.class);
                startActivity(i);

            }
        });




    }
}