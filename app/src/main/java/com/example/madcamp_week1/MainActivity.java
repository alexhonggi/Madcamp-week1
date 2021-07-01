package com.example.madcamp_week1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    TabLayout tabs;
    PhoneFragment fragmentPhone;
    ImageFragment fragmentImage;
    FreeFragment fragmentFree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentPhone = new PhoneFragment();
        fragmentImage = new ImageFragment();
        fragmentFree = new FreeFragment();

        getSupportFragmentManager().beginTransaction().add(R.id.container, fragmentPhone).commit();

        tabs = findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText("Phone"));
        tabs.addTab(tabs.newTab().setText("Image"));
        tabs.addTab(tabs.newTab().setText("Free"));

        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Fragment selected = null;
                if(position == 0)
                    selected = fragmentPhone;
                else if(position == 1)
                    selected = fragmentImage;
                else if(position == 2)
                    selected = fragmentFree;
                getSupportFragmentManager().beginTransaction().replace(R.id.container, selected).commit();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

}