package com.example.sambeas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class AddContact extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
    }

    public void addContact(View view) {
//        Intent intent = new Intent(this, MapsActivity.class);
//        startActivity(intent);
        Toast.makeText(this, "Adding of Contact still under development", Toast.LENGTH_LONG).show();
    }



    public void recordAudio(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
    public void panicButton(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}