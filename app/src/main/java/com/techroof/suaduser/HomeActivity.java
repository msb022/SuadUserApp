package com.techroof.suaduser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.smarteist.autoimageslider.SliderView;
import com.techroof.suaduser.Authentication.LoginActivity;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private RecyclerView servicesRv;
    private SliderView sliderView;
    private FirebaseFirestore db;
    private RecyclerView.LayoutManager servicesLayoutManager;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FirebaseApp.initializeApp(this);

        mAuth=FirebaseAuth.getInstance();

        db=FirebaseFirestore.getInstance();

        FirebaseUser user=mAuth.getCurrentUser();

        if (user==null){

            Intent login=new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(login);
            finish();
//            pd.dismiss();

        }
    }
}