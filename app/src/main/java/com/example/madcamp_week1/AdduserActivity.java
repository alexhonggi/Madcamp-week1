package com.example.madcamp_week1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;


public class AdduserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adduser);

        Button saveButton = findViewById(R.id.saveUserBtn);
        saveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                EditText name = findViewById(R.id.enterName);
                EditText number = findViewById(R.id.enterNumber);
                Intent intent = new Intent();

                intent.putExtra("name", name.getText().toString());
                intent.putExtra("number", number.getText().toString());
                setResult(2, intent);

                finish();
            }

        });
    }
}