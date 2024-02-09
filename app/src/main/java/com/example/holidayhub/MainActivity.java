package com.example.holidayhub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    String uid;
BottomNavigationView bottomNavigation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigation=findViewById(R.id.bottomNavigation);

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {


            int itemId= item.getItemId();


            if (itemId==R.id.nav_home){
                loadFrag(new HomeFragment(),true);
            }else if (itemId==R.id.nav_calender){
                loadFrag(new RequestFragment(), false);
            }else if(itemId==R.id.nav_setting) {
                loadFrag(new SettingsFragment(),false);
            }



            return true;
         }
        });
       bottomNavigation.setSelectedItemId(R.id.nav_home);


    }
public  void loadFrag (Fragment fragment,boolean flag){
    FragmentManager fm = getSupportFragmentManager();
    FragmentTransaction ft =fm.beginTransaction();

    Intent intent=getIntent();
    uid= intent.getStringExtra("userId");

    Bundle bundle=new Bundle();
    bundle.putString("userId",uid);

    fragment.setArguments(bundle);


     if (flag){
         ft.add(R.id.frameLayout,fragment);
     }
     else {
        ft.replace(R.id.frameLayout, fragment);
      }
    ft.commit();
    }
}