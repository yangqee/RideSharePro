package com.jack.ridesharepro;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jack.ridesharepro.BaseClasses.User;
import com.jack.ridesharepro.BaseClasses.Vehicle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AddVehicleActivity extends AppCompatActivity {
    private Button addVeh;
    private EditText plate;
    private EditText brand;
    private EditText eg;
    private EditText numSeats;
    private EditText price;
    private Button back;
    private String name;

    private int mileage;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);

        addVeh = (Button) this.findViewById(R.id.AddVehicle);
        plate = (EditText) this.findViewById(R.id.licensePlateNumber);
        eg = (EditText) this.findViewById(R.id.fuel);
        brand = (EditText) this.findViewById(R.id.brandName);
        numSeats = (EditText) this.findViewById(R.id.seatsAvailable);
        price = this.findViewById(R.id.priceOfRide);
        back = this.findViewById(R.id.back);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        db.collection("users").document(mAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot snapshot = task.getResult();
                User x = snapshot.toObject(User.class);
                name = x.getName();
            }
        });

//        if (user != null) {
//            // Name, email address, and profile photo Url
//            name = user.getDisplayName();
//            String email = user.getEmail();
//
//        }


        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(AddVehicleActivity.this,MenuActivity.class);
                startActivity(i);

            }
        });

        plate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                plate.getText().clear();
            }
        });
        eg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                eg.getText().clear();
            }
        });
        brand.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                brand.getText().clear();
            }
        });
        addVeh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String carPlate = plate.getText().toString();
                String model = brand.getText().toString();
                double priceR = Double.parseDouble(price.getText().toString());
                int numberOfSeats = Integer.parseInt(numSeats.getText().toString());
                System.out.println(name);
                try {
                    if(eg.getText().toString().equalsIgnoreCase("EV")){
                        Vehicle xp = new Vehicle(carPlate,model,numberOfSeats,priceR,true,name, mAuth.getUid(),0);
                        ArrayList<Vehicle> uCar = new ArrayList<>();
                        uCar.add(xp);
                        db.collection("users").document(mAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@androidx.annotation.NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot snapshot = task.getResult();
                                User x = snapshot.toObject(User.class);
                                assert x != null;
                                double lat = x.getLat();
                                double lon = x.getLon();
                                int mileage = (int) Math.ceil(distance(lat, lon));
                                ArrayList<Vehicle> vehicles = new ArrayList<>();
                                Vehicle xp = new Vehicle(carPlate,model,numberOfSeats,priceR,false,name, mAuth.getUid(),mileage*2);
                                vehicles.add(xp);
                                x.setUserCars(vehicles);
                                db.collection("users").document(mAuth.getUid()).set(x);
                                db.collection("Vehicles").document(carPlate).set(xp);

                            }
                        });
                        Intent i = new Intent(AddVehicleActivity.this,MenuActivity.class);
                        startActivity(i);

                    } else if (eg.getText().toString().equalsIgnoreCase("Gas")) {

                        db.collection("users").document(mAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@androidx.annotation.NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot snapshot = task.getResult();
                                User x = snapshot.toObject(User.class);
                                assert x != null;
                                double lat = x.getLat();
                                double lon = x.getLon();
                                int mileage = (int) Math.ceil(distance(lat, lon));
                                ArrayList<Vehicle> vehicles = new ArrayList<>();
                                Vehicle xp = new Vehicle(carPlate,model,numberOfSeats,priceR,false,name, mAuth.getUid(),mileage);
                                db.collection("Vehicles").document(xp.getLicensePlate()).set(xp);
                                vehicles.add(xp);
                                x.setUserCars(vehicles);
                                db.collection("users").document(mAuth.getUid()).set(x);


                            }
                        });
                        Intent i = new Intent(AddVehicleActivity.this,MenuActivity.class);
                        startActivity(i);
                    }else {
                        Toast.makeText(AddVehicleActivity.this, "Please fill in required parameters", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception err){
                    Toast.makeText(AddVehicleActivity.this, "Please fill in required parameters", Toast.LENGTH_SHORT).show();
                }



            }
        });




    }
    public static double distance(double lat1, double lon1) {
        final int R = 6371; // Radius of the earth
        double lat2 =22.283709894144884;
        double lon2 =114.19800870671365;

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c;

        distance = Math.pow(distance, 2);
        System.out.println(distance + "in km");
        return Math.sqrt(distance);
    }
}