package com.example.sambeas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.sambeas.database.AddActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivityRecyclerViewDB extends AppCompatActivity {

    RecyclerView recyclerView;
    FloatingActionButton floatingAddButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_recycler_view_db);

        recyclerView.findViewById(R.id.recyclerView);
        floatingAddButton.findViewById(R.id.floatingAddButton);

        floatingAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (MainActivityRecyclerViewDB.this, AddActivity.class);
                startActivity(intent);

            }
        });
    }
}