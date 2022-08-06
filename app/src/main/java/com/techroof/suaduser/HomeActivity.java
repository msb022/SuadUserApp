package com.techroof.suaduser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.smarteist.autoimageslider.SliderView;
import com.techroof.suaduser.Authentication.LoginActivity;
import com.techroof.suaduser.Fragments.HomeFragment;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private RecyclerView servicesRv;
    private SliderView sliderView;
    private FirebaseFirestore db;
    private RecyclerView.LayoutManager servicesLayoutManager;
    private ProgressDialog pd;
    private BottomNavigationView btmNav;
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btmNav = findViewById(R.id.btm_nav_view);

        FirebaseApp.initializeApp(this);

        mAuth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();

        FirebaseUser user=mAuth.getCurrentUser();

        if (user==null){

            Intent login=new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(login);
            finish();
//            pd.dismiss();

        }else{


            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment, new HomeFragment()).commit();

            //pd.dismiss();


            btmNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                    switch (item.getItemId()) {

                        case R.id.btm_home:
                            fragment = new HomeFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_fragment, fragment)
                            .commit();

                    return true;

                }
            });

        }


    }
}