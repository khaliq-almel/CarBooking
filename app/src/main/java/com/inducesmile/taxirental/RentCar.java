package com.inducesmile.taxirental;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.inducesmile.taxirental.models.CarListObject;

import java.io.IOException;
import java.util.UUID;

public class RentCar extends AppCompatActivity {


    private EditText cname;
    private EditText ctype;
    private EditText ccompany;
    private EditText cseats;
    private EditText cmileage;
    private EditText cfuel;
    private EditText cprice;

    private Button btnChoose, btnUpload;
    private ImageView imageView;

    private Uri filePath;

    private ProgressBar loginUserProgress;


    private final int PICK_IMAGE_REQUEST = 71;


    //Firebase
    FirebaseStorage storage;
    StorageReference storageReference;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_car);


        cname= (EditText) findViewById(R.id.car_name);
        ctype= (EditText) findViewById(R.id.car_type);
        ccompany= (EditText) findViewById(R.id.car_company);
        cseats= (EditText) findViewById(R.id.car_seats);
        cmileage= (EditText) findViewById(R.id.car_mileage);
        cfuel= (EditText) findViewById(R.id.car_fuel);
        cprice= (EditText) findViewById(R.id.car_price);


        loginUserProgress = (ProgressBar) findViewById(R.id.login_user_progress);




        //Initialize Views
        btnChoose = (Button) findViewById(R.id.btnChoose);
        btnUpload = (Button) findViewById(R.id.btnUpload);
        imageView = (ImageView) findViewById(R.id.imgView);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mAuth=FirebaseAuth.getInstance();


        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });



    }

    private void chooseImage() {


        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {


        final String carName= cname.getText().toString().trim();
        final String carType= ctype.getText().toString().trim();
        final String carCompany= ccompany.getText().toString().trim();
        final String carSeats= cseats.getText().toString().trim();
        final String carMileage= cmileage.getText().toString().trim();
        final String carFuel= cfuel.getText().toString().trim();
        final String carPrice= cprice.getText().toString().trim();

        if (carName.isEmpty()) {
            cname.setError(getString(R.string.input_error_name));
            cname.requestFocus();
            return;
        }

        if (carType.isEmpty()) {
            ctype.setError(getString(R.string.input_error_name));
            ctype.requestFocus();
            return;
        }

        if (carCompany.isEmpty()) {
            ccompany.setError(getString(R.string.input_error_name));
            ccompany.requestFocus();
            return;
        }

        if (carSeats.isEmpty()) {
            cseats.setError(getString(R.string.input_error_name));
            cseats.requestFocus();
            return;
        }

        if (carMileage.isEmpty()) {
            cmileage.setError(getString(R.string.input_error_name));
            cmileage.requestFocus();
            return;
        }

        if (carFuel.isEmpty()) {
            cfuel.setError(getString(R.string.input_error_name));
            cfuel.requestFocus();
            return;
        }

        if (carPrice.isEmpty()) {
            cprice.setError(getString(R.string.input_error_name));
            cprice.requestFocus();
            return;
        }

        float price= Float.parseFloat(carPrice);

        loginUserProgress.setVisibility(View.VISIBLE);



        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            String randomm=UUID.randomUUID().toString();
            StorageReference ref = storageReference.child("images/"+randomm);

            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            String uid = mAuth.getCurrentUser().getUid();
                            String random=taskSnapshot.getDownloadUrl().toString();
                            CarListObject newCar= new CarListObject(0,0,uid,carName,carCompany,random,carType,carSeats,carMileage,carFuel,"free",price);
                            FirebaseDatabase.getInstance().getReference("cars").child(uid).setValue(newCar).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){

                                        loginUserProgress.setVisibility(View.GONE);


                                        progressDialog.dismiss();
                                        Toast.makeText(RentCar.this, "Uploaded", Toast.LENGTH_SHORT).show();
                                        Intent forgottenIntent = new Intent(RentCar.this, PreHome.class);
                                        startActivity(forgottenIntent);

                                    }else{
                                        loginUserProgress.setVisibility(View.GONE);

                                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();

                                    }
                                }
                            });


                            /*
                            progressDialog.dismiss();
                            Toast.makeText(RentCar.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            Intent forgottenIntent = new Intent(RentCar.this, PreHome.class);
                            startActivity(forgottenIntent);

                             */

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loginUserProgress.setVisibility(View.GONE);

                            progressDialog.dismiss();
                            Toast.makeText(RentCar.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }
}
